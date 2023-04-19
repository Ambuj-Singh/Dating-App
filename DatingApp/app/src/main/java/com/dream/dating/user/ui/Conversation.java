package com.dream.dating.user.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.bumptech.glide.Glide;
import com.dream.dating.ConversationMessageList.ConversationListAdapter;
import com.dream.dating.Encryption;
import com.dream.dating.Models.MessageModel;
import com.dream.dating.Models.UserInfo;
import com.dream.dating.ProfileInfoGrabber;
import com.dream.dating.R;
import com.dream.dating.Services.DataContext;
import com.dream.dating.Tools;
import com.dream.dating.account.AccountActivity;
import com.dream.dating.databinding.ConversationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Conversation extends AppCompatActivity implements ConversationListAdapter.Interaction {

    private ConversationBinding binding;
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseReference databaseReference;
    private Data data;
    private String receiver, sender, receiver_uid, receiver_name;
    private DataContext dataContext;
    private ConversationListAdapter adapter;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static final int PERMISSION_STORAGE_GRANTED = 1;
    private boolean camera;
    private DocumentReference df_user;
    ActivityResultLauncher<Intent> cameraActivityLauncher;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        binding = ConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        camera = checkCameraHardware(this);
        checkAndRequestPermissions(this);

        data = new Data.Builder()
                .putString("status", "UserStatusCon")
                .build();

        Bundle bundle = getIntent().getExtras();
        receiver = bundle.getString("receiver");
        receiver_name = bundle.getString("receiver_name");
        binding.titleUsername.setText(receiver_name);
        Log.i("result_insert_bundle", receiver);
        sender = bundle.getString("sender");

        String msg_table = "message_table";

        //Accessing Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance(Tools.firebaseURL);
        databaseReference = database.getReference(receiver + "/messages");
        DatabaseReference reference = database.getReference(sender + "/messages");

        //Accessing Firebase Firestore
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("users").whereEqualTo("username", receiver);
//        String UserUID = FirebaseAuth.getInstance().getUid();

        //fetching receiver's profile picture
        getQuery(query, new getQueryResult() {
            @Override
            public void onCallback(String url, String uid) {
                    if(!url.equals("none")) {
                        Log.i("ProfileURl",url);
                        Glide.with(getApplicationContext())
                                .load(url)
                                .circleCrop()
                                .into(binding.titleImage);
                    }

                    if(uid!=null){
                        receiver_uid = uid;
                    }
                }

        });

        binding.titleImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!receiver_uid.isEmpty()) {
                    Intent i = new Intent(Conversation.this, UserProfile.class);
                    i.putExtra("uid", receiver_uid);
                    startActivity(i);
                }
                else {
                    Toast.makeText(Conversation.this, "Fetching user, try again also check your internet connection", Toast.LENGTH_LONG).show();
                }

            }
        });


    //Accessing Database
        dataContext = new DataContext(this);
        sqLiteDatabase = dataContext.getWritableDatabase();
        List<MessageModel> messageModelList = dataContext.getAllChatMessages(sender, receiver);

        //Initialising Chat adapter
        adapter = new ConversationListAdapter();
        RecyclerView recyclerView = binding.conversationRecyclerView;

        //sending message if the message is not empty
        if (!messageModelList.isEmpty()) {
            binding.noChatImg.setVisibility(View.GONE);
            adapter.setChatMessages(messageModelList, receiver);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);

        binding.messageInputLayout.sendMessage.setOnClickListener(view -> {
            String message = binding.messageInputLayout.inputMessageUser.getText().toString().trim();
            binding.messageInputLayout.inputMessageUser.getText().clear();
            if (!message.isEmpty()) {
                binding.noChatImg.setVisibility(View.GONE);
                MessageModel messageModel = new MessageModel();
                messageModel.setMessage(message);
                messageModel.setDelivery(false);
                messageModel.setRead(false);
                messageModel.setFilePath("None");
                messageModel.setReceiver(receiver);
                Log.i("result_insert_chat", receiver);
                messageModel.setSender(sender);
                messageModel.setImagePath("none");
                messageModel.setTimestamp(System.currentTimeMillis());
                //adding to the view
                adapter.addMessage(messageModel);
                recyclerView.smoothScrollToPosition(messageModelList.size());
                dataContext.insertMessage(messageModel);
                dataContext.updateTimestampOfUser(sender, messageModel.getTimestamp());

                //sending message to the server
                sendToServer(messageModel, value -> {
                    if (value) {
                        dataContext.setDeliveryUpdate(messageModel, true);
                        List<MessageModel> list = dataContext.getAllChatMessages(sender, receiver);
                        adapter.setChatMessages(list, receiver);
                        recyclerView.smoothScrollToPosition(list.size()-1);
                    }
                });
            }
        });


        //checking realtime for changes in messages
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                GenericTypeIndicator<String> genericTypeIndicator = new GenericTypeIndicator<String>() {};

                /* Map<String,String> map = snapshot.getValue(genericTypeIndicator);*/

                String message = snapshot.getValue(genericTypeIndicator);

                reference.child(snapshot.getKey()).removeValue();

                //inserting the message that is received after decrypting
                String decryptedMessage = new Encryption().decrypt(message, Tools.secret);
                Log.i("decrypted",decryptedMessage);
                String[] messageArray = Tools.getMessageArray(decryptedMessage);

                MessageModel messageModel = Tools.getMessageModel(messageArray);
                dataContext.insertMessageReceived(messageModel);
                dataContext.setReadUpdate(messageModel,true);

                List<MessageModel> list = dataContext.getAllChatMessages(sender, receiver);
                binding.noChatImg.setVisibility(View.GONE);
                adapter.setChatMessages(list, receiver);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<String> genericTypeIndicator = new GenericTypeIndicator<String>() {};

                String message = snapshot.getValue(genericTypeIndicator);
                String decryptedMessage = new Encryption().decrypt(message, Tools.secret);
                Log.i("decrypted",decryptedMessage);
                String[] messageArray = Tools.getMessageArray(decryptedMessage);

                MessageModel messageModel = Tools.getMessageModel(messageArray);
                dataContext.insertMessageReceived(messageModel);

                String messageReceiver = messageModel.getReceiver();
                String messageSender = messageModel.getSender();
                if((messageReceiver.equals(receiver)&&messageSender.equals(sender))
                        ||messageSender.equals(receiver)&&messageReceiver.equals(sender)) {
                    dataContext.setReadUpdate(messageModel,true);
                }
                List<MessageModel> list = dataContext.getAllChatMessages(sender, receiver);
                binding.noChatImg.setVisibility(View.GONE);
                adapter.setChatMessages(list, receiver);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //camera button click
        binding.messageInputLayout.displayCamera.setOnClickListener(view ->{

            checkAndRequestPermissions(this);

            if (checkCameraHardware(this))
                chooseImage(this, camera);
            else
                chooseImage(this,camera);
        });

        //cameraActivityLauncher registration
        cameraActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            sendToServerPicture(uri);
                        }
                    }
                });

    }

    @Override
    public void onMessageClicked(int position) {

    }

    @Override
    public void onMessageLongClicked(int position) {

    }

    public interface getResult {
        void onCallback(boolean value);
    }

    public void sendToServer(MessageModel messageModel, final getResult callback) {
        String query = Tools.getPlannedString(messageModel);
        String encryptedQuery = new Encryption().encrypt(query,Tools.secret);
        Log.i("sendToServer", query);
        databaseReference.push().setValue(encryptedQuery)
                .addOnSuccessListener(unused -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("clear chat","clear chat");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("clear chat", item.getTitle().toString());
        if (item.getItemId() == R.id.toolbar_delete) {
            Log.i("clear chat","clear chat");
            dataContext.clearChat(sender, receiver);
            adapter.deleteAllMessage();
            Toast.makeText(this, "Chat Cleared", Toast.LENGTH_SHORT).show();
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    public boolean checkAndRequestPermissions(Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
                chooseImage(this,camera);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(Conversation.this, camera);
                }
                break;
        }
    }

    private interface getQueryResult {
        void onCallback(String value, String uid);
    }

    private void getQuery(Query query, final getQueryResult callback) {
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() != 0) {
                  DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                  String url = documentSnapshot.getString("ProfileURL");
                  String uid = documentSnapshot.getString("UID");
                  callback.onCallback(url,uid);
                } else {
                    callback.onCallback("none",null);
                }
            }
        });
    }

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PERMISSION_STORAGE_GRANTED && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                sendToServerPicture(uri);
                 upload(uri, path);
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                sendToServerPicture(uri);
                upload(uri, path);
            }
        }
    }
*/
    private void chooseImage(Context context, boolean camera){
        final CharSequence[] options = {"Choose Camera","Choose from Gallery","Exit"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int i){
                if(options[i].equals("Choose Camera")){
                    if (camera){
                        openCamera();
                    }
                }
                else if (options[i].equals("Choose from Gallery")){
                    openDirectory();
                }
                else if (options[i].equals("Exit")) {
                    dialog.dismiss();
                }
            }
        }).create().show();
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(@NonNull Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void openDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, PERMISSION_STORAGE_GRANTED);
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 0);
    }

    private void sendToServerPicture(Uri uri) {
        Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.imagetester);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        binding.imagetester.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        sendToServerPicture(selectedImage);
                    }
                    break;
            }
        }
    }

   /* public void enterReveal() {
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
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* exitReveal();*/
    }

    public void hide_attachments(View view) {
        /* exitReveal();*/
    }

}
