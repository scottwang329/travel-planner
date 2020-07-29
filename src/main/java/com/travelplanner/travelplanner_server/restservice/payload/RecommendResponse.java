package com.travelplanner.travelplanner_server.restservice.payload;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendResponse {

    private String status;
    private RecommendData data;


}
