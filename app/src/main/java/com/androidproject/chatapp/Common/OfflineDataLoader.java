package com.androidproject.chatapp.Common;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by James Sarkar.
 */

public class OfflineDataLoader extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Load user display names and user bios offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Load user profile pictures offline
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));

        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
