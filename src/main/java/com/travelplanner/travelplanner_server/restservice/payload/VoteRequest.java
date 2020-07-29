package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VoteRequest {
    private String place_id;
    private int vote;
}
