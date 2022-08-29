package com.dream.dating.user.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.dream.dating.ConversationMessageList.ConversationListAdapter;
import com.dream.dating.Encryption;
import com.dream.dating.Models.MessageModel;
import com.dream.dating.Models.UserInfo;
import com.dream.dating.R;
import com.dream.dating.Services.DataContext;
import com.dream.dating.Tools;
import com.dream.dating.databinding.ConversationBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;


public class  Conversation extends AppCompatActivity implements ConversationListAdapter.Interaction {

    private ConversationBinding binding;
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseReference databaseReference;
    private Data data;
    private String receiver, sender;
    private DataContext dataContext;
    private ConversationListAdapter adapter;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        binding = ConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        data = new Data.Builder()
                .putString("status", "UserStatusCon")
                .build();

        Bundle bundle = getIntent().getExtras();
        receiver = bundle.getString("receiver");
        Log.i("result_insert_bundle", receiver);
        sender = bundle.getString("sender");
        String msg_table = "message_table";

        FirebaseDatabase database = FirebaseDatabase.getInstance(Tools.firebaseURL);
        databaseReference = database.getReference(receiver + "/messages");
        DatabaseReference reference = database.getReference(sender + "/messages");


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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.toolbar_delete) {
            dataContext.clearChat(sender, receiver);
            adapter.deleteAllMessage();
            Toast.makeText(this, "Chat Cleared", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(item.getItemId() == R.id.toolbar_view){
            UserInfo user = dataContext.getUserInfo(receiver);
            Intent i = new Intent(this,UserProfile.class);
            i.putExtra("uid",user.getUID());
            startActivity(i);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
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
                    chooseImage(Conversation.this);
                }
                break;
        }
    }

    private void chooseImage(Context context){
        final CharSequence[] options = {"Take photo","Choose from Gallery","Exit"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int i){
                if(options[i].equals("Take Photo")){
                    openCameraActivityForResult(binding.getRoot().getRootView());

                }
                else if (options[i].equals("Choose from Gallery")){
                    openGalleryPicker(binding.getRoot().getRootView());
                }
                else if (options[i].equals("Exit")) {
                    dialog.dismiss();
                }
            }
        });
    }

    public void openCameraActivityForResult(View view) {
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            sendToServerPicture()
                        }
                    }
                });
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        someActivityResultLauncher.launch(takePicture);
    }

    public void openGalleryPicker(View view) {
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri uri = data.getData();
                        }
                    }
                });
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(pickPhoto);
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
