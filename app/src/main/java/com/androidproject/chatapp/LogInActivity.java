package com.androidproject.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private EditText logInEmail, logInPassword;

    private Button logInButton;

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.log_in_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Log In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logInEmail = (EditText) findViewById(R.id.email);
        logInPassword = (EditText) findViewById(R.id.password);

        logInButton = (Button) findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = logInEmail.getText().toString();
                String password = logInPassword.getText().toString();

                logInUser(email, password);
            }
        });
    }

    private void logInUser(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getBaseContext(), "Please enter your email",Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getBaseContext(), "Please enter your password",Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setTitle("Logging in");
            progressDialog.setMessage("Please wait while we log you in");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "Error: " + task.getException(),Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();
                    }
                });
        }
    }
}
