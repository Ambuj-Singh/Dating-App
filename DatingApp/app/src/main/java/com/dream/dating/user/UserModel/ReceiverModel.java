package com.dream.dating.user.UserModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReceiverModel extends RealmObject {

    @PrimaryKey
    private String id;

    private RealmList<MessageModel> messages;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMessages(RealmList<MessageModel> messages) {
        this.messages = messages;
    }

    public RealmList<MessageModel> getMessages() {
        return messages;
    }
}
