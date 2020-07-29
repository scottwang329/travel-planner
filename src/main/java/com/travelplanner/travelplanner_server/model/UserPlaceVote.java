package com.travelplanner.travelplanner_server.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "vote")
@Getter
@CompoundIndex(def="{'user_id': 1, 'place_id': 1}", unique = true)
@Builder
public class UserPlaceVote {
    @Id
    private String id;
    private String user_id;
    private String place_id;
    private Date createdAt;
}
