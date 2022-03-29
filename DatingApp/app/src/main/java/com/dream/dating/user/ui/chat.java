package com.dream.dating.user.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dream.dating.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class chat extends Fragment {

    private FirebaseDatabase realtime;
    private FirebaseDatabase db;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseDatabase.getInstance();


        recyclerView = view.findViewById(R.id.chat_list);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        initializeChat();
    }

    public void initializeChat(){
        DatabaseReference databaseReference = db.getReference("dreamdating-101");
        Query query = databaseReference.child("chat");


    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_pic;
        ImageSwitcher status;
        TextView title, message, message_counter, time;

        public ChatViewHolder(View item) {
            super(item);
            profile_pic = item.findViewById(R.id.profile_display_list);
            title = item.findViewById(R.id.user_name_list);
            message = item.findViewById(R.id.message_view_list);
            message_counter = item.findViewById(R.id.message_counter_list);
            status = item.findViewById(R.id.message_delivery_status_list);
            time = item.findViewById(R.id.last_message_time_list);
        }
    }
}
