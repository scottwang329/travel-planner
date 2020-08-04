package com.travelplanner.travelplanner_server.services;


import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import com.travelplanner.travelplanner_server.mongodb.dal.UserPlaceVoteDAL;
import com.travelplanner.travelplanner_server.redis.RedisDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class VoteService {

    @Autowired
    private UserPlaceVoteDAL userPlaceVoteDAL;

    @Autowired
    private RedisDAL redisDAL;

    public void votePlace(String userId, String placeId) {
        UserPlaceVote userPlaceVote = UserPlaceVote.builder()
                .user_id(userId)
                .place_id(placeId)
                .createdAt(new Date())
                .vote(1)
                .build();
        if (redisDAL.saveVoteResult(userPlaceVote)) {
            redisDAL.incrementVoteCount(placeId);
        }
    }

    public void unDoVotePlace(String userId, String placeId) {
        UserPlaceVote userPlaceVote = UserPlaceVote.builder()
                .user_id(userId)
                .place_id(placeId)
                .createdAt(new Date())
                .vote(-1)
                .build();
        if (redisDAL.saveVoteResult(userPlaceVote)) {
            redisDAL.decrementVoteCount(placeId);
        }
    }

    public void syncRedisToDB() {
        List<Object> txResults = redisDAL.getAllUserVotesAndCountAndDelete();
        Map<String, Object> map = (Map<String, Object>) txResults.get(0);
        if (map.size() == 0) {
            return;
        }
        List<UserPlaceVote> failedVotes = userPlaceVoteDAL.insertVoteResults(redisDAL.convertVoteMapToObject(map));
        Map<String, Long> placeVoteResult = (Map<String, Long>) txResults.get(1);
        for (UserPlaceVote failedVote: failedVotes) {
            Long count = placeVoteResult.getOrDefault(failedVote.getPlace_id(), (long) 0);
            // Update count number
            count = failedVote.getVote() == 1 ? count - 1 : count + 1;
            placeVoteResult.put(failedVote.getPlace_id(), count);
        }
        userPlaceVoteDAL.updateVoteCount(placeVoteResult);
    }
}
