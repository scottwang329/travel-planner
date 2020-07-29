package com.travelplanner.travelplanner_server.restservice;

import com.google.common.graph.ValueGraph;
import com.google.maps.errors.ApiException;
import com.travelplanner.travelplanner_server.external.GoogleDistanceClient;
import com.travelplanner.travelplanner_server.services.TSPService;
import com.travelplanner.travelplanner_server.exception.GoogleMapAPIException;
import com.travelplanner.travelplanner_server.restservice.payload.RecommendData;
import com.travelplanner.travelplanner_server.restservice.payload.RecommendRequest;
import com.travelplanner.travelplanner_server.restservice.payload.RecommendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendController {

    @Autowired
    private TSPService service;

    @Autowired
    private GoogleDistanceClient googleDistanceClient;

    @Autowired
    public RecommendController(TSPService service, GoogleDistanceClient googleDistanceClient) {
        this.googleDistanceClient = googleDistanceClient;
        this.service = service;
    }

    @PostMapping(value = "/recommend", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendResponse> recommend(@RequestBody RecommendRequest req) {
        if (req == null || req.getPlace_ids() == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        ValueGraph distGraph;
        try {
            distGraph = googleDistanceClient.generateDistGraph(req.getStartPosition(), req.getPlace_ids());
        } catch (ApiException e) {
            e.printStackTrace();
            throw new GoogleMapAPIException();
        }
        TSPService.RouteCost routeCost = service.findShortestPath(distGraph);
        return ResponseEntity.ok().body(new RecommendResponse("OK",
                new RecommendData(routeCost.distance, routeCost.visitOrder)));
    }
}
