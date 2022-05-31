package com.example.bikerx.control;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;


import com.example.bikerx.map.Amenity;
import com.example.bikerx.ui.history.CyclingHistory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * A control class to retrieve and store API-related data through Firebase.
 */
public class ApiManager {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**Retrieve amenities from Firebase based on their type (e.g. Toilet, Shelter, Park Connector), and display them as markers on the map.
     * @param map The map used to display the markers on.
     * @param type The type of the amenities required.
     * @return Returns a MutableLiveData object, containing an ArrayList of Markers that are displayed on the map.
     */
    public MutableLiveData<ArrayList<Marker>> getAmenitiesData(GoogleMap map, String type) {
        MutableLiveData<ArrayList<Marker>> amenityMarkerList = new MutableLiveData<>(null);
        db.collection("amenities")
                .whereEqualTo("type", type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Marker> amenityArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> data = document.getData();
                                MarkerOptions marker = new MarkerOptions();
                                LatLng latLng = new LatLng((Double) data.get("latitude"), (Double) data.get("longitude"));

                                amenityArrayList.add(map.addMarker(marker.position(latLng).title((String)data.get("type")).snippet((String)data.get("name")).icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));

                            }
                            amenityMarkerList.setValue(amenityArrayList);
                        }
                    }
                });
        return amenityMarkerList;
    }

    /**Retrieve bicycle rack locations from Firebase , and display them as markers on the map.
     * @param map The map used to display the markers on.
     */
    public void getBicycleRacks(GoogleMap map) {
        db.collection("bicycle-racks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                MarkerOptions marker = new MarkerOptions();
                                LatLng latLng = new LatLng((Double) data.get("latitude"), (Double) data.get("longitude"));
                                map.addMarker(marker.position(latLng).title("BICYCLE RACK"));
                            }
                        }
                    }
                });
    }

    /**Helper method to load API data onto Firebase.
     */
    public void loadBicycleRacksIntoAmenities(AppCompatActivity activity) {
        String json = null;
        try {
            Log.d("Begin","");
            InputStream is = activity.getAssets().open("bicycle-rack.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            JSONArray jsonArray = obj.getJSONArray("features");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONArray coords = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                db.collection("amenities").add(new Amenity("", "BICYCLE RACK", coords.getDouble(0), coords.getDouble(1)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**Helper method to load API data onto Firebase.
     */
    public void loadBicycleRacks(AppCompatActivity activity) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("bicycle-rack.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            class BicycleRack {
                Double latitude;
                Double longitude;
                public BicycleRack(Double lat, Double lng) { this.latitude = lat; this.longitude = lng; }

                public Double getLatitude() {
                    return latitude;
                }

                public Double getLongitude() {
                    return longitude;
                }

                public void setLatitude(Double latitude) {
                    this.latitude = latitude;
                }

                public void setLongitude(Double longitude) {
                    this.longitude = longitude;
                }

            }
            JSONObject obj = new JSONObject(json);
            JSONArray jsonArray = obj.getJSONArray("features");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONArray coords = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                db.collection("bicycle-racks").add(new BicycleRack(coords.getDouble(0), coords.getDouble(1)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**Helper method to load API data onto Firebase.
     */
    public void loadRoutes (AppCompatActivity activity){
        String json = null;
        try{
            InputStream is = activity.getAssets().open("Biker-X-Routes.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            class Route {
                public String name;
                ArrayList<Double> coordinates;

                public Route(ArrayList<Double> coordinates,String name) {
                    this.name = name;
                    this.coordinates = coordinates;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public ArrayList<Double> getCoordinates() {
                    return coordinates;
                }

                public void setCoordinates(ArrayList<Double> coordinates) {
                    this.coordinates = coordinates;
                }

            }
            JSONObject obj = new JSONObject(json);
            JSONArray jsonArray = obj.getJSONArray("features");
            for (int i = 0; i<jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getJSONObject("properties").getString("Name");
                JSONArray outerArray = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                ArrayList<Double> coords = new ArrayList<>();
                for (int j = 0; j < outerArray.length(); j++) {
                    JSONArray innerArray = outerArray.getJSONArray(j);
                    for (int k = 0; k < innerArray.length(); k++) {
                        coords.add(k, innerArray.getDouble(k));
                    }
                }
                db.collection("PCN").add(new Route(coords, name));
            }


        } catch (Exception ex) {
                ex.printStackTrace();
        }
    }

    /**Helper method to load API data onto Firebase.
     */
    public void load (AppCompatActivity activity){
        String json1 = null;
        try{
            InputStream is = activity.getAssets().open("Biker-X-Routes.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json1 = new String(buffer, "UTF-8");

            class Route {
                public String name;

                public Route(String name) {
                    this.name = name;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

            }
            JSONObject obj = new JSONObject(json1);
            JSONArray jsonArray = obj.getJSONArray("features");
            for (int i = 0; i<jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getJSONObject("properties").getString("Name");
                //db.collection("routes1").add(new Route(name, "5.0"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}