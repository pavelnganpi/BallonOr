package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.paveynganpi.ballonor.ui.FollowersFragments;
import com.paveynganpi.ballonor.ui.FollowingFragment;
import com.paveynganpi.ballonor.ui.UserAllPostsFragment;
import com.paveynganpi.ballonor.ui.UserFavouritePostsFragment;

import java.util.Locale;

/**
 * Created by paveynganpi on 7/23/15.
 */
public class UserProfileSectionsPagerAdapter extends FragmentStatePagerAdapter {
    public Context mContext;

    public UserProfileSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new UserAllPostsFragment();
            case 1:
                return new UserFavouritePostsFragment();
            case 2:
                return new FollowersFragments();
            case 3:
                return new FollowingFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Posts".toUpperCase(l);
            case 1:
                return "Favourites".toUpperCase(l);
            case 2:
                return "Followers".toUpperCase(l);
            case 3:
                return "Following".toUpperCase(l);
        }
        return null;
    }
}
