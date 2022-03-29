package com.dream.dating.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dream.dating.R;
import com.dream.dating.user.Profile_Frags.Welcome;

public class ProfileMaker extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_maker);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        //Fragment transaction check
        Fragment welcome = new Welcome();
        fragmentTransaction.replace(R.id.placeholder,welcome);
        fragmentTransaction.commit();

    }

}