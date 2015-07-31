package com.paveynganpi.ballonor.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.pojo.Notifications;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
    protected ParseUser mCurrentUser;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.inject(this);

        mContext = PostDetailsActivity.this;
        mCurrentUser = ParseUser.getCurrentUser();
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


    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    if(mPostMessageLikes.containsKey(mCurrentUser.getObjectId())){
                        mPostDetailsLikeLabel.setSelected(true);
                        mPostDetailsLikeLabel.setText("LIKED");
                    }
                    else{
                        mPostDetailsLikeLabel.setSelected(false);
                        mPostDetailsLikeLabel.setText("LIKE");
                    };
                    setPostDetailsLikeLabel(list.get(0));
                    setPostDetailsCommentLabel();

                }
                else{
                    //error
                }
            }
        });

    }

    public void setPostDetailsLikeLabel(final ParseObject teamsObject){
        mPostDetailsLikeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPostDetailsLikeLabel.isSelected()) {
                    mPostMessageLikes.put(mCurrentUser.getObjectId(), mCurrentUser);
                    int likes = mPostMessageLikes.size();
                    mPostDetailsLikesCounter.setText((likes) + "");
                    teamsObject.put("likes", mPostMessageLikes);
                    teamsObject.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikes.size());
                    mPostDetailsLikeLabel.setSelected(true);
                    mPostDetailsLikeLabel.setText("LIKED");

                    //add post objectId to likedPosts table
                    mCurrentUser.add("likedPosts", teamsObject.getObjectId());
                    Notifications.sendPushNotifications(teamsObject);
                    //sendPushNotifications(teamsObject);
                } else if (mPostDetailsLikeLabel.isSelected()) {
                    mPostMessageLikes.remove(mCurrentUser.getObjectId());
                    int likes = mPostMessageLikes.size();
                    mPostDetailsLikesCounter.setText((likes) + "");
                    teamsObject.put("likes", mPostMessageLikes);
                    teamsObject.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikes.size());
                    mPostDetailsLikeLabel.setSelected(false);
                    mPostDetailsLikeLabel.setText("LIKE");

                    //remove post objectId from likedPosts table
                    ArrayList<String> likedPosts = (ArrayList<String>) mCurrentUser.get("likedPosts");
                    likedPosts.remove(teamsObject.getObjectId());
                    mCurrentUser.put("likedPosts", likedPosts);

                }

                teamsObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //success
                        } else {
                            //error
                            AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailsActivity.this);
                            builder.setMessage("Sorry, an error occured, Please try again")
                                    .setTitle("Opps, Error")
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });

                mCurrentUser.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //success
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailsActivity.this);
                            builder.setMessage("Sorry, an error occured liking this post, Please try again")
                                    .setTitle("Opps, Error")
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });
    }

    public void setPostDetailsCommentLabel(){
        mPostDetailsCommentLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostMessageCommentsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, mPostMessageObjectId);
                intent.putExtra("TeamName", mTeam);
                intent.putExtra(ParseConstants.KEY_SENDER_ID, mPostMessageCreatorId);
                mContext.startActivity(intent);
            }
        });
    }

    protected void sendPushNotifications(ParseObject liker) {

        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER_ID, liker.getString(ParseConstants.KEY_SENDER_ID));

        //send push notification
        final ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(liker.getString(ParseConstants.KEY_SCREEN_NAME_COLUMN) + " liked your post");
        push.sendInBackground();

    }

}
