package com.travelplanner.travelplanner_server.exception;

public class EmptyCommentException extends RuntimeException {
    public EmptyCommentException() {
        super("Empty comment(s).");
    }
}
