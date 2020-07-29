package com.travelplanner.travelplanner_server.mongodb.dal;

import com.google.maps.model.LatLng;
import com.travelplanner.travelplanner_server.model.Place;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor
public class PlaceDAL {

    @Autowired
    private final MongoTemplate mongoTemplate;

    public void savePlaces(List<Place> places) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Place.class);
        for (Place place: places) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(place.getId()));
            Update update = new Update();
            update.set("name", place.getName());
            update.set("location", place.getLocation());
            update.set("photo_refs", place.getPhoto_refs());
            update.set("rating", place.getRating());
            update.set("city", place.getCity());
            update.setOnInsert("upVotes", 0);
            update.setOnInsert("createAt", new Date());
            bulkOperations.upsert(query, update);
        }
        bulkOperations.execute();

    }

    public Place findOnePlace(String placeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(placeId));
        return mongoTemplate.findOne(query, Place.class);

    }

    /**
     * @param placeId
     * @return
     */
    public boolean hasPlace(String placeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(placeId));
        return mongoTemplate.exists(query, Place.class);
    }

    /**
     * retrieve all place details object
     * @param city: city name
     * @return list of place
     */
    public List<Place> getAllPlaceFromCity(String city) {
        Query query = new Query();
        query.addCriteria(Criteria.where("city").is(city));
        return mongoTemplate.find(query, Place.class);
    }

    /**
     * retrieve all place details object
     * @param city: city name
     * @param textQuery: text query
     * @return list of place
     */
    public List<Place> getAllPlaceFromCity(String city, String textQuery) {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching(textQuery);
        Query query = TextQuery.queryText(textCriteria);
        query.addCriteria(Criteria.where("city").is(city));
        return mongoTemplate.find(query, Place.class);

    }
}
