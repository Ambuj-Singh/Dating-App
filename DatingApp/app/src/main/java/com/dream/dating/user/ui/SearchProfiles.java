package com.dream.dating.user.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SearchProfiles extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        int columns = Tools.calculateNoOfColumns(getContext(), 180);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), columns);
        recyclerView = view.findViewById(R.id.search_list);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        EditText searchString = view.findViewById(R.id.search_box);

        ImageView search = view.findViewById(R.id.search_button_profiles);
        LinearLayout noSearch = view.findViewById(R.id.no_search_results);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = searchString.getText().toString().trim();

                Query query = db.collection("users")
                        .whereLessThanOrEqualTo("username", search);
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Searching...");
                progressDialog.create();
                progressDialog.show();
                ProgressBar progressBar = progressDialog.findViewById(android.R.id.progress);
                progressBar.getIndeterminateDrawable().setTint(getResources().getColor(R.color.colorAccent,null));

                getQuery(query, new getQueryResult() {
                    @Override
                    public void onCallback(boolean value) {

                        if (value) {
                            recyclerView.setVisibility(View.INVISIBLE);
                            noSearch.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noSearch.setVisibility(View.GONE);

                            initializeDataSearch(search);
                            StartListener();
                        }
                        progressDialog.cancel();
                    }
                });
            }
        });
    }

    private interface getQueryResult {
        void onCallback(boolean value);
    }

    private void getQuery(Query query, final getQueryResult callback) {
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() != 0) {
                    Log.i("query_size", String.valueOf(queryDocumentSnapshots.size()));
                    callback.onCallback(false);
                } else {
                    callback.onCallback(true);
                }
            }
        });
    }
    //fetching data from server
    public void initializeDataSearch(String username) {
        final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Query query = db.collection("users")
                .whereGreaterThanOrEqualTo("username", username);

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
            profile_pic = item.findViewById(R.id.received_image);
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