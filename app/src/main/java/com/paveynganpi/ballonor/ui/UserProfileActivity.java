package com.paveynganpi.ballonor.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
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
    @InjectView(R.id.profile_image_edit_view)
    ImageView mProfileImageEditView;
    @InjectView(R.id.followImageButton)
    ImageButton mFollowImageButton;
    private Toolbar mToolbar;
    protected ParseUser mCurrentUser;
    protected ParseObject mFollowRelation;
    protected String mUserId;
    protected ParseUser mProfileUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);

        mCurrentUser = ParseUser.getCurrentUser();
        mToolbar = (Toolbar) findViewById(R.id.app_bar_user_profile);
        mUserId = UserProfileActivity.this.getIntent().getStringExtra(ParseConstants.KEY_USER_ID);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mUserProfilePager.setAdapter(new UserProfileSectionsPagerAdapter(this, getSupportFragmentManager()));
        mUserProfileTabs.setViewPager(mUserProfilePager);
        String profileImageUrl = getIntent().getStringExtra(ParseConstants.KEY_PROFILE_IMAGE_URL);

        //get ParseUser object for mUserId
        setProfileUser();

        Picasso.with(this)
                .load(profileImageUrl)
                .into(mProfileImage);

        mProfileImageEditView.setOnClickListener(mProfileImageEditViewListener);
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

    protected View.OnClickListener mProfileImageEditViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
            builder.setItems(R.array.camera_choices, mDialogListener);
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };

    protected AdapterView.OnClickListener mFollowImageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mFollowImageButton.isSelected()) {
                mFollowImageButton.setSelected(true);
                mFollowImageButton.setImageResource(R.drawable.ic_account_check);
                mFollowImageButton.setBackgroundResource(R.drawable.account_checked_button_shape);

                //add visiting user as a user you are following
                mFollowRelation = new ParseObject(ParseConstants.KEY_FOLLOW_CLASS);
                mFollowRelation.put("from", mCurrentUser);
                mFollowRelation.put("to", mProfileUser);
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
                            Log.d("followwork", "woked");
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

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {


        }
    };


}
