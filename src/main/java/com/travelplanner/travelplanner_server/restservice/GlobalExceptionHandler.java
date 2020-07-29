package com.travelplanner.travelplanner_server.restservice;

import com.travelplanner.travelplanner_server.exception.DuplicateUserException;
import com.travelplanner.travelplanner_server.exception.EmptyCommentException;
import com.travelplanner.travelplanner_server.exception.FailedAuthenticationException;
import com.travelplanner.travelplanner_server.exception.InvalidTokenException;
import com.travelplanner.travelplanner_server.exception.*;
import com.travelplanner.travelplanner_server.restservice.payload.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> IllegalArgumentHandler(Exception ex, WebRequest req) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> DuplicateUserHandler(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(FailedAuthenticationException.class)
    public ResponseEntity<ErrorResponse> FailedAuthenticationHandler(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> InvalidTokenHandler(Exception ex, WebRequest request){
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(EmptyCommentException.class)
    public ResponseEntity<ErrorResponse> EmptyCommentHandler(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPlaceIdException.class)
    public ResponseEntity<ErrorResponse> InvalidPlaceIdHandler(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCommentIdException.class)
    public ResponseEntity<ErrorResponse> InvalidCommentIdHandler(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(VoteException.class)
    public ResponseEntity<ErrorResponse> DuplicateVoteException(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

}
