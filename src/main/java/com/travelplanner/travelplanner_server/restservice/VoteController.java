package com.travelplanner.travelplanner_server.restservice;


import com.travelplanner.travelplanner_server.exception.VoteException;
import com.travelplanner.travelplanner_server.mongodb.dal.UserPlaceVoteDAL;
import com.travelplanner.travelplanner_server.restservice.config.JwtTokenUtil;
import com.travelplanner.travelplanner_server.restservice.payload.VoteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VoteController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserPlaceVoteDAL userPlaceVoteDAL;

    @PostMapping(value = "/vote", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> votePlace(@RequestBody VoteRequest request,
                                          @RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.substring(7);
        String user_id = jwtTokenUtil.getUserIdFromToken(token);
        int vote = request.getVote();
        if (vote == 1) { // upVote
            if (userPlaceVoteDAL.hasVotedBefore(request.getPlace_id(), user_id)) {
                throw new VoteException("User has voted before");
            }
            userPlaceVoteDAL.upVotes(request.getPlace_id(), user_id);
        } else if (vote == -1) {
            if (!userPlaceVoteDAL.hasVotedBefore(request.getPlace_id(), user_id)) {
                throw new VoteException("User hasn't vote before");
            }
            userPlaceVoteDAL.downVotes(request.getPlace_id(), user_id);
        } else {
            throw new IllegalArgumentException("Wrong vote value: vote can only be either 1 or -1");
        }
        return ResponseEntity.ok().body(null);
    }



}
