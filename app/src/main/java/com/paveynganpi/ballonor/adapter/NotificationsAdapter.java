package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by paveynganpi on 7/29/15.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{
    protected Context mContext;
    protected List<ParseObject> mNotifications;
    protected HashMap<String, String> map = new HashMap<>();

    public NotificationsAdapter(Context context, List<ParseObject> notifications){
        this.mContext = context;
        mNotifications = notifications;
        map.put("like", "liked");
        map.put("comment", "commented on");
        map.put("follow", "followed");
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item, parent, false);
        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder notificationsViewHolder, int position) {
        notificationsViewHolder.bindNotifications(mNotifications.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected ImageView mNotificationsImageView;
        protected TextView mNotificationsDescriptionLable;
        private TextView mNotificationsTimeLabel;
        protected ImageView mNotificationsIconImageView;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            mNotificationsImageView = (ImageView) itemView.findViewById(R.id.notificationsProfileImageView);
            mNotificationsDescriptionLable = (TextView) itemView.findViewById(R.id.notificationsDescriptionLable);
            mNotificationsTimeLabel = (TextView) itemView.findViewById(R.id.notificationsTimeLabel);
            mNotificationsIconImageView = (ImageView) itemView.findViewById(R.id.notificationIconImageView);

        }

        public void bindNotifications(ParseObject notification){
            String screenName = notification.getString(ParseConstants.KEY_SENDER_SCREEN_NAME);
            String notificationType = map.get(notification.getString(ParseConstants.KEY_NOTIFICATION_TYPE));
            String notificationDescription = "<b>" +  screenName + "</b>" +" " + notificationType + " your post " +
                    "\"" + notification.getString(ParseConstants.KEY_POST_MESSAGE_COLUMN) + ".\"";
            mNotificationsDescriptionLable.setText(Html.fromHtml(String.valueOf(notificationDescription)));

            Picasso.with(mContext)
                    .load(notification.getString(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL))
                    .resize(180, 180)
                    .into(mNotificationsImageView);

            Date createdAt = notification.getCreatedAt();
            long now = new Date().getTime();//get current date
            String convertedDate = DateUtils.getRelativeTimeSpanString(
                    createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
            mNotificationsTimeLabel.setText(convertedDate); //sets the converted date into the message_item.xml view

            if(notificationType.equals(map.get("comment"))){
                mNotificationsIconImageView.setImageResource(R.drawable.ic_comment);
            }
            else if(notificationType.equals(map.get("like"))){
                mNotificationsIconImageView.setImageResource(R.drawable.ic_thumb_up_18dp);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
