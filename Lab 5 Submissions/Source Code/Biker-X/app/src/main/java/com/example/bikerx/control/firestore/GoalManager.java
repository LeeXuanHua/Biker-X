package com.example.bikerx.control.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.bikerx.ui.goals.Goal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Control class to fetch and update goals data for the user through Firebase Firestore.
 */
public class GoalManager extends DBManager{
    /**Retrieve all goals set by a particular user from the database.
     * @param userId The ID of the user.
     * @return A MutableLiveData object, containing ArrayList of goal (monthly distance and monthly time) objects.
     */
    public MutableLiveData<Goal> getGoal(String userId) {
        MutableLiveData<Goal> goal = new MutableLiveData<Goal>();
        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = task.getResult().getData();
                if (data == null) {
                    goal.setValue(null);
                } else {
                    HashMap<String, Object> goalData = (HashMap<String, Object>) data.get("goals");
                    if (goalData == null) {
                        goal.setValue(null);
                    } else {
                        Goal newGoal = new Goal();
                        if (goalData.get("distance") != null) {
                            try {
                                newGoal.setDistance((double)(long) (goalData.get("distance")));
                            } catch (ClassCastException e) {
                                newGoal.setDistance((double)(goalData.get("distance")));
                            }
                        }
                        if (goalData.get("duration") != null) {
                            newGoal.setDuration((long)goalData.get("duration"));
                        }
                        goal.setValue(newGoal);
                    }
                }
            }
        };
        queryDocument("users", userId, listener);
        return goal;
    }

    /**
     * Adds a Goals object to the database
     * @param userId The ID of the user
     * @param goal monthly distance goals set by user in km
     *
     */
    public void setGoal(String userId, Goal goal) {
        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = task.getResult().getData();
                if (data == null) {
                    // User does not exist in Firestore
                    data = new HashMap<String, Object>();
                }
                HashMap<String, Object> goals = new HashMap<String, Object>();
                goals.put("duration", goal.getDuration());
                goals.put("distance", goal.getDistance());
                data.put("goals", goals);
                addEntry("users", userId, data);
            }
        };
        queryDocument("users", userId, listener);
    }
}
