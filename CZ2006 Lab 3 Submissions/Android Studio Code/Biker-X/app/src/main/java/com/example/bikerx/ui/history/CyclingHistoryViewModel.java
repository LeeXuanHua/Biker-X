package com.example.bikerx.ui.history;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.DBManager;
import com.example.bikerx.entities.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**This ViewModel handles the backend and data for the CyclingHistoryFragment.
 */
public class CyclingHistoryViewModel extends ViewModel {
    private DBManager dbManager = new DBManager();
    private MutableLiveData<ArrayList<CyclingHistory>> cyclingHistory;
    private MutableLiveData<Goal> goals;

    /**Prompts the DBManager to start fetching cycling history data of the user.
     * @param userId The userId of the user.
     */
    public void fetchCyclingHistory(String userId) {
        cyclingHistory = dbManager.getCyclingHistory(userId);
    }

    /**Prompts the DBManager to start fetching goal data of the user.
     * @param userId The userId of the user.
     */
    public void fetchGoals(String userId) {
        goals = dbManager.getGoal(userId);
    }

    /**Helper method to filter cycling history by month, and aggregate cycling history into monthly distance and duration.
     * @param owner Designates the lifecycle which the observer is attached to.
     * @return A MutableLiveData object, containing a HashMap with "monthDistance" and "monthDuration" mappings.
     */
    public MutableLiveData<HashMap<String, Object>> calculateMonthlyData(LifecycleOwner owner) {
        MutableLiveData<HashMap<String, Object>> data = new MutableLiveData<HashMap<String, Object>>();
        data.setValue(new HashMap<String, Object>());
        cyclingHistory.observe(owner, new Observer<ArrayList<CyclingHistory>>() {
            @Override
            public void onChanged(ArrayList<CyclingHistory> cyclingHistoryArrayList) {
                double monthDistance = 0;
                long monthDuration = 0;
                Date date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                if (cyclingHistoryArrayList != null) {
                    for (CyclingHistory entry : cyclingHistoryArrayList) {
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

    public MutableLiveData<Goal> getGoals() { return goals; }

    public MutableLiveData<ArrayList<CyclingHistory>> getCyclingHistory() {
        return cyclingHistory;
    }
}