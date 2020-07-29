package com.travelplanner.travelplanner_server.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection="usedToken")
@Getter
@Setter
public class UsedToken {
    @Id
    private String id;
    @Indexed(unique = true)
    private String token;

    // constructor
    public UsedToken(){}
    public UsedToken(String token){
        this.token = token;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date updatedAt;
}

