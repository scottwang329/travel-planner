package com.travelplanner.travelplanner_server.restservice;

import com.travelplanner.travelplanner_server.exception.InvalidCommentIdException;
import com.travelplanner.travelplanner_server.exception.InvalidPlaceIdException;
import com.travelplanner.travelplanner_server.model.Comment;
import com.travelplanner.travelplanner_server.mongodb.dal.CommentDAL;
import com.travelplanner.travelplanner_server.mongodb.dal.PlaceDAL;
import com.travelplanner.travelplanner_server.mongodb.dal.TokenDAL;
import com.travelplanner.travelplanner_server.mongodb.dal.UserDAL;
import com.travelplanner.travelplanner_server.restservice.config.JwtTokenUtil;
import com.travelplanner.travelplanner_server.restservice.payload.CommentRequest;
import com.travelplanner.travelplanner_server.restservice.payload.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentDAL commentDAL;
    @Autowired
    private UserDAL userDAL;
    @Autowired
    private PlaceDAL placeDAL;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Create a new user comment.
     *
     * @param commentRequest body
     * @return comment_id of current comment
     */
    @RequestMapping(value = "/comment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> post(@RequestHeader("Authorization") String tokenHeader,
                                                @RequestBody CommentRequest commentRequest) {

        String token = tokenHeader.substring(7);
        String userId = jwtTokenUtil.getUsernameFromToken(token);
        String placeId = commentRequest.getPlace_id();
        if (placeId == null || !placeDAL.hasPlace(placeId)) {
            throw new InvalidPlaceIdException();
        }
        Comment comment = Comment.builder()
                .place_id(placeId)
                .username(userId)
                .content(commentRequest.getContent())
                .createTime(new Date())
                .build();
        comment = commentDAL.createComment(comment);
        CommentResponse commentResponse = new CommentResponse(comment.getId());

        return ResponseEntity.ok().body(commentResponse);
    }

    /**
     * Delete a specified comment from a specified place.
     * @param commentId comment_id
     */
    @RequestMapping(value = "/comment/{comment_id}", method = RequestMethod.DELETE)
    public void delete (@PathVariable("comment_id") String commentId){

        if (commentId == null || !commentDAL.hasComment(commentId)) {
            throw new InvalidCommentIdException();
        }
        commentDAL.deleteComment(commentId);
    }

    /**
     * Get all comments
     * @param placeId
     * @return
     */
    @RequestMapping(value = "/comments/{placeid}", method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> get(@PathVariable("placeid") String placeId){
        List<Comment> listComment = commentDAL.getAllCommentById(placeId);
        return ResponseEntity.ok().body(listComment);
    }
}