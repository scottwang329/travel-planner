package com.travelplanner.travelplanner_server.exception;

public class WrongFileException extends RuntimeException{
    public WrongFileException(){
        super("Your File uploaded is not correct!");
    }
}
