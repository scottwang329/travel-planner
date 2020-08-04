package com.travelplanner.travelplanner_server.redis;

import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class RedisDAL {

    @Autowired
    private RedisTemplate<String, String> template;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOps;

    private final String VOTE_INFO_KEY = "UserPlaceVotes";
    private final String VOTE_COUNT_KEY = "PlaceVotes";

    public boolean saveVoteResult(UserPlaceVote userPlaceVote) {
        String key = userPlaceVote.getUser_id() + ":" + userPlaceVote.getPlace_id() + ":" + userPlaceVote.getVote();
        return hashOps.putIfAbsent(VOTE_INFO_KEY, key, userPlaceVote.getCreatedAt());
    }

//    public boolean deleteVoteResult(UserPlaceVote userPlaceVote) {
//        String key = userPlaceVote.getUser_id() + ":" + userPlaceVote.getPlace_id() + ":" + userPlaceVote.getVote();
//        return hashOps.delete(VOTE_INFO_KEY, key) == 1;
//    }

    public void incrementVoteCount(String placeId) {
        hashOps.increment(VOTE_COUNT_KEY, placeId, 1);
    }

    public void decrementVoteCount(String placeId) {
        hashOps.increment(VOTE_COUNT_KEY, placeId, -1);
    }

//    public void deleteVoteCount(String placeId) {
//        hashOps.delete(VOTE_COUNT_KEY, placeId);
//    }

    public List<UserPlaceVote> convertVoteMapToObject(Map<String, Object> voteMap) {
        List<UserPlaceVote> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry: voteMap.entrySet()) {
            String[] keys = entry.getKey().split(":");
            String userId = keys[0];
            String placeId = keys[1];
            Integer vote = Integer.parseInt(keys[2]);
            Date createdAt = (Date) entry.getValue();
            result.add(UserPlaceVote.builder()
                    .user_id(userId)
                    .place_id(placeId)
                    .createdAt(createdAt)
                    .vote(vote)
                    .build());
        }
        return result;
    }


    public List<Object> getAllUserVotesAndCountAndDelete() {
        //execute a transaction
        return template.execute(new SessionCallback<List<Object>>() {
            public List<Object>  execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                // Get all userVotes
                operations.opsForHash().entries(VOTE_INFO_KEY);

                // Get all count
                operations.opsForHash().entries(VOTE_COUNT_KEY);

                operations.delete(VOTE_INFO_KEY);
                operations.delete(VOTE_COUNT_KEY);

                // This will contain the results of all operations in the transaction
                return operations.exec();
            }
        });
//        Map<String, Date> map = (Map<String, Date>) txResults.get(0);
//        return convertVoteMapToObject(map);
    }

}
