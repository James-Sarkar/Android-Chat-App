package com.androidproject.chatapp.Model;

/**
 * Created by James Sarkar.
 */

public class Conversation {

    private String userProfileBio;

    public Conversation() {

    }

    public Conversation(String userProfileBio) {
        this.userProfileBio = userProfileBio;
    }

    public String getUserProfileBio() {
        return userProfileBio;
    }

    public void setUserProfileBio(String userProfileBio) {
        this.userProfileBio = userProfileBio;
    }
}
