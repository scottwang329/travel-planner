package com.travelplanner.travelplanner_server.mongodb.dal;

import com.travelplanner.travelplanner_server.model.Place;
import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class UserPlaceVoteDAL {


    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserPlaceVoteDAL(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void upVotes(String place_id, String user_id) {
        UserPlaceVote userPlaceVote = UserPlaceVote.builder()
                .place_id(place_id)
                .user_id(user_id)
                .createdAt(new Date())
                .build();
        mongoTemplate.insert(userPlaceVote);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(place_id));
        Update update = new Update();
        update.inc("upVotes", 1);
        mongoTemplate.updateFirst(query, update, Place.class);
    }

    public void downVotes(String place_id, String user_id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("place_id").is(place_id).and("user_id").is(user_id));
        mongoTemplate.findAndRemove(query, UserPlaceVote.class);
        query = new Query();
        query.addCriteria(Criteria.where("id").is(place_id));
        Update update = new Update();
        update.inc("upVotes", -1);
        mongoTemplate.updateFirst(query, update, Place.class);
    }

    public boolean hasVotedBefore(String place_id, String user_id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("place_id").is(place_id).and("user_id").is(user_id));
        return mongoTemplate.exists(query, UserPlaceVote.class);
    }


}
