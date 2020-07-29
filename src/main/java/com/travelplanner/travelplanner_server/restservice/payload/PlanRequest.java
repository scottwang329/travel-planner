package com.travelplanner.travelplanner_server.restservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlanRequest {
    private String name;
    private List<String> place_id;
}
