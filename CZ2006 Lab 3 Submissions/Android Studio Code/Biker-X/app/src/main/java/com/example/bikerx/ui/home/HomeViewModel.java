package com.example.bikerx.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.VolleyError;
import com.example.bikerx.R;
import com.example.bikerx.control.DBManager;
import com.example.bikerx.control.WeatherApiService;
import com.example.bikerx.control.WeatherResult;
import com.example.bikerx.ui.history.CyclingHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import com.android.volley.VolleyError;
import com.example.bikerx.R;
import com.example.bikerx.control.WeatherApiService;
import com.example.bikerx.control.WeatherResult;

import org.json.JSONException;
import org.json.JSONObject;

/**This ViewModel handles the backend and data for the HomeFragment.
 */
public class HomeViewModel extends ViewModel {
    private DBManager dbManager = new DBManager();
    private MutableLiveData<String> userName = new MutableLiveData<String>();
    private MutableLiveData<ArrayList<Route>> homeRoutes;


    public void fetchHomeRoutes() {
        homeRoutes = dbManager.getHomeRoutes();
    }

    public MutableLiveData<ArrayList<Route>> getHomeRoutes() {
        return homeRoutes;
    }
    private WeatherApiService mVolleyService;
    private WeatherResult mResultCallback;
    private MutableLiveData<Drawable> res = new MutableLiveData<Drawable>();

    /**Retrieves current weather data from Data.gov API, and allocates appropriate drawable based on weather.
     * @param context App context for method to retrieve drawables.
     * @return Drawable to be displayed in HomeFragment.
     */
    public MutableLiveData<Drawable> getWeatherData(Context context) {
        mResultCallback = new WeatherResult() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                if (requestType.equals("GET_WEATHER_DATA")) {
                    try {
                        String forecast = response.getJSONArray("items").getJSONObject(0).getJSONArray("forecasts").getJSONObject(12).getString("forecast");
                        switch (forecast) {
                            case "Partly Cloudy (Day)":
                                res.setValue(context.getResources().getDrawable(R.drawable.cloudy_day));
                                break;
                            case "Partly Cloudy (Night)":
                                res.setValue(context.getResources().getDrawable(R.drawable.cloudy_night));
                                break;
                            case "Cloudy":
                                res.setValue(context.getResources().getDrawable(R.drawable.cloudy));
                                break;
                            case "Light Showers":
                            case "Showers":
                            case "Moderate Rain":
                                res.setValue(context.getResources().getDrawable(R.drawable.rain));
                                break;
                            case "Thundery Showers":
                            case "Heavy Thundery Showers with Gusty Winds":
                                res.setValue(context.getResources().getDrawable(R.drawable.storm));
                                break;
                            default:
                                res.setValue(context.getResources().getDrawable(R.drawable.sun));
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {

            }

        };
        mVolleyService = new WeatherApiService(mResultCallback, context);
        mVolleyService.getDataVolley("GET_WEATHER_DATA", "https://api.data.gov.sg/v1/environment/2-hour-weather-forecast");
        return res;
    }
}