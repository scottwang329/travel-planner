package com.travelplanner.travelplanner_server.mongodb.dal;

import com.travelplanner.travelplanner_server.model.UsedToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TokenDAL {
    private final MongoTemplate mongoTemplate;
    @Autowired
    public TokenDAL(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public UsedToken createUsedToken(UsedToken usedToken){
        return mongoTemplate.insert(usedToken);
    }

    public UsedToken findUsedTokenByToken(String token){
        Query query = new Query();
        query.addCriteria(Criteria.where("token").is(token));
        return mongoTemplate.findOne(query, UsedToken.class);
    }
}


