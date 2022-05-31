package com.example.bikerx.control.firestore;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.bikerx.ui.chat.ForumThread;
import com.example.bikerx.ui.chat.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Control class to fetch Forum threads and messages, and update new messages using Firebase Firestore.
 */
public class ForumManager extends DBManager {
    /**
     * Retrieves the list of forum threads from the database, specifically the thread id, thread name and content of the last message
     * The content of the last message will be used as the forum description displayed at ChatFragment
     * @return the mutablelivedata array list of ForumThread queried from database
     */
    public MutableLiveData<ArrayList<ForumThread>> getForumThread(){
        MutableLiveData<ArrayList<ForumThread>> forumThreadArrayList = new MutableLiveData<ArrayList<ForumThread>>();
        forumThreadArrayList.setValue(new ArrayList<ForumThread>());

        OnCompleteListener<QuerySnapshot> listener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String threadId = document.getData().get("threadId").toString();
                        String threadName = document.getData().get("threadName").toString();
                        List<HashMap<String, Object>> messageList = (List<HashMap<String, Object>>) document.getData().get("messages");
                        ArrayList<Message> newMessageArray = new ArrayList<>();
                        HashMap<String, Object> messageIndividual = messageList.get(messageList.size()-1);
                        Message newMessage = parseMessage(threadId, messageIndividual);
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
        };
        queryOrderedCollection("forum-threads", listener, "threadName", false, 0);

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

        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    if (data == null) {
                        forumMessageMutableArray.setValue(null);
                    } else {
                        List<HashMap<String, Object>> forumMessageList = (List<HashMap<String, Object>>) data.get("messages");
                        for (HashMap<String, Object> forumMessage : forumMessageList) {
                            Message message = parseMessage(threadId, forumMessage);
                            ArrayList<Message> newForumMessageMutableArray = forumMessageMutableArray.getValue();
                            newForumMessageMutableArray.add(message);
                            forumMessageMutableArray.setValue(newForumMessageMutableArray);
                        }
                    }
                }
            }
        };
        queryDocument("forum-threads", threadId, listener);
        return forumMessageMutableArray;
    }

    /**
     * Adds a Message object to the database
     * @param activity message fragment activity
     * @param message Message object containing data about the message.
     */
    public void addForumMessage(Activity activity, Message message){
        OnCompleteListener<DocumentSnapshot> listener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    List<HashMap<String, Object>> messages = (List<HashMap<String, Object>>) data.get("messages");
                    HashMap<String, Object> entry = new HashMap<String, Object>();
                    entry.put("userId", message.getUserId());
                    entry.put("userName", message.getUserName());
                    entry.put("messageId", message.getMessageId());
                    entry.put("time", message.getTime());
                    entry.put("messageContent", message.getMessageContent());
                    messages.add(entry);
                    updateDocument("forum-threads", message.getThreadId(), "messages", messages);
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Message was not sent sucessfully", Toast.LENGTH_LONG).show();
                }
            }
        };
        queryDocument("forum-threads", message.getThreadId(), listener);
    }

    /**Helper method to parse HashMap from Firestore to Message object.
     * @param messageMapping HashMap object containing Message mapping from Firestore.
     * @return Parsed Message object.
     */
    private Message parseMessage(String threadId, HashMap<String, Object> messageMapping) {
        return new Message(
                threadId,
                (String) messageMapping.get("userId"),
                (String) messageMapping.get("userName"),
                (String) messageMapping.get("messageID"),
                (Timestamp) messageMapping.get("time"),
                ((String) messageMapping.get("messageContent")).replace("\\n", "\n"));
    }
}
