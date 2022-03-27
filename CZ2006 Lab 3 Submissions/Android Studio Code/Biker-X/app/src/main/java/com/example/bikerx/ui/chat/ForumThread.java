package com.example.bikerx.ui.chat;

import java.util.ArrayList;

/**
 * An entity class containing the relevant attributes of a forum thread, and provides the get and set methods accordingly.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class ForumThread {
    /**
     * The id of this ForumThread object.
     */
    private String threadId;
    /**
     * The name of this ForumThread object.
     */
    private String threadName;
    /**
     * The message array list of this ForumThread object.
     */
    private ArrayList<Message> messageArrayList;

    /**
     * Constructs a new ForumThread object with the specified id, name, and message array list.
     * @param threadId the id of this ForumThread object
     * @param threadName the name of this ForumThread object
     * @param messageArrayList the message array list of this ForumThread object
     */
    public ForumThread(String threadId, String threadName, ArrayList<Message> messageArrayList) {
        this.threadId = threadId;
        this.threadName = threadName;
        this.messageArrayList = messageArrayList;
    }

    /**
     * Gets the id of this ForumThread object.
     * @return the id of this ForumThread object, specified as String
     */
    public String getThreadId() { return threadId; }
    /**
     * Sets the id of this ForumThread object.
     * @param threadId the updated id of this ForumThread object
     */
    public void setThreadId(String threadId) { this.threadId = threadId; }

    /**
     * Gets the name of this ForumThread object.
     * @return the name of this ForumThread object, specified as String
     */
    public String getThreadName() { return threadName; }
    /**
     * Sets the name of this ForumThread object.
     * @param threadName the updated name of this ForumThread object
     */
    public void setThreadName(String threadName) { this.threadName = threadName; }

    /**
     * Gets the message array list of this ForumThread object.
     * @return the message array list of this ForumThread object, specified as ArrayList
     */
    public ArrayList<Message> getMessageArrayList() { return messageArrayList; }
    /**
     * Sets the message array list of this ForumThread object.
     * @param messageArrayList the updated message array list of this ForumThread object
     */
    public void setMessageArrayList(ArrayList<Message> messageArrayList) { this.messageArrayList = messageArrayList; }

}
