package com.androidproject.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.androidproject.chatapp.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by James Sarkar.
 */

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView allUsersList;

    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = (Toolbar) findViewById(R.id.all_users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersList = (RecyclerView) findViewById(R.id.all_users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<User, AllUsersViewHolder>
                (User.class, R.layout.all_users_display_layout, AllUsersViewHolder.class, usersReference) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, User model, final int position) {
                viewHolder.setUserDisplayName(model.getUserDisplayName());
                viewHolder.setUserProfileBio(model.getUserProfileBio());
                viewHolder.setUserThumbnail(getApplicationContext(), model.getUserThumbnail());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        intent.putExtra("userId", getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        allUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {

        private View view;

        public AllUsersViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        private void setUserDisplayName(String userDisplayName) {
            TextView displayName = (TextView) view.findViewById(R.id.all_users_display_names);
            displayName.setText(userDisplayName);
        }

        private void setUserProfileBio(String userProfileBio) {
            TextView userBio = (TextView) view.findViewById(R.id.all_users_bios);
            userBio.setText(userProfileBio);
        }

        private void setUserThumbnail(final Context context, final String userThumbnail) {
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
    }
}
