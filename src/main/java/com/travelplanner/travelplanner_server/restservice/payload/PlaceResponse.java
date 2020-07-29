package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlaceResponse {
    private String status;
    private PlaceData data;
}
