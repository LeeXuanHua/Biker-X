package com.example.bikerx.ui.chat;

import com.google.firebase.Timestamp;

/**
 * An entity class containing the relevant attributes of a message, and provides the get and set methods accordingly.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class Message {
    /**
     * The user id of this Message object.
     */
    private String userId;
    /**
     * The user name of this Message object.
     */
    private String userName;
    /**
     * The message id of this Message object.
     * By default, the message id is the message index appended to the end of the parent forum id.
     */
    private String messageId;
    /**
     * The timestamp of this Message object.
     * By default, the timestamp is obtained from the user end.
     */
    private Timestamp time;
    /**
     * The message content of this Message object.
     */
    private String messageContent;

    /**
     * Constructs a new Message object with the specified user id, user name, message id, timestamp, and message content.
     * @param userId the user id of this Message object
     * @param userName the user name of this Message object
     * @param messageId the message id of this Message object
     * @param time the timestamp of this Message object
     * @param messageContent the message content of this Message object
     */
    public Message(String userId, String userName, String messageId, Timestamp time, String messageContent) {
        this.userId = userId;
        this.userName = userName;
        this.messageId = messageId;
        this.time = time;
        this.messageContent = messageContent;
    }

    /**
     * Gets the user id of this Message object.
     * @return the user id of this Message object, specified as String
     */
    public String getUserId() { return userId; }
    /**
     * Sets the user id of this Message object.
     * @param userId the updated user id of this Message object
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Gets the user name of this Message object.
     * @return the user name of this Message object, specified as String
     */
    public String getUserName() { return userName; }
    /**
     * Sets the user name of this Message object.
     * @param userName the updated user name of this Message object
     */
    public void setUserName(String userName) { this.userName = userName; }

    /**
     * Gets the message id of this Message object.
     * @return the message id of this Message object, specified as String
     */
    public String getMessageId() { return messageId; }
    /**
     * Sets the message id of this Message object.
     * @param messageId the updated message id of this Message object
     */
    public void setMessageID(String messageId) { this.messageId = messageId; }

    /**
     * Gets the timestamp of this Message object.
     * @return the timestamp of this Message object, specified as Timestamp
     */
    public Timestamp getTime() { return time; }
    /**
     * Sets the timestamp of this Message object.
     * @param time the updated timestamp of this Message object
     */
    public void setTime(Timestamp time) { this.time = time; }

    /**
     * Gets the message content of this Message object.
     * @return the message content of this Message object, specified as String
     */
    public String getMessageContent() { return messageContent; }
    /**
     * Sets the message content of this Message object.
     * @param messageContent the updated message content of this Message object
     */
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
}
