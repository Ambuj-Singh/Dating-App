package com.dream.dating.FriendList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dream.dating.Models.UserInfo;
import com.dream.dating.R;
import com.dream.dating.Tools;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListViewModel> {

    private List<UserInfo> usersList;
    private Context context;
    private Interaction interaction;

    public FriendsListAdapter(List<UserInfo> usersList, Interaction interaction, Context context){
        this.usersList = usersList;
        this.interaction = interaction;
        this.context = context;

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
//        File file = new File(user.getImage());
//        if (file.exists()){
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            holder.profile_pic.setImageBitmap(bitmap);
//        }

        DocumentReference df = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getUID());

        getStatusAndProfileURL(df, new Callback() {
            @Override
            public void onCallback(boolean value, String url) {
                try {
                    if (!url.equals("none")) {
                        Glide.with(context)
                                .load(url)
                                .circleCrop()
                                .into(holder.profile_pic);
                    }
                }
                catch (Exception e){
                    Log.i("exceptionChatList", Objects.requireNonNull(e.getMessage()));
                }

                if(value)
                    holder.onlineOffline.setVisibility(View.VISIBLE);
                else
                    holder.onlineOffline.setVisibility(View.GONE);
            }
        });


        holder.title.setText(user.getName());

        if(user.getTimestamp() > 0)
            holder.time.setText(Tools.getTimeInFormat(user.getTimestamp()));
        else
            holder.time.setVisibility(View.GONE);

        if (!(user.getMessage()==null))
            holder.message_view.setText(user.getMessage());
        else
            holder.message_view.setText(R.string.default_no_message_in_chat_list);

        if (user.getCounter() > 0)
            holder.messageCounter.setText(user.getCounter());
        else
            holder.messageCounter.setVisibility(View.GONE);
        //Resources.getSystem() when not extending the class to AppCompatActivity

        try {
            holder.itemView.setOnClickListener(view ->{
                    interaction.onChatClicked(holder.getBindingAdapterPosition());
                Log.i("userInfo",(user.getName()+" - name missing"));});

            holder.itemView.setOnLongClickListener(view -> {
                interaction.onChatClicked(holder.getBindingAdapterPosition());
                return false;
            });
        }
        catch (Exception e){
            Log.i("clickChatList", Objects.requireNonNull(e.getMessage()));
        }

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public interface Interaction {
        void onChatClicked(int position);
        void onChatLongClicked(int position);
    }



    private interface getQueryResult {
        void onCallback(boolean value);
    }
    private interface Callback {
        void onCallback(boolean value, String url);
    }

    public void getStatusAndProfileURL(DocumentReference df, final Callback callback) {
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean flag;
                String url;
                if (documentSnapshot.getBoolean("UserStatus") != null)
                    flag = documentSnapshot.getBoolean("UserStatus") != null;
                else
                    flag = false;

                if(documentSnapshot.getString("ProfileURL") != null)
                    url = documentSnapshot.getString("ProfileURL");
                else
                    url = "none";

                callback.onCallback(flag, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("network", "Interrupted");
            }
        });
    }


}
