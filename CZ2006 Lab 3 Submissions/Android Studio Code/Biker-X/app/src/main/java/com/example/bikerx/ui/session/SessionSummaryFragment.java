package com.example.bikerx.ui.session;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.example.bikerx.MainActivity;
import com.example.bikerx.databinding.SessionSummaryFragmentBinding;
import com.example.bikerx.map.AmenitiesMapFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Displays summary of cycling session. Only appears after user clicks on "Stop" button in CyclingSessionFragment.
 *  Also displays map with markers of bicycle rack locations.
 */
public class SessionSummaryFragment extends Fragment {
    private long totalTime;
    private String totalDistanceFormatted;
    private String routeId;
    private SessionSummaryViewModel mViewModel;
    private SessionSummaryFragmentBinding mBinding;


    /**Initialises SessionSummaryFragment. The SessionSummaryViewModel and SessionSummaryBinding is instantiated here.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SessionSummaryViewModel.class);
        mBinding = SessionSummaryFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    /**Initiates behaviour required of SessionSummaryFragment. This method is called after onCreateView.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displaySummary();
        displayBicycleRacks();
        if (routeId != null) bindRatingBar();
    }

    /**This method checks the AmenitiesMapFragment to see if the GoogleMap object has finished rendering and is ready.
     * Once ready, it will call the displayBicycleRacks method from AmenitiesMapFragment.
     */
    private void displayBicycleRacks() {
        AmenitiesMapFragment amenitiesMapFragment = (AmenitiesMapFragment) getChildFragmentManager().getFragments().get(0);
        amenitiesMapFragment.getMapReady().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean mapReady) {
                if (mapReady == true) amenitiesMapFragment.displayBicycleRacks();
            }
        });
    }


    /**This method populates the UI with data about the cycling session. Data is passed to this fragment when navigating from CyclingSessionFragment.
     */
    private void displaySummary() {
        routeId = SessionSummaryFragmentArgs.fromBundle(getArguments()).getRouteId();
        totalDistanceFormatted = SessionSummaryFragmentArgs.fromBundle(getArguments()).getDistanceTravelled();
        totalTime = SessionSummaryFragmentArgs.fromBundle(getArguments()).getTimeElapsed();
        mBinding.chronometer.setBase(SystemClock.elapsedRealtime() - totalTime);
        mBinding.distanceDetailsFloat.setText(totalDistanceFormatted);
        mBinding.avgSpeedFloat.setText(String.format("%.2f", 60000*Float.parseFloat(totalDistanceFormatted)/totalTime));
        Date date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy - h:mm a", Locale.getDefault());
        mBinding.dateTextView.setText(dateFormat.format(date));
    }


    /**This method will only run when the user has selected a route for the cycling session (routeId != null).
     * The method displays the rating bar, and listens for changes in the rating bar. Changes in rating will be updated using rateRoute method to the Firebase database.
     *
     */
    private void bindRatingBar() {
        mBinding.rateRouteRatingBar.setVisibility(View.VISIBLE);
        mBinding.rateRouteTextView.setVisibility(View.VISIBLE);
        mBinding.rateRouteRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) rateRoute(rating);
            }
        });
    }


    /**This is a helper method which calls the rateRoute method in the viewModel. The viewModel will update the route rating for the user in the Firebase database.
     * @param rating This is the rating which the user gives to the route.
     */
    private void rateRoute(float rating){
        String userId = ((MainActivity) getActivity()).getUserId();
        mViewModel.rateRoute(routeId, userId ,rating);
    }

}