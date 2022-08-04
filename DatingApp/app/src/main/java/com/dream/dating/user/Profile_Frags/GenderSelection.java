package com.dream.dating.user.Profile_Frags;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dream.dating.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GenderSelection extends Fragment {


    public GenderSelection() {
        // Required empty public constructor
    }
    protected FirebaseUser user;
    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        uid = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gender_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton next = view.findViewById(R.id.gender_saver);
        RadioGroup radioGroup = view.findViewById(R.id.genderRadioGroup);
        ProgressBar progressBar = view.findViewById(R.id.genderProgress);
        progressBar.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                next.setEnabled(false);
                next.setBackgroundColor(getResources().getColor(R.color.gray,null));
                int gender;
                int checked = radioGroup.getCheckedRadioButtonId();
                if(checked == R.id.maleRadio)
                    gender = 1;
                else if (checked == R.id.femaleRadio)
                    gender = 2;
                else if (checked == R.id.transRadio)
                    gender = 3;
                else
                    gender = 4;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = db.collection("users");

                if (radioGroup.getCheckedRadioButtonId() != -1)
                {
                    Log.i("gender", String.valueOf(gender));
                    Map<String, Object> data = new HashMap<>();
                    data.put("gender",gender);
                    data.put("WelcomeStage","gender");
                    collectionReference.document(uid).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                            Log.i("Saved gender", "Saved");
                            Fragment profile_pic = new ProfilePicUpload();
                            FragmentManager fragmentManager = getParentFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.placeholder,profile_pic);
                            fragmentTransaction.commit();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Saved gender", "gender cancelled");
                            progressBar.setVisibility(View.GONE);
                            next.setEnabled(true);
                            next.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                            Toast.makeText(getContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    next.setEnabled(true);
                    next.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}