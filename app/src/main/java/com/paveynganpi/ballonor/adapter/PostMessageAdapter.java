package com.paveynganpi.ballonor.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.pojo.Notifications;
import com.paveynganpi.ballonor.ui.PostDetailsActivity;
import com.paveynganpi.ballonor.ui.PostMessageCommentsActivity;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paveynganpi on 6/26/15.
 */
public class PostMessageAdapter extends RecyclerView.Adapter<PostMessageAdapter.PostMessageViewHolder> {
    private Context mContext;
    private List<ParseObject> messages;
    private ParseUser mCurrentUser;
    private String mCurrentUserFullName;
    private Twitter mCurrentTwitterUser;
    private String mTeam;

    public PostMessageAdapter(Context context, List<ParseObject> messages, String team) {
        this.mContext = context;
        this.messages = messages;
        mTeam = team;
        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentTwitterUser = ParseTwitterUtils.getTwitter();
    }

    @Override
    public PostMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_item, parent, false);
        mCurrentUserFullName = mCurrentUser.getString(ParseConstants.KEY_TWITTER_FULL_NAME);
        PostMessageViewHolder postMessageViewHolder = new PostMessageViewHolder(view);
        return postMessageViewHolder;
    }

    @Override
    public void onBindViewHolder(PostMessageViewHolder postMessageViewHolder, int position) {
        postMessageViewHolder.bindPostMessages(messages.get(position), position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void refill(List<ParseObject> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    public class PostMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mProfileImageView;
        public TextView mScreenNameLabel;
        public TextView mProfileNameLable;
        public TextView mPostMessageLabel;
        public TextView mPostMessageLikeLabel;
        protected TextView mPostMessageTimeLabel;
        TextView mPostMessageCommentLabel;
        TextView mPostMessageCommentsCounter;
        TextView mPostMessagelikesCounter;
        View parent;
        String mSenderProfileImageUrl;
        Map<String, Object> mPostMessageLikesMap;
        List<ParseObject> mPostMessageComments;
        protected HashMap<String, String> finalMap;
        protected int mPosition;


        public PostMessageViewHolder(View itemView) {
            super(itemView);
            mProfileImageView = (ImageView) itemView.findViewById(R.id.allPostsProfileImageView);
            mScreenNameLabel = (TextView) itemView.findViewById(R.id.screenNameLabel);
            mProfileNameLable = (TextView) itemView.findViewById(R.id.profileNameLable);
            mPostMessageLabel = (TextView) itemView.findViewById(R.id.allPostsMessageLabel);
            mPostMessageLikeLabel = (TextView) itemView.findViewById(R.id.allPostsMessageLikeLabel);
            mPostMessagelikesCounter = (TextView) itemView.findViewById(R.id.allPostsMessagelikesCounter);
            mPostMessageCommentLabel = (TextView) itemView.findViewById(R.id.allPostsMessageCommentLabel);
            mPostMessageCommentsCounter = (TextView) itemView.findViewById(R.id.allPostsCommentsCounter);
            mPostMessageTimeLabel = (TextView) itemView.findViewById(R.id.allPostsMessageTimeLabel);
            parent = itemView.findViewById(R.id.root);
            itemView.setOnClickListener(this);
        }

        public void bindPostMessages(final ParseObject message, int position) {
            mPosition = position;
            mScreenNameLabel.setText(message.getString(ParseConstants.KEY_SCREEN_NAME_COLUMN));
            mProfileNameLable.setText(message.getString(ParseConstants.KEY_FULL_NAME));
            mPostMessageLabel.setText(message.getString(ParseConstants.KEY_POST_MESSAGE_COLUMN));

            //number oflikes
            mPostMessageLikesMap =  ((message.getMap("likes") != null) ? message.getMap("likes") : new HashMap<String, Object>());
            mPostMessagelikesCounter.setText(mPostMessageLikesMap.size() + "");
            if(mPostMessageLikesMap.containsKey(mCurrentUser.getObjectId())){
                mPostMessageLikeLabel.setSelected(true);
            }
            else{
                mPostMessageLikeLabel.setSelected(false);
            }

            //number of comments
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_COMMENTS_CLASS);
            query.whereEqualTo(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, message.getObjectId());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> comments, ParseException e) {

                    if (e == null) {
                        mPostMessageComments = (comments != null) ? comments : new ArrayList<ParseObject>();
                        mPostMessageCommentsCounter.setText(mPostMessageComments.size() + "");
                    } else {
                        Log.d("PostMessageComment", "error querying comments");
                    }

                }
            });

            //Todo: delete the default image url after login is perfect
            mSenderProfileImageUrl = (message.getString(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL)) == null
                    ? "http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3.png"
                    : message.getString(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL);

            Picasso.with(mContext)
                    .load(mSenderProfileImageUrl)
                    .resize(180, 180)
                    .into(mProfileImageView);

            Date createdAt = message.getCreatedAt();//get the date the message was created from parse backend
            long now = new Date().getTime();//get current date
            String convertedDate = DateUtils.getRelativeTimeSpanString(
                    createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
            mPostMessageTimeLabel.setText(convertedDate); //sets the converted date into the message_item.xml view

            //code for setting touch delegate. Creates an area around LIKE label
            //for responding to click events arounf LIKE label
            parent.post(new Runnable() {
                public void run() {
                    // Post in the parent's message queue to make sure the parent
                    // lays out its children before we call getHitRect()
                    Rect delegateArea = new Rect();
                    TextView delegate = mPostMessageLikeLabel;
                    delegate.getHitRect(delegateArea);
                    delegateArea.top -= 100;
                    delegateArea.bottom += 100;
                    delegateArea.left -= 100;
                    delegateArea.right += 100;
                    TouchDelegate expandedArea = new TouchDelegate(delegateArea,
                            delegate);
                    // give the delegate to an ancestor of the view we're
                    // delegating the
                    // area to
                    if (View.class.isInstance(delegate.getParent())) {
                        ((View) delegate.getParent())
                                .setTouchDelegate(expandedArea);
                    }
                }

                ;
            });
            mPostMessageLikeLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mPostMessageLikeLabel.isSelected()){
                        Log.d("like buttob", mPostMessageLikeLabel.isSelected() + "");
                        mPostMessageLikesMap.put(mCurrentUser.getObjectId(), mCurrentUser);
                        int likes = mPostMessageLikesMap.size();
                        mPostMessagelikesCounter.setText((likes) + "");
                        message.put("likes", mPostMessageLikesMap);
                        message.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikesMap.size());
                        mPostMessageLikeLabel.setSelected(true);

                        //add post objectId to likedPosts table
                        mCurrentUser.add("likedPosts", message.getObjectId());
                        Notifications.sendPushNotifications(message, mCurrentUser);
                    }
                    else if (mPostMessageLikeLabel.isSelected()){
                        Log.d("like buttob", mPostMessageLikeLabel.isSelected() + "");
                        mPostMessageLikesMap.remove(mCurrentUser.getObjectId());
                        int likes = mPostMessageLikesMap.size();
                        mPostMessagelikesCounter.setText((likes) + "");
                        message.put("likes", mPostMessageLikesMap);
                        message.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikesMap.size());
                        mPostMessageLikeLabel.setSelected(false);

                        //remove post objectId from likedPosts table
                        ArrayList<String> likedPosts = (ArrayList<String>) mCurrentUser.get("likedPosts");
                        likedPosts.remove(message.getObjectId());
                        mCurrentUser.put("likedPosts", likedPosts);

                    }

                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                //success
                            }
                            else{
                                //error
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                            if(e == null){
                                //success
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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

            mPostMessageCommentLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ParseUser parseUser = (ParseUser) message.get(ParseConstants.KEY_SENDER_ID);
                    Intent intent = new Intent(mContext, PostMessageCommentsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, message.getObjectId());
                    intent.putExtra("TeamName", mTeam);
                    intent.putExtra(ParseConstants.KEY_SENDER_ID, message.getString(ParseConstants.KEY_SENDER_ID));
                    intent.putExtra(ParseConstants.KEY_SCREEN_NAME_COLUMN, message.getString(ParseConstants.KEY_SCREEN_NAME_COLUMN));
                    mContext. startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            ParseObject message = messages.get(mPosition);
            Date createdAt = message.getCreatedAt();
            long now = new Date().getTime();//get current date
            String convertedDate = DateUtils.getRelativeTimeSpanString(
                    createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();

            Intent intent = new Intent(mContext, PostDetailsActivity.class);
            intent.putExtra(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, message.getObjectId());
            intent.putExtra(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL, message.getString(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL));
            intent.putExtra(ParseConstants.KEY_SCREEN_NAME_COLUMN, message.getString(ParseConstants.KEY_SCREEN_NAME_COLUMN));
            intent.putExtra(ParseConstants.KEY_POST_MESSAGE_COLUMN, message.getString(ParseConstants.KEY_POST_MESSAGE_COLUMN));
            intent.putExtra(ParseConstants.KEY_POST_MESSAGE_CREATED_AT, convertedDate);
            mContext.startActivity(intent);

        }
    }

}
