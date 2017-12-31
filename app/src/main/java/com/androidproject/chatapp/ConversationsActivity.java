package com.androidproject.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ConversationsActivity extends AppCompatActivity {

    private String messageReceiverUserId, messageReceiverUserDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        messageReceiverUserId = getIntent().getExtras().get("userId").toString();
        messageReceiverUserDisplayName = getIntent().getExtras().get("userDisplayName").toString();

        Toast.makeText(getBaseContext(), messageReceiverUserDisplayName, Toast.LENGTH_LONG).show();
    }
}
