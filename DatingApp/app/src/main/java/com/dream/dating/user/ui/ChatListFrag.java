package com.dream.dating.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dream.dating.FriendList.FriendsListAdapter;
import com.dream.dating.Models.UserInfo;
import com.dream.dating.R;
import com.dream.dating.Services.DataContext;
import com.dream.dating.databinding.FragmentChatBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;



public class ChatListFrag extends Fragment implements FriendsListAdapter.Interaction {

    private DataContext dataContext;
    private List<UserInfo> users;
    private FragmentChatBinding binding;
    private FriendsListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataContext = new DataContext(getContext());
        users = dataContext.getAllUsers();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentChatBinding.bind(view);
        binding.noChatImg.setVisibility(View.VISIBLE);
        if(users.isEmpty()){
            binding.noChatImg.setVisibility(View.VISIBLE);
        }
        else {
            binding.noChatImg.setVisibility(View.GONE);
            RecyclerView recyclerView = binding.chatList;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            adapter = new FriendsListAdapter(users,this , getContext());
            recyclerView.setAdapter(adapter);
        }


    }

    @Override
    public void onChatClicked(int position) {
        String receiver = users.get(position).getUsername();
        String receiver_name = users.get(position).getName();
        Log.i("result_insert_list", receiver);
        Intent i = new Intent(getActivity(), Conversation.class);
        String sender = dataContext.getUsername();
        i.putExtra("sender",sender);
        i.putExtra("receiver", receiver);
        i.putExtra("receiver_name",receiver_name);
        Log.i("sender_server",sender);
        startActivity(i);
    }

    @Override
    public void onChatLongClicked(int position) {

    }


}
