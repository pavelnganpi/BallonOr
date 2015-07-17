package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.paveynganpi.ballonor.ui.ArsenalfcFragment;
import com.paveynganpi.ballonor.ui.ChelseafcFragment;
import com.paveynganpi.ballonor.ui.RealMadridfcFragment;

import java.util.Locale;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;//creating context so as to call it in the getString() getPageTitle(), since we are not in an activity

    public SectionsPagerAdapter(Context context, FragmentManager fm) {

        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        switch (position){
            case 0:
                return new ChelseafcFragment();
            case 1:
                return new RealMadridfcFragment();
            case 2:
                return new ArsenalfcFragment();
        }
        return  null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "chelseafc".toUpperCase(l);
            case 1:
                return "Real Madrid".toUpperCase(l);
            case 2:
                return "Arsenal".toUpperCase(l);
        }
        return null;
    }
}
