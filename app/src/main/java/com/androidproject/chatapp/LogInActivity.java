package com.androidproject.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends AppCompatActivity {

    EditText logInEmail, logInPassword;

    Button logInButton;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mToolbar = (Toolbar) findViewById(R.id.log_in_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Android Chat App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logInEmail = (EditText) findViewById(R.id.email);
        logInPassword = (EditText) findViewById(R.id.password);

        logInButton = (Button) findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
