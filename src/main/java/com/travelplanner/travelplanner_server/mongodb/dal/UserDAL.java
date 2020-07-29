package com.travelplanner.travelplanner_server.mongodb.dal;

import com.travelplanner.travelplanner_server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


/*
 *    For how to use query in MongoTemplate, please refer to: https://www.baeldung.com/queries-in-spring-data-mongodb
 *    MongoTemplate reference: https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html
 */
@Repository
public class UserDAL {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserDAL(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public User createUser (User user) {
        return mongoTemplate.insert(user);
    }

    public String findIdByUsername(String username){
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        User user = mongoTemplate.findOne(query, User.class);
        return user.getId();
    }

    public User findUserByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, User.class);
    }
}
