package com.example.bikerx.control.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.bikerx.ui.home.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Control class to retrieve route data through Firebase Firestore.
 */
public class RouteManager extends DBManager {
    /**
     * Get the routes stored in database
     * @return ArrayList of Route to be displayed in HomeFragment and RecommendationsFragment
     */
    public MutableLiveData<ArrayList<Route>> getHomeRoutes(String caller) {
        MutableLiveData<ArrayList<Route>> routeList = new MutableLiveData<ArrayList<Route>>();
        routeList.setValue(new ArrayList<Route>());
        OnCompleteListener<QuerySnapshot> listener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Route route = parseRouteData(document);
                        ArrayList<Route> currentRouteArray = routeList.getValue();
                        currentRouteArray.add(route);
                        routeList.setValue(currentRouteArray);
                    }
                }
            }
        };
        if(caller.equals("homeFragment")) {
            queryOrderedCollection("PCN", listener, "ratings", true, 5);
        }
        else {
            queryOrderedCollection("PCN", listener, "name", false, 0);
        }
        return routeList;
    }

    /**
     * Retrieves the route chosen by user. This route is used to draw path of chosen route
     * @param routeId id of the route selected
     * @return Route selected by user
     */
    public MutableLiveData<Route> getRecommendedRoute(String routeId) {
        MutableLiveData<Route> route = new MutableLiveData<Route>();
        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Route newRoute = parseRouteData(document);

                    route.setValue(newRoute);
                }
            }
        };
        queryDocument("PCN", routeId, listener);
        return route;
    }

    /**
     * Function to parse document that contains route data into route
     * @param document that contains data of route
     * @return
     */
    protected Route parseRouteData(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        Route route = new Route(null, document.getId(),null, null, null);
        Object imageIdObj = data.get("imageId");

        if (imageIdObj != null) {
            long imageIdLong = (Long) imageIdObj;
            route.setImageId( (int) imageIdLong);
        }

        String name = data.get("name").toString();
        route.setRouteName(name);

        ArrayList<Double> coordinates = (ArrayList<Double>) data.get("coordinates");
        ArrayList<LatLng> latLngs = new ArrayList<>();
        int i = 0;
        while (i < coordinates.size()) {
            LatLng latLng = new LatLng(coordinates.get(i+1), coordinates.get(i));
            latLngs.add(latLng);
            i += 2;
        }
        route.setCoordinates(latLngs);

        Object ratingsObj = data.get("ratings");
        if (ratingsObj != null ) {
            route.setRatings((ArrayList<HashMap<String, Object>>) ratingsObj);
        }
        return route;
    }
}
