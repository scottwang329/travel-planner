package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RecommendData {
    private Long minimumDistance;
    private List<String> place_ids;
}
