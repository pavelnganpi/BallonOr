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
 * Created by paveynganpi on 7/24/15.
 */
public class AllPostsAdapter extends RecyclerView.Adapter<AllPostsAdapter.AllPostsViewHolder> {
    protected Context mContext;
    protected List<ParseObject> mAllPosts;
    private ParseUser mCurrentUser;
    private String mCurrentUserFullName;
    private Twitter mCurrentTwitterUser;

    public AllPostsAdapter(Context context, List<ParseObject> allPosts) {
        mContext = context;
        mAllPosts = allPosts;
    }

    @Override
    public AllPostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_posts_item, parent, false);
        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserFullName = mCurrentUser.getString(ParseConstants.KEY_TWITTER_FULL_NAME);
        mCurrentTwitterUser = ParseTwitterUtils.getTwitter();
        return new AllPostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllPostsViewHolder allPostsViewHolder, int position) {
        allPostsViewHolder.bindAllPosts(mAllPosts.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mAllPosts.size();
    }

    public void refill(List<ParseObject> allPosts) {
        this.mAllPosts = allPosts;
        notifyDataSetChanged();
    }

    public class AllPostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String mSenderProfileImageUrl;
        Map<String, Object> mPostMessageLikesMap;
        List<ParseObject> mPostMessageComments;
        View parent;
        protected ImageView mAllPostsProfileImageView;
        protected TextView mScreenNameLabel;
        protected TextView mProfileNameLable;
        protected TextView mAllPostsMessageLabel;
        protected TextView mAllPostsMessageCommentLabel;
        protected TextView mAllPostsMessageLikeLabel;
        protected TextView mAllPostsMessageTimeLabel;
        protected TextView mAllPostsCommentsCounter;
        protected TextView mAllPostsMessagelikesCounter;
        protected int mPosition;

        public AllPostsViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.root);
            mAllPostsProfileImageView = (ImageView) itemView.findViewById(R.id.allPostsProfileImageView);
            mScreenNameLabel = (TextView) itemView.findViewById(R.id.screenNameLabel);
            mProfileNameLable = (TextView) itemView.findViewById(R.id.profileNameLable);
            mAllPostsMessageLabel = (TextView) itemView.findViewById(R.id.allPostsMessageLabel);
            mAllPostsMessageTimeLabel = (TextView) itemView.findViewById(R.id.allPostsMessageTimeLabel);
            mAllPostsMessageCommentLabel = (TextView) itemView.findViewById(R.id.allPostsMessageCommentLabel);
            mAllPostsCommentsCounter = (TextView) itemView.findViewById(R.id.allPostsCommentsCounter);
            mAllPostsMessagelikesCounter = (TextView) itemView.findViewById(R.id.allPostsMessagelikesCounter);
            mAllPostsMessageLikeLabel = (TextView) itemView.findViewById(R.id.allPostsMessageLikeLabel);

            itemView.setOnClickListener(this);
        }

        public void bindAllPosts(final ParseObject post, int position){
            mPosition = position;

            mScreenNameLabel.setText(mCurrentTwitterUser.getScreenName());
            mProfileNameLable.setText(mCurrentUserFullName);
            mAllPostsMessageLabel.setText(post.getString(ParseConstants.KEY_POST_MESSAGE_COLUMN));

            //number oflikes
            mPostMessageLikesMap =  ((post.getMap("likes") != null) ? post.getMap("likes") : new HashMap<String, Object>());
            mAllPostsMessagelikesCounter.setText(mPostMessageLikesMap.size() + "");
            if(mPostMessageLikesMap.containsKey(mCurrentUser.getObjectId())){
                mAllPostsMessageLikeLabel.setSelected(true);
            }
            else{
                mAllPostsMessageLikeLabel.setSelected(false);
            }

            //number of comments
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_COMMENTS_CLASS);
            query.whereEqualTo(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, post.getObjectId());
            query.whereEqualTo(ParseConstants.KEY_TEAM_COLUMN, post.getString(ParseConstants.KEY_TEAM_COLUMN));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> comments, ParseException e) {
                    Log.d("AllPostsMessageComment", comments.size() +"");
                    if (e == null) {
                        mPostMessageComments = (comments != null) ? comments : new ArrayList<ParseObject>();
                        mAllPostsCommentsCounter.setText(mPostMessageComments.size() + "");
                    } else {
                        Log.d("AllPostsMessageComment", "error querying comments");
                    }

                }
            });

            //Todo: delete the default image url after login is perfect
            mSenderProfileImageUrl = (post.getString(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL)) == null
                    ? "http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3.png"
                    : post.getString(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL);

            Picasso.with(mContext)
                    .load(mSenderProfileImageUrl)
                    .resize(180, 180)
                    .into(mAllPostsProfileImageView);

            Date createdAt = post.getCreatedAt();//get the date the message was created from parse backend
            long now = new Date().getTime();//get current date
            String convertedDate = DateUtils.getRelativeTimeSpanString(
                    createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
            mAllPostsMessageTimeLabel.setText(convertedDate); //sets the converted date into the message_item.xml view

            //code for setting touch delegate. Creates an area around LIKE label
            //for responding to click events arounf LIKE label
            parent.post(new Runnable() {
                public void run() {
                    // Post in the parent's message queue to make sure the parent
                    // lays out its children before we call getHitRect()
                    Rect delegateArea = new Rect();
                    TextView delegate = mAllPostsMessageLikeLabel;
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
            mAllPostsMessageLikeLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mAllPostsMessageLikeLabel.isSelected()) {
                        Log.d("like buttob", mAllPostsMessageLikeLabel.isSelected() + "");
                        mPostMessageLikesMap.put(mCurrentUser.getObjectId(), mCurrentUser);
                        int likes = mPostMessageLikesMap.size();
                        mAllPostsMessagelikesCounter.setText((likes) + "");
                        post.put("likes", mPostMessageLikesMap);
                        post.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikesMap.size());
                        mAllPostsMessageLikeLabel.setSelected(true);
                    } else if (mAllPostsMessageLikeLabel.isSelected()) {
                        Log.d("like buttob", mAllPostsMessageLikeLabel.isSelected() + "");
                        mPostMessageLikesMap.remove(mCurrentUser.getObjectId());
                        int likes = mPostMessageLikesMap.size();
                        mAllPostsMessagelikesCounter.setText((likes) + "");
                        post.put("likes", mPostMessageLikesMap);
                        post.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikesMap.size());
                        mAllPostsMessageLikeLabel.setSelected(false);
                    }

                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //success
                            } else {
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
                }
            });

            mAllPostsMessageCommentLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostMessageCommentsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, post.getObjectId());
                    intent.putExtra("TeamName", post.getString(ParseConstants.KEY_TEAM_COLUMN));

                    //intent.putExtra(ParseConstants.KEY_SENDER_ID, post.getString(ParseConstants.KEY_SENDER_ID));
                    mContext.startActivity(intent);
                }
            });

        }

        @Override
        public void onClick(View v) {
            ParseObject message = mAllPosts.get(mPosition);
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
