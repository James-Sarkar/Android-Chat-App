package com.androidproject.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference databaseReference;

    private Button sendFriendRequestButton, declineFriendRequestButton;

    private TextView userDisplayName, userBio;

    private CircleImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        sendFriendRequestButton = (Button) findViewById(R.id.send_friend_request_button);
        declineFriendRequestButton = (Button) findViewById(R.id.decline_friend_request_button);

        userDisplayName = (TextView) findViewById(R.id.profile_user_display_name);
        userBio = (TextView) findViewById(R.id.profile_user_bio);

        profilePicture = (CircleImageView) findViewById(R.id.profile_user_picture);

        String userId = getIntent().getExtras().get("userId").toString();

        mToolbar = (Toolbar) findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseReference.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userDisplayName.setText(dataSnapshot.child("userDisplayName").getValue().toString());
                        userBio.setText(dataSnapshot.child("userProfileBio").getValue().toString());

                        Picasso.with(getBaseContext())
                                .load(dataSnapshot.child("userProfileImage").getValue().toString())
                                .placeholder(R.drawable.default_profile)
                                .into(profilePicture);

                        getSupportActionBar().setTitle(dataSnapshot.child("userDisplayName").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
