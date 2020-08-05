package com.travelplanner.travelplanner_server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.sql.Date;


@Document(collection = "vote")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(def="{'user_id': 1, 'place_id': 1}", unique = true)
@Builder
public class UserPlaceVote {
    @Id
    private String id;
    private String user_id;
    private String place_id;
    private Date createdAt;
}
