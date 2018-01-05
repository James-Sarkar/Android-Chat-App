package com.androidproject.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/**
 * Created by James Sarkar.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    private static final String IMAGE_EXTENSION = ".jpg";

    private Toolbar mToolbar;

    private CircleImageView settingsUserPicture;

    private TextView settingsUserDisplayName, settingsUserBio;

    private Button changeUserPictureButton, changeUserBioButton;

    private DatabaseReference currentUserReference;

    private FirebaseAuth mAuth;

    private StorageReference profileImagesReference, thumbnailImagesReference;

    private ProgressDialog progressDialog;

    private Bitmap thumbnailBitmap = null;

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

        profileImagesReference = FirebaseStorage.getInstance().getReference().child("profileImages");

        thumbnailImagesReference = FirebaseStorage.getInstance().getReference().child("thumbnailImages");

        currentUserReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(mAuth.getCurrentUser().getUid());
        currentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                settingsUserDisplayName.setText(dataSnapshot.child("userDisplayName").getValue().toString());
                settingsUserBio.setText(dataSnapshot.child("userProfileBio").getValue().toString());

                Picasso.with(getBaseContext())
                        .load(dataSnapshot.child("userProfileImage").getValue().toString())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_profile)
                        .into(settingsUserPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getBaseContext())
                                        .load(dataSnapshot.child("userProfileImage").getValue().toString())
                                        .placeholder(R.drawable.default_profile)
                                        .into(settingsUserPicture);
                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currentUserReference.keepSynced(true);

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
                progressDialog.setTitle("Updating Profile Picture");
                progressDialog.setMessage("Please wait while we update your profile picture");
                progressDialog.show();

                Uri resultUri = result.getUri();

                final File thumbnailPath = new File(resultUri.getPath());

                try {
                    thumbnailBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumbnailPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

                final byte[] THUMBNAIL_BYTES = byteArrayOutputStream.toByteArray();

                final StorageReference THUMBNAIL_IMAGE_PATH = thumbnailImagesReference.child(mAuth.getCurrentUser().getUid() + IMAGE_EXTENSION);

                StorageReference profileImagePath = profileImagesReference.child(mAuth.getCurrentUser().getUid() + IMAGE_EXTENSION);
                profileImagePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String DOWNLOADED_URL = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = THUMBNAIL_IMAGE_PATH.putBytes(THUMBNAIL_BYTES);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbnailTask) {
                                    String downloadedThumbnailUrl = thumbnailTask.getResult().getDownloadUrl().toString();

                                    if (task.isSuccessful()) {
                                        Map updateUserData = new HashMap<>();
                                        updateUserData.put("userProfileImage", DOWNLOADED_URL);
                                        updateUserData.put("userThumbnail", downloadedThumbnailUrl);


                                        currentUserReference.updateChildren(updateUserData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();

                                                    Toast.makeText(getBaseContext(), "Profile picture updated", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(getBaseContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
