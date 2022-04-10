package com.example.bikerx.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.control.firestore.DBManager;
import com.example.bikerx.control.firestore.ForumManager;

import java.util.ArrayList;

/**
 * A view model class that inherits the built-in ViewModel class to support back end operations relating to ForumThread objects.
 * This class mainly offers functionalities for the Chat/Forum component of the application.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class ChatViewModel extends ViewModel {
    /**
     * Starts a new instance of DBManager
     * @see DBManager
     */
    private ForumManager forumManager = new ForumManager();
    /**
     * The mutablelivedata forum thread array list of this ChatViewModel object.
     */
    public MutableLiveData<ArrayList<ForumThread>> forumThreadArrayList;

    /**
     * Calls the DBManager to instantiate the forumThreadArrayList of this ChatViewModel object.
     */
    public void fetchForumThread() {
        forumThreadArrayList = forumManager.getForumThread();
    }

    /**
     * Gets the mutablelivedata array list of ForumThread of this ChatViewModel object.
     * @return the mutablelivedata array list of ForumThread queried in fetchForumThread
     */
    public MutableLiveData<ArrayList<ForumThread>> getForumThread() {
        return forumThreadArrayList;
    }
}