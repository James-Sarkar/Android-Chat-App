package com.androidproject.chatapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidproject.chatapp.Model.FriendRequest;
import com.androidproject.chatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView friendRequestsList;

    private View mainView;

    private DatabaseReference currentUsersFriendRequestReference, usersReference, friendsReference, friendRequestsReference;

    private FirebaseAuth mAuth;

    private String currentUserId;

    private Button acceptButton, declineButton, cancelButton;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_requests, container, false);

        friendRequestsList = (RecyclerView) mainView.findViewById(R.id.requests_list);

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        friendRequestsReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        currentUsersFriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(currentUserId);

        friendRequestsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setReverseLayout(true);

        linearLayoutManager.setStackFromEnd(true);

        friendRequestsList.setLayoutManager(linearLayoutManager);

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<FriendRequest, ListFriendRequestsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FriendRequest, ListFriendRequestsViewHolder>
                (FriendRequest.class, R.layout.friend_requests_layout, ListFriendRequestsViewHolder.class, currentUsersFriendRequestReference) {
            @Override
            protected void populateViewHolder(final ListFriendRequestsViewHolder viewHolder, FriendRequest model, int position) {
                final String userId = getRef(position).getKey();

                DatabaseReference requestTypeReference = getRef(position).child("requestType").getRef();
                requestTypeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String reqType = dataSnapshot.getValue().toString();

                            acceptButton = viewHolder.view.findViewById(R.id.friend_request_accept_button);
                            acceptButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    acceptFriendRequest(userId);
                                }
                            });

                            declineButton = (Button) viewHolder.itemView.findViewById(R.id.friend_request_decline_button);
                            declineButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelFriendRequest(userId);
                                }
                            });

                            cancelButton = viewHolder.view.findViewById(R.id.friend_request_cancel_button);
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelFriendRequest(userId);
                                }
                            });


                            if (reqType.equals("received")) {
                                usersReference.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        viewHolder.setUserDisplayName(dataSnapshot.child("userDisplayName").getValue().toString());
                                        viewHolder.setUserThumbnail(getContext(), dataSnapshot.child("userThumbnail").getValue().toString());
                                        viewHolder.setUserBio(dataSnapshot.child("userProfileBio").getValue().toString());


                                        cancelButton.setEnabled(false);
                                        cancelButton.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else if (reqType.equals("sent")) {
                                usersReference.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        viewHolder.setUserDisplayName(dataSnapshot.child("userDisplayName").getValue().toString());
                                        viewHolder.setUserThumbnail(getContext(), dataSnapshot.child("userThumbnail").getValue().toString());
                                        viewHolder.setUserBio(dataSnapshot.child("userProfileBio").getValue().toString());

                                        acceptButton.setEnabled(false);
                                        acceptButton.setVisibility(View.INVISIBLE);

                                        declineButton.setEnabled(false);
                                        declineButton.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        friendRequestsList.setAdapter(firebaseRecyclerAdapter);
    }

    private void acceptFriendRequest(final String userId) {
        Calendar calendarCurrentDate = Calendar.getInstance();

        SimpleDateFormat simpleDateFormatCurrentDate = new SimpleDateFormat("dd-MMMM-yyyy");

        final String currentDate = simpleDateFormatCurrentDate.format(calendarCurrentDate.getTime());

        friendsReference.child(currentUserId).child(userId).child("date").setValue(currentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendsReference.child(userId).child(currentUserId).child("date").setValue(currentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendRequestsReference.child(currentUserId).child(userId).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            friendRequestsReference.child(userId).child(currentUserId).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void cancelFriendRequest(final String userId) {
        friendRequestsReference.child(currentUserId).child(userId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestsReference.child(userId).child(currentUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public static class ListFriendRequestsViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ListFriendRequestsViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setUserDisplayName(String userDisplayName) {
            TextView username = (TextView) view.findViewById(R.id.friend_requests_display_names);
            username.setText(userDisplayName);
        }

        public void setUserThumbnail(final Context context, final String userThumbnail) {
            final CircleImageView userThumbnailImage = (CircleImageView) view.findViewById(R.id.friend_requests_profile_picture);

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

        private void setUserBio(String userProfileBio) {
            TextView userBio = (TextView) view.findViewById(R.id.friend_requests_user_bios);
            userBio.setText(userProfileBio);
        }
    }
}
