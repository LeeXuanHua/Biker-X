package com.example.bikerx.ui.goals;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.firestore.GoalManager;
import com.example.bikerx.control.firestore.SessionManager;
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
    private SessionManager sessionManager = new SessionManager();
    private GoalManager goalManager = new GoalManager();
    private MutableLiveData<Goal> goal;
    private MutableLiveData<ArrayList<CyclingHistory>> cyclingHistory;

    /**Prompts the DBManager to start fetching goals data of the user.
     * @param userId The userId of the user.
     * @return Goals object which can be observed when its updated.
     */
    public MutableLiveData<Goal> fetchGoals(String userId) {
        goal = goalManager.getGoal(userId);
        return goal;
    }

    /**
     * prompts DBManager to update the goals set by user to the database
     * @param userId The userId of the user
     * @param goal Goal object which stores goal data in terms of distance and duration.
     */
    public void updateGoal(String userId, Goal goal) {
        goalManager.setGoal(userId, goal);
    }

    /**
     * prompts DBManager to get cycling history data from the database
     * @param userId The userId of the user
     */
    public void fetchCyclingHistory(String userId){
        cyclingHistory = sessionManager.getCyclingHistory(userId);
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
