package com.dream.dating.user.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dream.dating.ProfileInfoGrabber;
import com.dream.dating.R;
import com.dream.dating.Tools;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class Search extends Fragment {


    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private String search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        int columns = Tools.calculateNoOfColumns(getContext(), 180);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), columns);
        recyclerView = view.findViewById(R.id.search_list);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Query query = db.collection("users");

        final EditText bio = view.findViewById(R.id.search_string);

        bio.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search = bio.getText().toString().trim();
                    initializeDataSearch(search);
                    StartListener();
                    handled = true;
                }
                return handled;
            }
        });


    }



    //fetching data from server
    public void initializeDataSearch(String username) {
        final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Query query = db.collection("users")
                        .whereNotEqualTo("UID", uid)
                        .whereLessThanOrEqualTo("username", username);

        FirestoreRecyclerOptions<ProfileInfoGrabber> response = new FirestoreRecyclerOptions.Builder<ProfileInfoGrabber>()
                .setQuery(query, ProfileInfoGrabber.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<ProfileInfoGrabber, ProfileViewHolder>(response) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, final ProfileInfoGrabber model) {


                holder.title.setText(String.valueOf(model.getusername()));
                holder.age.setText(String.valueOf(model.getAge()));

                //setting user status on data change
                if (model.getUserStatus()) {
                    holder.status.setVisibility(View.VISIBLE);
                } else {
                    holder.status.setVisibility(View.INVISIBLE);
                }

                Glide.with(getContext())
                        .load(String.valueOf(model.getProfileURL()))
                        .into(holder.profile_pic);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), UserProfile.class);
                        i.putExtra("uid", model.getUID());
                        startActivity(i);
                    }
                });

            }

            @NonNull
            @Override
            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress, parent, false);
                return new ProfileViewHolder(v);

            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", Objects.requireNonNull(e.getMessage()));
            }
        };

        recyclerView.setAdapter(adapter);

    }

    //cardView
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView materialCardView;
        ImageView profile_pic, status;
        TextView title, age;

        public ProfileViewHolder(View item) {
            super(item);
            materialCardView = item.findViewById(R.id.card);
            profile_pic = item.findViewById(R.id.display_profile);
            title = item.findViewById(R.id.user_name);
            age = item.findViewById(R.id.age_view);
            status = item.findViewById(R.id.User_status);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    public void StartListener() {
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
