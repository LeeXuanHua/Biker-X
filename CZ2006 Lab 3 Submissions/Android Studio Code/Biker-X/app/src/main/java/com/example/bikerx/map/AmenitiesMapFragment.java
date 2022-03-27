package com.example.bikerx.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.bikerx.control.ApiManager;
import com.example.bikerx.ui.fullmap.FullMapFragment;
import com.example.bikerx.ui.session.SessionSummaryFragment;
import com.google.android.gms.maps.GoogleMap;

/** The subclass of MapFragment which is used to display Amenities on FullMapFragment and SessionSummaryFragment.
 */
public class AmenitiesMapFragment extends MapFragment {
    private ApiManager apiManager;
    private MutableLiveData<Boolean> mapReady = new MutableLiveData<>(false);

    /**Initialises AmenitiesMapFragment. It makes use of the implementation in the superclass MapFragment.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**Initiates behaviour required of AmenitiesMapFragment. It makes use of the implementation in the superclass MapFragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**Initiates required behaviour when the map param has finished rendering.
     * The method instantiates the ApiManager, which is used to fetch data from Data.gov API.
     * @param map The GoogleMap object which is displayed on the UI.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        super.onMapReady(map);
        this.apiManager = new ApiManager();
        this.mapReady.setValue(true);
    }

    /**This method calls the ApiManager to fetch and display locations of Bicycle Racks on the map as markers.
     *
     */
    public void displayBicycleRacks() {
        apiManager.getBicycleRacks(super.getMap());
    }

    public MutableLiveData<Boolean> getMapReady() {
        return this.mapReady;
    }

}
