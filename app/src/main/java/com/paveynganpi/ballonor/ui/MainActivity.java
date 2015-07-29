package com.paveynganpi.ballonor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.SectionsPagerAdapter;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.paveynganpi.ballonor.utils.SlidingTabLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private SlidingTabLayout mTabs;
    protected String mDrawerItemTeam;
    protected ParseUser mCurrentUser;
    protected ArrayList<String> favouriteTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser == null) {
            navigateToLogin();
        }

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setTitle("");


        NavigationDrawerFragment navigationDrawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_navigation_drawer);

        navigationDrawerFragment.setUpDrawer(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        navigationDrawerFragment.addDrawerTeams();
        favouriteTeams = navigationDrawerFragment.getFavouriteTeams();


        mViewPager = (ViewPager) findViewById(R.id.pager);
        if(mDrawerItemTeam == null){
            Log.d("drawerItemTeam", "mDrawerItemTeam is null");
            if(favouriteTeams.isEmpty())
                mViewPager.setAdapter(new SectionsPagerAdapter(this, getSupportFragmentManager(), "Chelseafc"));
            else
                mViewPager.setAdapter(new SectionsPagerAdapter(this, getSupportFragmentManager(), favouriteTeams.get(0)));
        }
        else{
            Log.d("drawerItemTeam", "mDrawerItemTeam is "+ mDrawerItemTeam);
            mViewPager.setAdapter(new SectionsPagerAdapter(this, getSupportFragmentManager(), mDrawerItemTeam));
        }
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setViewPager(mViewPager);

        ParseAnalytics.trackAppOpened(getIntent());

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onDrawerItemClicked(String team){
        Log.d("drawerItemTeam", "onDrawerItemClickes is called");
        mDrawerItemTeam = team;
        mViewPager.setCurrentItem(0);
        mViewPager.setAdapter(new SectionsPagerAdapter(this, getSupportFragmentManager(), team));
        mTabs.setViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_logout:
                ParseUser.logOut();
                //move to login screen
                navigateToLogin();
            case R.id.action_profile:
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.putExtra(ParseConstants.KEY_USER_ID, mCurrentUser.getObjectId());
                startActivity(intent);
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            ParseUser.logOut();
//            //move to login screen
//            navigateToLogin();
//        }

        return super.onOptionsItemSelected(item);
    }

}
