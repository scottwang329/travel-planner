package com.travelplanner.travelplanner_server.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import org.bson.types.Binary;
import java.util.Date;

// upload a file in MongoDB:https://blog.csdn.net/augustu1/article/details/89519163

@Document(collection="uploadFile")
@Getter
@Setter
@Data
public class UploadFile {
    @Id
    private String id;
    @Indexed(unique = true)
    private String filename;
    private Binary content;
    private String contentType;
    private long size;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date updatedAt;

    // constructor
    public UploadFile(){}
}
