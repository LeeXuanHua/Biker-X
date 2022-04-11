package com.example.bikerx.control;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.bikerx.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Control class used to track live location of the user.
 */
public class LocationManager {
    private AppCompatActivity activity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;
    private MutableLiveData<LatLng> liveLocation = new MutableLiveData<LatLng>();
    private MutableLiveData<ArrayList<LatLng>> liveLocations = new MutableLiveData<ArrayList<LatLng>>();
    private MutableLiveData<Double> liveDistance = new MutableLiveData<Double>();
    private ArrayList<LatLng> locations = new ArrayList<LatLng>();
    private Double distance = 0.0;
    private locationCallback callback = new locationCallback();
    private LocationRequest locationRequest;

    public LocationManager(AppCompatActivity activity) {
        this.activity = activity;
    }

    public LocationManager(Context context) {
        this.context = context;
    }

    /**
     * Callback used to update location data whenever location of the user changes.
     */
    private class locationCallback extends LocationCallback {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location currentLocation = locationResult.getLastLocation();
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            LatLng lastLocation = locations.size() > 0 ? locations.get(locations.size() - 1) : null;

            if (lastLocation != null) {
                distance += SphericalUtil.computeDistanceBetween(lastLocation, latLng)/1000;
                liveDistance.setValue(distance);
            }
            locations.add(latLng);
            liveLocation.setValue(latLng);
            liveLocations.setValue(locations);
        }
    }

    /**
     * Method to initiate tracking of the user.
     */
    public void trackUser() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);

        resetData();
        getDeviceLocation(checkLocationPermission());

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    /**
     * Method to pause tracking of the user.
     */
    public void pauseTracking() {
        fusedLocationProviderClient.removeLocationUpdates(callback);
    }

    /**
     * Method to resume tracking of the user.
     */
    @SuppressLint("MissingPermission")
    public void resumeTracking() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    /**
     * Method to stop tracking of the user.
     */
    public void stopTracking() {
        fusedLocationProviderClient.removeLocationUpdates(callback);
        resetData();
    }

    /**
     * Clears all past location data of the user.
     * Called before locationManager starts tracking the user's location, and after locationManager stops tracking the user's location.
     */
    private void resetData() {
        liveLocations = new MutableLiveData<ArrayList<LatLng>>();
        liveDistance = new MutableLiveData<Double>();
        locations.clear();
        distance = 0.0;
    }

    /**
     * Method to request permission from the user for location tracking.
     */
    public void getLocationPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                activity.registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = getOrDefault(result,
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = getOrDefault(result,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {

                            } else if (coarseLocationGranted != null && coarseLocationGranted) {

                            } else {

                            }
                        }
                );

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * Retrieves last location of the user.
     */
    public void getLastLocation() {
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (checkLocationPermission()) {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng latLng;
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    } else {
                        latLng = new LatLng(0, 0);
                    }
                    locations.add(latLng);
                    liveLocation.setValue(latLng);
                }
            });
        }
    }

    /**
     * Method to check if user has given the app permission to track his/her location.
     */
    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**Checks if user has granted permission for the app to track his/her location.
     * If true, the method will call getLastLocation() to get user's last location.
     * @param locationPermissionGranted Boolean to check if user has granted permission for the app to track his/her location.
     */
    public void getDeviceLocation(boolean locationPermissionGranted) {
        if (locationPermissionGranted) {
            getLastLocation();
        }
    }

    public MutableLiveData<LatLng> getLiveLocation() {
        return liveLocation;
    }

    public MutableLiveData<ArrayList<LatLng>> getLiveLocations() {
        return liveLocations;
    }

    public MutableLiveData<Double> getLiveDistance() {
        return liveDistance;
    }

    /**
     * Helper method to get retrieve data or return default value if data is not found.
     */
    private <K, V> V getOrDefault(@NonNull Map<K, V> map, K key, V defaultValue) {
        V v;
        return (((v = map.get(key)) != null) || map.containsKey(key))
                ? v
                : defaultValue;
    }
}
