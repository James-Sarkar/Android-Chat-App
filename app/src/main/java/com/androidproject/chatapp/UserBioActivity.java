package com.androidproject.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserBioActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText userBio;

    private Button saveButton, discardButton;

    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bio);

        mToolbar = (Toolbar) findViewById(R.id.user_bio_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Bio");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(mAuth.getCurrentUser().getUid());
        databaseReference.keepSynced(true);

        userBio = (EditText) findViewById(R.id.user_bio);
        userBio.setImeOptions(EditorInfo.IME_ACTION_DONE);
        userBio.setRawInputType(InputType.TYPE_CLASS_TEXT);
        userBio.setText(getIntent().getExtras().get("user_old_bio").toString());

        saveButton = (Button) findViewById(R.id.save_bio_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newBio = userBio.getText().toString();

                updateUserBio(newBio);
            }
        });

        discardButton = (Button) findViewById(R.id.discard_bio_button);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserBioActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateUserBio(String newBio) {
        if (TextUtils.isEmpty(newBio)) {
            Toast.makeText(getBaseContext(), "Your bio cannot be empty.", Toast.LENGTH_LONG).show();
        } else if (newBio.length() > 120) {
            Toast.makeText(getBaseContext(), "Your bio cannot be more than 120 characters long.", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setTitle("Updating Your Bio");
            progressDialog.setMessage("Please wait while we update your bio.");
            progressDialog.show();

            databaseReference.child("userProfileBio").setValue(newBio)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Your bio has been updated", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(UserBioActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    }
}
