package com.androidproject.chatapp.Model;

/**
 * Created by James Sarkar.
 */

public class FriendRequest {

    private String userDisplayName, userPofileBio, userThumbnail;

    public FriendRequest() {

    }

    public FriendRequest(String userDisplayName, String userPofileBio, String userThumbnail) {
        this.userDisplayName = userDisplayName;
        this.userPofileBio = userPofileBio;
        this.userThumbnail = userThumbnail;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserPofileBio() {
        return userPofileBio;
    }

    public void setUserPofileBio(String userPofileBio) {
        this.userPofileBio = userPofileBio;
    }

    public String getUserThumbnail() {
        return userThumbnail;
    }

    public void setUserThumbnail(String userThumbnail) {
        this.userThumbnail = userThumbnail;
    }
}
