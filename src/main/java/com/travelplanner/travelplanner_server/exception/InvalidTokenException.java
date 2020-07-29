package com.travelplanner.travelplanner_server.exception;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException() {
        super("This Token is not Valid!");
    }
}