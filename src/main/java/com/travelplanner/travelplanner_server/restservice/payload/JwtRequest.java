package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
// This class is required for storing the username and password we received from the client
public class JwtRequest implements Serializable {
    private String username;
    private String password;

    // Need default constructor for JSON Parsing
    public JwtRequest(){
    }

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}

