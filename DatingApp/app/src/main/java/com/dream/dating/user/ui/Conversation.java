package com.dream.dating.user.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dream.dating.Encryption;
import com.dream.dating.ProfileInfoGrabber;
import com.dream.dating.R;
import com.dream.dating.user.UserModel.MessageModel;
import com.dream.dating.user.UserModel.ReceiverModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;


public class  Conversation extends AppCompatActivity {

    private RecyclerView recyclerView;
    private boolean circularReveal = true;
    private View attachments_layout;
    private ImageButton files, camera, gallery, audio, location, contact;
    private ArrayList<String> receiverDetails;
    private Realm realm;
    private long id;
    private Timestamp timestamp;
    private CollectionReference collection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        Realm.init(this);
        attachments_layout = findViewById(R.id.attachments_layout);
        //initializing imageButtons
        initializeAttachments(files, camera, gallery, audio, location, contact);

        String receiver = getIntent().getStringExtra("uid");
        collection = enablePersistence().collection("users");

        String filename = "DreamDating/DD Database";
        String path = this.getFilesDir().getAbsolutePath() + "/" + filename;


        //username
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.create();
        progressDialog.show();
        ProgressBar progressBar = progressDialog.findViewById(android.R.id.progress);
        progressBar.getIndeterminateDrawable().setTint(Color.rgb(98, 0, 238));
        getUsername(collection, receiver, result -> {
            receiverDetails = result;
            progressDialog.dismiss();
            String receiver_username = receiverDetails.get(0);
            //Realm Configuration
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .modules(new BaseModel())
                    .name("DDChat.realm")
                    .directory(new File(path))
                    .build();

            if (!new File(Realm.getDefaultConfiguration().getPath()).exists()) {
                realm = InitializeDatabase(config);
            } else {
                realm = Realm.getDefaultInstance();
            }

        });

        //send message
        FloatingActionButton sendMessage = findViewById(R.id.send_message);
        sendMessage.setOnClickListener(v -> {
            EditText message = findViewById(R.id.input_message_user);
            String input_message = message.getText().toString().trim();

            if (!(input_message.isEmpty())) {
                Date sentDate = new Date();
                timestamp = new Timestamp(sentDate);
                createIdForNewMessage(realm, receiverDetails.get(0), input_message, receiverDetails.get(2), sentDate, "text", "wait");
            } else {
                Toast.makeText(this, "Please type in a message", Toast.LENGTH_SHORT).show();
            }
        });
        //attachments
        ImageView attachments = findViewById(R.id.display_attachment);
        attachments.setOnClickListener(v -> {
            if (circularReveal)
                exitReveal();
            else
                enterReveal();
        });
    }

    public FirebaseFirestore enablePersistence() {
        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
        return db;
    }


    private void getUsername(CollectionReference collectionReference, String receiver, final getResult value) {
        ArrayList<String> result = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        collectionReference.document(receiver).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {
                ProfileInfoGrabber getter = documentSnapshot.toObject(ProfileInfoGrabber.class);
                if (getter != null) {
                    String username = getter.getusername();
                    String dp = getter.getProfileURL();
                    String uid_receiver = getter.getUID();
                    //receiver username at [0]
                    result.add(username);
                    //receiver dp_url at[1]
                    result.add(dp);
                    collectionReference.document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot != null) {
                                ProfileInfoGrabber getter = documentSnapshot.toObject(ProfileInfoGrabber.class);
                                if (getter != null) {
                                    //sender username at [2]
                                    String username = getter.getusername();
                                    result.add(username);
                                    value.Callback(result);
                                }
                            }
                        }
                    });
                    //receiver_uid at [3]
                    result.add(uid_receiver);
                    //sender_uid at [4]
                    result.add(uid);
                }
            }
        });
    }

    private void createIdForNewMessage(Realm database, String to, String input_message, String from, Date date, String messageType, String messageStatus) {
        database.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ReceiverModel receiverModel = realm.where(ReceiverModel.class).equalTo("id", to).findFirst();
                if (receiverModel != null) {
                    RealmList<MessageModel> messageModel = receiverModel.getMessages();
                    if (!messageModel.isEmpty()) {
                        id = messageModel.max("id").longValue() + 1;
                    } else {
                        id = 1;
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                saveToLocalDatabase(database, to, input_message, from, date, messageType, "wait", id);
            }

        });

    }


    private void saveToLocalDatabase(Realm database, String to, String input_message, String from, Date date, String messageType, String sentStatus, long id) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String sentTime = dateFormat.format(date).toLowerCase();

        String messageServer = id+","+messageType+","+from+","+to+","+timestamp+","+"null"+","+sentStatus+","+input_message+","+sentTime;

        RealmList<MessageModel> messagesList = new RealmList<>();
        MessageModel messageModel = new MessageModel();
        messageModel.setId(id);
        messageModel.setMessageType(messageType);
        messageModel.setFrom(from);
        messageModel.setTo(to);
        messageModel.setDate(date);
        messageModel.setMediaLink(null);
        messageModel.setMessageStatus(sentStatus);
        messageModel.setMessage(input_message);
        messageModel.setSentTime(sentTime);

        //adding message to the realmList
        messagesList.add(messageModel);
        ReceiverModel receiverModel = new ReceiverModel();
        receiverModel.setId(receiverDetails.get(0));
        receiverModel.setMessages(messagesList);

        database.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(receiverModel);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
              String encryptedText =  new Encryption().encrypt(messageServer,from+"_"+to+"_");
                System.out.println(from+"_"+to+"_");
              sendToServer(encryptedText,id);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.i("Realm",error.getMessage());
            }
        });

    }

    private void sendToServer(String encryptedText, long id){

        DocumentReference df = collection.document(receiverDetails.get(0)+"_"+receiverDetails.get(2));

        Map<String,ArrayList<String>> listMap = new HashMap<>();
        ArrayList<String> messages = new ArrayList<>();
        messages.add(encryptedText);
        listMap.put("messages",messages);
        df.set(listMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                statusUpdater(id);
                Toast.makeText(Conversation.this, "Message Sent : "+id, Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Conversation.this, "Network Error : "+id, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void statusUpdater(long id) {

    }

    private void initializeAttachments(ImageButton files, ImageButton camera, ImageButton gallery, ImageButton audio, ImageButton location, ImageButton contact) {
        files = findViewById(R.id.attach_files);
        camera = findViewById(R.id.open_camera);
        gallery = findViewById(R.id.attach_from_gallery);
        audio = findViewById(R.id.attach_audio);
        location = findViewById(R.id.attach_my_location);
        contact = findViewById(R.id.attach_contact);
    }

    private Realm InitializeDatabase(RealmConfiguration realmConfiguration) {

         Realm.setDefaultConfiguration(realmConfiguration);

         return Realm.getInstance(realmConfiguration);
    }

    public void enterReveal() {
        circularReveal = true;
        // get the center for the clipping circle
        int cx = attachments_layout.getMeasuredWidth() / 4;
        int cy = attachments_layout.getMeasuredHeight() / 4;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(attachments_layout.getWidth(), attachments_layout.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(attachments_layout, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        attachments_layout.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void exitReveal() {
        circularReveal = false;
        // get the center for the clipping circle
        int cx = attachments_layout.getMeasuredWidth() / 2;
        int cy = attachments_layout.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = attachments_layout.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(attachments_layout, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                attachments_layout.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitReveal();
    }

    public void hide_attachments(View view) {
        exitReveal();
    }

    private interface getResult {
        void Callback(ArrayList<String> result);
    }
}
