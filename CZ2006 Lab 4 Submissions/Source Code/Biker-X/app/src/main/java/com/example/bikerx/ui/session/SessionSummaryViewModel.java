package com.example.bikerx.ui.session;

import android.media.Rating;

import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.firestore.DBManager;
import com.example.bikerx.control.firestore.RatingManager;


/**This ViewModel handles the backend and data for the SessionSummaryFragment.
 */
public class SessionSummaryViewModel extends ViewModel {
    RatingManager ratingManager = new RatingManager();

    /**This method calls the addRatings method of the DBManager class, to add/update the route rating for the user.
     * @param routeId The ID for the route that is rated.
     * @param userId The ID for the user which completed the cycling session on the route.
     * @param rating The rating given by the user for the route.
     */
    public void rateRoute(String routeId, String userId, float rating) {
        ratingManager.addRatings(routeId, userId, rating);
    }
}