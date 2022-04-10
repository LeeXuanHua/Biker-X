package com.example.bikerx.ui.fullmap;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.ApiManager;
import com.example.bikerx.map.AmenitiesMapFragment;
import com.example.bikerx.map.Amenity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * FullMapViewModel handles the backend and data fetching for the FullMapFragment.
 */
public class FullMapViewModel extends ViewModel {

    protected ArrayList<Amenity> FullAmenitiesList = null;

    protected ArrayList<Marker> AccessPointMarkerList = null;
    protected ArrayList<Marker> BicycleRacksMarkerList = null;
    protected ArrayList<Marker> BicycleRentalShopMarkerList = null;
    protected ArrayList<Marker> FitnessAreaMarkerList = null;
    protected ArrayList<Marker> FNBEateryMarkerList = null;
    protected ArrayList<Marker> PlaygroundMarkerList = null;
    protected ArrayList<Marker> ShelterMarkerList = null;
    protected ArrayList<Marker> ToiletMarkerList = null;
    protected ArrayList<Marker> WaterCoolerMarkerList = null;

    private GoogleMap map;
    private ApiManager apiManager = new ApiManager();

    /**
     * This method sets the visibility of markers to visible when checked=true, and invisible when checked=false
     * @param markerList The ArrayList of markers for a specific amenity
     * @param checked The state of the checkbox (whether it is checked/unchecked)
     */
    public void setMarkerVisibility(ArrayList<Marker> markerList, boolean checked){
        for (Marker m : markerList){
            m.setVisible(checked); }
    }

    /**
     * This method moves map camera to re-focus on searched location
     * @param latlng Latitude & longtitude of searched location
     */
    public Marker moveCamera(LatLng latlng, String location){
        Marker marker;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
        marker = map.addMarker(new MarkerOptions().position(latlng).title(location).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        return marker;
    }

    /**
     *This method initialises map markers by fetching data of the respective amenities from firebase and storing them in ArrayLists of Markers for the respective amenities
     * @param fragment This is the FullMapFragment.
     */
    public void initialiseMarkers(FullMapFragment fragment){

        if (PlaygroundMarkerList == null){
            AmenitiesMapFragment amenitiesMapFragment = (AmenitiesMapFragment) fragment.getChildFragmentManager().getFragments().get(0);
            map = amenitiesMapFragment.getMap();
            map.clear();

            apiManager.getAmenitiesData(map,"ACCESS POINT").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    AccessPointMarkerList = markers;
                }
            });

            apiManager.getAmenitiesData(map,"BICYCLE RACK").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    BicycleRacksMarkerList = markers;
                }
            });

            apiManager.getAmenitiesData(map,"BICYCLE RENTAL SHOP").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    BicycleRentalShopMarkerList = markers;
                }
            });

            apiManager.getAmenitiesData(map,"FITNESS AREA").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    FitnessAreaMarkerList = markers;
                }
            });

            apiManager.getAmenitiesData(map,"F&B").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    FNBEateryMarkerList = markers;
                }
            });

            apiManager.getAmenitiesData(map,"PLAYGROUND").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    PlaygroundMarkerList = markers;
                }
            });

            apiManager.getAmenitiesData(map,"SHELTER").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    ShelterMarkerList = markers;
                }
            });

            apiManager.getAmenitiesData(map,"TOILET").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    ToiletMarkerList = markers;

                }
            });

            apiManager.getAmenitiesData(map,"WATER POINT").observe(fragment.getViewLifecycleOwner(), new Observer<ArrayList<Marker>>() {
                @Override
                public void onChanged(ArrayList<Marker> markers) {
                    WaterCoolerMarkerList = markers;

                }
            });

        }
    }

}