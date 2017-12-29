package com.androidproject.chatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.androidproject.chatapp.Model.AllUsers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView allUsersList;

    private DatabaseReference databaseReference;

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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>
                (AllUsers.class, R.layout.all_users_display_layout, AllUsersViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, int position) {
                viewHolder.setUserDisplayName(model.getUserDisplayName());
                viewHolder.setUserProfileBio(model.getUserProfileBio());
                viewHolder.setUserProfileImage(getApplicationContext(), model.getUserProfileImage());
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

        private void setUserProfileImage(Context context, String userProfileImage) {
            CircleImageView userImage = (CircleImageView) view.findViewById(R.id.all_users_profile_picture);

            Picasso.with(context).load(userProfileImage).into(userImage);
        }
    }
}
