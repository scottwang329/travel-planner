package com.travelplanner.travelplanner_server.restservice.payload;

import com.google.maps.model.LatLng;
import com.travelplanner.travelplanner_server.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PlaceData {
    private String place_id;
    private String name;
    private LatLng location;
    private List<String> photo_reference;
    private String description;
    private long upVotes;
    private Date createAt;
    private boolean isLiked;
    private List<Comment> comments;
}
