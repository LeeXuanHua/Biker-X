package com.example.bikerx.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.firestore.DBManager;
import com.example.bikerx.control.firestore.RouteManager;

import java.util.ArrayList;

/**
 * Class to hold and manage data required for RecommendationsFragment
 */
public class RecommendationsViewModel extends ViewModel {
    private RouteManager routeManager = new RouteManager();
    private MutableLiveData<ArrayList<Route>> routes;

    /**
     * Calls dbManager to fetch routes and stores result in routes
     */
    public void fetchRoutes() {
        routes = routeManager.getHomeRoutes("recommendationsFragment");
    }

    /**
     * Function to return arraylist of route from viewModel
     * @return MutableLiveData arraylist of route
     */
    public MutableLiveData<ArrayList<Route>> getRoutes() {
        return routes;
    }
}