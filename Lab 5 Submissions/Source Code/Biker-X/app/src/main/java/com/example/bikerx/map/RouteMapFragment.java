package com.example.bikerx.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.bikerx.R;
import com.example.bikerx.control.ApiManager;
import com.example.bikerx.ui.session.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.kml.KmlLayer;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** The subclass of MapFragment which is used to track the route for CyclingSessionFragment.
 * The users path during a cycling session is drawn using the drawRoute method, which is called whenever there is a change in location.
 */
public class RouteMapFragment extends MapFragment{

    /**Initialises RouteMapFragment. It makes use of the implementation in the superclass MapFragment.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**Initiates behaviour required of RouteMapFragment. It makes use of the implementation in the superclass MapFragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**Initiates required behaviour when the map param has finished rendering.
     * The method creates an observer to observe changes in the user's location.
     * @param map The GoogleMap object which is displayed on the UI.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        super.onMapReady(map);
        super.getViewModel().getSession().observe(this, new Observer<Session>() {
            @Override
            public void onChanged(Session session) {
                drawRoute(session.getUserPath(), "SESSION");
            }
        });
    }


    /**This method draws a route on the map.
     * @param path Keeps track of the locations along the route.
     * @param type Indicates whether the route drawn is from recommended route or from cycling session. Affects the colour of the path drawn.
     */
    public void drawRoute(List<LatLng> path, String type) {
        PolylineOptions polylineOptions;
        if (type == "SESSION") {
            polylineOptions = new PolylineOptions().color(getResources().getColor(R.color.teal_700));
        }
        else {
            polylineOptions = new PolylineOptions().color(getResources().getColor(R.color.purple_700));
        }
        List<LatLng> points = polylineOptions.getPoints();
        points.addAll(path);
        super.getMap().addPolyline(polylineOptions);
    }

}
