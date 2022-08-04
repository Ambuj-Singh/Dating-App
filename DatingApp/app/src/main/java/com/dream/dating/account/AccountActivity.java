package com.dream.dating.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.dream.dating.ProfileInfoGrabber;
import com.dream.dating.R;
import com.dream.dating.Services.DataContext;
import com.dream.dating.Status_Offline;
import com.dream.dating.Status_Online;
import com.dream.dating.user.ui.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AccountActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private OneTimeWorkRequest oneTimeWorkRequestA, oneTimeWorkRequestB;
    private ImageView profile_image;
    private TextView username;
    private TextView name;
    private DocumentReference df_user;
    private DataContext datacontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String UserUID = FirebaseAuth.getInstance().getUid();
        df_user = firebaseFirestore.collection("users").document(UserUID);
        profile_image = findViewById(R.id.imageView_profile_pic);
        username = findViewById(R.id.username_nav_view);
        name = findViewById(R.id.name_nav_view);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.create();
        progressDialog.show();
        ProgressBar progressBar = progressDialog.findViewById(android.R.id.progress);
        progressBar.getIndeterminateDrawable().setTint(Color.rgb(98,0,238));

        datacontext = new DataContext(this);
        String usernameText = "@"+datacontext.getUsername();
        String nameText = datacontext.getName();
        username.setText(usernameText);
        name.setText(nameText);

        fetchUserData(df_user, new waitTillResults() {
            @Override
            public void Callback(ProfileInfoGrabber grabber) {

                Glide.with(getApplicationContext())
                        .load(grabber.getProfileURL())
                        .circleCrop()
                        .into(profile_image);
            }
        });

    }


    private interface waitTillResults{
        void Callback(ProfileInfoGrabber grabber);
    }

    private void fetchUserData(DocumentReference df_user, final waitTillResults onCallback) {
        df_user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    ProfileInfoGrabber getter = documentSnapshot.toObject(ProfileInfoGrabber.class);
                    if (getter != null) {
                        onCallback.Callback(getter);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(oneTimeWorkRequestA!=null) {
            UUID getID = oneTimeWorkRequestA.getId();
            WorkManager.getInstance(getApplicationContext()).cancelWorkById(getID);
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        oneTimeWorkRequestB = new OneTimeWorkRequest.Builder(Status_Online.class)
                .setConstraints(constraints)
                .addTag("Status_Update")
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequestB);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(oneTimeWorkRequestB!=null) {
            UUID getID = oneTimeWorkRequestB.getId();
            WorkManager.getInstance(getApplicationContext()).cancelWorkById(getID);
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        oneTimeWorkRequestA = new OneTimeWorkRequest.Builder(Status_Offline.class)
                .setConstraints(constraints)
                .addTag("Status_Update")
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequestA);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(oneTimeWorkRequestA!=null) {
            UUID getID = oneTimeWorkRequestA.getId();
            WorkManager.getInstance(getApplicationContext()).cancelWorkById(getID);
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        oneTimeWorkRequestB = new OneTimeWorkRequest.Builder(Status_Online.class)
                .setConstraints(constraints)
                .addTag("Status_Update")
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequestB);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(oneTimeWorkRequestB!=null) {
            UUID getID = oneTimeWorkRequestB.getId();
            WorkManager.getInstance(getApplicationContext()).cancelWorkById(getID);
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        oneTimeWorkRequestA = new OneTimeWorkRequest.Builder(Status_Offline.class)
                .setConstraints(constraints)
                .addTag("Status_Update")
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequestA);
    }


    public void jumpToUserProfile(View view) {
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
    }
}