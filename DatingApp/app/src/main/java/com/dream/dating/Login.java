package com.dream.dating;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dream.dating.Services.DataContext;
import com.dream.dating.user.ProfileMaker;
import com.dream.dating.user.ui.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends AppCompatActivity {

    private Button signUp,login;
    private EditText email,password;
    private TextView forgot_password;
    private FirebaseAuth mAuth;
    private String email_i,password_i;
    private TextInputLayout til_email_lg, til_password_lg;
    private ProgressBar pb;
    private static boolean REQUEST_DENIED = false, REQUEST_ACCEPTED = true;
    private boolean flag;
    private String username;
    private DocumentReference df;
    private CollectionReference collectionReference;
    private DataContext dataContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dataContext = new DataContext(this);
        signUp = findViewById(R.id.sign_up);
        login = findViewById(R.id.login);
        email = findViewById(R.id.l_email);
        password = findViewById(R.id.l_pass);

        forgot_password = findViewById(R.id.forgotten_pass);
        forgot_password.setVisibility(View.GONE);
        //TextInputLayout
        til_email_lg = findViewById(R.id.til_email_login);
        til_password_lg = findViewById(R.id.til_password_login);



        mAuth = FirebaseAuth.getInstance();
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.GONE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,SignUp.class);
                startActivity(i);
                finish();
            }
        });

        //login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                login.setVisibility(View.INVISIBLE);
                email_i = email.getText().toString().trim();
                password_i = password.getText().toString().trim();

                if(validation()){
                    mAuth.signInWithEmailAndPassword(email_i,password_i).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                createFolderStructure();
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(Login.this, "Logged In",
                                        Toast.LENGTH_SHORT).show();
                                //Checking for welcome flag
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                collectionReference = db.collection("users");
                                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert currentUser != null;

                                df = collectionReference.document(currentUser.getUid());
                                String uidUser = currentUser.getUid();

                                getWelcomeFlag(df, (value, value2) -> {

                                    saveToLocalDB(uidUser,username);
                                    if(value) {

                                        Intent i = new Intent(Login.this, UserActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {
                                        Map<String, Object> uid = new HashMap<>();
                                        uid.put("UID",currentUser.getUid());
                                        uid.put("UserStatus",true);
                                        df.update(uid).addOnSuccessListener(aVoid -> {
                                            Log.i("Uid","Added");
                                            Intent i = new Intent(Login.this, ProfileMaker.class);
                                            startActivity(i);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Snackbar.make(login,"Check your internet connection", Snackbar.LENGTH_SHORT)
                                                .show());

                                    }
                                });
                            } else {
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Invalid email or password",
                                        Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                                login.setVisibility(View.VISIBLE);
                                forgot_password.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                }
                else{
                    Toast.makeText(Login.this, "Please fill all the fields",
                            Toast.LENGTH_SHORT).show();

                    pb.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                }

            }
        });

        //forgot password reset dialog
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditText import
                LayoutInflater layout = LayoutInflater.from(Login.this);
                View view = layout.inflate(R.layout.input,null);
                final TextInputEditText input = view.findViewById(R.id.email_reset);
                new MaterialAlertDialogBuilder(Login.this)
                .setTitle("Reset Password")
                .setView(view)
                .setMessage("Enter your registered email. A reset password " +
                        "link will be sent to your registered email.")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                        progressDialog.setMessage("Sending...");
                        progressDialog.create();
                        progressDialog.show();
                        ProgressBar progressBar =  progressDialog.findViewById(android.R.id.progress);
                        progressBar.getIndeterminateDrawable().setTint(getResources().getColor(R.color.colorAccent,null));
                        String email_reset;
                        email_reset = Objects.requireNonNull(input.getText()).toString().trim();
                        //Checking for User input for null value
                        if (!email_reset.isEmpty()) {
                            mAuth.sendPasswordResetEmail(email_reset).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                progressDialog.cancel();
                                    Toast.makeText(Login.this, "Email has been sent. Check your inbox.", Toast.LENGTH_LONG)
                                            .show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.cancel();
                                            Toast.makeText(Login.this, "Email invalid or check your network.", Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });
                        }
                        else{
                            progressDialog.cancel();
                            Toast.makeText(Login.this,"Invalid Email",Toast.LENGTH_SHORT)
                            .show();
                        }
                    }
                }).create()
                .show();

            }
        });
    }

    private void saveToLocalDB(String uid, String username) {
        SQLiteDatabase sqLiteDatabase = dataContext.getWritableDatabase();
        String insert = "insert into username(username, uid) values('"+username+"', '"+uid+"');";
        sqLiteDatabase.execSQL(insert);
    }

    private void createFolderStructure() {

        ArrayList<String> folder = new ArrayList<>();
        folder.add("Backups");
        folder.add("Databases");
        folder.add("Media");
        folder.add("Media/Wallpaper");
        folder.add("Media/DDAnimatedGifs");
        folder.add("Media/DDAudio");
        folder.add("Media/DDocuments");
        folder.add("Media/DDImages");
        folder.add("Media/DDProfilePhotos");
        folder.add("Media/DDStickers");
        folder.add("Media/DDVideo");
        folder.add("Media/DDVoiceNotes");

        for(String path : folder){
           File Folder = new File(this.getFilesDir().getAbsolutePath()+File.separator+"DreamDating/"+path);
            if(!Folder.exists()){
                Folder.mkdirs();
            }
        }
    }

    //validation
    private boolean validation() {
        reset_error();
        if (email_i.isEmpty()) {
            til_email_lg.setError("Invalid Email");
            if (password_i.isEmpty()) {
                til_password_lg.setError("Invalid Password");
                return REQUEST_DENIED;
            }
            return REQUEST_DENIED;
        } else if (password_i.isEmpty()) {
            til_password_lg.setError("Invalid Password");
            return REQUEST_DENIED;
        } else {
            return REQUEST_ACCEPTED;
        }
    }

    private void reset_error(){
        til_email_lg.setError(null);
        til_password_lg.setError(null);
    }
    private interface WelcomeUser {
        void CallBack(boolean value, String username);
    }

    public void getWelcomeFlag(DocumentReference df, final WelcomeUser callback) {
        df.get().addOnSuccessListener(documentSnapshot -> {
            try {
                username = documentSnapshot.getString("username");
                if (documentSnapshot.getBoolean("Welcome") != null)
                    flag = Boolean.TRUE.equals(documentSnapshot.getBoolean("Welcome"));
                 else
                    flag = false;

                callback.CallBack(flag,username);
            }
            catch (Exception e){
                Log.i("network", "Interrupted");
            }
        }).addOnFailureListener(e -> Log.i("network", "Interrupted"));
    }



}