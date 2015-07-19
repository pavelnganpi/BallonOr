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
    }

    @Override
    public PostMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_item, parent, false);
        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserFullName = mCurrentUser.getString(ParseConstants.KEY_TWITTER_FULL_NAME);
        mCurrentTwitterUser = ParseTwitterUtils.getTwitter();
        PostMessageViewHolder postMessageViewHolder = new PostMessageViewHolder(view);
        return postMessageViewHolder;
    }

    @Override
    public void onBindViewHolder(PostMessageViewHolder postMessageViewHolder, int position) {
        postMessageViewHolder.bindPostMessages(messages.get(position));
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


        public PostMessageViewHolder(View itemView) {
            super(itemView);
            mProfileImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
            mScreenNameLabel = (TextView) itemView.findViewById(R.id.screenNameLabel);
            mProfileNameLable = (TextView) itemView.findViewById(R.id.profileNameLable);
            mPostMessageLabel = (TextView) itemView.findViewById(R.id.postMessageLabel);
            mPostMessageLikeLabel = (TextView) itemView.findViewById(R.id.postMessageLikeLabel);
            mPostMessagelikesCounter = (TextView) itemView.findViewById(R.id.postMessagelikesCounter);
            mPostMessageCommentLabel = (TextView) itemView.findViewById(R.id.postMessageCommentLabel);
            mPostMessageCommentsCounter = (TextView) itemView.findViewById(R.id.postMessageCommentsCounter);
            mPostMessageTimeLabel = (TextView) itemView.findViewById(R.id.postMessageTimeLabel);
            parent = itemView.findViewById(R.id.root);
            itemView.setOnClickListener(this);
        }

        public void bindPostMessages(final ParseObject message) {
            mScreenNameLabel.setText(mCurrentTwitterUser.getScreenName().toString());
            mProfileNameLable.setText(mCurrentUserFullName);
            mPostMessageLabel.setText(message.getString(mTeam));

            //number oflikes
//            mPostMessageLikesMap = (message.get(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT) == null)
//                    ? 0 : message.getInt(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT);
//            mPostMessagelikesCounter.setText(mPostMessageLikesMap + "");

            mPostMessageLikesMap =  ((message.getMap("likes") != null) ? message.getMap("likes") : new HashMap<String, Object>());
            mPostMessagelikesCounter.setText(mPostMessageLikesMap.size() + "");
            if(mPostMessageLikesMap.containsKey(mCurrentUser.getObjectId())){
                mPostMessageLikeLabel.setSelected(true);
            }
            else{
                mPostMessageLikeLabel.setSelected(false);
            }

            //number of comments
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_PARSE_OBJECT_COMMENTS);
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

//            mPostMessageCommentsCounter.setText(mPostMessageComments.size() + "");

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
//                    if (!v.isSelected()) {
//                        Log.d("like buttob", v.isSelected() + "");
//                        int likes = (message.get(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT) == null)
//                                ? 0 : message.getInt(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT);
//                        mPostMessagelikesCounter.setText((likes+1) + "");
//                        message.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, likes+1);
//                        v.setSelected(true);
//                    } else if (v.isSelected()){
//                        Log.d("like buttob", v.isSelected() + "");
//                        int likes = (message.get(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT) == null)
//                                ? 0 : message.getInt(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT);
//                        mPostMessagelikesCounter.setText((likes-1) + "");
//                        message.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, likes-1);
//                        v.setSelected(false);
//                    }
                    if(!mPostMessageLikeLabel.isSelected()){
                        Log.d("like buttob", mPostMessageLikeLabel.isSelected() + "");
                        mPostMessageLikesMap.put(mCurrentUser.getObjectId(), mCurrentUser);
                        int likes = mPostMessageLikesMap.size();
                        mPostMessagelikesCounter.setText((likes) + "");
                        message.put("likes", mPostMessageLikesMap);
                        message.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikesMap.size());
                        mPostMessageLikeLabel.setSelected(true);
                    }
                    else if (mPostMessageLikeLabel.isSelected()){
                        Log.d("like buttob", mPostMessageLikeLabel.isSelected() + "");
                        mPostMessageLikesMap.remove(mCurrentUser.getObjectId());
                        int likes = mPostMessageLikesMap.size();
                        mPostMessagelikesCounter.setText((likes) + "");
                        message.put("likes", mPostMessageLikesMap);
                        message.put(ParseConstants.KEY_POST_MESSAGE_LIKES_COUNT, mPostMessageLikesMap.size());
                        mPostMessageLikeLabel.setSelected(false);
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
                }
            });

            mPostMessageCommentLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostMessageCommentsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, message.getObjectId());
                    intent.putExtra("TeamName", mTeam);
                    mContext. startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(mContext, ParseTwitterUtils.getTwitter().getScreenName().toString(), Toast.LENGTH_SHORT).show();

        }
    }
}
