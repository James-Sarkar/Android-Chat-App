package com.androidproject.chatapp.Model;

/**
 * Created by James Sarkar.
 */

public class User {

    private String userDisplayName, userProfileImage, userProfileBio, userThumbnail;

    public User() {

    }

    public User(String userDisplayName, String userProfileImage, String userProfileBio, String userThumbnail) {
        this.userDisplayName = userDisplayName;
        this.userProfileImage = userProfileImage;
        this.userProfileBio = userProfileBio;
        this.userThumbnail = userThumbnail;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUserProfileBio() {
        return userProfileBio;
    }

    public void setUserProfileBio(String userProfileBio) {
        this.userProfileBio = userProfileBio;
    }

    public String getUserThumbnail() {
        return userThumbnail;
    }

    public void setUserThumbnail(String userThumbnail) {
        this.userThumbnail = userThumbnail;
    }
}
