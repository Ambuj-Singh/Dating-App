package com.dream.dating.user.ui;

import com.dream.dating.user.UserModel.MessageModel;
import com.dream.dating.user.UserModel.ReceiverModel;

import io.realm.annotations.RealmModule;

@RealmModule(classes = {MessageModel.class, ReceiverModel.class})
public class BaseModel {
}
