package com.androidproject.chatapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidproject.chatapp.Model.FriendRequest;
import com.androidproject.chatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView friendRequestsList;

    private View mainView;

    private DatabaseReference friendsRequestReference, usersReference;

    private FirebaseAuth mAuth;

    private String currentUserId;

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

        friendsRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(currentUserId);

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
                (FriendRequest.class, R.layout.friend_requests_layout, ListFriendRequestsViewHolder.class, friendsRequestReference) {
            @Override
            protected void populateViewHolder(final ListFriendRequestsViewHolder viewHolder, FriendRequest model, int position) {
                final String userId = getRef(position).getKey();

                usersReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setUserDisplayName(dataSnapshot.child("userDisplayName").getValue().toString());
                        viewHolder.setUserThumbnail(getContext(), dataSnapshot.child("userThumbnail").getValue().toString());
                        viewHolder.setUserBio(dataSnapshot.child("userProfileBio").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        friendRequestsList.setAdapter(firebaseRecyclerAdapter);
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
