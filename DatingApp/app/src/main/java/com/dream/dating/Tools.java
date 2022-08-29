package com.dream.dating;

import static org.threeten.bp.Period.between;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.dream.dating.Models.MessageModel;

import org.threeten.bp.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

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


    public static String getDateInFormat(long timestamp){
        //converting timestamp to String in 12:00 pm format
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
            return sdf.format(new Date(timestamp));
        }
        catch (Exception e){
            return "Error";
        }
    }

    public static boolean ageValidator(LocalDate date, LocalDate today){
        return ((between(date,today).getYears()>18)||(between(date,today).getYears()==18)&&(between(date,today).getMonths()>0||between(date,today).getDays()>=1));
    }

    public static boolean usernameValidator(String username){
        return (username.matches("^([a-zA-Z0-9_]){5,30}$")&&username.length()>=5);
    }

    public static boolean passwordValidator(@NonNull String pass){
        return pass.matches("^([a-zA-z0-9_@$*!?]){8,30}$");
    }

    public static boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
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
                +","+new Encryption().encrypt(messageModel.getMessage(),Tools.secret)+","+messageModel.getTimestamp()
                +","+messageModel.getFilePath();
    }

    public static String[] getMessageArray(String message){
        return message.split(",");
    }

    public static MessageModel getMessageModel(String[] messageArray) {
        MessageModel messageModel = new MessageModel();

        messageModel.setSender(messageArray[0].trim());
        messageModel.setReceiver(messageArray[1].trim());
        messageModel.setImagePath(messageArray[2].trim());
        messageModel.setMessage(new Encryption().decrypt(messageArray[3].trim(),Tools.secret));
        messageModel.setTimestamp(Long.parseLong(messageArray[4].trim()));
        messageModel.setFilePath(messageArray[5].trim());

        return messageModel;
    }

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { //columnWidthDp = your item width
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }
}
