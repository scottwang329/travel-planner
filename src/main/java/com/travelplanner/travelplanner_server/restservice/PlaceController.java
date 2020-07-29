package com.travelplanner.travelplanner_server.restservice;

import com.travelplanner.travelplanner_server.external.GooglePlaceClient;
import com.travelplanner.travelplanner_server.model.UserPlaceVote;
import com.travelplanner.travelplanner_server.mongodb.dal.UserPlaceVoteDAL;
import com.travelplanner.travelplanner_server.restservice.config.JwtTokenUtil;
import com.travelplanner.travelplanner_server.restservice.payload.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.travelplanner.travelplanner_server.exception.InvalidPlaceIdException;
import com.travelplanner.travelplanner_server.model.Comment;
import com.travelplanner.travelplanner_server.model.Place;
import com.travelplanner.travelplanner_server.mongodb.dal.CommentDAL;
import com.travelplanner.travelplanner_server.mongodb.dal.PlaceDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class PlaceController {

    @Autowired
    private PlaceDAL placeDAL;
    @Autowired
    private CommentDAL commentDAL;
    @Autowired
    private UserPlaceVoteDAL userPlaceVoteDAL;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private GooglePlaceClient googlePlaceClient;


    /**
     * Get a single place detail with place_id.
     * locahost：8080/attraction/12345？max_width=200
     * @param placeId place_id
     * @return
     */
    @RequestMapping(value = "/place/{placeid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlaceResponse> getOnePlace(@PathVariable("placeid") String placeId,
                                                     @RequestHeader("Authorization") String tokenHeader) {
        if (placeId == null) {
            throw new InvalidPlaceIdException();
        }
        String token = tokenHeader.substring(7);
        String user_id = jwtTokenUtil.getUserIdFromToken(token);
        Place place = placeDAL.findOnePlace(placeId);
        List<Comment> comments = commentDAL.getAllCommentById(placeId);
        boolean isLiked = userPlaceVoteDAL.hasVotedBefore(placeId, user_id);
        PlaceData placeData = PlaceData.builder()
                .place_id(placeId)
                .name(place.getName())
                .location(place.getLocation())
                .description(place.getDescription())
                .photo_reference(place.getPhoto_refs())
                .upVotes(place.getUpVotes())
                .isLiked(isLiked)
                .comments(comments)
                .createAt(place.getCreateAt())
                .build();
        return ResponseEntity.ok().body(new PlaceResponse("OK", placeData));
    }

    /**
     * Get all place details.
     * @param
     * @return
     */
    @RequestMapping(value = "/places", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlacesResponse> getAllPlace(@RequestParam(value = "query", required = false) String query,
                                                      @RequestParam(value = "city", required = true) String city,
                                                      @RequestParam(value= "sort", required = false)SortType sortType) {
        List<Place> places = new ArrayList<>();
        if (query != null) {
            places = placeDAL.getAllPlaceFromCity(city, query);
            if (places.size() < 30) {
                places = googlePlaceClient.getCityPlacesWithQuery(city, query, 30);
                for (Place place: places) {
                    place.setCity(city);
                }
                placeDAL.savePlaces(places);
                places = placeDAL.getAllPlaceFromCity(city, query);
            }
        } else {
            places = placeDAL.getAllPlaceFromCity(city);
            if (places.size() < 30) {
                places = googlePlaceClient.getCityPlaces(city, 30);
                for (Place place : places) {
                    place.setCity(city);
                }
                placeDAL.savePlaces(places);
                places = placeDAL.getAllPlaceFromCity(city);
            }
        }
        // Sorting
        if (sortType != null) {
            switch (sortType) {
                case UPVOTES:
                    places.sort(Comparator.comparingLong(Place::getUpVotes).reversed());
                    break;
                case RATING:
                    places.sort(Comparator.comparingDouble(Place::getRating).reversed());
                    break;
                default:
                    throw new IllegalArgumentException("Sort value should be either upvotes or rating");
            }
        }
        return ResponseEntity.ok().body(PlacesResponse.builder().status("OK").data(new PlacesData(places)).build());
    }
}
