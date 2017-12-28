package com.androidproject.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    Toolbar mToolbar;

    CircleImageView settingsUserPicture;

    TextView settingsUserDisplayName, settingsUserBio;

    Button changeUserPictureButton, changeUserBioButton;

    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        settingsUserPicture = (CircleImageView) findViewById(R.id.setting_user_picture);

        settingsUserDisplayName = (TextView) findViewById(R.id.settings_user_display_name);
        settingsUserBio = (TextView) findViewById(R.id.settings_user_bio);

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                settingsUserDisplayName.setText(dataSnapshot.child("User_Display_Name").getValue().toString());
                settingsUserBio.setText(dataSnapshot.child("User_Profile_Bio").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeUserPictureButton = (Button) findViewById(R.id.change_picture_button);
        changeUserPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        changeUserBioButton = (Button) findViewById(R.id.change_bio_button);
        changeUserBioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
