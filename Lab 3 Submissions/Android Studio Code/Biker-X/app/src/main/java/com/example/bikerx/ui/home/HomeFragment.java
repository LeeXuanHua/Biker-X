package com.example.bikerx.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikerx.R;
import com.example.bikerx.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays UI for the app landing page.
 * Contains weather data, recommended routes, and navigation buttons to other app features.
 */
public class HomeFragment extends Fragment implements HomeRecommendationsAdapter.MyViewHolder.HomeRouteListener{
    private String TAG = "HOME_FRAGMENT";
    private FragmentHomeBinding mBinding;
    private List<Route> routeList;
    private HomeRecommendationsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private HomeViewModel viewModel;

    /**Initialises HomeFragment. The HomeViewModel and FragmentHomeBinding is instantiated here.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    /**Initiates behaviour required of HomeFragment. This method is called after onCreateView.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindButtons();
        displayHomeRoutes();
        displayWeather();
    }

    /**
     * Displays weather data using data fetched from viewModel.
     */
    private void displayWeather() {
        viewModel.getWeatherData(getContext()).observe(this, new Observer<Drawable>() {
            @Override
            public void onChanged(Drawable drawable) {
                mBinding.imageWeather.setImageDrawable(drawable);
            }
        });

    }

    /**
     * This method sets the logic of the buttons in the UI.
     */
    private void bindButtons() {
        mBinding.recommendedRouteSeeAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavDirections action = HomeFragmentDirections.actionNavigationHomeToRecommendationsFragment();
                Navigation.findNavController(v).navigate(action);
            }
        });
        mBinding.viewMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.navigation_map);
            }
        });
        mBinding.viewGoalsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavDirections action = HomeFragmentDirections.actionNavigationHomeToGoalsFragment();
                Navigation.findNavController(v).navigate(action);
            }
        });
        mBinding.ownRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = HomeFragmentDirections.actionNavigationHomeToStartCyclingFragment(null);
                Navigation.findNavController(v).navigate(action);
            }
        });
    }

    /**
     * Navigate to start cycling fragment and pass route ID to fragment after click
     * @param position Value to indicate position of click
     */
    @Override
    public void homeRouteClick(int position) {
        NavDirections action = HomeFragmentDirections.actionNavigationHomeToStartCyclingFragment(routeList.get(position).getRouteId());
        Navigation.findNavController(this.getView()).navigate(action);
    }

    /**
     * Fetch routes and display routes in recycler view
     */
    private void displayHomeRoutes() {
        viewModel.fetchHomeRoutes();
        viewModel.getHomeRoutes().observe(this, new Observer<ArrayList<Route>>() {
            @Override
            public void onChanged(ArrayList<Route> homeRoutes) {
                if (homeRoutes != null) {
                    routeList = homeRoutes;
                    adapter = new HomeRecommendationsAdapter(homeRoutes, HomeFragment.this);
                    layoutManager = new LinearLayoutManager(getActivity());
                    mBinding.HomeRecyclerView.setLayoutManager(layoutManager);
                    mBinding.HomeRecyclerView.setAdapter(adapter);
                }
            }
        });
    }
}