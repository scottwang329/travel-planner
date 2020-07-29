package com.travelplanner.travelplanner_server.mongodb.dal;

import com.travelplanner.travelplanner_server.model.Comment;
import com.travelplanner.travelplanner_server.model.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class PlanDAL {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PlanDAL(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Plan createPlan(Plan plan) {
        return mongoTemplate.insert(plan);
    }

    public Plan updatePlan(Plan plan, List<String> place_id, Date date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(plan.getId()));
        Update update = new Update();
        update.set("place_id", place_id);
        update.set("updatedAt", date);
        return mongoTemplate.findAndModify(query, update, Plan.class);
    }

    public void deletePlanByUserIdAndName(String name,String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name).and("user_id").is(userId));
        mongoTemplate.findAndRemove(query, Plan.class);
    }

    public List<Plan> findPlansByUserId(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));
        return mongoTemplate.find(query, Plan.class);
    }

    public Plan findPlanByUserIdAndName(String userId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId).and("name").is(name));
        return mongoTemplate.findOne(query, Plan.class);
    }

    public boolean hasPlan(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        return mongoTemplate.exists(query, Plan.class);
    }

}
