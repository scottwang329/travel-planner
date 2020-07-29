package com.travelplanner.travelplanner_server.restservice.payload;

import com.google.maps.model.LatLng;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendRequest {
    private LatLng startPosition;
    private List<String> place_ids;

}
