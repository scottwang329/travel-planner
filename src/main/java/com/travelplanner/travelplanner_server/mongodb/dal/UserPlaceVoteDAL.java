package com.travelplanner.travelplanner_server.mongodb.dal;

import com.mongodb.DBObject;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.travelplanner.travelplanner_server.model.Place;
import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserPlaceVoteDAL {


    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserPlaceVoteDAL(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    // Return: a list of userPlaceVotes that are duplicate (Failed to insert to our DB)
    public List<UserPlaceVote> insertVoteResults(List<UserPlaceVote> userPlaceVotes) {
        return mongoTemplate.execute(UserPlaceVote.class, mongoCollection -> {
            List<UserPlaceVote> res = new ArrayList<>();
            List<InsertOneModel<Document>> operations = new ArrayList<>();
            for (UserPlaceVote userPlaceVote: userPlaceVotes) {
                Document newDoc = new Document()
                        .append("user_id", userPlaceVote.getUser_id())
                        .append("place_id", userPlaceVote.getPlace_id())
                        .append("createdAt", userPlaceVote.getCreatedAt());
                operations.add(new InsertOneModel<>(newDoc));
            }
            try {
                mongoCollection.bulkWrite(operations, new BulkWriteOptions().ordered(false));
                return res;
            } catch (MongoBulkWriteException ex) {
                for (BulkWriteError error: ex.getWriteErrors()) {
                    ex.getStackTrace();
                    //log.error("BulkWriteError ---------------- {}", ex.getStackTrace());
                    res.add(userPlaceVotes.get(error.getIndex()));
                }
                return res;
            }
        });
    }

    public List<UserPlaceVote> deleteVoteResults(List<UserPlaceVote> userPlaceVotes) {
        return mongoTemplate.execute(UserPlaceVote.class, mongoCollection -> {
            List<UserPlaceVote> res = new ArrayList<>();
            List<DeleteOneModel<Document>> operations = new ArrayList<>();
            for (com.travelplanner.travelplanner_server.model.UserPlaceVote userPlaceVote: userPlaceVotes) {
                Document newDoc = new Document()
                        .append("user_id", userPlaceVote.getUser_id())
                        .append("place_id", userPlaceVote.getPlace_id());
                operations.add(new DeleteOneModel<>(newDoc));
            }
            try {
                mongoCollection.bulkWrite(operations, new BulkWriteOptions().ordered(false));
                return res;
            } catch (MongoBulkWriteException ex) {
                for (BulkWriteError error: ex.getWriteErrors()) {
                    res.add(userPlaceVotes.get(error.getIndex()));
                }
                return res;
            }
        });
    }
//        List<UserPlaceVote> res = new ArrayList<UserPlaceVote>();
//        BulkOperations bulkOperations = DefaultBulkOperations
//        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserPlaceVote.class);
//            for (UserPlaceVote userPlaceVote : userPlaceVotes) {
//                bulkOperations.insert(userPlaceVote);
//
//                    // Remove the userPlaceVote document in vote collections
////                    Query query = new Query();
////                    query.addCriteria(Criteria.where("place_id").is(userPlaceVote.getPlace_id())
////                            .and("user_id").is(userPlaceVote.getUser_id()));
////                    bulkOperations.remove(query);
//                }
//        try {
//            bulkOperations.execute();
//            return res;
//        } catch (MongoBulkWriteException ex) {
//            for (BulkWriteError error: ex.getWriteErrors()) {
//                res.add(userPlaceVotes.get(error.getIndex()));
//            }
//            return res;
//        }
//    }

    public void updateVoteCount(Map<String, Integer> countMap) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Place.class);
        for (Map.Entry<String, Integer> entry: countMap.entrySet()) {
            Integer votes = entry.getValue();
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
    public boolean hasVotedBefore(String place_id, String user_id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("place_id").is(place_id).and("user_id").is(user_id));
        return mongoTemplate.exists(query, UserPlaceVote.class);
    }


}
