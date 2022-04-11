package com.example.bikerx.control.firestore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Control class to update route ratings from users onto Firebase Firestore.
 */
public class RatingManager extends DBManager {
    /**Add/Update the ratings given by a user for a recommended route.
     * @param routeId The ID of the recommended route,
     * @param userId The ID of the user giving the rating.
     * @param rating The rating given by the user for the recommended route.
     */
    public void addRatings(String routeId, String userId, float rating) {
        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = task.getResult().getData();
                List<HashMap<String, Object>> ratings = (List<HashMap<String, Object>>) data.get("ratings");
                if (ratings == null) {
                    ratings = new ArrayList<HashMap<String, Object>>();
                }
                for (HashMap<String, Object> user : ratings) {
                    if (user.get("userId").toString().compareTo(userId) == 0) {
                        ratings.remove(user);
                        break;
                    }
                }
                HashMap<String, Object> entry = new HashMap<String, Object>();
                entry.put("userId", userId);
                entry.put("rating", rating);
                ratings.add(entry);
                updateDocument("PCN", routeId, "ratings", ratings);
            }
        };
        queryDocument("PCN", routeId, listener);
    }
}
