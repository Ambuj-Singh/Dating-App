package com.dream.dating.Models;

public class UserInfo {

    private String username;
    private String name;
    private String image;
    private String message;
    private long timestamp;
    private int age;
    private String UID;
    private int UserStatus;
    private int Counter;

    public UserInfo(String username, String name, String image, String message, long timestamp, int age, String UID, int UserStatus, int Counter){
        this.username = username;
        this.name = name;
        this.image = image;
        this.message = message;
        this.timestamp = timestamp;
        this.age = age;
        this.UID = UID;
        this.UserStatus = UserStatus;
        this.Counter = Counter;
    }

    public UserInfo(){

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }


    public int getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(int userStatus) {
        UserStatus = userStatus;
    }

    public int getCounter() {
        return Counter;
    }

    public void setCounter(int counter) {
        Counter = counter;
    }
}
