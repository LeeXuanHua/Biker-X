package com.example.bikerx.ui.chat;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bikerx.MainActivity;
import com.example.bikerx.control.firestore.DBManager;
import com.example.bikerx.control.firestore.ForumManager;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

/**
 * A view model class that inherits the built-in ViewModel class to support back end operations relating to Message objects.
 * This class mainly offers functionalities for the Chat/Forum component of the application.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class MessageViewModel extends ViewModel {
    /**
     * Starts a new instance of DBManager
     * @see DBManager
     */
    ForumManager forumManager = new ForumManager();
    /**
     * The mutablelivedata message array list of this MessageViewModel object.
     */
    public MutableLiveData<ArrayList<Message>> mutableMessageArrayList;

    /**
     * Calls the DBManager to instantiate the mutableMessageArrayList of this MessageViewModel object.
     */
    public void fetchMessages(String threadId) {
        mutableMessageArrayList = forumManager.getForumMessage(threadId);
    }

    /**
     * Gets the mutablelivedata array list of Messages of this MessageViewModel object.
     * @return the mutablelivedata array list of Messages queried in getForumMessage
     */
    public MutableLiveData<ArrayList<Message>> getMessages() {
        return mutableMessageArrayList;
    }

    /**
     * Calls the DBManager to send a message to Firebase database, and updates the existing mutablelivedata and recycler view.
     * @param activity message fragment activity
     * @param threadId thread id of the Message object
     * @param messageContent message content of the Message object
     * @param mAdapter adapter that controls the message recycler view, of the class MessageAdapter
     */
    public void sendMessage(FragmentActivity activity, String threadId, String messageContent, MessageAdapter mAdapter) {
        String userName = ((MainActivity) activity).getSupportActionBar().getTitle().toString().replace("Hello, ", "").replace("!","");
        String messageId = threadId + Integer.toString(mutableMessageArrayList.getValue().size());
        Timestamp currentTimestamp = Timestamp.now();

        Message newMessage = new Message(threadId, ((MainActivity)activity).getUserId(), userName, messageId, currentTimestamp, messageContent);
        forumManager.addForumMessage(activity, newMessage);

        Message addedMessage = new Message(threadId, ((MainActivity)activity).getUserId(), userName, messageId, currentTimestamp, messageContent);
        ArrayList<Message> messageArrayList = mutableMessageArrayList.getValue();
        messageArrayList.add(addedMessage);
        mutableMessageArrayList.setValue(messageArrayList);
    }
}