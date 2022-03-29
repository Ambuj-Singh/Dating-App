package com.dream.dating.chat;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatListModel {

    private String username;
    private String UID;
    private String message;
    private String time;
    private String media_doc_link;
    private int message_status;

    public ChatListModel() {
    }

    public ChatListModel(String username,String UID, String message, String time, String media_doc_link, int message_status) {
        this.username = username;
        this.UID = UID;
        this.message = message;
        this.time = time;
        this.media_doc_link = media_doc_link;
        this.message_status = message_status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMedia_doc_link() {
        return media_doc_link;
    }

    public void setMedia_doc_link(String media_doc_link) {
        this.media_doc_link = media_doc_link;
    }

    public int getMessage_status() {
        return message_status;
    }

    public void setMessage_status(int message_status) {
        this.message_status = message_status;
    }
}