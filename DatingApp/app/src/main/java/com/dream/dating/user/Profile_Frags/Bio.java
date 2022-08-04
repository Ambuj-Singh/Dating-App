package com.dream.dating.user.Profile_Frags;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dream.dating.R;
import com.dream.dating.user.ui.UserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Bio extends Fragment {
    protected FirebaseUser user;
    private String uid;
    protected static boolean REQUEST_ACCEPTED = true, REQUEST_DENIED = false;

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
        return inflater.inflate(R.layout.fragment_bio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText bio = view.findViewById(R.id.about_me);
        final TextInputLayout til_bio = view.findViewById(R.id.til_about_me);
        FloatingActionButton Saver = view.findViewById(R.id.bio_saver);
        ProgressBar progressBar = view.findViewById(R.id.aboutProgress);
        progressBar.setVisibility(View.INVISIBLE);
        Saver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = db.collection("users");
                Saver.setEnabled(false);
                Saver.setBackgroundColor(getResources().getColor(R.color.gray,null));

                String Bio = bio.getText().toString().trim();
                if (checkValidity(til_bio)) {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.i("message", Bio);
                    Map<String, Object> data = new HashMap<>();
                    data.put("Bio", Bio);
                    data.put("Welcome", true);
                    data.put("WelcomeStage","complete");
                    collectionReference.document(uid).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("Saved Bio", "Saved");
                            //adding ProfileMaker flag
                            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            Log.i("BIO","saved");

                            //Moving to user Activity
                            Intent i = new Intent(getActivity(),UserActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Saver.setEnabled(true);
                                    Saver.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                                    Log.i("BIO","Not saved");
                                }
                            });
                }
                else{
                    Saver.setEnabled(true);
                    Saver.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                    progressBar.setVisibility(View.GONE);
                    Log.i("TIL","ErrorIsEnabled");
                }
            }
        });

    }

    protected boolean checkValidity(TextInputLayout bio) {

        if (bio.isErrorEnabled()) {
            bio.setError("Only 100 characters are allowed");
            return REQUEST_DENIED;
        }
        bio.setErrorEnabled(false);
        return REQUEST_ACCEPTED;

    }
}