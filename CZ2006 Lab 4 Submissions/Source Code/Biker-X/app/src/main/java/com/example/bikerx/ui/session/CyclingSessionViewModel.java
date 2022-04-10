package com.example.bikerx.ui.session;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.MainActivity;
import com.example.bikerx.R;
import com.example.bikerx.control.firestore.DBManager;
import com.example.bikerx.control.LocationManager;
import com.example.bikerx.control.firestore.RouteManager;
import com.example.bikerx.control.firestore.SessionManager;
import com.example.bikerx.ui.home.Route;
import com.google.android.gms.maps.model.LatLng;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**This ViewModel handles the backend and data for the CyclingSessionFragment.
 */
public class CyclingSessionViewModel extends ViewModel {

    private LocationManager locationManager;
    private boolean locationPermissionGranted;
    private final Session EMPTY = new Session("0.00", null, new ArrayList<LatLng>());
    private MutableLiveData<Session> session = new MutableLiveData<Session>(EMPTY);
    private SessionManager sessionManager;
    private RouteManager routeManager;
    private AppCompatActivity activity;

    public CyclingSessionViewModel(Context context, AppCompatActivity activity) {
        this.activity = activity;
        this.locationManager = new LocationManager(context);
        this.locationPermissionGranted = locationManager.checkLocationPermission();
        this.sessionManager = new SessionManager();
        this.routeManager = new RouteManager();
    }

    /**Initialises a cycling session. This method is only called when the user clicks on the start button in CyclingSessionFragment.
     * LocationManager is prompted to start tracking through the startTracking method.
     * Observers are set on data stored by LocationManager, to get live location updates.
     * @param activity Activity acts as the frame of reference for when to stop observing data from LocationManager (data is no longer observed when lifecycle of activity ends).
     */
    public void initialiseSession(AppCompatActivity activity) {
        startTracking();
        if (locationPermissionGranted){
            locationManager.getLiveLocations().observe(activity, new Observer<ArrayList<LatLng>>() {
                @Override
                public void onChanged(ArrayList<LatLng> locations) {
                    Session current = session.getValue();
                    if (current != null) {
                        current.setUserPath(locations);
                        session.setValue(current);
                    }
                }
            });
            locationManager.getLiveLocation().observe(activity, new Observer<LatLng>() {
                @Override
                public void onChanged(LatLng currentLocation) {
                    Session current = session.getValue();
                    if (current != null) {
                        current.setCurrentLocation(currentLocation);
                        session.setValue(current);
                    }
                }
            });
            locationManager.getLiveDistance().observe(activity, new Observer<Double>() {
                @Override
                public void onChanged(Double distance) {
                    Session current = session.getValue();
                    String formattedDistance = activity.getString(R.string.distance_value, distance);
                    if (current != null) {
                        current.setFormattedDistance(formattedDistance);
                        session.setValue(current);
                    }
                }
            });
        }
    }

    /**
     * LocationManager is prompted to start tracking the distance travelled, and the current and past locations of the user.
     */
    public void startTracking() { locationManager.trackUser(); }

    /**
     * LocationManager is prompted to pause tracking the distance travelled, and the current and past locations of the user.
     */
    public void pauseTracking() { locationManager.pauseTracking(); }

    /**
     * LocationManager is prompted to resume tracking the distance travelled, and the current and past locations of the user.
     */
    public void resumeTracking() { locationManager.resumeTracking(); }

    /**
     * Data about the current cycling session is retrieved and sent to Firebase through DBManager.
     * Cycling Session data is reset.
     * LocationManager is prompted to stop tracking the distance travelled, and the current and past locations of the user.
     * @param duration Time elapsed during the cycling session. It is tracked using UI element "Chronometer" in CyclingSessionFragment, and passed here to be stored in Firebase.
     */
    public void stopTracking(long duration) {
        Session session = this.session.getValue();
        Date date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy - h:mm a", Locale.getDefault());
        sessionManager.addCyclingSession(((MainActivity)activity).getUserId(), dateFormat.format(date), session.getFormattedDistance(), duration);

        this.session.setValue(new Session("0.00", null, new ArrayList<LatLng>()));
        locationManager.stopTracking();
    }

    /**Prompts DBManager to retrieve route data from Firebase, based on routeId.
     * @param routeId The route ID of the desired route.
     * @return MutableLiveData of Route object.
     */
    public MutableLiveData<Route> getRecommendedRoute(String routeId) {
        return routeManager.getRecommendedRoute(routeId);
    }

    public MutableLiveData<Session> getSession() {
        return session;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }

    public boolean getLocationPermissionGranted() {
        return locationPermissionGranted;
    }

}