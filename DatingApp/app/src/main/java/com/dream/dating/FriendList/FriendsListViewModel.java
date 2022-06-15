package com.dream.dating.FriendList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dream.dating.R;
import com.google.android.material.card.MaterialCardView;


//ViewHolder of FriendsList
public class FriendsListViewModel extends RecyclerView.ViewHolder implements View.OnClickListener{
    MaterialCardView materialCardView;
    ImageView profile_pic;
    TextView title, message_view, time, messageCounter, readStatus;
    ImageView onlineOffline;
    FriendListAdapter.Interaction interaction;

    FriendsListViewModel(View item, FriendListAdapter.Interaction interaction) {
        super(item);
        materialCardView = item.findViewById(R.id.friends_list_card);
        profile_pic = item.findViewById(R.id.profile_display_friend_list);
        title = item.findViewById(R.id.user_name_chat_list);
        message_view = item.findViewById(R.id.message_view_list);
        time = item.findViewById(R.id.last_message_time_list);
        messageCounter = item.findViewById(R.id.message_counter_list);
        readStatus = item.findViewById(R.id.message_delivery_status_list);
        onlineOffline = item.findViewById(R.id.online_offline_friend_user);
        this.interaction = interaction;
    }

    @Override
    public void onClick(View view) {
        interaction.onChatClicked(getBindingAdapterPosition());
    }
}
