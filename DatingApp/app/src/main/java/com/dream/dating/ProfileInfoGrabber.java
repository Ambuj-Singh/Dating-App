package com.dream.dating;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;


@IgnoreExtraProperties
public class ProfileInfoGrabber {

    private String username;
    private int Age;
    private String Bio;
    private String ProfileURL;
    private Boolean UserStatus;
    private String UID;
    private int gender;
    private List<String> favourite;
    private boolean ProfileShow;
    private String Name;

    public ProfileInfoGrabber(){

    }

    public ProfileInfoGrabber(String username, int Age, String Bio, String ProfileURL, Boolean UserStatus, String UID, int gender,List<String> favourite,boolean ProfileShow, String Name) {
        this.username = username;
        this.Age = Age;
        this.Bio = Bio;
        this.ProfileURL = ProfileURL;
        this.UserStatus = UserStatus;
        this.UID = UID;
        this.gender = gender;
        this.favourite = favourite;
        this.ProfileShow = ProfileShow;
        this.Name = Name;

    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int Age) {
        this.Age = Age;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String Bio) {
        this.Bio = Bio;
    }

    public String getProfileURL() {
        return ProfileURL;
    }

    public void setProfileURL(String ProfileURL) {
        this.ProfileURL = ProfileURL;
    }

    public Boolean getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        UserStatus = userStatus;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public int getGender() {
        return gender;
    }

    public List<String> getFavourite() {
        return favourite;
    }

    public boolean isProfileShow() {
        return ProfileShow;
    }

    public String getName() {
        return Name;
    }
}