package com.travelplanner.travelplanner_server.mongodb.dal;

import com.travelplanner.travelplanner_server.model.Comment;
import com.travelplanner.travelplanner_server.model.Place;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CommentDAL {

    @Autowired
    private final MongoTemplate mongoTemplate;

    /**
     * Create a Comment instance and store it to Place's comment list
     *
     * @param comment
     * @return a new instance of comment
     */
    public Comment createComment(Comment comment) {
        return mongoTemplate.insert(comment);
    }

    /**
     * @param comment_id
     * @return
     */
    public void deleteComment(String comment_id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(comment_id));
        mongoTemplate.findAndRemove(query, Comment.class);
    }

    /**
     * Get a single comment
     * @param id comment_id
     * @return comment corresponding to the id
     */
    public Comment getComment(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Comment.class);
    }

    /**
     * Get all comments of a place by place_id
     * @param placeId place_id
     * @return list of existing comments
     */
    public List<Comment> getAllCommentById(String placeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("place_id").is(placeId));
        return mongoTemplate.findAll(Comment.class);
    }

    /**
     * Check if the given comment_id is valid and has a comment
     * @param commentId comment_id
     * @return
     */
    public boolean hasComment(String commentId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(commentId));
        return mongoTemplate.exists(query, Comment.class);
    }
}
