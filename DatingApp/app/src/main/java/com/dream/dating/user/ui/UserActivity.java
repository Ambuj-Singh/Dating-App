package com.dream.dating.user.ui;

import static org.threeten.bp.Period.between;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.dream.dating.R;
import com.dream.dating.Status_Offline;
import com.dream.dating.Status_Online;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserActivity extends AppCompatActivity {

    private LocalDate date;
    private static final String BACK_STACK_ROOT_TAG = "root_fragment", FAV="fragment_fav",
    VIS = "fragment_visited",PRO="fragment_profiles",SEA="fragment_search";
    private OneTimeWorkRequest oneTimeWorkRequestA, oneTimeWorkRequestB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_user);
         BottomNavigationView navView = findViewById(R.id.nav_view);
        //saving latest age
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        saveLatestAge(db);

        navView.setSelectedItemId(R.id.navigation_chat);
        FragmentManager fm_ini = getSupportFragmentManager();
        FragmentTransaction ft_ini = fm_ini.beginTransaction();
        ChatListFrag Chat = new ChatListFrag();
        ft_ini.replace(R.id.nav_host_fragment, Chat, BACK_STACK_ROOT_TAG);
        ft_ini.commit();

        //bottom navigation
        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.navigation_profiles:
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Profiles profiles = new Profiles();
                    ft.replace(R.id.nav_host_fragment, profiles, PRO);
                    ft.commit();

                    break;
                case R.id.navigation_visited:
                    FragmentManager fm_v = getSupportFragmentManager();
                    FragmentTransaction ft_v = fm_v.beginTransaction();
                    Visited visited = new Visited();
                    ft_v.replace(R.id.nav_host_fragment, visited,VIS);
                    ft_v.commit();
                    break;
                case R.id.navigation_chat:
                    FragmentManager fm_c = getSupportFragmentManager();
                    FragmentTransaction ft_c = fm_c.beginTransaction();
                    ChatListFrag Chat1 = new ChatListFrag();
                    ft_c.replace(R.id.nav_host_fragment, Chat1, BACK_STACK_ROOT_TAG);
                    ft_c.commit();
                    break;
                case R.id.navigation_favourites:
                    FragmentManager fm_f = getSupportFragmentManager();
                    FragmentTransaction ft_f = fm_f.beginTransaction();
                    Favourite fav = new Favourite();
                    ft_f.replace(R.id.nav_host_fragment, fav,FAV);
                    ft_f.commit();
                    break;

                case R.id.navigation_search:
                    FragmentManager fm_s = getSupportFragmentManager();
                    FragmentTransaction ft_s = fm_s.beginTransaction();
                    Search sea = new Search();
                    ft_s.replace(R.id.nav_host_fragment, sea,SEA);
                    ft_s.commit();
                    break;
            }
            return true;
        });
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {

        BottomNavigationView navView = findViewById(R.id.nav_view);
        Fragment Chat1 = getSupportFragmentManager().findFragmentByTag(BACK_STACK_ROOT_TAG);

        if(Chat1 != null && Chat1.isVisible() && (navView.getSelectedItemId()==R.id.navigation_chat)){
            Log.i("Alert","Working");
            MaterialAlertDialogBuilder exit = new MaterialAlertDialogBuilder(UserActivity.this);
                    exit.setTitle("Exit")
                    .setMessage("Are you sure you wanna go?")
                    .setPositiveButton("EXIT", (dialog, which) -> finish())
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel()).create().show();
        }
       else{
            Log.i("Alert","Working1");
            FragmentManager fm_ini = getSupportFragmentManager();
            FragmentTransaction ft_ini = fm_ini.beginTransaction();
            ChatListFrag Chat = new ChatListFrag();
            ft_ini.replace(R.id.nav_host_fragment, Chat, BACK_STACK_ROOT_TAG);
            ft_ini.commit();
            navView.setSelectedItemId(R.id.navigation_chat);

        }
    }


    //system region based current date pickup
    private LocalDate Today() {
        return LocalDate.now(ZoneId.systemDefault());
    }

    //updating Age everyday
    private void saveLatestAge(FirebaseFirestore db) {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final LocalDate today = Today();
        CollectionReference cr = db.collection("users");
        assert currentUser != null;

        DocumentReference df = cr.document(currentUser.getUid());
        getDateOfBirth(df, (date, username) -> {

            SharedPreferences sharedPreferences = UserActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username",username);
            editor.putString("uid",currentUser.getUid());
            editor.apply();

            int age = between(date, today).getYears();
            Map<String, Object> Age = new HashMap<>();
            Age.put("Age", age);
            Age.put("UserStatus",true);
            FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            CollectionReference cr1 = db1.collection("users");
            assert currentUser1 != null;
            DocumentReference df1 = cr1.document(currentUser1.getUid());
            df1.update(Age).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("Age", "Saved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Age", "Not Saved");
                }
            });
        });

    }

    //interface for getting DOB
    private interface gettingDate {
        void onCallback(LocalDate date, String username);
    }

    //Date Of Birth fetching from server
    private void getDateOfBirth(DocumentReference df, final gettingDate callback) {
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                date = LocalDate.parse(Objects.requireNonNull(documentSnapshot.getString("DOB")));
                String username = documentSnapshot.getString("username");
                callback.onCallback(date,username);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Network", "interrupted");
            }
        });
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
        if (oneTimeWorkRequestB != null) {
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

}