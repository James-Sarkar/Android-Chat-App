package com.androidproject.chatapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidproject.chatapp.Fragment.ConversationsFragment;
import com.androidproject.chatapp.Fragment.FriendsFragment;
import com.androidproject.chatapp.Fragment.RequestsFragment;

/**
 * Created by James Sarkar.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private static final int NUMBER_OF_TABS = 3;

    private static final String REQUESTS_FRAGMENT_TITLE = "Requests";

    private static final String CONVERSATIONS_FRAGMENT_TITLE = "Conversations";

    private static final String FRIENDS_FRAGMENT_TITLE = "Friends";

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RequestsFragment();

            case 1:
                return new ConversationsFragment();

            case 2:
                return new FriendsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return REQUESTS_FRAGMENT_TITLE;

            case 1:
                return CONVERSATIONS_FRAGMENT_TITLE;

            case 2:
                return FRIENDS_FRAGMENT_TITLE;

            default:
                return null;
        }
    }
}
