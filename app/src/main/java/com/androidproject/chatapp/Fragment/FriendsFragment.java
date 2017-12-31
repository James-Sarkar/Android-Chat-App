package com.androidproject.chatapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidproject.chatapp.Model.Friends;
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

        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter
                 = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(Friends.class, R.layout.all_users_display_layout, FriendsViewHolder.class, friendsReference) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                viewHolder.setDate(model.getDate());

                String userIdList = getRef(position).getKey();

                usersReference.child(userIdList).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("online")) {
                            viewHolder.setUserOnline((Boolean) dataSnapshot.child("online").getValue());
                        }

                        viewHolder.setUserDisplayName(dataSnapshot.child("userDisplayName").getValue().toString());
                        viewHolder.setUserThumbnail(getContext(), dataSnapshot.child("userThumbnail").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        friendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View view;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setDate(String date) {
            TextView sinceFriendsDate = (TextView) view.findViewById(R.id.all_users_bios);
            sinceFriendsDate.setText(date);
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

        public void setUserOnline(Boolean online) {
            ImageView onlineStatusView = (ImageView) view.findViewById(R.id.online_status);

            if (online) {
                onlineStatusView.setVisibility(View.VISIBLE);
            } else {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
