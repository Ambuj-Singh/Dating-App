package com.dream.dating.user.Profile_Frags;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dream.dating.R;
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

public class NameFrg extends Fragment {

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
        return inflater.inflate(R.layout.fragment_name_frg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText firstname = view.findViewById(R.id.first_name);
        final EditText lastname = view.findViewById(R.id.last_name);
        final TextInputLayout til_f_name = view.findViewById(R.id.til_first_name);
        final TextInputLayout til_l_name = view.findViewById(R.id.til_last_name);
        FloatingActionButton Saver = view.findViewById(R.id.name_saver);
        ProgressBar progressBar = view.findViewById(R.id.nameProgress);
        progressBar.setVisibility(View.INVISIBLE);
        Saver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = db.collection("users");
                Saver.setEnabled(false);
                Saver.setBackgroundColor(getResources().getColor(R.color.gray,null));
                String fName = firstname.getText().toString().trim();
                String lName = lastname.getText().toString().trim();
                String FullName = fName + " " + lName;
                if (checkValidity(FullName,til_f_name,til_l_name, fName)) {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.i("message", FullName);
                    Map<String, Object> data = new HashMap<>();
                    data.put("FullName", FullName);
                    data.put("WelcomeStage","name");
                    collectionReference.document(uid).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("Saved FullName", "Saved");
                                    //adding ProfileMaker flag
                                    Log.i("Welcome","Saved");
                                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                                    Fragment gender = new GenderSelection();
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.placeholder,gender);
                                    fragmentTransaction.commit();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Saver.setEnabled(true);
                                    Saver.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                                    Log.i("Saving", "FullName cancelled");
                                }
                            });
                }
                else{
                    Saver.setEnabled(true);
                    Saver.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.i("TIL","ErrorIsEnabled");
                }
            }
        });

    }

    protected boolean checkValidity(String name, TextInputLayout til_f_name, TextInputLayout til_l_name, String fname) {
    String space = " ";
    String error = "Name can not be left empty";
    String error_f_name = "First name is necessary";
    if(fname.isEmpty()){
        til_f_name.setError(error_f_name);
        til_f_name.setErrorEnabled(true);
        return REQUEST_DENIED;
    }
        if (name.equals(space)) {
            til_f_name.setError(error);
            til_f_name.setErrorEnabled(true);
            til_l_name.setError(error);
            til_l_name.setErrorEnabled(true);
            return REQUEST_DENIED;
        }
        til_f_name.setErrorEnabled(false);
        til_l_name.setErrorEnabled(false);
        return REQUEST_ACCEPTED;

    }
}