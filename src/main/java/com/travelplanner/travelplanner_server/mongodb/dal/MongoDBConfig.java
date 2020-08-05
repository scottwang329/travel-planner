package com.travelplanner.travelplanner_server.mongodb.dal;


import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MongoConverter;

@Configuration
public class MongoDBConfig {
//    public @Bean
//    MongoClient mongoClient(@Value("${spring.data.}")) {
//        return MongoClients.create("mongodb://localhost:27017");
//    }

//    @Autowired
//    public @Bean MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory, MongoConverter mongoConverter) {
//        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, mongoConverter);
//        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
//        mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
//        return mongoTemplate;
//    }
}
