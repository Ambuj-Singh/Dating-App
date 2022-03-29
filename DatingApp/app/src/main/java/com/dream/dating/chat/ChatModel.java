package com.dream.dating.chat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ChatModel {

    private String username;
    private String UID;
    private String message;
    private String time;
    private String media_doc_link;
    private int message_status;

    public ChatModel() {
    }

    public ChatModel(String username,String UID, String message, String time, int message_status, String media_doc_link) {
        this.username = username;
        this.UID = UID;
        this.message = message;
        this.time = time;
        this.message_status = message_status;
        this.media_doc_link = media_doc_link;
    }

    @Exclude
    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("receiver", UID);
        result.put("TimeStamp", time);
        result.put("message", message);
        result.put("message_status", message_status);
        result.put("media_doc_link", media_doc_link);

        return result;
    }
}