package com.androidproject.chatapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.androidproject.chatapp.Adapter.MessageAdapter;
import com.androidproject.chatapp.Common.LastSeenTime;
import com.androidproject.chatapp.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsActivity extends AppCompatActivity {

    private String messageReceiverUserId, messageReceiverUserDisplayName;

    private Toolbar mToolbar;

    private TextView userDisplayName, userLastSeen;

    private CircleImageView userProfilePicture;

    private DatabaseReference rootReference;

    private ImageButton sendMessageButton, selectAnImageButton;

    private EditText inputMessageBox;

    private FirebaseAuth mAuth;

    private String messageSenderUserId;

    private RecyclerView messageHistory;

    private final List<Message> MESSAGES = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        mAuth = FirebaseAuth.getInstance();

        messageSenderUserId = mAuth.getCurrentUser().getUid();

        rootReference = FirebaseDatabase.getInstance().getReference();

        messageAdapter = new MessageAdapter(MESSAGES);

        messageHistory = (RecyclerView) findViewById(R.id.message_history);

        linearLayoutManager = new LinearLayoutManager(this);

        messageHistory.setHasFixedSize(true);
        messageHistory.setLayoutManager(linearLayoutManager);
        messageHistory.setAdapter(messageAdapter);

        messageReceiverUserId = getIntent().getExtras().get("userId").toString();
        messageReceiverUserDisplayName = getIntent().getExtras().get("userDisplayName").toString();

        mToolbar = (Toolbar) findViewById(R.id.conversations_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View actionBarView = inflater.inflate(R.layout.conversations_custom_bar, null);

        getSupportActionBar().setCustomView(actionBarView);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchMessages();

        selectAnImageButton = (ImageButton) findViewById(R.id.select_an_image_button);

        inputMessageBox = (EditText) findViewById(R.id.message_box);

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

    private void fetchMessages() {
        rootReference.child("Messages").child(messageSenderUserId).child(messageReceiverUserId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Message message = dataSnapshot.getValue(Message.class);

                        MESSAGES.add(message);

                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage() {
        String messageText = inputMessageBox.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(getBaseContext(), "Please write a message", Toast.LENGTH_LONG).show();
        } else {
            String messageSenderReference = "Messages/" + messageSenderUserId + "/" + messageReceiverUserId;

            String messageReceiverReference = "Messages/" + messageReceiverUserId + "/" + messageSenderUserId;

            DatabaseReference userMessagesReference = rootReference.child("Messages")
                    .child(messageSenderUserId).child(messageReceiverUserId).push();

            String messagePushId = userMessagesReference.getKey();

            Map messageBody = new HashMap();
            messageBody.put("message", messageText);
            messageBody.put("seen", false);
            messageBody.put("type", "text");
            messageBody.put("time", ServerValue.TIMESTAMP);
            messageBody.put("from", messageSenderUserId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderReference + "/" + messagePushId, messageBody);
            messageBodyDetails.put(messageReceiverReference + "/" + messagePushId, messageBody);

            rootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("DEBUG", databaseError.getMessage().toString());
                    }
                    inputMessageBox.setText("");
                }
            });
        }
    }
}
