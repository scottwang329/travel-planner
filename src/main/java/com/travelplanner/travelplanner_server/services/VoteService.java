package com.travelplanner.travelplanner_server.services;


import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import com.travelplanner.travelplanner_server.mongodb.dal.UserPlaceVoteDAL;
import com.travelplanner.travelplanner_server.redis.RedisDAL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VoteService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private UserPlaceVoteDAL userPlaceVoteDAL;

    @Autowired
    private RedisDAL redisDAL;

    public void votePlace(String userId, String placeId) {
        UserPlaceVote userPlaceVote = UserPlaceVote.builder()
                .user_id(userId)
                .place_id(placeId)
                .createdAt(new Date(System.currentTimeMillis()))
                .build();
        if (redisDAL.saveUpVoteResult(userPlaceVote)) {
            redisDAL.incrementVoteCount(placeId);
        }
        if (redisDAL.deleteUndoVoteResult(userPlaceVote)) {
            redisDAL.incrementVoteCount(placeId);
        }
    }

    public void unDoVotePlace(String userId, String placeId) {
        UserPlaceVote userPlaceVote = UserPlaceVote.builder()
                .user_id(userId)
                .place_id(placeId)
                .build();
        if (redisDAL.saveUndoVoteResult(userPlaceVote)) {
            redisDAL.decrementVoteCount(placeId);
        }
        if (redisDAL.deleteUpVoteResult(userPlaceVote)) {
            redisDAL.decrementVoteCount(placeId);
        }
    }

    public void syncRedisToDB() {
        List<Object> txResults = redisDAL.getAllUserVotesAndCountAndDelete();
        log.info("Size of all txResults is {} ------------{}", txResults.size(), sdf.format(new Date(System.currentTimeMillis())));
        Map<String, Object> upVoteMap = (Map<String, Object>) txResults.get(0);
        Map<String, Object> downVoteMap = (Map<String, Object>) txResults.get(1);
        log.info("Size of all votes is {} ------------{}", upVoteMap.size(), sdf.format(new Date(System.currentTimeMillis())));
        List<UserPlaceVote> failedUpVotes = new ArrayList<>();
        if (upVoteMap.size() == 0 && downVoteMap.size() == 0) {
            return;
        }
        if (upVoteMap.size() > 0) {
            failedUpVotes = userPlaceVoteDAL.insertVoteResults(redisDAL.convertVoteMapToObject(upVoteMap));
        }
        List<UserPlaceVote> failedUndoVotes = new ArrayList<>();
        if (downVoteMap.size() > 0) {
            failedUndoVotes = userPlaceVoteDAL.deleteVoteResults(redisDAL.convertVoteMapToObject(downVoteMap));
        }

        Map<String, Integer> placeVoteResult = (Map<String, Integer>) txResults.get(2);
        for (UserPlaceVote failedVote: failedUpVotes) {
            Integer count = placeVoteResult.getOrDefault(failedVote.getPlace_id(),0);
            // Update count number
            count = count - 1;
            placeVoteResult.put(failedVote.getPlace_id(), count);
        }
        for (UserPlaceVote failedVote: failedUndoVotes) {
            Integer count = placeVoteResult.getOrDefault(failedVote.getPlace_id(),0);
            // Update count number
            count = count + 1;
            placeVoteResult.put(failedVote.getPlace_id(), count);
        }
        placeVoteResult.values().removeIf(votes -> votes == 0);
        if (placeVoteResult.size() > 0) {
            userPlaceVoteDAL.updateVoteCount(placeVoteResult);
        }
    }
}
