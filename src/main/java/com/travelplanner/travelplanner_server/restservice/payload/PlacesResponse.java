package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlacesResponse {
    private String status;
    private PlacesData data;


}
