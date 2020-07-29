package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String username;
    private String password;
    private String passwordConfirmation;
    private String firstname;
    private String lastname;
    private String email;
    private String profileUrl;
}
