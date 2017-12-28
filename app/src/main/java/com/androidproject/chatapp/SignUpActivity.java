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

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    EditText mSignUpDisplayName, mSignUpEmail, mSignUpPassword;

    Button signUpButton, cancelButton;

    Toolbar mToolbar;

    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.sign_up_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        mSignUpDisplayName = (EditText) findViewById(R.id.dsiplay_name_signup);
        mSignUpEmail = (EditText) findViewById(R.id.email_signup);
        mSignUpPassword = (EditText) findViewById(R.id.password_signup);

        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signUpDisplayName = mSignUpDisplayName.getText().toString();
                String signUpEmail = mSignUpEmail.getText().toString();
                String signUpPassword = mSignUpPassword.getText().toString();

                createAccount(signUpDisplayName, signUpEmail, signUpPassword);
            }
        });

        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createAccount(String signUpDisplayName, String signUpEmail, String signUpPassword) {
        if (TextUtils.isEmpty(signUpDisplayName)) {
            Toast.makeText(getBaseContext(), "Please enter a display name", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(signUpEmail)) {
            Toast.makeText(getBaseContext(), "Please provide a valid email address", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(signUpPassword)) {
            Toast.makeText(getBaseContext(), "Please enter a password", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setTitle("Creating account");
            progressDialog.setMessage("Please wait while we create your account");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(signUpEmail, signUpPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Failed to create an account: " + task.getException(), Toast.LENGTH_LONG).show();
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }
}
