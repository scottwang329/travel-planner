package com.travelplanner.travelplanner_server.mongodb.dal;

import com.mongodb.bulk.BulkWriteError;
import com.travelplanner.travelplanner_server.model.Place;
import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class UserPlaceVoteDAL {


    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserPlaceVoteDAL(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // Return: a list of userPlaceVotes that are duplicate (Failed to insert to our DB)
    public List<UserPlaceVote> insertVoteResults(List<UserPlaceVote> userPlaceVotes) {
        List<UserPlaceVote> res = new ArrayList<UserPlaceVote>();
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserPlaceVote.class);
        for (UserPlaceVote userPlaceVote : userPlaceVotes) {
            if (userPlaceVote.getVote() == 1) {
                bulkOperations.insert(userPlaceVote);
            } else {
                // Remove the userPlaceVote document in vote collections
                Query query = new Query();
                query.addCriteria(Criteria.where("place_id").is(userPlaceVote.getPlace_id())
                        .and("user_id").is(userPlaceVote.getUser_id()));
                bulkOperations.remove(query);
            }
        }
        try {
            bulkOperations.execute();
            return res;
        } catch (BulkOperationException ex) {
            for (BulkWriteError error: ex.getErrors()) {
                res.add(userPlaceVotes.get(error.getIndex()));
            }
            return res;
        }
    }

    public void updateVoteCount(Map<String, Long> countMap) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserPlaceVote.class);
        for (Map.Entry<String, Long> entry: countMap.entrySet()) {
            Long votes = entry.getValue();
            if (votes == 0) {
                continue;
            }
            String placeId = entry.getKey();
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(placeId));
            Update update = new Update().inc("upVotes", votes);
            bulkOperations.updateOne(query, update);
        }
        bulkOperations.execute();
    }

//    public void votePlace(UserPlaceVote userPlaceVote) {
//        mongoTemplate.insert(userPlaceVote);
//        Query query = new Query();
//        query.addCriteria(Criteria.where("id").is(place_id));
//        Update update = new Update();
//        update.inc("upVotes", 1);
//        mongoTemplate.updateFirst(query, update, Place.class);
//    }

//    public void undoVotePlace(String place_id, String user_id) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("place_id").is(place_id).and("user_id").is(user_id));
//        mongoTemplate.findAndRemove(query, UserPlaceVote.class);
//        query = new Query();
//        query.addCriteria(Criteria.where("id").is(place_id));
//        Update update = new Update();
//        update.inc("upVotes", -1);
//        mongoTemplate.updateFirst(query, update, Place.class);
//    }
//
//    public boolean hasVotedBefore(String place_id, String user_id) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("place_id").is(place_id).and("user_id").is(user_id));
//        return mongoTemplate.exists(query, UserPlaceVote.class);
//    }


}
