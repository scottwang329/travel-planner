package com.travelplanner.travelplanner_server.exception;

public class InvalidPlaceIdException extends RuntimeException{
    public InvalidPlaceIdException() {
        super("invalid place_id.");
    }
}
