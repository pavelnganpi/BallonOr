package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.ui.UserProfileActivity;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paveynganpi on 8/6/15.
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowAdapterViewHolder> {
    protected Context mContext;
    protected List<ParseObject> mFollows;
    protected ParseUser mCurrentUser;
    protected String mFollowType;

    public FollowAdapter(Context context, List<ParseObject> follows, String followType){
        mFollows = follows;
        mContext = context;
        mFollowType = followType;
    }

    @Override
    public FollowAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.follow_item, parent, false);
        mCurrentUser = ParseUser.getCurrentUser();
        return new FollowAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FollowAdapterViewHolder followAdapterViewHolder, int position) {
        followAdapterViewHolder.bindFollow(mFollows.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mFollows.size();
    }

    public void refill(List<ParseObject> follows) {
        mFollows = follows;
    }

    public class FollowAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected ImageView mFollowProfileImageView;
        protected TextView mFollowScreenNameLabel;
        protected TextView mFollowProfileNameLable;
        protected int mPosition;
        protected ParseUser user;
        protected  ParseQuery<ParseUser> query;


        public FollowAdapterViewHolder(View itemView) {
            super(itemView);
            mFollowProfileImageView = (ImageView) itemView.findViewById(R.id.followProfileImageView);
            mFollowScreenNameLabel = (TextView) itemView.findViewById(R.id.followScreenNameLabel);
            mFollowProfileNameLable = (TextView) itemView.findViewById(R.id.followProfileNameLable);
            itemView.setOnClickListener(this);
        }

        public void bindFollow(ParseObject follow, int position){
            mPosition = position;
            user = mFollowType.equals(ParseConstants.KEY_FOLLOW_TYPE_FOLLOWERS) ?
                    (ParseUser) follow.get(ParseConstants.KEY_FROM) : (ParseUser) follow.get(ParseConstants.KEY_TO);

            query = ParseQuery.getQuery("_User");
            query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, user.getObjectId());
            try {
                user = query.getFirst();
                String userProfileImageUrl = user.getString(ParseConstants.KEY_PROFILE_IMAGE_URL);
                mFollowScreenNameLabel.setText(user.getUsername());
                mFollowProfileNameLable.setText(user.getString(ParseConstants.KEY_TWITTER_FULL_NAME));

                Picasso.with(mContext)
                        .load(userProfileImageUrl)
                        .resize(180, 180)
                        .into(mFollowProfileImageView);
            } catch (ParseException e) {
                e.printStackTrace();
            }



        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(mContext, UserProfileActivity.class);
            query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, user.getObjectId());
            try {
                user = query.getFirst();
                String userProfileImageUrl = user.getString(ParseConstants.KEY_PROFILE_IMAGE_URL);
                Bundle bundle = new Bundle();
                ArrayList<String> likedPosts = user.get("likedPosts") != null
                        ? (ArrayList<String>) user.get("likedPosts") : new ArrayList<String>();
                bundle.putStringArrayList("userLikedPostsLists", likedPosts);
                intent.putExtra(ParseConstants.KEY_USER_ID, user.getObjectId());
                intent.putExtra(ParseConstants.KEY_PROFILE_IMAGE_URL, user.getString(ParseConstants.KEY_PROFILE_IMAGE_URL));
                intent.putExtra(ParseConstants.KEY_SCREEN_NAME_COLUMN, user.getUsername());
                intent.putExtra(ParseConstants.KEY_FULL_NAME, user.getString(ParseConstants.KEY_TWITTER_FULL_NAME));
                intent.putExtras(bundle);
                mContext.startActivity(intent);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    }
}
