package com.example.bikerx.ui.session;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Class to store data of a cycling session.
 */
public class Session {
    /**
     * Stores the distance travelled during the cycling session. Represented as a string. Unit is nn kilometres.
     */
    private String formattedDistance;
    /**
     * Stores the current location of the user during the cycling session. Represented as a pair of coordinates (latitude, longitude) in a LatLng object.
     */
    private LatLng currentLocation;
    /**
     * Stores the past locations of the user during the cycling session. Represented as a List of LatLng objects.
     */
    private List<LatLng> userPath ;
    public Session(String formattedDistance, LatLng currentLocation, List<LatLng> userPath){
        this.formattedDistance = formattedDistance;
        this.currentLocation = currentLocation;
        this.userPath = userPath;
    }
    public String getFormattedDistance() { return formattedDistance; }
    public LatLng getCurrentLocation() { return currentLocation; }
    public List<LatLng> getUserPath() { return userPath; }
    public void setFormattedDistance(String formattedDistance) { this.formattedDistance = formattedDistance; }
    public void setCurrentLocation(LatLng currentLocation) { this.currentLocation = currentLocation; }
    public void setUserPath(List<LatLng> userPath) { this.userPath = userPath; }
}
