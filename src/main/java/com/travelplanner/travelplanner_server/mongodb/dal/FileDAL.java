package com.travelplanner.travelplanner_server.mongodb.dal;

import com.travelplanner.travelplanner_server.model.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FileDAL {
    private final MongoTemplate mongoTemplate;
    @Autowired
    public FileDAL(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public UploadFile createFile(UploadFile file){
        return mongoTemplate.insert(file);
    }

}
