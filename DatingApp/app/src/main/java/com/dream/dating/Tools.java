package com.dream.dating;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.dream.dating.Models.MessageModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

    private static String username;
    public static final String secret = "DECRYPT12345@";
    public static final String firebaseURL = "https://dreamdating-101.firebaseio.com/";

    @NonNull
    public static String getTimeInFormat(long timestamp){
        //converting timestamp to String in 12:00 pm format
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            return sdf.format(new Date(timestamp));
        }
        catch (Exception e){
            return "Error";
        }
    }

    public static boolean passwordValidator(@NonNull String pass){
        return pass.matches("^([a-zA-z0-9_@$*!?]){8,30}$");
    }

    public static Integer booleanToInteger(boolean var){
        return var?1:0;
    }

    public static Boolean integerToBoolean(int var){
        return var == 1;
    }

    public static String getQueryFromModel(MessageModel messageModel,String senderTable){
        return "insert into "+senderTable+" (sender, receiver, imagePath, message, timestamp, filePath, read, delivery) values ('"+messageModel.getSender()+"','"+messageModel.getReceiver()+"','"+messageModel.getImagePath()
                +"','" +messageModel.getMessage()+"',"+messageModel.getTimestamp()+",'"+messageModel.getFilePath()+"',"+Tools.booleanToInteger(messageModel.getRead())+",1);";
    }

    public static String getPlannedString(MessageModel messageModel){
        return messageModel.getSender()+","+messageModel.getReceiver()+","+messageModel.getImagePath()
                +","+messageModel.getMessage()+","+messageModel.getTimestamp()
                +","+messageModel.getFilePath();
    }

    public static String[] getMessageArray(String message){
        return message.split(",");
    }

    public static String getUsername() {
        return username;
    }

    public static MessageModel getMessageModel(String[] messageArray) {
        MessageModel messageModel = new MessageModel();

        messageModel.setSender(messageArray[0].trim());
        messageModel.setReceiver(messageArray[1].trim());
        messageModel.setImagePath(messageArray[2].trim());
        messageModel.setMessage(messageArray[3].trim());
        messageModel.setTimestamp(Long.parseLong(messageArray[4].trim()));
        messageModel.setFilePath(messageArray[5].trim());

        return messageModel;
    }
}
