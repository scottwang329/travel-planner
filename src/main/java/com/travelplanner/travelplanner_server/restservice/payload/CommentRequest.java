package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.Getter;
import java.io.Serializable;

@Getter
public class CommentRequest implements Serializable {
    private String content;
    private String place_id;

}



