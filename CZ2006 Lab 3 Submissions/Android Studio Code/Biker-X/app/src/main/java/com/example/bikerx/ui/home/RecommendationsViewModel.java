package com.example.bikerx.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.DBManager;

import java.util.ArrayList;

/**
 * Class to hold and manage data required for RecommendationsFragment
 */
public class RecommendationsViewModel extends ViewModel {
    private DBManager dbManager = new DBManager();
    private MutableLiveData<ArrayList<Route>> routes;

    /**
     * Calls dbManager to fetch routes and stores result in routes
     */
    public void fetchRoutes() {
        routes = dbManager.getHomeRoutes();
    }

    /**
     * Function to return arraylist of route from viewModel
     * @return MutableLiveData arraylist of route
     */
    public MutableLiveData<ArrayList<Route>> getRoutes() {
        return routes;
    }
}