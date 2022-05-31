package com.example.bikerx.ui.home;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Entity class to store relevant information of a cycling route
 */
public class Route {
    /**
     * Stores image ID of route overview
     */
    private Integer imageId;
    /**
     * Stores route ID of route
     */
    private String routeId;
    /**
     * Store route name of route
     */
    private String routeName;
    /**
     * ArrayList of HashMap to store previous ratings given for this route
     */
    private ArrayList<HashMap<String, Object>> ratings;
    /**
     * ArrayList of LatLng objects for this route
     */
    private ArrayList<LatLng> coordinates;


    public Route(Integer imageId, String routeId, String routeName, ArrayList<HashMap<String, Object>> ratings, ArrayList<LatLng> coordinates){
        this.imageId = imageId;
        this.routeId = routeId;
        this.routeName = routeName;
        this.ratings = ratings;
        this.coordinates = coordinates;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<HashMap<String, Object>> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<HashMap<String, Object>> ratings) {
        this.ratings = ratings;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
