package com.androidproject.chatapp.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.androidproject.chatapp.Model.Friend;
import com.androidproject.chatapp.ProfileActivity;
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
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView friendsList;

    private DatabaseReference friendsReference, usersReference;

    private FirebaseAuth mAuth;

    private String currentUserId;

    private View mainView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        mainView = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsList = (RecyclerView) mainView.findViewById(R.id.friends_list);

        currentUserId = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        friendsReference.keepSynced(true);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);

        friendsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        friendsList.setLayoutManager(linearLayoutManager);

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friend, ListFriendsViewHolder> firebaseRecyclerAdapter
                 = new FirebaseRecyclerAdapter<Friend, ListFriendsViewHolder>
                (Friend.class, R.layout.all_users_display_layout, ListFriendsViewHolder.class, friendsReference) {
            @Override
            protected void populateViewHolder(final ListFriendsViewHolder viewHolder, Friend model, int position) {
                viewHolder.setDate(model.getDate());

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
                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence[] options = new CharSequence[] {
                                        dataSnapshot.child("userDisplayName").getValue().toString() + "'s profile",
                                        "Send a message"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select an option");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent intent = new Intent(getContext(), ProfileActivity.class);
                                            intent.putExtra("userId", userId);
                                            startActivity(intent);
                                        } else if (which == 1) {
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
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        friendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ListFriendsViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ListFriendsViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setDate(String date) {
            TextView sinceFriendsDate = (TextView) view.findViewById(R.id.all_users_bios);
            sinceFriendsDate.setText("Friends since \n" + date);
        }

        public void setUserDisplayName(String userDisplayName) {
            TextView username = (TextView) view.findViewById(R.id.all_users_display_names);
            username.setText(userDisplayName);
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
    }
}
