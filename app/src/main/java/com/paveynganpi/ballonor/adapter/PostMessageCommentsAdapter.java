package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by paveynganpi on 7/6/15.
 */
public class PostMessageCommentsAdapter  extends RecyclerView.Adapter<PostMessageCommentsAdapter.PostMessageCommentsViewHolder> {
    private Context mContext;
    private List<ParseObject> mComments;
    protected String mTeam;

    public PostMessageCommentsAdapter(Context context, List<ParseObject> comments, String team) {
        this.mContext = context;
        this.mComments = comments;
        this.mTeam = team;
    }

    @Override
    public PostMessageCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_message_comments_item, parent, false);
        return new PostMessageCommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostMessageCommentsViewHolder postMessageCommentsViewHolder, int position) {
        postMessageCommentsViewHolder.bindPostMessages(mComments.get(position));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void refill(List<ParseObject> messages){
        mComments = messages;
        notifyDataSetChanged();
    }

    public class PostMessageCommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mCommenterProfileImageView;
        public TextView mCommenterScreenNameLabel;
        public TextView mCommenterProfileNameLabel;
        public TextView mCommentsLabel;
        public TextView mCommentsTimeLabel;

        public PostMessageCommentsViewHolder(View itemView) {
            super(itemView);
            mCommenterProfileImageView = (ImageView) itemView.findViewById(R.id.commenterProfileImageView);
            mCommenterScreenNameLabel = (TextView) itemView.findViewById(R.id.commenterScreenNameLabel);
            mCommenterProfileNameLabel = (TextView) itemView.findViewById(R.id.commenterProfileNameLabel);
            mCommentsLabel = (TextView) itemView.findViewById(R.id.commentsLabel);
            mCommentsTimeLabel = (TextView) itemView.findViewById(R.id.commentsTimeLabel);
        }

        public void bindPostMessages(final ParseObject comment) {
            mCommenterScreenNameLabel.setText(comment.getString(ParseConstants.KEY_USERNAME));
            mCommenterProfileNameLabel.setText(comment.getString(ParseConstants.KEY_TWITTER_FULL_NAME));
            mCommentsLabel.setText(comment.getString(ParseConstants.KEY_COMMENTS_COLUMN));

            String commenterProfileImageUrl = comment.getString(ParseConstants.KEY_PROFILE_IMAGE_URL);
            Picasso.with(mContext)
                    .load(commenterProfileImageUrl)
                    .resize(180, 180)
                    .into(mCommenterProfileImageView);

            Date createdAt = comment.getCreatedAt();//get the date the message was created from parse backend
            long now = new Date().getTime();//get current date
            String convertedDate = DateUtils.getRelativeTimeSpanString(
                    createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
            mCommentsTimeLabel.setText(convertedDate); //sets the converted date into the message_item.xml view

        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(mContext, ParseTwitterUtils.getTwitter().getScreenName().toString(), Toast.LENGTH_SHORT).show();

        }
    }
}

