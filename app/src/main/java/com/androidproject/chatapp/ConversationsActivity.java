package com.androidproject.chatapp;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.androidproject.chatapp.Common.LastSeenTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsActivity extends AppCompatActivity {

    private String messageReceiverUserId, messageReceiverUserDisplayName;

    private Toolbar mToolbar;

    private TextView userDisplayName, userLastSeen;

    private CircleImageView userProfilePicture;

    private DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        rootReference = FirebaseDatabase.getInstance().getReference();

        messageReceiverUserId = getIntent().getExtras().get("userId").toString();
        messageReceiverUserDisplayName = getIntent().getExtras().get("userDisplayName").toString();

        mToolbar = (Toolbar) findViewById(R.id.conversations_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View actionBarView = inflater.inflate(R.layout.conversations_custom_bar, null);

        getSupportActionBar().setCustomView(actionBarView);

        userDisplayName = (TextView) findViewById(R.id.user_display_name_conversations_custom);
        userDisplayName.setText(messageReceiverUserDisplayName);

        userLastSeen = (TextView) findViewById(R.id.last_seen_conversations_custom);

        userProfilePicture = (CircleImageView) findViewById(R.id.user_profile_picture_conversations_custom);

        rootReference.child("Users").child(messageReceiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userThumbnail = dataSnapshot.child("userThumbnail").getValue().toString();

                Picasso.with(getBaseContext())
                        .load(userThumbnail)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_profile)
                        .into(userProfilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getBaseContext())
                                        .load(userThumbnail)
                                        .placeholder(R.drawable.default_profile)
                                        .into(userProfilePicture);
                            }
                        });

                if (online.equals("true")) {
                    userLastSeen.setText("Online");
                } else {
                    LastSeenTime lastSeenTime = new LastSeenTime();

                    long lastSeen = Long.parseLong(online);

                    String lastSeenDisplayTime = lastSeenTime.getTimeAgo(lastSeen, getApplicationContext()).toString();

                    userLastSeen.setText(lastSeenDisplayTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
