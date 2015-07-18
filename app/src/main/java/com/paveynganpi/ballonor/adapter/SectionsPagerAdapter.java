package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.paveynganpi.ballonor.ui.ArsenalfcFragment;
import com.paveynganpi.ballonor.ui.ChelseafcFragment;
import com.paveynganpi.ballonor.ui.RealMadridfcFragment;

import java.util.Locale;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    protected Context mContext;//creating context so as to call it in the getString() getPageTitle(), since we are not in an activity
    protected String mTeam;

    public SectionsPagerAdapter(Context context, FragmentManager fm, String team) {

        super(fm);
        Log.d("drawerItemTeam", "sectionPager constructor is called with " + team);
        mContext = context;
        mTeam = team;
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        android.support.v4.app.Fragment fragment = new ChelseafcFragment();

        switch (position){
            case 0:
                if(mTeam.equals("Chelseafc") || mTeam.equals("Chelsea FC")){
                    Log.d("drawerItemTeam", "team in sectionPager chelsea is " + mTeam);
                    fragment = new RealMadridfcFragment();
                }
                else if(mTeam.equals("Real Madrid CF")){
                    fragment = new ChelseafcFragment();
                }
                return fragment;
            case 1:
                return new ArsenalfcFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mTeam.toUpperCase(l);
            case 1:
                return "Arsenald".toUpperCase(l);
        }
        return mTeam.toUpperCase(l);
    }
}
