package com.example.bikerx.ui.session;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.example.bikerx.databinding.CyclingSessionFragmentBinding;
import com.example.bikerx.map.RouteMapFragment;
import com.example.bikerx.ui.home.Route;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Displays UI to track current cycling session. Allows user to start, stop, pause, and resume their cycling session.
 */
public class CyclingSessionFragment extends Fragment {
    private CyclingSessionViewModel viewModel;
    private CyclingSessionFragmentBinding mBinding;
    private Chronometer chronometer;
    private long pausedTimeElapsed = 0;
    private SessionState state;
    private String routeId;

    /**Initialises CyclingSessionFragment. The CyclingSessionViewModel and CyclingSessionFragmentBinding is instantiated here.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = CyclingSessionFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new CyclingSessionViewModelFactory(requireContext(), (AppCompatActivity) requireActivity()))
                .get(CyclingSessionViewModel.class);
        return mBinding.getRoot();
    }

    /**Initiates behaviour required of CyclingSessionFragment. This method is called after onCreateView.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        routeId = CyclingSessionFragmentArgs.fromBundle(getArguments()).getRouteId();
        if (routeId != null) {
            drawRecommendedRoute(routeId);
        }
        bindButtons();
        bindData();
    }

    /**Draws route path on map based on routeId as selected by user in HomeFragment or RecommendationsFragment.
     * Data for the route is fetched from viewModel.
     * @param routeId The ID of the desired route.
     */
    private void drawRecommendedRoute(String routeId) {
        viewModel.getRecommendedRoute(routeId).observe(this, new Observer<Route>() {
            @Override
            public void onChanged(Route route) {
                RouteMapFragment routeMapFragment = (RouteMapFragment) getChildFragmentManager().getFragments().get(0);
                routeMapFragment.drawRoute(route.getCoordinates(), "RECOMMENDED");
            }
        });
    }

    /**
     * This method fetches and displays required data onto the UI.
     */
    private void bindData() {
        Date date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy - h:mm a", Locale.getDefault());
        mBinding.dateTextView.setText(dateFormat.format(date));
        viewModel.getSession().observe(this, new Observer<Session>() {
            @Override
            public void onChanged(Session session) {
                float distance = Float.parseFloat(session.getFormattedDistance());
                float timeElapsed = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;
                float speed = calculateSpeed(distance, timeElapsed);
                String formattedSpeed = String.format("%.2f", speed);
                mBinding.distanceDetailsFloat.setText(session.getFormattedDistance());
                if (state != SessionState.PAUSED) mBinding.avgSpeedFloat.setText(formattedSpeed);
            }
        });
    }

    /**
     * This method sets the logic of the buttons in the UI.
     */
    private void bindButtons() {
        chronometer = mBinding.chronometer;
        state = SessionState.PRE_START;
        mBinding.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSession();
            }
        });
        mBinding.pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSession();
            }
        });
        mBinding.resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeSession();
            }
        });
        mBinding.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSession();
            }
        });
    }

    /**
     * Calls the viewModel to start tracking location and distance data of the cycling session.
     * Starts the chronometer to keep track of time elapsed during the cycling session.
     * Sets state of the app to STARTED.
     */
    private void startSession() {
        viewModel.initialiseSession((AppCompatActivity) getActivity());
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        state = SessionState.STARTED;
        mBinding.startButton.setVisibility(View.GONE);
        mBinding.pauseButton.setVisibility(View.VISIBLE);
        mBinding.stopButton.setVisibility(View.VISIBLE);
    }

    /**
     * Calls the viewModel to pause tracking location and distance data of the cycling session.
     * Pauses the chronometer to pause keeping track of time elapsed during the cycling session.
     * Sets state of the app to PAUSED.
     */
    private void pauseSession() {
        viewModel.pauseTracking();
        chronometer.stop();
        state = SessionState.PAUSED;
        pausedTimeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
        mBinding.pauseButton.setVisibility(View.GONE);
        mBinding.resumeButton.setVisibility(View.VISIBLE);
    }

    /**
     * Calls the viewModel to resume tracking location and distance data of the cycling session.
     * Resumes the chronometer to keep track of time elapsed during the cycling session.
     * Sets state of the app to STARTED.
     */
    private void resumeSession() {
        viewModel.resumeTracking();
        chronometer.setBase(SystemClock.elapsedRealtime() - pausedTimeElapsed);
        chronometer.start();
        state = SessionState.STARTED;
        mBinding.pauseButton.setVisibility(View.VISIBLE);
        mBinding.resumeButton.setVisibility(View.GONE);
    }

    /**
     * Calls the viewModel to stp[ tracking location and distance data of the cycling session.
     * Gathers data for the cycling session, and passes it to viewModel to be stored in Firebase database.
     * App will automatically navigate to SessionSummaryFragment.
     */
    private void stopSession() {
        if (state == SessionState.PAUSED) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pausedTimeElapsed);
        }
        String formattedDistance = viewModel.getSession().getValue().getFormattedDistance();
        long timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
        viewModel.stopTracking(timeElapsed);
        NavDirections action = CyclingSessionFragmentDirections
                .actionStartCyclingFragmentToSessionSummaryFragment(formattedDistance, timeElapsed, routeId);
        NavHostFragment.findNavController(this).navigate(action);
    }

    /**Helper method to calculate speed based on distance and time.
     * @param distance The distance travelled by the user during the cycling session. Stored in kilometres.
     * @param timeElapsed The duration of the cycling session. Stored in seconds.
     * @return Average speed the user is travelling at, in km/h units.
     */
    private float calculateSpeed(float distance, float timeElapsed) {
        if (timeElapsed == 0) {
            return 0F;
        } else {
            return 60 * 60 * distance/timeElapsed;
        }
    }
}