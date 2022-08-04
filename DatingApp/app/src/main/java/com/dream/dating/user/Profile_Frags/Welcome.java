package com.dream.dating.user.Profile_Frags;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dream.dating.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class Welcome extends Fragment {

    private Fragment fragment;
    private String welcome;
    private DocumentReference df;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users");
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        df = collectionReference.document(currentUser.getUid());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);

        ProgressBar progressBar = view.findViewById(R.id.welcomeProgress);
        FloatingActionButton next1 = view.findViewById(R.id.next1);
        next1.setEnabled(false);
        next1.setBackgroundColor(getResources().getColor(R.color.gray,null));
        progressBar.setVisibility(View.VISIBLE);
        getWelcomeFlag(df, new WelcomeUser() {
            @Override
            public void CallBack(String welcome) {
                switch (welcome){
                    case "name":
                        fragment = new GenderSelection();
                        next1.setEnabled(true);
                        next1.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                        progressBar.setVisibility(View.GONE);
                        break;
                    case "gender":
                        fragment = new ProfilePicUpload();
                        next1.setEnabled(true);
                        next1.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                        progressBar.setVisibility(View.GONE);
                        break;
                    case "dp":
                        fragment = new Bio();
                        next1.setEnabled(true);
                        next1.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                        progressBar.setVisibility(View.GONE);
                        break;
                    default:
                        fragment = new NameFrg();
                        next1.setEnabled(true);
                        next1.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                        progressBar.setVisibility(View.GONE);
                        break;
                }

            }
        });
        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.placeholder,fragment);
                fragmentTransaction.commit();
            }
        });
    }

    private interface WelcomeUser {
        void CallBack(String welcome);
    }

    public void getWelcomeFlag(DocumentReference df, final WelcomeUser callback) {
        df.get().addOnSuccessListener(documentSnapshot -> {
            try {
                welcome = documentSnapshot.getString("WelcomeStage");
                callback.CallBack(welcome);
            }
            catch (Exception e){
                Log.i("network", "Interrupted");
                Toast.makeText(getContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Log.i("network", "Interrupted"));
    }
}