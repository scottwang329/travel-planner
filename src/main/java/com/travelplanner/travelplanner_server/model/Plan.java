package com.travelplanner.travelplanner_server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;



@Document(collection = "plan")
@Builder
@Getter
@Setter
@CompoundIndex(def="{'user_id': 1, 'name': 1}", unique = true)
public class Plan {
    @Id
    private String id;
    private String user_id;
    private List<String> place_id;
    private String name;
    private Date createdAt;
    private Date updatedAt;
}
