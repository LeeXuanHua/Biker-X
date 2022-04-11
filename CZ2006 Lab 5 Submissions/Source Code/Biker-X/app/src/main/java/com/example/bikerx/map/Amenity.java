package com.example.bikerx.map;

/**
 * Class to represent an amenity
 */
public class Amenity {
    private String name;
    private String type;
    private double latitude;
    private double longitude;

    /**
     * Amenity constructor which is used to create amenity object upon being called
     * @param name Name of area amenity is in
     * @param type Type of amenity
     * @param latitude Latitude of amenity location
     * @param longitude Longtitude of amenity location
     */
    public Amenity(String name, String type, double latitude, double longitude) {
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns name of amenity area
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of amenity area
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns type of amenity
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Set type of amenity
     * @param type Type of amenity
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns latitude of amenity location
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set latitude of amenity location
     * @param latitude Latitude of amenity location
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Return longtitude of amenity location
     * @return longtitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set longtitude of amenity location
     * @param longitude Longtitude of amenity location
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
