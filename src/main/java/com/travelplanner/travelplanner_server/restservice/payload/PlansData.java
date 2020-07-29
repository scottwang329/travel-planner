package com.travelplanner.travelplanner_server.restservice.payload;

import com.travelplanner.travelplanner_server.model.Plan;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PlansData {
    private List<Plan> plans;
}
