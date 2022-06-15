package com.dream.dating.FriendList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dream.dating.Models.UserInfo;
import com.dream.dating.R;
import com.dream.dating.Tools;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendsListViewModel> {

    private List<UserInfo> usersList = new ArrayList<>();
    private FirebaseFirestore db;
    private FriendsListViewModel holder;
    private int position;

    public FriendListAdapter(List<UserInfo> usersList, Interaction interaction){
        this.usersList = usersList;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public FriendsListViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_view, parent, false);
        return new FriendsListViewModel(view, interaction);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsListViewModel holder,  int position) {
        final UserInfo user = usersList.get(position);

        //holder items
        holder.title.setText(user.getUsername());
        boolean value = Tools.integerToBoolean(user.getUserStatus());
        holder.time.setText(Tools.getTimeInFormat(user.getTimestamp()));
        holder.message_view.setText(user.getMessage());
        holder.messageCounter.setText(user.getCounter());
        //Resources.getSystem() when not extending the class to AppCompatActivity
        if(value)
            holder.onlineOffline.setVisibility(View.VISIBLE);
        else
            holder.onlineOffline.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(view ->
                interaction.onChatClicked(holder.getBindingAdapterPosition()));


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public interface Interaction {
        void onChatClicked(int position);
        void onChatLongClicked(int position);
    }

    private Interaction interaction;


}
