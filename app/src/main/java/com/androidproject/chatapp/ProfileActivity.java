package com.androidproject.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference usersReference, friendRequestsReference, friendsReference;

    private Button sendFriendRequestButton, declineFriendRequestButton;

    private TextView userDisplayName, userBio;

    private CircleImageView profilePicture;

    private String currentState;

    private FirebaseAuth mAuth;

    private String receiverUserId, senderUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        senderUserId = mAuth.getCurrentUser().getUid();

        currentState = "notFriends";

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRequestsReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        sendFriendRequestButton = (Button) findViewById(R.id.send_friend_request_button);
        declineFriendRequestButton = (Button) findViewById(R.id.decline_friend_request_button);

        userDisplayName = (TextView) findViewById(R.id.profile_user_display_name);
        userBio = (TextView) findViewById(R.id.profile_user_bio);

        profilePicture = (CircleImageView) findViewById(R.id.profile_user_picture);

        receiverUserId = getIntent().getExtras().get("userId").toString();

        mToolbar = (Toolbar) findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        usersReference.child(receiverUserId)
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

                        friendRequestsReference.child(senderUserId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            if (dataSnapshot.hasChild(receiverUserId)) {
                                                String reqType = dataSnapshot.child(receiverUserId).child("requestType").getValue().toString();

                                                if (reqType.equals("sent")) {
                                                    currentState = "requestSent";
                                                    sendFriendRequestButton.setText("CANCEL FRIEND REQUEST");
                                                } else if (reqType.equals("received")) {
                                                    currentState = "requestReceived";
                                                    sendFriendRequestButton.setText("ACCEPT FRIEND REQUEST");
                                                }
                                            }
                                        } else {
                                            friendsReference.child(senderUserId)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChild(receiverUserId)) {
                                                                currentState = "friends";

                                                                sendFriendRequestButton.setText("UN-FRIEND");
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if (!senderUserId.equals(receiverUserId)) {
            sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFriendRequestButton.setEnabled(false);

                    if (currentState.equals("notFriends")) {
                        sendFriendRequest();
                    } else if (currentState.equals("requestSent")) {
                        cancelFriendRequest();
                    } else if (currentState.equals("requestReceived")) {
                        acceptFriendRequest();
                    } else if (currentState.equals("friends")) {
                        unfriendUser();
                    }
                }
            });
        } else {
            sendFriendRequestButton.setVisibility(View.INVISIBLE);
            declineFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void unfriendUser() {
        friendsReference.child(senderUserId).child(receiverUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendsReference.child(receiverUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendRequestButton.setEnabled(true);
                                                sendFriendRequestButton.setText("SEND FRIEND REQUEST");

                                                currentState = "notFriends";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptFriendRequest() {
        Calendar calendarCurrentDate = Calendar.getInstance();

        SimpleDateFormat simpleDateFormatCurrentDate = new SimpleDateFormat("dd-MMMM-yyyy");

        final String CURRENT_DATE = simpleDateFormatCurrentDate.format(calendarCurrentDate.getTime());

        friendsReference.child(senderUserId).child(receiverUserId).setValue(CURRENT_DATE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendsReference.child(receiverUserId).child(senderUserId).setValue(CURRENT_DATE)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendRequestsReference.child(senderUserId).child(receiverUserId).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            friendRequestsReference.child(receiverUserId).child(senderUserId).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                sendFriendRequestButton.setEnabled(true);
                                                                                sendFriendRequestButton.setText("UN-FRIEND");

                                                                                currentState = "friends";
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

    private void cancelFriendRequest() {
        friendRequestsReference.child(senderUserId).child(receiverUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestsReference.child(receiverUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendRequestButton.setEnabled(true);
                                                sendFriendRequestButton.setText("SEND FRIEND REQUEST");

                                                currentState = "notFriends";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendFriendRequest() {
        friendRequestsReference.child(senderUserId).child(receiverUserId).child("requestType").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestsReference.child(receiverUserId).child(senderUserId).child("requestType").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendRequestButton.setEnabled(true);
                                                sendFriendRequestButton.setText("CANCEL FRIEND REQUEST");

                                                currentState = "requestSent";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
