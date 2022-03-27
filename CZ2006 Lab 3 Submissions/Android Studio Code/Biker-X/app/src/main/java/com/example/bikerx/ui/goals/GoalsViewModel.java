package com.example.bikerx.ui.goals;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.DBManager;
import com.example.bikerx.entities.Goal;
import com.example.bikerx.entities.GoalsInfo;
import com.example.bikerx.ui.history.CyclingHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**This ViewModel handles the backend and data for the GoalsFragment.
 */
public class GoalsViewModel extends ViewModel {
    private DBManager dbManager = new DBManager();
    private MutableLiveData<Goal> goals;
    private MutableLiveData<ArrayList<CyclingHistory>> cyclingHistory;

    /**Prompts the DBManager to start fetching goals data of the user.
     * @param userId The userId of the user.
     */
    public void fetchGoals(String userId) {
        goals = dbManager.getGoal(userId);
    }

    /**
     * get goals from the database
     * @return
     */
    public MutableLiveData<Goal> getGoals() {
        return goals;
    }

    /**
     * prompts DBManager to update the goals set by user to the database
     * @param userId The userId of the user
     * @param monthlyDistanceInKm MonthlyDistance in km set by the user
     */
    public void updateDistanceGoal(String userId, int monthlyDistanceInKm){
        dbManager.setMonthlyDistanceGoal(userId, monthlyDistanceInKm);
    }

    /**
     * prompts DBManager to update the goals set by user to the database
     * @param userId The userId of the user
     * @param monthlyTimeInHours MonthlyDistance in hours set by the user
     */
    public void updateTimeGoal(String userId, int monthlyTimeInHours){
        dbManager.setMonthlyTimeGoal(userId, monthlyTimeInHours);
    }

    /**
     * prompts DBManager to get cycling history data from the database
     * @param userId The userId of the user
     */
    public void getCyclingHistory(String userId){
        cyclingHistory = dbManager.getCyclingHistory(userId);
    }

    /**Helper method to filter cycling history by month, and aggregate cycling history into monthly distance and duration.
     * @param owner Designates the lifecycle which the observer is attached to.
     * @return A MutableLiveData object, containing a HashMap with "monthDistance" and "monthDuration" mappings.
     */
    public MutableLiveData<HashMap<String, Object>> calculateMonthlyData(LifecycleOwner owner) {
        MutableLiveData<HashMap<String, Object>> data = new MutableLiveData<HashMap<String, Object>>();
        data.setValue(new HashMap<String, Object>());
        cyclingHistory.observe( owner, new Observer<ArrayList<CyclingHistory>>() {
            @Override
            public void onChanged(ArrayList<CyclingHistory> cyclingHistory) {
                double monthDistance = 0;
                long monthDuration = 0;
                Date date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                if (cyclingHistory != null) {
                    for (CyclingHistory entry : cyclingHistory) {
                        String targetDate = dateFormat.format(date);
                        if (targetDate.compareTo(entry.getDate().substring(3, 11)) == 0) {
                            monthDistance += Double.parseDouble(entry.getFormattedDistance());
                            monthDuration += entry.getDuration();
                        }
                    }
                    HashMap<String, Object> hashMap = data.getValue();
                    hashMap.put("monthDistance", monthDistance);
                    hashMap.put("monthDuration", monthDuration);
                    data.setValue(hashMap);
                } else {
                    data.setValue(null);
                }
            }
        });
        return data;
    }
}
