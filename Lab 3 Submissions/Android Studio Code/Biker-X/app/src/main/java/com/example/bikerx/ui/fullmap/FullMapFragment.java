package com.example.bikerx.ui.fullmap;


import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikerx.R;
import com.example.bikerx.control.ApiManager;
import com.example.bikerx.databinding.FragmentFullMapBinding;
import com.example.bikerx.map.AmenitiesMapFragment;
import com.example.bikerx.map.Amenity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;

/**
 * Displays full map. Users can view, filter and search for amenities
 */
public class FullMapFragment extends Fragment {

    private FullMapViewModel fullMapViewModel;
    private FragmentFullMapBinding binding;

    private AppCompatActivity activity;
    private ApiManager apiManager;

    /**
     * Initialises FullMapFragment. The FullMapViewModel is instantiated here.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fullMapViewModel =
                new ViewModelProvider(this).get(FullMapViewModel.class);

        binding = FragmentFullMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    /**
     * Initialises behaviour required of FullMapFragment. This method is called after onCreateView.
     * Initialises markers upon map ready.
     */
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        getMapReady().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean mapReady) {
                if (mapReady) initialiseMarkers();
            }
        });

        filterAmenities(view);
        searchAmenities(view);
    }

    /**
     * Initialises map markers.
     */
    public void initialiseMarkers(){
        fullMapViewModel.initialiseMarkers(this);
    }


    /**
     * Filters amenities map markers
     * Clicking the toggle icon would display/hide the filter list.
     * Checking the checkboxes of the respective amenities filters would cause the corresponding map markers to appear on map & change colour of checkbox text
     * @param view View
     */
    private void filterAmenities(View view){

        ImageView mToggleButton = (ImageView) view.findViewById(R.id.toggleButton);
        LinearLayout mVisibleCheckBoxes = view.findViewById(R.id.checkBoxLayout);

        CheckBox mCheckAccessPoint = view.findViewById(R.id.checkBox_accesspoint);
        CheckBox mCheckBicycleRacks = view.findViewById(R.id.checkBox_bicyclerack);
        CheckBox mCheckBicycleRentalShop = view.findViewById(R.id.checkBox_bicyclerentalshop);
        CheckBox mCheckFitnessArea = view.findViewById(R.id.checkBox_fitnessarea);
        CheckBox mCheckFNBEatery = view.findViewById(R.id.checkBox_fnbeatery);
        CheckBox mCheckHawkerCentre = view.findViewById(R.id.checkBox_hawkercentre);
        CheckBox mCheckPlayground = view.findViewById(R.id.checkBox_playground);
        CheckBox mCheckShelter = view.findViewById(R.id.checkBox_shelter);
        CheckBox mCheckToilet = view.findViewById(R.id.checkBox_toilet);
        CheckBox mCheckWaterCooler = view.findViewById(R.id.checkBox_watercooler);

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            boolean visible;

            @Override
            public void onClick(View view) {
                visible = !visible;
                mVisibleCheckBoxes.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        mCheckAccessPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.AccessPointMarkerList, isChecked);
                changeCheckTextColour(mCheckAccessPoint, isChecked);
            }
        });

        mCheckBicycleRacks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.BicycleRacksMarkerList, isChecked);
                changeCheckTextColour(mCheckBicycleRacks, isChecked);
            }
        });

        mCheckBicycleRentalShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.BicycleRentalShopMarkerList, isChecked);
                changeCheckTextColour(mCheckBicycleRentalShop, isChecked);
            }
        });

        mCheckFitnessArea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.FitnessAreaMarkerList, isChecked);
                changeCheckTextColour(mCheckFitnessArea, isChecked);
            }
        });


        mCheckFNBEatery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.FNBEateryMarkerList, isChecked);
                changeCheckTextColour(mCheckFNBEatery, isChecked);
            }
        });

        mCheckHawkerCentre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.HawkerCentreMarkerList, isChecked);
                changeCheckTextColour(mCheckHawkerCentre, isChecked);
            }
        });

        mCheckPlayground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.PlaygroundMarkerList, isChecked);
                changeCheckTextColour(mCheckPlayground, isChecked);
            }
        });

        mCheckShelter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.ShelterMarkerList, isChecked);
                changeCheckTextColour(mCheckShelter, isChecked);
            }
        });

        mCheckToilet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.ToiletMarkerList, isChecked);
                changeCheckTextColour(mCheckToilet, isChecked);
            }
        });

        mCheckWaterCooler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                fullMapViewModel.setMarkerVisibility(fullMapViewModel.WaterCoolerMarkerList, isChecked);
                changeCheckTextColour(mCheckWaterCooler, isChecked);
            }
        });
    }

    /**
     * Search map function. User can search a location, and map camera will move to re-focus to that location.
     */
    private void searchAmenities(View view){
        SearchView searchView = view.findViewById(R.id.searchViewAmenities);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            Marker marker;
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                List<Address> addressList = null;
                if (marker != null){
                    marker.remove();
                }
                else{
                    marker = null;
                }

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(getActivity());
                    try{
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                    marker = fullMapViewModel.moveCamera(latlng, location);
                } else {
                    Toast.makeText(getContext(), "Location does not exist.", Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    /**
     * Change colour of checkbox text upon check/uncheck
     * @param mCheck Checkbox of amenity filter
     * @param isChecked state of checkbox (checked/unchecked)
     */
    private void changeCheckTextColour(CheckBox mCheck, boolean isChecked){
        if (isChecked) {
            mCheck.setTextColor(Color.parseColor("#3361A6"));
        } else {
            mCheck.setTextColor(Color.parseColor("#6C757D"));
        }
    }

    /**
     * Checks if map is loaded
     * @return amenitiesMapFragment.getMapReady() - returns true if map is ready
     */
    public MutableLiveData<Boolean> getMapReady() {
        AmenitiesMapFragment amenitiesMapFragment = (AmenitiesMapFragment) getChildFragmentManager().getFragments().get(0);
        return amenitiesMapFragment.getMapReady();
    }

}