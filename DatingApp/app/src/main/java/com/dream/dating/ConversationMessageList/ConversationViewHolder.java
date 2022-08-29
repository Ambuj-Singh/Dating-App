package com.dream.dating.ConversationMessageList;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    ConversationListAdapter.Interaction interaction;
    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);

    }
    @Override
    public void onClick(View view) {
        interaction.onMessageClicked(getBindingAdapterPosition());
    }


    @Override
    public boolean onLongClick(View view) {
        interaction.onMessageLongClicked(getBindingAdapterPosition());
        return false;
    }
}
