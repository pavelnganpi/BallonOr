package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.UserProfileSectionsPagerAdapter;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.paveynganpi.ballonor.utils.SlidingTabLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    @InjectView(R.id.profile_image)
    CircleImageView mProfileImage;
    @InjectView(R.id.userProfileTabs)
    SlidingTabLayout mUserProfileTabs;
    @InjectView(R.id.userProfilePager)
    ViewPager mUserProfilePager;
    @InjectView(R.id.userProfileScreenName)
    TextView mUserProfileScreenName;
    @InjectView(R.id.userProfileFullName)
    TextView mUserProfileFullName;
    @InjectView(R.id.followersCount)
    TextView mFollowersCount;
    @InjectView(R.id.followersLable)
    TextView mFollowersLable;
    @InjectView(R.id.followingCount)
    TextView mFollowingCount;
    @InjectView(R.id.followingLable)
    TextView mFollowingLable;
    @InjectView(R.id.followImageButton)
    ImageButton mFollowImageButton;
    private Toolbar mToolbar;
    protected ParseUser mCurrentUser;
    protected ParseObject mFollowRelation;
    protected String mUserId;
    protected ParseUser mProfileUser;
    protected String mScreenName;
    protected String mFullName;
    protected String mProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);

//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCurrentUser = ParseUser.getCurrentUser();
        mToolbar = (Toolbar) findViewById(R.id.app_bar_user_profile);
        mUserId = getIntent().getStringExtra(ParseConstants.KEY_USER_ID);
        mScreenName = getIntent().getStringExtra(ParseConstants.KEY_SCREEN_NAME_COLUMN);
        mFullName = getIntent().getStringExtra(ParseConstants.KEY_FULL_NAME);
        mProfileImageUrl = getIntent().getStringExtra(ParseConstants.KEY_PROFILE_IMAGE_URL);

        mUserProfileScreenName.setText(mScreenName);
        mUserProfileFullName.setText(mFullName);

        mUserProfilePager.setAdapter(new UserProfileSectionsPagerAdapter(this, getSupportFragmentManager()));
        mUserProfileTabs.setViewPager(mUserProfilePager);


    }

    @Override
    protected void onResume() {
        super.onResume();

        //get ParseUser object for mUserId
        setProfileUser();

        try {
            setFollowersCount();
            setFollowingCount();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(this)
                .load(mProfileImageUrl)
                .into(mProfileImage);

        mFollowImageButton.setOnClickListener(mFollowImageButtonListener);

    }

    private void setProfileUser() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    //success
                    mProfileUser = list.get(0);
                    setFollowImageButton();
                } else {
                    //error
                }
            }
        });
        Log.d("followimage", "reached");
    }

    public void setFollowersCount () throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_FOLLOW_CLASS);
        query.whereEqualTo(ParseConstants.KEY_TO_USER_ID, mUserId);
        int count = query.count();
        mFollowersCount.setText(count+"");

    }

    public void setFollowingCount () throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_FOLLOW_CLASS);
        query.whereEqualTo(ParseConstants.KEY_FROM_USER_ID, mUserId);
        int count = query.count();
        mFollowingCount.setText(count+"");

    }

    //set the follow button based on if the current
    //user is following the profile user
    public void setFollowImageButton(){

        //if current user is checking his/her profile,
        //hide the follow button
        Log.d("followimage", "setFollowImageBUtton is reached");
        if(mUserId.equals(mCurrentUser.getObjectId())){
            mFollowImageButton.setVisibility(View.INVISIBLE);
            Log.d("followimage", "current user is same");
        }
        else{
            Log.d("followimage", "else block reached");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_FOLLOW_CLASS);
            query.whereEqualTo("from", mCurrentUser);
            query.whereEqualTo("to", mProfileUser);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if(e == null){
                        //success
                        if(list.size() != 0){
                            mFollowImageButton.setSelected(true);
                            mFollowImageButton.setImageResource(R.drawable.ic_account_check);
                            mFollowImageButton.setBackgroundResource(R.drawable.account_checked_button_shape);
                        }
                    }
                    else{
                        //error
                        Log.d("followwork", "error");
                    }
                }
            });

        }

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

    protected AdapterView.OnClickListener mFollowImageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mFollowImageButton.isSelected()) {
                mFollowImageButton.setSelected(true);
                mFollowImageButton.setImageResource(R.drawable.ic_account_check);
                mFollowImageButton.setBackgroundResource(R.drawable.account_checked_button_shape);

                //add visiting user as a user you are following
                mFollowRelation = new ParseObject(ParseConstants.KEY_FOLLOW_CLASS);
                mFollowRelation.put(ParseConstants.KEY_FROM, mCurrentUser);
                mFollowRelation.put(ParseConstants.KEY_TO, mProfileUser);
                mFollowRelation.put(ParseConstants.KEY_FROM_USER_ID, mCurrentUser.getObjectId());
                mFollowRelation.put(ParseConstants.KEY_TO_USER_ID, mProfileUser.getObjectId());
                mFollowRelation.saveEventually();


            } else if (mFollowImageButton.isSelected()) {
                mFollowImageButton.setSelected(false);
                mFollowImageButton.setImageResource(R.drawable.ic_account_plus);
                mFollowImageButton.setBackgroundResource(R.drawable.account_button_plus_shape);

                //remove visiting user as user you are following
                ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_FOLLOW_CLASS);
                query.whereEqualTo("from", mCurrentUser);
                query.whereEqualTo("to", mProfileUser);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if(e == null){
                            //success
                            list.get(0).deleteEventually();
                        }
                        else{
                            //error
                            Log.d("followwork", "error");
                        }
                    }
                });


            }
        }
    };
}
