package com.example.bikerx.control;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bikerx.ui.chat.ForumThread;
import com.example.bikerx.ui.chat.Message;
import com.example.bikerx.entities.Goal;
import com.example.bikerx.ui.history.CyclingHistory;
import com.example.bikerx.ui.home.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A control class to retrieve and store user data through Firebase.
 */
public class DBManager {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "DBManager";

    /**Add/Update the ratings given by a user for a recommended route.
     * @param routeId The ID of the recommended route,
     * @param userId The ID of the user giving the rating.
     * @param rating The rating given by the user for the recommended route.
     */
    public void addRatings(String routeId, String userId, float rating) {
        db.collection("PCN").document(routeId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                Log.d("test", ratings.toString());
                db.collection("PCN").document(routeId).update("ratings", ratings);
            }
        });
    }

    /**Add a cycling session for a user as cycling history.
     * @param userId The ID of the user.
     * @param date The ending date and time of the cycling session. Stored as a String.
     * @param formattedDistance The distance travelled during the cycling session. Stored as a String.
     * @param duration The duration of the cycling session.
     */
    public void addCyclingSession(String userId, String date, String formattedDistance, long duration) {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                    db.collection("users").document(userId).set(newUser);
                } else {
                    List<HashMap<String, Object>> history = (List<HashMap<String, Object>>) data.get("history");
                    if (history == null) {
                        history = new ArrayList<HashMap<String, Object>>();
                    }
                    history.add(entry);
                    db.collection("users").document(userId).update("history", history);
                }
            }
        });
    }

    /**Retrieve all past cycling sessions of a particular user.
     * @param userId The ID of the user.
     * @return A MutableLiveData object, containing an ArrayList of CyclingHistory objects.
     */
    public MutableLiveData<ArrayList<CyclingHistory>> getCyclingHistory(String userId) {
        MutableLiveData<ArrayList<CyclingHistory>> history = new MutableLiveData<ArrayList<CyclingHistory>>();
        history.setValue(new ArrayList<CyclingHistory>());
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        });
        return history;
    }

    /**
     * Retrieves the list of forum threads from the database, specifically the thread id, thread name and content of the last message
     * The content of the last message will be used as the forum description displayed at ChatFragment
     * @return the mutablelivedata array list of ForumThread queried from database
     */
    public MutableLiveData<ArrayList<ForumThread>> getForumThread(){
        MutableLiveData<ArrayList<ForumThread>> forumThreadArrayList = new MutableLiveData<ArrayList<ForumThread>>();
        forumThreadArrayList.setValue(new ArrayList<ForumThread>());

        db.collection("forum-threads").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String threadId = document.getData().get("threadId").toString();
                        String threadName = document.getData().get("threadName").toString();
                        List<HashMap<String, Object>> messageList = (List<HashMap<String, Object>>) document.getData().get("messages");
                        ArrayList<Message> newMessageArray = new ArrayList<>();
                        HashMap<String, Object> messageIndividual = messageList.get(messageList.size()-1);
                        Message newMessage = new Message(
                                (String) messageIndividual.get("userId"),
                                (String) messageIndividual.get("userName"),
                                (String) messageIndividual.get("messageID"),
                                (Timestamp) messageIndividual.get("time"),
                                ((String) messageIndividual.get("messageContent")).replace("\\n", "\n"));
                        newMessageArray.add(newMessage);

                        ForumThread newForumThread = new ForumThread(
                                threadId,
                                threadName,
                                newMessageArray
                        );
                        ArrayList<ForumThread> newForumThreadArray = forumThreadArrayList.getValue();
                        newForumThreadArray.add(newForumThread);
                        forumThreadArrayList.setValue(newForumThreadArray);
                    }
                }
            }
        });

        return forumThreadArrayList;
    }

    /**
     * Retrieves the specified list of messages from the database
     * @param threadId the forum id to query the list of messages
     * @return the mutablelivedata array list of Message queried from database
     */
    public MutableLiveData<ArrayList<Message>> getForumMessage(String threadId) {
        MutableLiveData<ArrayList<Message>> forumMessageMutableArray = new MutableLiveData<ArrayList<Message>>();
        forumMessageMutableArray.setValue(new ArrayList<Message>());

        db.collection("forum-threads").document(threadId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    if (data == null) {
                        forumMessageMutableArray.setValue(null);
                    } else {
                        List<HashMap<String, Object>> forumMessageList = (List<HashMap<String, Object>>) data.get("messages");
                        for (HashMap<String, Object> forumMessage : forumMessageList) {
                            Message message = new Message(
                                    (String) forumMessage.get("userId"),
                                    (String) forumMessage.get("userName"),
                                    (String) forumMessage.get("messageID"),
                                    (Timestamp) forumMessage.get("time"),
                                    ((String) forumMessage.get("messageContent")).replace("\\n", "\n"));
                            ArrayList<Message> newForumMessageMutableArray = forumMessageMutableArray.getValue();
                            newForumMessageMutableArray.add(message);
                            forumMessageMutableArray.setValue(newForumMessageMutableArray);
                        }
                    }
                }
            }
        });
        return forumMessageMutableArray;
    }

    /**
     * Adds a Message object to the database
     * @param activity message fragment activity
     * @param threadId forum id where the message will be added to
     * @param userId id of user who sent the message
     * @param userName name of user who sent the message
     * @param messageId id of Message object
     * @param time timestamp of Message object
     * @param messageContent content of Message object
     */
    public void addForumMessage(Activity activity, String threadId, String userId, String userName, String messageId, Timestamp time, String messageContent){
        db.collection("forum-threads").document(threadId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    List<HashMap<String, Object>> messages = (List<HashMap<String, Object>>) data.get("messages");
                    HashMap<String, Object> entry = new HashMap<String, Object>();
                    entry.put("userId", userId );
                    entry.put("userName", userName);
                    entry.put("messageId", messageId );
                    entry.put("time", time);
                    entry.put("messageContent", messageContent );
                    messages.add(entry);
                    db.collection("forum-threads").document(threadId).update("messages", messages);
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Message was not sent sucessfully", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Get the routes stored in database
     * @return ArrayList of Route to be displayed in HomeFragment and RecommendationsFragment
     */
    public MutableLiveData<ArrayList<Route>> getHomeRoutes() {
        MutableLiveData<ArrayList<Route>> routeList = new MutableLiveData<ArrayList<Route>>();
        routeList.setValue(new ArrayList<Route>());
        db.collection("PCN").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Route route = parseRouteData(document);
                        ArrayList<Route> currentRouteArray = routeList.getValue();
                        currentRouteArray.add(route);
                        routeList.setValue(currentRouteArray);
                    }
                }
            }
        });
        return routeList;
    }

    /**
     * Retrieves the route chosen by user. This route is used to draw path of chosen route
     * @param routeId id of the route selected
     * @return Route selected by user
     */
    public MutableLiveData<Route> getRecommendedRoute(String routeId) {
        MutableLiveData<Route> route = new MutableLiveData<Route>();
        db.collection("PCN").document(routeId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Route newRoute = parseRouteData(document);

                    route.setValue(newRoute);
                }
            }
        });
        return route;
    }

    /**
     * Function to parse document that contains route data into route
     * @param document that contains data of route
     * @return
     */
    private Route parseRouteData(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        Route route = new Route(null, document.getId(),null, null, null);
        Object imageIdObj = data.get("imageId");

        if (imageIdObj != null) {
            long imageIdLong = (Long) imageIdObj;
            route.setImageId( (int) imageIdLong);
        }

        String name = data.get("name").toString();
        route.setRouteName(name);

        ArrayList<Double> coordinates = (ArrayList<Double>) data.get("coordinates");
        ArrayList<LatLng> latLngs = new ArrayList<>();
        int i = 0;
        while (i < coordinates.size()) {
            LatLng latLng = new LatLng(coordinates.get(i+1), coordinates.get(i));
            latLngs.add(latLng);
            i += 2;
        }
        route.setCoordinates(latLngs);

        Object ratingsObj = data.get("ratings");
        if (ratingsObj != null ) {
            route.setRatings((ArrayList<HashMap<String, Object>>) ratingsObj);
        }
        return route;
    }

    /**Retrieve all goals set by a particular user from the database.
     * @param userId The ID of the user.
     * @return A MutableLiveData object, containing ArrayList of goal (monthly distance and monthly time) objects.
     */
    public MutableLiveData<Goal> getGoal(String userId) {
        MutableLiveData<Goal> goal = new MutableLiveData<Goal>();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            newGoal.setDistance((double)((long)goalData.get("distance")));
                        }
                        if (goalData.get("duration") != null) {
                            newGoal.setDuration((long)goalData.get("duration"));
                        }
                        goal.setValue(newGoal);
                    }
                }
            }
        });
        return goal;
    }

    /**
     * Adds a Goals object to the database
     * @param userId The ID of the user
     * @param monthlyDistanceInKm monthly distance goals set by user in km
     *
     */
    public void setMonthlyDistanceGoal(String userId, int monthlyDistanceInKm) {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = task.getResult().getData();
                if (data == null) {
                    // User does not exist in Firestore
                    data = new HashMap<String, Object>();
                    HashMap<String, Object> goals = new HashMap<String, Object>();
                    goals.put("distance", monthlyDistanceInKm);
                    data.put("goals", goals);
                } else {
                    // User exists in Firestore
                    HashMap<String, Object> goals = new HashMap<String, Object>();
                    goals.put("distance", monthlyDistanceInKm);
                    if (data.get("goals") != null) {
                        Map<String, Object> existingGoals = (Map<String, Object>) data.get("goals");
                        if (existingGoals.get("duration") != null) {
                            goals.put("duration", existingGoals.get("duration"));
                        }
                    }
                    data.put("goals", goals);
                }
                db.collection("users").document(userId).set(data);
            }
        });
    }

    /**
     * Adds a Goals object to the database
     * @param userId The ID of the user
     * @param monthlyTimeInHours monthly time goals set by user in Hours
     *
     */
    public void setMonthlyTimeGoal(String userId, int monthlyTimeInHours) {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = task.getResult().getData();
                if (data == null) {
                    // User does not exist in Firestore
                    data = new HashMap<String, Object>();
                    HashMap<String, Object> goals = new HashMap<String, Object>();
                    goals.put("duration", monthlyTimeInHours * 3600 * 1000);
                    data.put("goals", goals);
                } else {
                    HashMap<String, Object> goals = new HashMap<String, Object>();
                    goals.put("duration", monthlyTimeInHours * 3600 * 1000);
                    if (data.get("goals") != null) {
                        Map<String, Object> existingGoals = (Map<String, Object>) data.get("goals");
                        if (existingGoals.get("distance") != null) {
                            goals.put("distance", existingGoals.get("distance"));
                        }
                    }
                    data.put("goals", goals);
                    db.collection("users").document(userId).set(data);
                }
            }
        });
    }

}
