package com.dream.dating.user.UserModel;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class User_Getter {

    private String Bio;
    private String OwnWords;
    private String ProfileURL;
    private String Age;

    //general
    private int Balance;
    private int Activity;
    private int Neatness;
    private int Nature;
    private List<String> languages;
    private List<String> looking;
    private List<String> smoker;
    private List<String> bodyType;
    private List<String> bodyHair;
    private List<String> eyeColor;
    private List<String> hairColor;
    private List<String> piercings;
    private List<String> tattoos;

    //social
    private List<String> profession;
    private List<String> religion;
    private List<String> food;
    private List<String> music;
    private List<String> sports;
    private List<String> going;
    private List<String> travel;
    private List<String> interests;
    private boolean generalSwitch;
    private boolean socialSwitch;

    public User_Getter(){
    }

    public User_Getter(String Bio,String OwnWords,String ProfileURL,String Age,int Balance,int Activity,int Neatness,int Nature,List<String> profession,List<String> religion,List<String> food,List<String> music,List<String> sports,List<String> going,List<String> travel,List<String> interests,List<String> languages,List<String> looking,List<String> smoker,List<String> bodyType,List<String> bodyHair,List<String> eyeColor,List<String> hairColor,List<String> piercings,List<String> tattoos, boolean generalSwitch, boolean socialSwitch){

        this.Bio = Bio;
        this.OwnWords = OwnWords;
        this.ProfileURL = ProfileURL;
        this.Age = Age;

        this.Balance = Balance;
        this.Activity = Activity;
        this.Neatness = Neatness;
        this.Nature = Nature;
        
        this.languages = languages;
        this.looking = looking;
        this.smoker = smoker;
        this.bodyType = bodyType;
        this.bodyHair = bodyHair;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.piercings = piercings;
        this.tattoos = tattoos;

        this.profession = profession;
        this.religion = religion;
        this.food = food;
        this.music = music;
        this.sports = sports;
        this.going = going;
        this.travel = travel;
        this.interests = interests;
        this.generalSwitch = generalSwitch;
        this.socialSwitch = socialSwitch;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public List<String> getLooking() {
        return looking;
    }

    public List<String> getSmoker() {
        return smoker;
    }

    public List<String> getBodyType() {
        return bodyType;
    }

    public List<String> getBodyHair() {
        return bodyHair;
    }

    public List<String> getEyeColor() {
        return eyeColor;
    }

    public List<String> getHairColor() {
        return hairColor;
    }

    public List<String> getPiercings() {
        return piercings;
    }

    public List<String> getTattoos() {
        return tattoos;
    }

    public List<String> getProfession() {
        return profession;
    }

    public List<String> getReligion() {
        return religion;
    }

    public List<String> getFood() {
        return food;
    }

    public List<String> getMusic() {
        return music;
    }

    public List<String> getSports() {
        return sports;
    }

    public List<String> getGoing() {
        return going;
    }

    public List<String> getTravel() {
        return travel;
    }

    public List<String> getInterests() {
        return interests;
    }

    public String getBio() {
        return Bio;
    }

    public String getOwnWords() {
        return OwnWords;
    }

    public String getProfileURL() {
        return ProfileURL;
    }

    public String getAge() {
        return Age;
    }

    public int getBalance() {
        return Balance;
    }

    public int getActivity() {
        return Activity;
    }

    public int getNeatness() {
        return Neatness;
    }

    public int getNature() {
        return Nature;
    }

    public boolean isGeneralSwitch() {
        return generalSwitch;
    }

    public boolean isSocialSwitch() {
        return socialSwitch;
    }

}