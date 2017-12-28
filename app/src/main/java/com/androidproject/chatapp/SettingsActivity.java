package com.androidproject.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    private static final String IMAGE_EXTENSION = ".jpg";

    Toolbar mToolbar;

    CircleImageView settingsUserPicture;

    TextView settingsUserDisplayName, settingsUserBio;

    Button changeUserPictureButton, changeUserBioButton;

    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    StorageReference storageReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        settingsUserPicture = (CircleImageView) findViewById(R.id.setting_user_picture);

        settingsUserDisplayName = (TextView) findViewById(R.id.settings_user_display_name);
        settingsUserBio = (TextView) findViewById(R.id.settings_user_bio);

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                settingsUserDisplayName.setText(dataSnapshot.child("User_Display_Name").getValue().toString());
                settingsUserBio.setText(dataSnapshot.child("User_Profile_Bio").getValue().toString());

                if (!dataSnapshot.child("User_Image").getValue().toString().equals("default_profile")) {
                    Picasso.with(getBaseContext()).load(dataSnapshot.child("User_Image").getValue().toString()).into(settingsUserPicture);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeUserPictureButton = (Button) findViewById(R.id.change_picture_button);
        changeUserPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICK);
            }
        });

        changeUserBioButton = (Button) findViewById(R.id.change_bio_button);
        changeUserBioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, UserBioActivity.class);
                intent.putExtra("user_old_bio", settingsUserBio.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                StorageReference path = storageReference.child(mAuth.getCurrentUser().getUid() + IMAGE_EXTENSION);
                path.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressDialog.setTitle("Updating Profile Picture");
                            progressDialog.setMessage("Please wait while we update your profile picture");
                            progressDialog.show();

                            String downloadedUrl = task.getResult().getDownloadUrl().toString();

                            databaseReference.child("User_Image").setValue(downloadedUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();

                                            Toast.makeText(getBaseContext(), "Profile picture updated", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getBaseContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                //TODO
            }
        }
    }
}
