package com.travelplanner.travelplanner_server.external;

import com.google.maps.*;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.*;
//import com.travelplanner.travelplanner_server.model.City;
import com.travelplanner.travelplanner_server.model.Place;

import java.io.IOException;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.maps.FindPlaceFromTextRequest.InputType;
import com.google.maps.FindPlaceFromTextRequest.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/*
 * Client class of Google Place API:
 * */


//Todo: Implement time out of each functioncall.
@Component
public class GooglePlaceClient {
    /*
     * Members
     * */

    private GeoApiContext context;
    private int time_limit_in_milli;

    private static final String city_places_template = "tourist+attraction+in+";
    private static final String range_places_template = "tourist+attraction+near+";
    private static final String restruant_template = "restaurant+near+";

    /*
     * Constructor:
     *
     * Params:
     * String MY_API_KEY:                   API KEY provided by google cloud.
     * int connect_timeout_in_milli:        Connection timeout of google connection
     * int retry_timeout_in_milli:          Retry timeout of google connection
     * int time_limit_in_milli:             Upper bound of time cost of each function call in this class.
     *
     * */
//    public GooglePlaceClient(int connect_timeout_in_milli, int retry_timeout_in_milli, int time_limit_in_milli) {
//        this.time_limit_in_milli = time_limit_in_milli;
//        this.context = new GeoApiContext.Builder()
//                .apiKey(API_KEY)
//                .connectTimeout(connect_timeout_in_milli, TimeUnit.MILLISECONDS)
//                .retryTimeout(retry_timeout_in_milli, TimeUnit.MILLISECONDS)
//                .build();
//    }

    /*
     * Constructor:
     * Params:
     * String MY_API_KEY:                   API KEY provided by google cloud.
     *
     * */
    public GooglePlaceClient(@Value("${google.map.api.key}") String API_KEY) {
        this.context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
    }

    /*
     * Public interfaces
     * please use those interfaces.
     * */

    /*
     * Params:
     * String city_name:         City Name. ex: "san francisco"
     *
     * Return:
     * A place object related to that city.
     *
     * */
    public List<Place> getCityPlaces(String city_name, int max_place_number) {
        return getCityPlacesInCircle(city_name, max_place_number, 50000); // 50000 is the maximum radius supported by google.
    }

    public List<Place> getCityPlacesWithQuery(String city_name, String query, int max_place_number) {
        return getPlacesFromTextSearchQuery(query + "+in+" + city_name, max_place_number);
    }

    /*
     * Params:
     * String city_name:        City Name. ex: "san francisco"
     * int max_place_number:    Place number need to provied by the client. ex: 10
     * int radius_in_meter:     Radius, in meter. ex: 10000
     *
     * Return:
     * List of places in a circle, whose center is citycenter in google, radius is radius_in_meter.
     *
     * */

    public List<Place> getCityPlacesInCircle(String city_name, int max_place_number, int radius_in_meter) {
        LatLng city_center = getCityCenterPosition(city_name);
        return getPlacesFromTextSearchQueryCircle(city_places_template + city_name, max_place_number, city_center.lat, city_center.lng, radius_in_meter);
    }

    /*
     * Params:
     * String city_name:        City Name. ex: "san francisco"
     * int max_place_number:    Place number need to provied by the client. ex: 10
     * double left_top_lat:     latitude of top left point of the rectangle: ex: 37.771212
     * double left_top_lng:     longitude of top left point of the rectangle: ex: -122.432810
     * double right_down_lat:   latitude of right down point of the rectangle: ex: 37.771212
     * double right_down_lng:   longitude of right down point of the rectangle: ex: -122.432810
     *
     * Return: List of places in a rectangle determined by lefttop point(left_top_lat, left_top_lng) and rightdown point(right_down_lat, right_down_lng).
     *
     * */
    public List<Place> getCityPlacesInRect(String city_name, int max_place_number, double left_top_lat, double left_top_lng, double right_down_lat, double right_down_lng) {
        return getPlacesFromTextSearchQueryRect(city_places_template + city_name, max_place_number, left_top_lat, left_top_lng, right_down_lat, right_down_lng);
    }

    /*
     * Params:
     * Place center:            The cicle center place object.
     * int max_place_number:    Place number need to provied by the client. ex: 10
     * int radius_in_meter:     Radius, in meter. ex: 10000
     *
     * Return: List of places in a rectangle determined by lefttop point(left_top_lat, left_top_lng) and rightdown point(right_down_lat, right_down_lng).
     *
     * */
    public List<Place> getNearbyPlacesInCricle(Place center, int max_place_number, int radius_in_meter) {
        return getPlacesFromNearbySearchrequests(center.getLocation(), radius_in_meter, max_place_number);
    }

    /*
     * Params:
     * String place_id: PlaceID provided by Google
     *
     * Return:
     * A place object
     * */

    public Place getPlaceDetails(String place_id) {
        return getPlaceDetailsFromPlaceDetailSearch(place_id);
    }


    /*
     * lower layer of the client: call google API directly
     *
     * PART I:
     * Get a list of places
     * Contains a certain amount of Places
     * by calling google client directly:
     * 1.Find Place requests(findplacefromtext)
     * 2.Nearby Search requests(nearbysearch)
     * 3.Text Search requests(textsearch)
     *
     * PART II:
     * 4. Place Detail Search
     *
     * //todo
     * PART III:
     * 5. Auto complete
     * */

    //1.Find Place requests(findplacefromtext)
    //input String can be a name, address, or phone number.
    private List<Place> getPlaceFromTextSearch(String input, int max_place_number, boolean input_is_a_phonenumber, FindPlaceFromTextRequest.LocationBias bias) {
        //prepare result
        HashSet<Place> places = new HashSet<Place>();
        while (places.size() < max_place_number) {
            FindPlaceFromTextRequest request = input_is_a_phonenumber
                    ? PlacesApi.findPlaceFromText(context, input, InputType.PHONE_NUMBER)
                    : PlacesApi.findPlaceFromText(context, input, InputType.TEXT_QUERY);

            request.fields(FindPlaceFromTextRequest.FieldMask.NAME,
                    FindPlaceFromTextRequest.FieldMask.PLACE_ID,
                    FindPlaceFromTextRequest.FieldMask.GEOMETRY,
                    FindPlaceFromTextRequest.FieldMask.OPENING_HOURS,
                    FindPlaceFromTextRequest.FieldMask.GEOMETRY,
                    FindPlaceFromTextRequest.FieldMask.RATING,
                    FindPlaceFromTextRequest.FieldMask.PHOTOS,
                    FindPlaceFromTextRequest.FieldMask.TYPES,
                    FindPlaceFromTextRequest.FieldMask.PRICE_LEVEL);

            request.locationBias(bias);

            try {
                FindPlaceFromText response = request.await();
                PlacesSearchResult[] results = response.candidates;

                if (results.length == 0) break;

                //de-duplicate
                int last_round_size = places.size();

                for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                    PlacesSearchResult new_place_result = results[i];
                    Place new_place = getPlaceDetailsFromPlaceDetailSearch(new_place_result.placeId);
                    places.add(new_place);

                    //if have duplicate, the search is done.
                    if (places.size() == last_round_size) {
                        return new ArrayList<Place>(places);
                    }
                    last_round_size = places.size();
                }
            } catch (Exception e) {
                handleGooglePlaceAPIException();
                e.printStackTrace();
            }
        }

        return new ArrayList<Place>(places);
    }

    //2.Nearby Search requests(nearbysearch)
    private List<Place> getPlacesFromNearbySearchrequests(LatLng location, int radius_in_meter, int max_place_number) {
        //prepare result
        HashSet<Place> places = new HashSet<Place>();

        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, location);
        request.location(location);
        //radius larger than 50000 is not support by google API right now.
        request.radius(Math.min(radius_in_meter, 50000));
        request.type(PlaceType.TOURIST_ATTRACTION);

        try {
            PlacesSearchResponse response = request.await();
            PlacesSearchResult[] results = response.results;
            for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                PlacesSearchResult new_place_result = results[i];
                Place new_place = ConvertGooglePlaceResponseToPlaceModel(new_place_result);
                places.add(new_place);
            }
            while (places.size() < max_place_number && response.nextPageToken != null) {
                try {
                    response = PlacesApi.nearbySearchNextPage(context, response.nextPageToken).await();
                } catch (InvalidRequestException e) {
                    Thread.sleep(200); // Wait some time to call the nextPage token
                    continue;
                }
                results = response.results;
                for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                    PlacesSearchResult new_place_result = results[i];
                    Place new_place = ConvertGooglePlaceResponseToPlaceModel(new_place_result);
                    places.add(new_place);
                }
            }

        } catch (Exception e) {
            handleGooglePlaceAPIException();
            e.printStackTrace();
        }

        return new ArrayList<Place>(places);
    }

    private Place ConvertGooglePlaceResponseToPlaceModel(PlacesSearchResult new_place_result) {
        return Place.builder()
                .name(new_place_result.name)
                .rating(new_place_result.rating)
                .id(new_place_result.placeId)
                .photo_refs(getPhotoRefsFromResult(new_place_result))
                .location(new_place_result.geometry.location)
                .build();
    }


    //3. Text Search requests(textsearch)
    private List<Place> getPlacesFromTextSearchQuery(String query_text, int max_place_number) {

        //prepare result
        HashSet<Place> places = new HashSet<Place>();


        TextSearchRequest request = PlacesApi.textSearchQuery(this.context, query_text);

        try {
            PlacesSearchResponse response = request.await();
            PlacesSearchResult[] results = response.results;
            for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                PlacesSearchResult new_place_result = results[i];
                Place new_place = ConvertGooglePlaceResponseToPlaceModel(new_place_result);
                places.add(new_place);
            }
            while (places.size() < max_place_number && response.nextPageToken != null) {
                try {
                    response = PlacesApi.textSearchNextPage(context, response.nextPageToken).await();
                } catch (InvalidRequestException e) {
                    Thread.sleep(200); // Wait some time to call the nextPage token
                    continue;
                }
                results = response.results;
                for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                    PlacesSearchResult new_place_result = results[i];
                    Place new_place = ConvertGooglePlaceResponseToPlaceModel(new_place_result);
                    places.add(new_place);
                }
            }

        } catch (Exception e) {
            handleGooglePlaceAPIException();
            e.printStackTrace();
        }

        return new ArrayList<Place>(places);
    }

    private List<Place> getPlacesFromTextSearchQueryRect(String query_text, int max_place_number, double left_top_lat, double left_top_lng, double right_down_lat, double right_down_lng) {

        //prepare result
        HashSet<Place> places = new HashSet<Place>();

        String nextPageToken = "";

        //call google api to get the list
        while (places.size() < max_place_number && nextPageToken != null) {
            TextSearchRequest request = nextPageToken.length() == 0
                    ? PlacesApi.textSearchQuery(this.context, query_text)
                    : PlacesApi.textSearchNextPage(this.context, nextPageToken);

            try {
                PlacesSearchResponse search_response = request.await();
                PlacesSearchResult[] results = search_response.results;

                nextPageToken = search_response.nextPageToken;

                if (results.length == 0) break;

                for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                    PlacesSearchResult new_place_result = results[i];

                    Place new_place = Place.builder()
                            .name(new_place_result.name)
                            .rating(new_place_result.rating)
                            .id(new_place_result.placeId)
                            .photo_refs(getPhotoRefsFromResult(new_place_result))
                            .location(new_place_result.geometry.location)
                            .build();

                    if (new_place.isInRect(new LatLng(left_top_lat, left_top_lng), new LatLng(right_down_lat, right_down_lng))) {
                        places.add(new_place);
                    }
                }
            } catch (Exception e) {
                handleGooglePlaceAPIException();
                e.printStackTrace();
                break;
            }
        }

        return new ArrayList<Place>(places);
    }

    private List<Place> getPlacesFromTextSearchQueryCircle(String query_text, int max_place_number, double center_lat, double center_lng, int radius_in_meter) {

        //prepare result
        HashSet<Place> places = new HashSet<Place>();

        TextSearchRequest request = PlacesApi.textSearchQuery(this.context, query_text);

        try {
            PlacesSearchResponse response = request.await();
            PlacesSearchResult[] results = response.results;
            for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                PlacesSearchResult new_place_result = results[i];
                Place new_place = ConvertGooglePlaceResponseToPlaceModel(new_place_result);
                if (new_place.isInCircle(new LatLng(center_lat, center_lng), radius_in_meter)) {
                    places.add(new_place);
                }
            }
            while (places.size() < max_place_number && response.nextPageToken != null) {
                try {
                    response = PlacesApi.textSearchNextPage(context, response.nextPageToken).await();
                } catch (InvalidRequestException e) {
                    Thread.sleep(200); // Wait some time to call the nextPage token
                    continue;
                }
                results = response.results;
                for (int i = 0; places.size() < max_place_number && i < results.length; i++) {
                    PlacesSearchResult new_place_result = results[i];
                    Place new_place = ConvertGooglePlaceResponseToPlaceModel(new_place_result);
                    if (new_place.isInCircle(new LatLng(center_lat, center_lng), radius_in_meter)) {
                        places.add(new_place);
                    }
                }
            }

        } catch (Exception e) {
            handleGooglePlaceAPIException();
            e.printStackTrace();
        }

        return new ArrayList<Place>(places);
    }

    //4.Detail Search
    private Place getPlaceDetailsFromPlaceDetailSearch(String place_id) {
        PlaceDetailsRequest request = PlacesApi.placeDetails(context, place_id);
        request.fields(PlaceDetailsRequest.FieldMask.NAME,
                PlaceDetailsRequest.FieldMask.USER_RATINGS_TOTAL,
                PlaceDetailsRequest.FieldMask.RATING,
                PlaceDetailsRequest.FieldMask.GEOMETRY,
                PlaceDetailsRequest.FieldMask.GEOMETRY_LOCATION,
                PlaceDetailsRequest.FieldMask.OPENING_HOURS,
                PlaceDetailsRequest.FieldMask.PHOTOS);

        PlaceDetails details = null;
        try {
            details = request.await();
        } catch (Exception e) {
            handleGooglePlaceAPIException();
            e.printStackTrace();
        }
        Place result_place = Place.builder()
                .name(details.name)
                .id(place_id)
                .location(details.geometry.location)
                .photo_refs(getPhotoRefsFromResult(details))
                .rating(details.rating)
                .build();

        return result_place;
    }

    /*
     * innerclass utils
     * */
    private void handleGooglePlaceAPIException(){
        System.out.println("ERROR: GOOGLE PLACE API ERROR.");
    }

    private List<String> getPhotoRefsFromResult(PlaceDetails details) {
        List<String> place_photo_refs = new ArrayList<>();
        if (details.photos != null && details.photos.length > 0) {
            Arrays.stream(details.photos).forEach(e -> place_photo_refs.add(e.photoReference));
        }
        return place_photo_refs;
    }

    private List<String> getPhotoRefsFromResult(PlacesSearchResult place) {
        List<String> place_photo_refs = new ArrayList<>();
        if (place.photos != null && place.photos.length > 0) {
            Arrays.stream(place.photos).forEach(e -> place_photo_refs.add(e.photoReference));
        }
        return place_photo_refs;
    }

    private LatLng getCityCenterPosition(String city_name) {
        return getPlacesFromTextSearchQuery(city_name, 1).get(0).getLocation();
    }

    /*
     * debugging utils
     *  */
    public static void PrintPlaceDetails(Place place) {
        System.out.println("------------------");
        System.out.println("name: " + place.getName());
        System.out.println("placeid: " + place.getId());
        System.out.println("rating: " + place.getRating());
        System.out.println("location: " + place.getLocation().lat + place.getLocation().lng);
        //System.out.println(place.getPhoto_refs());
        System.out.println("------------------");
    }

    public static void PrintPlacesDetails(List<Place> places) {
        for (Place place : places) {
            PrintPlaceDetails(place);
        }
    }

    /*
     * temp tests
     * */
//    public static void main(String[] args) {
//        System.out.println("start");
//        test1();
//        System.out.println("end1");
//        test2();
//        System.out.println("end2");
//        test3();
//        System.out.println("end3");
//        test4();
//        System.out.println("end4");
//        test5();
//        System.out.println("end5");
//        test6();
//        System.out.println("end6");
//    }
//
//    public static void test1() {
//        GooglePlaceClient client = new GooglePlaceClient();
//        LatLng center = new LatLng(37.771212, -122.432810);
//        FindPlaceFromTextRequest.LocationBiasCircular circle_area = new FindPlaceFromTextRequest.LocationBiasCircular(center, 30000);
//        List<Place> list1 = client.getPlaceFromTextSearch("pier 39", 1, false, circle_area);
//        PrintPlacesDetails(list1);
//    }
//
//    public static void test2() {
//        GooglePlaceClient client = new GooglePlaceClient(2000,2000, 4000);
//        LatLng center = new LatLng(37.771212, -122.432810);
//        List<Place> list1 = client.getPlacesFromNearbySearchrequests(center, 200000, 10);
//        PrintPlacesDetails(list1);
//    }
//
//    public static void test3() {
//        GooglePlaceClient client = new GooglePlaceClient(2000,2000, 4000);
//        String city_name = "San Francisco";
//        List<Place> list1 = client.getPlacesFromTextSearchQuery(city_places_template + city_name, 50);
//        PrintPlacesDetails(list1);
//    }
//
//    public static void test4() {
//        GooglePlaceClient client = new GooglePlaceClient(2000,2000, 4000);
//        String site = "Pier 39";
//        List<Place> list1 = client.getPlacesFromTextSearchQuery(range_places_template + site, 5);
//        PrintPlacesDetails(list1);
//    }
//
//    public static void test5() {
//        GooglePlaceClient client = new GooglePlaceClient(2000,2000, 4000);
//        String city_name = "San Francisco";
//        List<Place> list = client.getCityPlacesInCircle(city_name, 3, 50000);
//        PrintPlacesDetails(list);
//    }
//
//    public static void test6() {
//        GooglePlaceClient client = new GooglePlaceClient(2000,2000, 4000);
//        String city_name = "San Francisco";
//        List<Place> list = client.getCityPlacesInRect(city_name, 4, 37.785063, -122.467674, 37.754999, -122.395695);
//        PrintPlacesDetails(list);
//    }

}
