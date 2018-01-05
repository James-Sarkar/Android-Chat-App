package com.androidproject.chatapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidproject.chatapp.ConversationsActivity;
import com.androidproject.chatapp.Model.Conversation;
import com.androidproject.chatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by James Sarkar.
 */

public class ConversationsFragment extends Fragment {

    private View mainView;

    private RecyclerView conversationsList;

    private DatabaseReference friendsReference, usersReference;

    private FirebaseAuth mAuth;

    private String currentUserId;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_conversations, container, false);

        conversationsList = (RecyclerView) mainView.findViewById(R.id.conversations_list);

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        friendsReference.keepSynced(true);

        conversationsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        conversationsList.setLayoutManager(linearLayoutManager);

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Conversation, ListConversationsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Conversation, ListConversationsViewHolder>
                (Conversation.class, R.layout.all_users_display_layout, ListConversationsViewHolder.class, friendsReference) {
            @Override
            protected void populateViewHolder(final ListConversationsViewHolder viewHolder, Conversation model, int position) {
                final String userId = getRef(position).getKey();

                usersReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("online")) {
                            String onlineStatus = dataSnapshot.child("online").getValue().toString();

                            viewHolder.setUserOnline(onlineStatus);
                        }

                        viewHolder.setUserDisplayName(dataSnapshot.child("userDisplayName").getValue().toString());
                        viewHolder.setUserThumbnail(getContext(), dataSnapshot.child("userThumbnail").getValue().toString());
                        viewHolder.setUserBio(dataSnapshot.child("userProfileBio").getValue().toString());


                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dataSnapshot.child("online").exists()) {
                                    Intent intent = new Intent(getContext(), ConversationsActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("userDisplayName", dataSnapshot.child("userDisplayName").getValue().toString());
                                    startActivity(intent);
                                } else {
                                    usersReference.child(userId).child("online").setValue(ServerValue.TIMESTAMP)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(getContext(), ConversationsActivity.class);
                                                    intent.putExtra("userId", userId);
                                                    intent.putExtra("userDisplayName", dataSnapshot.child("userDisplayName").getValue().toString());
                                                    startActivity(intent);
                                                }
                                            });
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        conversationsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ListConversationsViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ListConversationsViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setUserDisplayName(String userDisplayName) {
            TextView usename = (TextView) view.findViewById(R.id.all_users_display_names);
            usename.setText(userDisplayName);
        }

        public void setUserThumbnail(final Context context, final String userThumbnail) {
            final CircleImageView userThumbnailImage = (CircleImageView) view.findViewById(R.id.all_users_profile_picture);

            Picasso.with(context)
                    .load(userThumbnail)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_profile)
                    .into(userThumbnailImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context)
                                    .load(userThumbnail)
                                    .placeholder(R.drawable.default_profile)
                                    .into(userThumbnailImage);
                        }
                    });
        }

        public void setUserOnline(String online) {
            ImageView onlineStatusView = (ImageView) view.findViewById(R.id.online_status);

            if (online.equals("true")) {
                onlineStatusView.setVisibility(View.VISIBLE);
            } else {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }

        public void setUserBio(String userBio) {
            TextView usrBio = (TextView) view.findViewById(R.id.all_users_bios);

            usrBio.setText(userBio);
        }
    }
}