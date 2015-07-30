package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostDetailsActivity extends AppCompatActivity {
    @InjectView(R.id.postDetailsProfileImageView) ImageView mPostDetailsProfileImageView;
    @InjectView(R.id.postDetailsScreenNameLabel) TextView mPostDetailsScreenNameLabel;
    @InjectView(R.id.postDetailsProfileNameLable) TextView mPostDetailsProfileNameLable;
    @InjectView(R.id.postDetailsMessageLabel) TextView mPostDetailsMessageLabel;
    @InjectView(R.id.postDetailsCommentLabel) TextView mPostDetailsCommentLabel;
    @InjectView(R.id.postDetailsLikeLabel) TextView mPostDetailsLikeLabel;
    @InjectView(R.id.postDetailsTimeLabel) TextView mPostDetailsTimeLabel;
    @InjectView(R.id.postDetailsCommentsCounter) TextView mPostDetailsCommentsCounter;
    @InjectView(R.id.postDetailsLikesCounter) TextView mPostDetailsLikesCounter;
//    @InjectView(R.id.imageView) ImageView mImageView;
//    @InjectView(R.id.imageView2) ImageView mImageView2;
//    @InjectView(R.id.root) RelativeLayout mRoot;
    private Toolbar mToolbar;
    protected String mPostMessageObjectId;
    protected String mPostMessageCreatorId;
    protected Map<String, Object> mPostMessageLikes;
    protected String mTeam;
    protected String mSenderProfileImageView;
    protected String mScreenName;
    protected String mpostMessage;
    protected String mPostCreatedAt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.inject(this);

        mPostMessageObjectId = getIntent().getStringExtra(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID);
        mSenderProfileImageView = getIntent().getStringExtra(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL);
        mScreenName = getIntent().getStringExtra(ParseConstants.KEY_SCREEN_NAME_COLUMN);
        mpostMessage = getIntent().getStringExtra(ParseConstants.KEY_POST_MESSAGE_COLUMN);
        mPostCreatedAt = getIntent().getStringExtra(ParseConstants.KEY_POST_MESSAGE_CREATED_AT);

        Picasso.with(PostDetailsActivity.this)
                .load(mSenderProfileImageView)
                .resize(180, 180)
                .into(mPostDetailsProfileImageView);

        mPostDetailsScreenNameLabel.setText(mScreenName);
        mPostDetailsProfileNameLable.setText(mScreenName);
        mPostDetailsMessageLabel.setText(mpostMessage);
        mPostDetailsTimeLabel.setText(mPostCreatedAt);
        retrieveTeams();
        retrieveComments();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_details, menu);
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

    public void retrieveComments(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_COMMENTS_CLASS);
        query.whereEqualTo(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, mPostMessageObjectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    //success
                    mPostDetailsCommentsCounter.setText(list.size() + "");
                    //Log.d("commentsSize", list.size() + "");
                }
                else{
                    //error
                }
            }
        });


    }

    public void retrieveTeams(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_TEAMS_PARSE_CLASS);
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mPostMessageObjectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    //success
                    mPostMessageLikes = ((list.get(0).getMap("likes") != null) ? list.get(0).getMap("likes") : new HashMap<String, Object>());
                    mTeam = list.get(0).getString(ParseConstants.KEY_TEAM_COLUMN);
                    mPostMessageCreatorId = list.get(0).getString(ParseConstants.KEY_SENDER_ID);
                    mPostDetailsLikesCounter.setText(mPostMessageLikes.size() +"");

                }
                else{
                    //error
                }
            }
        });

    }
}
