package com.travelplanner.travelplanner_server.external;


import com.google.common.collect.Lists;
import com.google.common.graph.ValueGraph;
import com.google.maps.model.LatLng;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class GoogleDistanceClientTest {

    @Autowired
    private GoogleDistanceClient client;

    @Test
    void contextLoads() {
    }

    // Vancouver Aquarium: ChIJp2zKeo1xhlQRWOMOmCcWJV8
    // Canada Place: ChIJIeDiJJ1xhlQRCWHIheB_Bbc
    // Museum of Vancouver: ChIJHZSzNzNyhlQRzmus61cqE5Y

    @Test
    void generateDistGraphTest() {
        LatLng startPoint =  new LatLng(49.255801, -123.184393);
        List<String> place_ids = Lists.newArrayList("ChIJp2zKeo1xhlQRWOMOmCcWJV8",
                "ChIJIeDiJJ1xhlQRCWHIheB_Bbc", "ChIJHZSzNzNyhlQRzmus61cqE5Y");
        try {
            ValueGraph graph = client.generateDistGraph(startPoint, place_ids);
            graph.toString();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception shouldn't be thrown");
        }
    }




}
