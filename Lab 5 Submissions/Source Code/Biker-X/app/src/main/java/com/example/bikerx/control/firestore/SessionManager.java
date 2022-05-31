package com.example.bikerx.control.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.bikerx.ui.history.CyclingHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Control class to update user's cycling sessions and retrieve cycling history through Firebase Firestore.
 */
public class SessionManager extends DBManager{
    /**Add a cycling session for a user as cycling history.
     * @param userId The ID of the user.
     * @param date The ending date and time of the cycling session. Stored as a String.
     * @param formattedDistance The distance travelled during the cycling session. Stored as a String.
     * @param duration The duration of the cycling session.
     */
    public void addCyclingSession(String userId, String date, String formattedDistance, long duration) {
        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = task.getResult().getData();
                HashMap<String, Object> entry = new HashMap<String, Object>();
                entry.put("date", date);
                entry.put("formattedDistance", formattedDistance);
                entry.put("duration", duration);
                if (data == null) {
                    HashMap<String, List<HashMap<String, Object>>> newUser = new HashMap<String, List<HashMap<String, Object>>>();
                    ArrayList<HashMap<String, Object>> history = new ArrayList<HashMap<String, Object>>();
                    history.add(entry);
                    newUser.put("history", history);
                    addEntry("users", userId, newUser);
                } else {
                    List<HashMap<String, Object>> history = (List<HashMap<String, Object>>) data.get("history");
                    if (history == null) {
                        history = new ArrayList<HashMap<String, Object>>();
                    }
                    history.add(entry);
                    updateDocument("users", userId, "history", history);
                }
            }
        };
        queryDocument("users", userId, listener);
    }

    /**Retrieve all past cycling sessions of a particular user.
     * @param userId The ID of the user.
     * @return A MutableLiveData object, containing an ArrayList of CyclingHistory objects.
     */
    public MutableLiveData<ArrayList<CyclingHistory>> getCyclingHistory(String userId) {
        MutableLiveData<ArrayList<CyclingHistory>> history = new MutableLiveData<ArrayList<CyclingHistory>>();
        history.setValue(new ArrayList<CyclingHistory>());

        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = task.getResult().getData();
                if (data == null) {

                } else {
                    List<HashMap<String, Object>> historyData = (List<HashMap<String, Object>>) data.get("history");
                    if (historyData == null) {
                        history.setValue(null);
                    }
                    else {
                        for (HashMap<String, Object> session: historyData) {
                            CyclingHistory newHistory = new CyclingHistory(
                                    (String) session.get("date"),
                                    (String) session.get("formattedDistance"),
                                    (long) session.get("duration"));
                            ArrayList<CyclingHistory> newHistoryArray = history.getValue();
                            newHistoryArray.add(newHistory);
                            history.setValue(newHistoryArray);
                        }
                    }
                }
            }
        };
        queryDocument("users", userId, listener);
        return history;
    }
}
