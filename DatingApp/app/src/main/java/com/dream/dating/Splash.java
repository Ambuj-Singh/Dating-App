package com.dream.dating;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dream.dating.user.ProfileMaker;
import com.dream.dating.user.UserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Splash extends AppCompatActivity {
    protected static int SPLASH_SCREEN_TIME_OUT = 2000;
    protected FirebaseAuth mAuth;
    protected FirebaseUser currentUser;
    protected boolean flag;
    protected DocumentReference df;
    protected CollectionReference collectionReference;

    protected ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");

        if(currentUser!=null){
            currentUser.reload();
            df = collectionReference.document(currentUser.getUid());
        }


        logo = findViewById(R.id.logo_view);

        Glide.with(this)
                .load(R.raw.logo_anime_2)
                .into(logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (df != null) {
                    getWelcomeFlag(df, new WelcomeUser() {
                        @Override
                        public void CallBack(boolean value) {
                            //User welcome
                            if (currentUser == null && !value) {
                                Intent i = new Intent(Splash.this, Login.class);
                                startActivity(i);
                                finish();
                            } else if (currentUser != null && value) {
                                Intent i = new Intent(Splash.this, UserActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(Splash.this, ProfileMaker.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                } else {
                    Intent i = new Intent(Splash.this, Login.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    private interface WelcomeUser {
        void CallBack(boolean value);
    }

    public void getWelcomeFlag(DocumentReference df, final WelcomeUser callback) {
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getBoolean("Welcome") != null)
                    flag = documentSnapshot.getBoolean("Welcome") != null;
                else
                    flag = false;
                callback.CallBack(flag);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("network", "Interrupted");
            }
        });
    }

}