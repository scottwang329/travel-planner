package com.travelplanner.travelplanner_server.exception;

public class InvalidCommentIdException extends RuntimeException {
    public InvalidCommentIdException() {
        super("invalid comment_id.");
    }
}
