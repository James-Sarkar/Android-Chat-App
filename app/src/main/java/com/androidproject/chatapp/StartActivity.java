package com.androidproject.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by James Sarkar.
 */

public class StartActivity extends AppCompatActivity {

    private Button createNewAccountButton;

    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        createNewAccountButton = (Button) findViewById(R.id.create_new_account_button);
        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        logInButton = (Button) findViewById(R.id.start_log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }
}
