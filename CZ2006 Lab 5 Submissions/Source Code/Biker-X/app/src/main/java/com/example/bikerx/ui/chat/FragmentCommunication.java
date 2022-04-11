package com.example.bikerx.ui.chat;

/**
 * An interface to connect the adapter with the fragment
 */
public interface FragmentCommunication {
    /**
     * Allows for the adapter to send data back to the fragment
     * @param threadId the thread id of the selected ForumThread object
     * @param threadName the thread name of the selected ForumThread object
     */
    void onForumClick(String threadId, String threadName);
}
