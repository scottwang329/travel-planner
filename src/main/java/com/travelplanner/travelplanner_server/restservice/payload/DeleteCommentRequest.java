package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.Getter;

@Getter
public class DeleteCommentRequest {
    private String comment_id;
    private String token;
}