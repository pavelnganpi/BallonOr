package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.UserProfileSectionsPagerAdapter;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.paveynganpi.ballonor.utils.SlidingTabLayout;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    @InjectView(R.id.profile_image) CircleImageView mProfileImage;
    @InjectView(R.id.userProfileTabs) SlidingTabLayout mUserProfileTabs;
    @InjectView(R.id.userProfilePager) ViewPager mUserProfilePager;
    private Toolbar mToolbar;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);

        mCurrentUser = ParseUser.getCurrentUser();
        mToolbar = (Toolbar) findViewById(R.id.app_bar_user_profile);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mUserProfilePager.setAdapter(new UserProfileSectionsPagerAdapter(this, getSupportFragmentManager()));
        mUserProfileTabs.setViewPager(mUserProfilePager);
        String profileImageUrl = mCurrentUser.getString(ParseConstants.KEY_PROFILE_IMAGE_URL);

        Picasso.with(this)
                .load(profileImageUrl)
                .into(mProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
