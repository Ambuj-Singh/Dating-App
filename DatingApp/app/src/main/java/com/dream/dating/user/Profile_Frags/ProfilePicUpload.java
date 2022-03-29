package com.dream.dating.user.Profile_Frags;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.dream.dating.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfilePicUpload extends Fragment {

    protected FirebaseUser user;
    private static final int PERMISSION_STORAGE_GRANTED = 1;
    private String path;
    private ImageView dp;
    private Button next2;
    private ProgressBar progressBar;
    private ImageView check;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        path = uid + "/profile_pic";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_picupload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.linearPb);
        progressBar.setVisibility(View.INVISIBLE);

        next2 = view.findViewById(R.id.next2);

        final Button upload2 = view.findViewById(R.id.upload2);
        dp = view.findViewById(R.id.displayPic);
        check = view.findViewById(R.id.checked);
        check.setVisibility(View.INVISIBLE);
        upload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload2.setVisibility(View.INVISIBLE);
                next2.setEnabled(false);
                next2.setTextColor(getResources().getColor(R.color.gray,null));
                getStoragePermission();
            }
        });


        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment Bio = new Bio();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.placeholder,Bio);
                fragmentTransaction.commit();
            }
        });

    }

    private void getStoragePermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openDirectory();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_GRANTED);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE_GRANTED) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDirectory();
            }
        }

    }

    public void openDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, PERMISSION_STORAGE_GRANTED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        getActivity();
        if (requestCode == PERMISSION_STORAGE_GRANTED && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                upload(uri, path);
            }
        }
    }

    private void upload(Uri uri, final String path) {
        try {
           Glide.with(getContext())
                   .load(new File(uri.getPath()))
                   .into(dp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReferenceImg = storageReference.child(path);
        Log.i("uri", path);
        UploadTask uploadTask = storageReferenceImg.putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Network unavailable",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Profile Picture uploaded",
                        Toast.LENGTH_SHORT).show();


                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference storageReferenceImg = storageReference.child(path);
                storageReferenceImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                      saveDownloadLink(uri.toString());
                      next2.setEnabled(true);
                      next2.setTextColor(getResources().getColor(R.color.BlueTone,null));

                      progressBar.setVisibility(View.INVISIBLE);
                      setProfilePic(uri.toString());
                      check.setVisibility(View.VISIBLE);
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Toast.makeText(getContext(), "Network Interrupted", Toast.LENGTH_SHORT).show();
                  }
              });


            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    //Save profile download url for glide
    private void saveDownloadLink(String download){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users");

        Map<String,Object> url = new HashMap<>();
        url.put("ProfileURL",download);

        collectionReference.document(uid).update(url).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Save Url","Saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Save Url","Not Saved");
            }
        });
    }

    private void setProfilePic(String path) {

        Glide.with(getContext())
                .load(path)
                .into(dp);
    }

}