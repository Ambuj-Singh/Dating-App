package com.dream.dating.user.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.dream.dating.Models.UserInfo;
import com.dream.dating.Models.User_Getter;
import com.dream.dating.ProfileInfoGrabber;
import com.dream.dating.R;
import com.dream.dating.Services.DataContext;
import com.dream.dating.Tools;
import com.dream.dating.account.AccountActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class UserProfile extends AppCompatActivity {

    private DataContext dataContext;
    private List<String> checked_chips;
    private ChipGroup chipGroup;
    private static final int PERMISSION_STORAGE_GRANTED = 1;
    public View header_visiting, header_user;
    //profile header
    public ImageView header_profile_display, header_gender_display, header_gender_display_user, header_profile_display_user;
    //Social chip selection view
    public TextView header_username_display, header_username_display_user, header_age_display, header_age_display_user, social_option_prof, social_option_religion, social_option_fav_food, social_option_music,
            social_option_sports, social_option_going_out, social_option_travel, social_option_interests,
            language, looking, smoker, body, body_hair, eye_color, hair_color, piercings, tattoos, save_about_button,
            save_own_words_button, save_general_button, save_social_button;
    private String Type, path;
    //edit buttons
    private ImageButton Bio_edit, general_edit,Social_edit, Own_words_edit;
    private DocumentReference documentReference,df_user;
    //Switches
    private SwitchMaterial social_switch, general_switch;
    //Social edit buttons
    private RelativeLayout social_option_1, social_option_2, social_option_3, social_option_4,
            social_option_5, social_option_6, social_option_7, social_option_8, social_option_9, social_option_10,
            social_option_11, social_option_12, general_option_1, general_option_2, general_option_3, general_option_4,
            general_option_5, general_option_6, general_option_7, general_option_8, general_option_9;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //initializing server and local database
        dataContext = new DataContext(this);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //initializing view elements of the page
        initializeViewElements();
        //initializing display units
        initializeDisplayUnits();

        //Headers
        header_visiting = findViewById(R.id.header_visiting);
        header_visiting.setVisibility(View.GONE);
        header_user = findViewById(R.id.header_user);
        header_user.setVisibility(View.GONE);

        //Intent UID
        final String uid = getIntent().getStringExtra("uid");

        final String UserUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        //Checking User uid to allow edit options
        boolean header;
        if (uid != null) {
            if(!UserUID.equals(uid)) {

                //profile header elements for visiting
                header_gender_display = findViewById(R.id.gender_display);
                header_profile_display = findViewById(R.id.profile_display);
                header_age_display = findViewById(R.id.age_display);
                header_username_display = findViewById(R.id.display_name_visiting);

                documentReference = firebaseFirestore.collection("users").document(uid).collection("profile").document("profile_view");
                header_visiting.setVisibility(View.VISIBLE);
                //remove Edit options if the user is not the logged in user
                df_user = firebaseFirestore.collection("users").document(uid);
                header = false;

            } else {
                //enable Edit options if the user is the logged in user
                //profile header elements for user
                header_gender_display_user = findViewById(R.id.gender_display_user);
                header_profile_display_user = findViewById(R.id.profile_display_user);
                header_age_display_user = findViewById(R.id.age_display_user);
                header_username_display_user = findViewById(R.id.display_name_user);

                documentReference = firebaseFirestore.collection("users").document(UserUID).collection("profile").document("profile_view");
                header_user.setVisibility(View.VISIBLE);
                df_user = firebaseFirestore.collection("users").document(UserUID);
                header = true;
            }
        } else {
            //profile header elements for user
            header_gender_display_user = findViewById(R.id.gender_display_user);
            header_profile_display_user = findViewById(R.id.profile_display_user);
            header_age_display_user = findViewById(R.id.age_display_user);
            header_username_display_user = findViewById(R.id.display_name_user);

            documentReference = firebaseFirestore.collection("users").document(UserUID).collection("profile").document("profile_view");
            header_user.setVisibility(View.VISIBLE);
            df_user = firebaseFirestore.collection("users").document(UserUID);
            header = true;
        }
        enableEditOptions();
        header_init(df_user, header);
        query_maker(documentReference);
        final FloatingActionButton fab_favourite = findViewById(R.id.favourite_profile);


        df_user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                   ProfileInfoGrabber grabber = documentSnapshot.toObject(ProfileInfoGrabber.class);
                   if(documentSnapshot.get("favourite")!=null){
                        List<String> fav_list = grabber.getFavourite();
                        if (fav_list.contains(UserUID)) {
                            profile_favoured(fab_favourite, false);
                        } else {
                            profile_unfavoured(fab_favourite, false);
                        }
                    }
                   else{
                       profile_unfavoured(fab_favourite, false);
                   }
                }
            }
        });

        //back button for user own profile
        FloatingActionButton profileUserBack = findViewById(R.id.profile_user_back);
        profileUserBack.setOnClickListener(v -> {
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
            finish();
        });
        //favourite action

            fab_favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab_favourite.setImageResource(R.drawable.loading_main_anim);
                    Animatable animatable = (Animatable) fab_favourite.getDrawable();
                    animatable.start();
                    getFavourite(df_user, fab_favourite);
                }
            });
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.create();
        progressDialog.show();
        ProgressBar progressBar = progressDialog.findViewById(android.R.id.progress);
        progressBar.getIndeterminateDrawable().setTint(Color.rgb(98,0,238));
        setProfileShow(new settingProfileShow() {
            @Override
            public void onCallback(boolean value) {

                progressDialog.cancel();
            }
        });

        //Social Display Switch
        social_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socialSwitchSet();
            }
        });

        general_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalSwitchSet();
            }
        });
        //Chat button pressed action
        //adding user to local storage to initiate conversation
        FloatingActionButton chat = findViewById(R.id.chat_profile);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.create();
                progressDialog.show();
                ProgressBar progressBar = progressDialog.findViewById(android.R.id.progress);
                progressBar.getIndeterminateDrawable().setTint(Color.rgb(98,0,238));

                getUserInfo(df_user, userInfo -> {
                    dataContext.insertUser(userInfo);
                    progressDialog.cancel();
                    Toast.makeText(UserProfile.this, "Friend Added", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UserProfile.this,Conversation.class);
                    i.putExtra("uid",uid);
                    startActivity(i);
                });
            }
        });
    }

    private interface ChatCallback{
        void onCallback(UserInfo userInfo);
    }

    private void getUserInfo(DocumentReference df_user, final ChatCallback callback) {
        df_user.get().addOnSuccessListener(documentSnapshot -> {
            try{
                UserInfo user = documentSnapshot.toObject(UserInfo.class);
                  callback.onCallback(user);
            }
            catch (Exception e){
                Log.i("chatButton",e.getMessage());
            }
        }).addOnFailureListener(e -> Log.i("chatbutton",e.getMessage()+" Local data save failed"));
    }

    private void generalSwitchSet(){
        if(!general_switch.isChecked()){
            Map<String, Object> data = new HashMap<>();
            data.put("generalSwitch",false);
            generalSwitchCheckCallback(false);
            saveInfoString(data,documentReference);
        }
        else{
            Map<String, Object> data = new HashMap<>();
            data.put("generalSwitch",true);
            generalSwitchCheckCallback(true);
            saveInfoString(data,documentReference);
        }

    }

    private void socialSwitchSet(){
        if(!social_switch.isChecked()){
            Map<String, Object> data = new HashMap<>();
            data.put("socialSwitch",false);
            socialSwitchCheckCallback(false);
            saveInfoString(data,documentReference);
        }
        else{
            Map<String, Object> data = new HashMap<>();
            data.put("socialSwitch",true);
            socialSwitchCheckCallback(true);
            saveInfoString(data,documentReference);
        }
    }

    public void getFavourite(DocumentReference df, final FloatingActionButton floatingActionButton){
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String UserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ProfileInfoGrabber grabber = documentSnapshot.toObject(ProfileInfoGrabber.class);
                    if (documentSnapshot.get("favourite")!=null){
                        List<String> fav_list = grabber.getFavourite();
                        if (fav_list.contains(UserUID)) {
                            profile_unfavoured(floatingActionButton, true);
                        } else {
                            profile_favoured(floatingActionButton, true);
                        }
                    }
                    else{
                        profile_favoured(floatingActionButton, true);
                    }
                }
                else{
                    profile_favoured(floatingActionButton, true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
/*
    public void go_back(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }
*/
    private interface settingProfileShow{
        void onCallback(boolean value);
    }

    private void setProfileShow(final settingProfileShow callback){
        df_user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    ProfileInfoGrabber grabber = documentSnapshot.toObject(ProfileInfoGrabber.class);
                    if(grabber!=null){
                      callback.onCallback(grabber.isProfileShow());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Network is unavailable", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initializeDisplayUnits() {
        //social card unit
        social_option_prof = findViewById(R.id.social_profession_choice);
        social_option_religion = findViewById(R.id.social_religion_choice);
        social_option_fav_food = findViewById(R.id.social_food_choice);
        social_option_music = findViewById(R.id.social_music_choice);
        social_option_sports = findViewById(R.id.social_sports_choice);
        social_option_going_out = findViewById(R.id.social_going_choice);
        social_option_travel = findViewById(R.id.social_travel_choice);
        social_option_interests = findViewById(R.id.social_interests_choice);

        //general card unit
        language = findViewById(R.id.general_language_choice);
        looking = findViewById(R.id.general_looking_choice);
        smoker = findViewById(R.id.general_smoker_choice);
        body = findViewById(R.id.general_body_choice);
        body_hair = findViewById(R.id.general_body_hair_choice);
        eye_color = findViewById(R.id.general_eye_color_choice);
        hair_color = findViewById(R.id.general_hair_color_choice);
        piercings = findViewById(R.id.general_piercings_choice);
        tattoos = findViewById(R.id.general_tattoos_choice);
    }

    //initialize view elements
    private void initializeViewElements() {
        //Dot selection buttons
        social_option_1 = findViewById(R.id.social_dot_1);
        social_option_2 = findViewById(R.id.social_dot_2);
        social_option_3 = findViewById(R.id.social_dot_3);
        social_option_4 = findViewById(R.id.social_dot_4);

        //Chip Selection Buttons
        social_option_5 = findViewById(R.id.profession_choose);
        social_option_6 = findViewById(R.id.religion_choose);
        social_option_7 = findViewById(R.id.food_choose);
        social_option_8 = findViewById(R.id.music_choose);
        social_option_9 = findViewById(R.id.sports_choose);
        social_option_10 = findViewById(R.id.going_choose);
        social_option_11 = findViewById(R.id.travel_choose);
        social_option_12 = findViewById(R.id.interests_choose);

        //Edit buttons
        //edit_profile
        Bio_edit = findViewById(R.id.bio_edit);
        Social_edit = findViewById(R.id.social_edit);
        Own_words_edit = findViewById(R.id.my_own_words_edit);

        //Switches
        social_switch = findViewById(R.id.social_switch);
        general_switch = findViewById(R.id.general_switch);

        //General Elements
        general_option_1 = findViewById(R.id.language_choose);
        general_option_2 = findViewById(R.id.looking_choose);
        general_option_3 = findViewById(R.id.smoker_choose);
        general_option_4 = findViewById(R.id.body_choose);
        general_option_5 = findViewById(R.id.body_hair_choose);
        general_option_6 = findViewById(R.id.eye_choose);
        general_option_7 = findViewById(R.id.hair_color_choose);
        general_option_8 = findViewById(R.id.piercings_choose);
        general_option_9 = findViewById(R.id.tattoos_choose);
        general_edit = findViewById(R.id.general_edit);

        //done illusion
        save_about_button = findViewById(R.id.save_about_me);
        save_general_button = findViewById(R.id.save_general);
        save_social_button = findViewById(R.id.save_social);
        save_own_words_button = findViewById(R.id.save_own);
        //hiding the illusion
        save_own_words_button.setVisibility(View.GONE);
        save_about_button.setVisibility(View.GONE);
        save_general_button.setVisibility(View.GONE);
        save_social_button.setVisibility(View.GONE);
    }

    //favourite functions start
    private void profile_favoured(final FloatingActionButton fab, boolean value) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(value) {

            df_user.update("favourite", FieldValue.arrayUnion(uid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    fab.setImageResource(R.drawable.favrouite_anime);
                    Animatable animatable = (Animatable) fab.getDrawable();
                    animatable.start();

                    Toast.makeText(UserProfile.this, "Added to your favourites.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfile.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            fab.setImageResource(R.drawable.favrouite_anime);
            Animatable animatable = (Animatable) fab.getDrawable();
            animatable.start();
        }
    }

    private void profile_unfavoured(final FloatingActionButton fab, boolean value) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(value) {
            df_user.update("favourite", FieldValue.arrayRemove(uid)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fab.setImageResource(R.drawable.unfavourite_profile);
                Animatable animatable = (Animatable) fab.getDrawable();
                animatable.start();
                Toast.makeText(UserProfile.this, "Removed from your favourites.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
        }
        else{
            fab.setImageResource(R.drawable.unfavourite_profile);
            Animatable animatable = (Animatable) fab.getDrawable();
            animatable.start();
        }
    }
    //favourite functions end

    /*remove edit options
    public void removeEditOptions() {
        social_switch.setVisibility(View.GONE);
        Bio_edit.setVisibility(View.GONE);
        Social_edit.setVisibility(View.GONE);
        Own_words_edit.setVisibility(View.GONE);
        general_edit.setVisibility(View.GONE);
        general_switch.setVisibility(View.GONE);


        //disabling
        social_option_1.setEnabled(false);
        social_option_2.setEnabled(false);
        social_option_3.setEnabled(false);
        social_option_4.setEnabled(false);
        social_option_5.setEnabled(false);
        social_option_6.setEnabled(false);
        social_option_7.setEnabled(false);
        social_option_8.setEnabled(false);
        social_option_9.setEnabled(false);
        social_option_10.setEnabled(false);
        social_option_11.setEnabled(false);
        social_option_12.setEnabled(false);
        social_switch.setEnabled(false);
        Bio_edit.setEnabled(false);
        Social_edit.setEnabled(false);
        Own_words_edit.setEnabled(false);
        general_edit.setEnabled(false);

        //general options
        general_option_1.setEnabled(false);
        general_option_2.setEnabled(false);
        general_option_3.setEnabled(false);
        general_option_4.setEnabled(false);
        general_option_5.setEnabled(false);
        general_option_6.setEnabled(false);
        general_option_7.setEnabled(false);
        general_option_8.setEnabled(false);
        general_option_9.setEnabled(false);
        general_switch.setEnabled(false);
    }
*/
    //enabling all edit options
    public void enableEditOptions() {

        //edit buttons
        social_switch.setVisibility(View.VISIBLE);
        Bio_edit.setVisibility(View.VISIBLE);
        Social_edit.setVisibility(View.VISIBLE);
        Own_words_edit.setVisibility(View.VISIBLE);
        general_edit.setVisibility(View.VISIBLE);
        general_switch.setVisibility(View.VISIBLE);
        //enabling options
        social_option_1.setEnabled(true);
        social_option_2.setEnabled(true);
        social_option_3.setEnabled(true);
        social_option_4.setEnabled(true);
        social_option_5.setEnabled(true);
        social_option_6.setEnabled(true);
        social_option_7.setEnabled(true);
        social_option_8.setEnabled(true);
        social_option_9.setEnabled(true);
        social_option_10.setEnabled(true);
        social_option_11.setEnabled(true);
        social_option_12.setEnabled(true);
        social_switch.setEnabled(true);
        Bio_edit.setEnabled(true);
        Social_edit.setEnabled(true);
        Own_words_edit.setEnabled(true);


        //general options
        general_option_1.setEnabled(true);
        general_option_2.setEnabled(true);
        general_option_3.setEnabled(true);
        general_option_4.setEnabled(true);
        general_option_5.setEnabled(true);
        general_option_6.setEnabled(true);
        general_option_7.setEnabled(true);
        general_option_8.setEnabled(true);
        general_option_9.setEnabled(true);
        general_edit.setEnabled(true);
        general_switch.setEnabled(true);
    }

    //social card starts here
    public void showDialog_dot_1(View view) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(R.id.social_1);
        ids.add(R.id.social_2);
        ids.add(R.id.social_3);
        ids.add(R.id.social_4);
        ids.add(R.id.social_5);
        createDialogForRange("Balance", ids);
    }

    public void showDialog_dot_2(View view) {

        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(R.id.social_6);
        ids.add(R.id.social_7);
        ids.add(R.id.social_8);
        ids.add(R.id.social_9);
        ids.add(R.id.social_10);
        createDialogForRange("Activity", ids);
    }

    public void showDialog_dot_3(View view) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(R.id.social_11);
        ids.add(R.id.social_12);
        ids.add(R.id.social_13);
        ids.add(R.id.social_14);
        ids.add(R.id.social_15);
        createDialogForRange("Neatness", ids);
    }

    public void showDialog_dot_4(View view) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(R.id.social_16);
        ids.add(R.id.social_17);
        ids.add(R.id.social_18);
        ids.add(R.id.social_19);
        ids.add(R.id.social_20);
        createDialogForRange("Nature", ids);
    }


    public void showDialog_social_prof(View view) {
        List<String> professionList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.profession_list)));
        createDialogList("Profession", professionList, "profession",false, social_option_prof);
    }

    public void showDialog_social_religion(View view) {
       List<String> religionList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.religion_list)));
        createDialogList("Religion", religionList, "religion",false,social_option_religion);
    }

    public void showDialog_social_food(View view) {
        List<String> foodList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.food_list)));
        createDialogList("Food", foodList, "food",false,social_option_fav_food);
    }

    public void showDialog_social_music(View view) {
        List<String> musicList =new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.music_list)));
        createDialogList("Music", musicList, "music",false,social_option_music);
    }

    public void showDialog_social_sports(View view) {
        List<String> sportsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Sports_list)));
        createDialogList("Sports", sportsList, "sports",false,social_option_sports);
    }

    public void showDialog_social_going_out(View view) {
        List<String> goingList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Going_list)));
        createDialogList("Going Out", goingList, "going",false,social_option_going_out);
    }

    public void showDialog_social_travel(View view) {
        List<String> travelList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.travel_list)));
        createDialogList("Travel", travelList, "travel",false,social_option_travel);
    }

    public void showDialog_social_interests(View view) {
        List<String> interestsList =new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Interests_list)));
        createDialogList("Interests", interestsList, "interests",false,social_option_interests);
    }

    //social card ends here
    public void enable_edit(View view) {
        save_social_button.setVisibility(View.VISIBLE);
    }

    public void showDialog_own_words_edit(View view) {
        String title = "Your own Words";
        String error = "Words exceeded the given limit";
        int maxLength = 2500;
        createDialogEdit(title, error, maxLength);
        save_own_words_button.setVisibility(View.VISIBLE);

    }

    public void showDialog_about_me_edit(View view) {
        String title = "About Me";
        String error = "Words exceeded the given limit";
        int maxLength = 100;
        createDialogEdit(title, error, maxLength);

        save_about_button.setVisibility(View.VISIBLE);

    }


    public void socialSwitchCheck(SwitchMaterial switchMaterial) {
        if (!switchMaterial.isChecked()) {
            LinearLayout linearLayout = findViewById(R.id.social_card_view);
            linearLayout.setVisibility(View.GONE);

        } else {
            LinearLayout linearLayout = findViewById(R.id.social_card_view);

            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    //Create dialog for dots selection
    public void createDialogForRange(final String data_label, final ArrayList<Integer> ids) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.slider_social_dots_dialog, null);
        final Slider slider = view.findViewById(R.id.slider_social);
        new MaterialAlertDialogBuilder(this)
                .setTitle(data_label)
                .setView(view)
                .setPositiveButton("SAVE", (dialog, which) -> {
                    int value = (int) slider.getValue();
                    int save = ids.get(value);
                    Map<String, Object> data = new HashMap<>();
                    data.put(data_label, save);
                    Log.i("Slider_value", String.valueOf(value));
                    saveInfoDots(data,data_label);
                }).create()
        .show();

    }

    //Create dialog
    public void  createDialogList(String title, List<String> list, final String data_label, boolean singleSelection, final TextView textView) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_chip_group, null);

        chipGroup = view.findViewById(R.id.social_chip_group);
        setChipsSelection(list, chipGroup);

        chipGroup.setSingleSelection(singleSelection);

        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setView(view).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    checked_chips = getCheckedChips(chipGroup);
                    saveInfoList(checked_chips, data_label, textView);
                }
                catch (Exception e){
                    Log.i("Dialog_save_new", Objects.requireNonNull(e.getMessage()));
                }
            }
        }).create()
                .show();
    }

    public void createDialogEdit(String title, String error, int maxLength) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.input_string, null);
        TextInputLayout til_input = view.findViewById(R.id.til_email_reset);

        final TextInputEditText til_editText = view.findViewById(R.id.email_reset);
        til_input.setCounterMaxLength(maxLength);
        til_input.setCounterEnabled(true);
        til_input.setError(error);
        til_input.setErrorEnabled(false);
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = til_editText.getText().toString().trim();
                        Map<String, Object> data = new HashMap<>();
                        data.put("Bio", message);
                        saveInfoString(data,df_user);
                    }
                }).create()
                .show();
    }

    public void setChipsSelection(List<String> list, ChipGroup chipGroup) {
        for (String chips : list) {
                Chip mChip = (Chip) this.getLayoutInflater().inflate(R.layout.item_selection_chip, null, false);
                mChip.setText(chips);
                chipGroup.addView(mChip);
            }
    }
    public List<String> getCheckedChips(ChipGroup chipGroup) {
        List<String> chips_selected = new ArrayList<>();
        for (int i=0; i<chipGroup.getChildCount();i++){
            Chip chip = (Chip)chipGroup.getChildAt(i);
            if (chip.isChecked()){
                chips_selected.add(chip.getText().toString());
            }
        }
        return chips_selected;
    }

    //Save info
    public void saveInfoString(Map<String, Object> data, final DocumentReference documentReference) {
        documentReference.set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("data func", "edit Saved");
                query_maker(documentReference);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("data func", "edit not saved");
                    }
                });
    }

    public void saveInfoDots(Map<String, Object> data, final String data_label) {
        documentReference.set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("data func", "edit Saved");
                query_maker_sub_range_dots(documentReference,data_label);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("data func", "edit not saved");
                    }
                });
    }

    public void saveInfoList(List<String> data_list, final String label, final TextView textView) {

        try {
            Map<String, List<String>> data = new HashMap<>();
            data.put(label, data_list);
            documentReference.set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("ProfileChip", "Chips saved");
                    query_list_single(textView,label);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("ProfileChip", "Chips not saved");
                }
            });
        }
        catch (Exception e){
            Log.i("exception", Objects.requireNonNull(e.getMessage()));
        }
    }



    //general card starts here

    public void showDialog_general_language(View view) {
        List<String> languageList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_list)));
        try {
            createDialogList("Language", languageList, "languages",false,language);
        }
        catch(Exception e){
            Log.i("language", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void showDialog_general_looking(View view) {
        List<String> lookingList =new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.looking_for)));
        createDialogList("Looking For", lookingList, "looking",true,looking);
    }

    public void showDialog_general_smoker(View view) {
        List<String> smokerList =new ArrayList<>( Arrays.asList(getResources().getStringArray(R.array.Smoker_list)));
        createDialogList("Smoking", smokerList, "smoker",true,smoker);
    }

    public void showDialog_body_choose(View view) {
        List<String> bodyList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.body_list)));
        createDialogList("Body Type", bodyList, "bodyType",true,body);
    }

    public void showDialog_general_body_hair(View view) {
        List<String> bodyHairList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.body_hair_list)));
        createDialogList("Body Hair", bodyHairList, "bodyHair",true,body_hair);
    }

    public void showDialog_general_eyes_color(View view) {
        List<String> eyeColorList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.eyes_list)));
        createDialogList("Eye Color", eyeColorList, "eyeColor",true,eye_color);
    }

    public void showDialog_general_hair_color(View view) {
        List<String> HairColorList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hair_color_list)));
        createDialogList("Hair Color", HairColorList, "hairColor",true,hair_color);
    }

    public void showDialog_general_piercings(View view) {
        List<String> piercingsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Piercings_list)));
        createDialogList("Piercings", piercingsList, "piercings",true,piercings);
    }

    public void showDialog_general_tattoos(View view) {
       List<String> TattoosList =new ArrayList<>( Arrays.asList(getResources().getStringArray(R.array.tattoos_list)));
        createDialogList("Tattoos", TattoosList, "tattoos",true,tattoos);
    }

    public void enable_general_edit(View view) {
        save_general_button.setVisibility(View.VISIBLE);
    }
    //general card ends here

    //edit display picture upload function
    public void edit_profile_pic(View view) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        path = uid + "/profile_pic";
        Type = "image/*";
        getStoragePermission();
    }



    private void getStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openDirectory(Type);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_GRANTED);

        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_GRANTED) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDirectory(Type);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }


ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Uri uri = intent.getData();
                    upload(uri, path);
                }
            }
        });

    public void openDirectory(String type) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(type);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activityLauncher.launch(intent);
    }

    public void upload(Uri uri, final String path) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.pic_upload_dialog, null);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReferenceImg = storageReference.child(path);
        Log.i("uri", path);
        UploadTask uploadTask = storageReferenceImg.putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UserProfile.this, "Network unavailable",
                        Toast.LENGTH_SHORT).show();
                 findViewById(R.id.linearPb_dialog).setVisibility(View.INVISIBLE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserProfile.this, "Profile Picture uploaded",
                        Toast.LENGTH_SHORT).show();


                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference storageReferenceImg = storageReference.child(path);
                storageReferenceImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveDownloadLink(uri.toString());
                        saveToLocalDatabaseFromServer(System.currentTimeMillis());
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        });

    }

    //edit display picture upload function ends here

    //Save profile download url for glide
    private void saveDownloadLink(String download) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid().trim();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users");

        Map<String, Object> url = new HashMap<>();
        url.put("ProfileURL", download);

        if (uid != null) {
            collectionReference.document(uid).set(url, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("Save Url", "Saved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Save Url", "Not Saved");
                }
            });
        }
    }
    //Save profile download url for glide ends here

    public void save_about_me(View view) {
        save_about_button.setVisibility(View.GONE);
    }

    public void save_general(View view) {
        save_general_button.setVisibility(View.GONE);
    }

    public void save_own_words(View view) {
        save_own_words_button.setVisibility(View.GONE);
    }

    public void save_social(View view) {
        save_social_button.setVisibility(View.GONE);
    }

    public void header_init(DocumentReference df_user, final boolean header_selection){
        df_user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null){
                    ProfileInfoGrabber getter = documentSnapshot.toObject(ProfileInfoGrabber.class);
                    if(getter!=null){
                        String about_me = getter.getBio();
                        String username = getter.getusername();
                        int age = getter.getAge();
                        int gender = getter.getGender();
                        String dp = getter.getProfileURL();
                        feedDataToHeaderDisplayUnit(about_me,username,age,gender,dp,header_selection);
                    }
                }
            }
        });
    }

    private void feedDataToHeaderDisplayUnit(String about_me, String username, int age, int gender, String dp, boolean header_selection) {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.trans_symbol);
        list.add(R.drawable.male_symbol);
        list.add(R.drawable.female_symbol);
        TextView textView = findViewById(R.id.profile_bio);
        if(header_selection){
            textView.setText(about_me);
            header_username_display_user.setText(username);
            Glide.with(this)
                    .load(dp)
                    .circleCrop()
                    .into(header_profile_display_user);
            if(gender>0) {
                Glide.with(this)
                        .load(list.get(gender-1))
                        .into(header_gender_display_user);
            }
            String Age = age +"y";
            header_age_display_user.setText(Age);
        }
        else{
            textView.setText(about_me);
            header_username_display.setText(username);
            Glide.with(this).
                    load(dp)
                    .circleCrop()
                    .into(header_profile_display);
            if(gender>0) {
                Glide.with(this)
                        .load(list.get(gender - 1))
                        .into(header_gender_display);
            }
            String Age = age + "y";
            header_age_display.setText(Age);
        }
    }

    public void query_maker(DocumentReference df){

        clearSocialDots();
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User_Getter getter = documentSnapshot.toObject(User_Getter.class);
                    if (getter != null) {
                        int balance = getter.getBalance();
                        int activity = getter.getActivity();
                        int neatness = getter.getNeatness();
                        int nature = getter.getNature();
                        List<String> profession = getter.getProfession();
                        List<String> religion = getter.getReligion();
                        List<String> food = getter.getFood();
                        List<String> music = getter.getMusic();
                        List<String> sports = getter.getSports();
                        List<String> going = getter.getGoing();
                        List<String> travel = getter.getTravel();
                        List<String> interests = getter.getInterests();
                        List<String> languages = getter.getLanguages();
                        List<String> lookings = getter.getLooking();
                        List<String> smokers = getter.getSmoker();
                        List<String> bodyType = getter.getBodyType();
                        List<String> bodyHair = getter.getBodyHair();
                        List<String> eyeColor = getter.getEyeColor();
                        List<String> hairColor = getter.getHairColor();
                        List<String> piercing = getter.getPiercings();
                        List<String> tattoo = getter.getTattoos();
                        boolean social_switch = getter.isSocialSwitch();
                        boolean general_switch = getter.isGeneralSwitch();

                        feedingDataToDisplayUnits(balance,activity,neatness,nature,profession,religion,food,music,sports,going,travel,interests,languages,lookings,smokers,bodyType,bodyHair,eyeColor,hairColor,piercing,tattoo,general_switch,social_switch);

                    }
                    else{
                        DisplayForNone();
                    }
            }
        }
        });
    }

    private void feedingDataToDisplayUnits(int balance, int activity, int neatness, int nature,List<String> profession,List<String> religion,List<String> food,List<String> music,List<String> sports,List<String> going,List<String> travel,List<String> interests, List<String> languages,List<String> lookings,List<String> smokers,List<String> bodyType,List<String> bodyHair,List<String> eyeColor,List<String> hairColor,List<String> piercing,List<String> tattoo, boolean general_switch, boolean social_switch){
        String delimiter = ", ";
        //dots image view
        ImageView social_1 = findViewById(balance);
        ImageView social_2 = findViewById(activity);
        ImageView social_3 = findViewById(neatness);
        ImageView social_4 = findViewById(nature);
        List<ImageView> dots = new ArrayList<>();
        dots.add(social_1);
        dots.add(social_2);
        dots.add(social_3);
        dots.add(social_4);
        feedImageViewWithNewImage(dots);
        social_option_prof.setText(conversion(delimiter,profession));
        social_option_religion.setText(conversion(delimiter,religion));
        social_option_fav_food.setText(conversion(delimiter,food));
        social_option_music.setText(conversion(delimiter,music));
        social_option_sports.setText(conversion(delimiter,sports));
        social_option_going_out.setText(conversion(delimiter,going));
        social_option_travel.setText(conversion(delimiter,travel));
        social_option_interests.setText(conversion(delimiter,interests));
        language.setText(conversion(delimiter,languages));
        looking.setText(checkingForNone(lookings));
        smoker.setText(checkingForNone(smokers));
        body.setText(checkingForNone(bodyType));
        body_hair.setText(checkingForNone(bodyHair));
        eye_color.setText(checkingForNone(eyeColor));
        hair_color.setText(checkingForNone(hairColor));
        piercings.setText(checkingForNone(piercing));
        tattoos.setText(checkingForNone(tattoo));

        generalSwitchCheckCallback(general_switch);
        socialSwitchCheckCallback(social_switch);
    }

    private void socialSwitchCheckCallback(boolean switch_value) {
        SwitchMaterial switchMaterial = findViewById(R.id.social_switch);
        LinearLayout linearLayout = findViewById(R.id.social_card_view);
        if(switch_value){
            switchMaterial.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            switchMaterial.setChecked(false);
            linearLayout.setVisibility(View.GONE);
        }
    }

    private void generalSwitchCheckCallback(boolean switch_value) {
        SwitchMaterial switchMaterial = findViewById(R.id.general_switch);
        LinearLayout linearLayout = findViewById(R.id.general_card_view);
        if(switch_value){
            switchMaterial.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            switchMaterial.setChecked(false);
            linearLayout.setVisibility(View.GONE);
        }
    }

    private void feedImageViewWithNewImage(List<ImageView> dots) {
        for(ImageView dot : dots){
            if (dot!=null) {
                Glide.with(this)
                        .load(R.drawable.select_circle)
                        .into(dot);
            }
        }
    }

    public String conversion(String delimiter,List<String> list){
        if(list==null){
            return "None";
        }
        else if(list.size()==1){
            return list.get(0);
        }
        else{
            return TextUtils.join(delimiter,list);
        }
    }

    public String checkingForNone(List<String> string){
       if(string==null){
           return "None";
       }
       else{
           return  string.get(0);
       }
    }
    @SuppressLint("SetTextI18n")
    public void DisplayForNone(){
        clearSocialDots();
        social_option_prof.setText("None");
        social_option_religion.setText("None");
        social_option_fav_food.setText("None");
        social_option_music.setText("None");
        social_option_sports.setText("None");
        social_option_going_out.setText("None");
        social_option_travel.setText("None");
        social_option_interests.setText("None");
        language.setText("None");
        looking.setText("None");
        smoker.setText("None");
        body.setText("None");
        body_hair.setText("None");
        eye_color.setText("None");
        hair_color.setText("None");
        piercings.setText("None");
        tattoos.setText("None");
    }

    private void clearSocialDots(){
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(R.id.social_1);
        ids.add(R.id.social_2);
        ids.add(R.id.social_3);
        ids.add(R.id.social_4);
        ids.add(R.id.social_5);

        ids.add(R.id.social_6);
        ids.add(R.id.social_7);
        ids.add(R.id.social_8);
        ids.add(R.id.social_9);
        ids.add(R.id.social_10);

        ids.add(R.id.social_11);
        ids.add(R.id.social_12);
        ids.add(R.id.social_13);
        ids.add(R.id.social_14);
        ids.add(R.id.social_15);

        ids.add(R.id.social_16);
        ids.add(R.id.social_17);
        ids.add(R.id.social_18);
        ids.add(R.id.social_19);
        ids.add(R.id.social_20);

        for (int social : ids){
            ImageView img = findViewById(social);
            Glide.with(this)
                    .load(R.drawable.empty_circle)
                    .into(img);
        }
    }

    private void query_maker_sub_range_dots(DocumentReference documentReference, final String data_label) {
        final ArrayList<Integer> Balance = new ArrayList<>();
        Balance.add(R.id.social_1);
        Balance.add(R.id.social_2);
        Balance.add(R.id.social_3);
        Balance.add(R.id.social_4);
        Balance.add(R.id.social_5);
        final ArrayList<Integer> Activity = new ArrayList<>();
        Activity.add(R.id.social_6);
        Activity.add(R.id.social_7);
        Activity.add(R.id.social_8);
        Activity.add(R.id.social_9);
        Activity.add(R.id.social_10);
        final ArrayList<Integer> Neatness = new ArrayList<>();
        Neatness.add(R.id.social_11);
        Neatness.add(R.id.social_12);
        Neatness.add(R.id.social_13);
        Neatness.add(R.id.social_14);
        Neatness.add(R.id.social_15);
        final ArrayList<Integer> Nature = new ArrayList<>();
        Nature.add(R.id.social_16);
        Nature.add(R.id.social_17);
        Nature.add(R.id.social_18);
        Nature.add(R.id.social_19);
        Nature.add(R.id.social_20);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User_Getter getter = documentSnapshot.toObject(User_Getter.class);
                    int id = 0;
                    if (getter != null) {
                        if ("Balance".equals(data_label)) {
                            id = getter.getBalance();
                            for (int social : Balance) {
                                ImageView img = findViewById(social);
                                Glide.with(UserProfile.this)
                                        .load(R.drawable.empty_circle)
                                        .into(img);
                            }
                        } else if ("Activity".equals(data_label)) {
                            id = getter.getActivity();
                            for (int social : Activity) {
                                ImageView img = findViewById(social);
                                Glide.with(UserProfile.this)
                                        .load(R.drawable.empty_circle)
                                        .into(img);
                            }
                        } else if ("Neatness".equals(data_label)) {
                            id = getter.getNeatness();
                            for (int social : Neatness) {
                                ImageView img = findViewById(social);
                                Glide.with(UserProfile.this)
                                        .load(R.drawable.empty_circle)
                                        .into(img);
                            }
                        } else {
                            for (int social : Nature) {
                                id = getter.getNature();
                                ImageView img = findViewById(social);
                                Glide.with(UserProfile.this)
                                        .load(R.drawable.empty_circle)
                                        .into(img);
                            }
                        }
                        if (id != 0) {
                            ImageView imageView = findViewById(id);
                            Glide.with(UserProfile.this)
                                    .load(R.drawable.select_circle)
                                    .into(imageView);
                        }
                    }
                }
            }
        });
    }

    public void query_list_single(final TextView textView, final String data_label){
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get(data_label) != null) {
                    String data = Objects.requireNonNull(documentSnapshot.get(data_label)).toString().replace("[", " ").replace("]", " ").trim();
                    textView.setText(data);
                }
                else{
                    textView.setText("None");
                }
            }
        });
    }

    //pending picture save to local database from uri
    private void saveToLocalDatabaseFromServer(long timestamp) {
        String local_path = "Media/DDProfilePhotos";
        String name = "IMGPP"+ Tools.getDateInFormat(timestamp)+".jpeg";
        File rootPath = new File(this.getFilesDir().getAbsolutePath()+File.separator+"DreamDating/"+local_path);

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference = reference.child(path);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,name);
            storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(UserProfile.this, "Profile Picture changed", Toast.LENGTH_SHORT).show();
                dataContext.updateProfilePic(localFile.toString());
            }).addOnFailureListener(e -> {
                Toast.makeText(UserProfile.this, "Network interrupted", Toast.LENGTH_SHORT).show();
                Log.i("UserProfile", Objects.requireNonNull(e.getMessage()));
            });

    }

}