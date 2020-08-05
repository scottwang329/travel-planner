package com.travelplanner.travelplanner_server.redis;

import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.Date;

@Repository
public class RedisDAL {

    @Autowired
    private RedisTemplate<String, Object> template;


    private final String UPVOTE_INFO_KEY = "UserPlaceVotes";
    private final String UNDO_VOTE_INFO_KEY = "UndoUserPlaceVotes";
    private final String VOTE_COUNT_KEY = "PlaceVotes";

    public boolean saveUpVoteResult(UserPlaceVote userPlaceVote) {
        String key = userPlaceVote.getUser_id() + ":" + userPlaceVote.getPlace_id();
        return template.opsForHash().putIfAbsent(UPVOTE_INFO_KEY, key, userPlaceVote);
    }

    public boolean saveUndoVoteResult(UserPlaceVote userPlaceVote) {
        String key = userPlaceVote.getUser_id() + ":" + userPlaceVote.getPlace_id();
        return template.opsForHash().putIfAbsent(UNDO_VOTE_INFO_KEY, key, userPlaceVote);
    }

    public boolean deleteUpVoteResult(UserPlaceVote userPlaceVote) {
        String key = userPlaceVote.getUser_id() + ":" + userPlaceVote.getPlace_id();
        return template.opsForHash().delete(UPVOTE_INFO_KEY, key) == 1;
    }

    public boolean deleteUndoVoteResult(UserPlaceVote userPlaceVote) {
        String key = userPlaceVote.getUser_id() + ":" + userPlaceVote.getPlace_id();
        return template.opsForHash().delete(UNDO_VOTE_INFO_KEY, key) == 1;
    }

//    public boolean deleteVoteResult(UserPlaceVote userPlaceVote) {
//        String key = userPlaceVote.getUser_id() + ":" + userPlaceVote.getPlace_id() + ":" + userPlaceVote.getVote();
//        return hashOps.delete(VOTE_INFO_KEY, key) == 1;
//    }

    public void incrementVoteCount(String placeId) {
        template.opsForHash().increment(VOTE_COUNT_KEY, placeId, 1);
    }

    public void decrementVoteCount(String placeId) {
        template.opsForHash().increment(VOTE_COUNT_KEY, placeId, -1);
    }

//    public void deleteVoteCount(String placeId) {
//        hashOps.delete(VOTE_COUNT_KEY, placeId);
//    }

    public List<UserPlaceVote> convertVoteMapToObject(Map<String, Object> voteMap) {
        List<UserPlaceVote> result = new ArrayList<>();
        voteMap.values().forEach(object -> {
            result.add((UserPlaceVote)object);
        });
//        for (Map.Entry<String, Object> entry: voteMap.entrySet()) {
//            String[] keys = entry.getKey().split(":");
//            String userId = keys[0];
//            String placeId = keys[1];
//            UserPlaceVote userPlaceVote = (UserPlaceVote) entry.
//            Integer vote = Integer.parseInt(keys[2]);
//            Date createdAt = Date.valueOf(entry.getValue());
//            result.add(UserPlaceVote.builder()
//                    .user_id(userId)
//                    .place_id(placeId)
//                    .createdAt(createdAt)
//                    .vote(vote)
//                    .build());
//        }
        return result;
    }


    public List<Object> getAllUserVotesAndCountAndDelete() {
        //execute a transaction
        return template.execute(new SessionCallback<List<Object>>() {
            public List<Object>  execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                // Get all userVotes
                operations.opsForHash().entries(UPVOTE_INFO_KEY);
                operations.opsForHash().entries(UNDO_VOTE_INFO_KEY);

                // Get all count
                operations.opsForHash().entries(VOTE_COUNT_KEY);

                operations.delete(UPVOTE_INFO_KEY);
                operations.delete(UNDO_VOTE_INFO_KEY);
                operations.delete(VOTE_COUNT_KEY);

                // This will contain the results of all operations in the transaction
                return operations.exec();
            }
        });
//        Map<String, Date> map = (Map<String, Date>) txResults.get(0);
//        return convertVoteMapToObject(map);
    }

}
