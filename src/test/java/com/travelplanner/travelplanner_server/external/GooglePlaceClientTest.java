package com.travelplanner.travelplanner_server.external;


import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.model.LatLng;
import com.travelplanner.travelplanner_server.model.Place;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.travelplanner.travelplanner_server.external.GooglePlaceClient.PrintPlaceDetails;
import static com.travelplanner.travelplanner_server.external.GooglePlaceClient.PrintPlacesDetails;

@SpringBootTest
public class GooglePlaceClientTest {

    @Autowired
    private GooglePlaceClient client;


    @Test
    void contextLoads() {
    }

    @Test
    void getCityPlacesTest() {
        List<Place> list1 = client.getCityPlaces("san francisco", 5);
        PrintPlacesDetails(list1);
    }

    @Test
    void getCityPlacesWithQueryTest() {
        List<Place> list1 = client.getCityPlacesWithQuery("Vancouver", "UBC", 5);
        PrintPlacesDetails(list1);
    }

    @Test
    void getCityPlacesInCircleTest() {
        String city_name = "San Francisco";
        List<Place> list = client.getCityPlacesInCircle(city_name, 5, 50000);
        PrintPlacesDetails(list);
    }

    @Test
    void getCityPlacesInRectTest() {
        String city_name = "San Francisco";
        List<Place> list = client.getCityPlacesInRect(city_name, 5, 37.785063, -122.467674, 37.754999, -122.395695);
        PrintPlacesDetails(list);
    }

    @Test
    void getNearbyPlacesInCricleTest() {
        LatLng center = new LatLng(37.771212, -122.432810);
        Place place = Place.builder().location(center).build();
        List<Place> list1 = client.getNearbyPlacesInCricle(place, 5, 20000);
        PrintPlacesDetails(list1);
    }

    @Test
    void getPlaceDetailsTest() {
        Place place = client.getPlaceDetails("ChIJIeDiJJ1xhlQRCWHIheB_Bbc");
        PrintPlaceDetails(place);
    }

}
