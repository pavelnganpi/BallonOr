package com.paveynganpi.ballonor.pojo;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by paveynganpi on 7/31/15.
 */
public class Notifications {

    public static void sendPushNotifications(ParseObject message, ParseUser liker) {
        if(!liker.getObjectId().equals(message.getString(ParseConstants.KEY_SENDER_ID))){
            final ParseObject notifications = saveToNotifications(message, liker);

            ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
            query.whereEqualTo(ParseConstants.KEY_USER_ID, message.getString(ParseConstants.KEY_SENDER_ID));

            //send push notification
            final ParsePush push = new ParsePush();
            push.setQuery(query);
            push.setMessage(liker.getUsername() + " liked your post");

            notifications.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //success
                        push.sendInBackground();

                    } else {
                        //error
                        Log.d("notificationserror", e.getMessage());
                    }
                }
            });

        }
    }

    public static ParseObject saveToNotifications(ParseObject postMessage, ParseUser liker){

        Map<String, Object> likes = ((postMessage.getMap("likes") != null) ? postMessage.getMap("likes") : new HashMap<String, Object>());
        ParseObject notifications = new ParseObject(ParseConstants.KEY_NOTIFICATIONS_CLASS);
        notifications.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        notifications.put(ParseConstants.KEY_SENDER_FULL_NAME, liker.getString(ParseConstants.KEY_TWITTER_FULL_NAME));
        notifications.put(ParseConstants.KEY_SENDER_SCREEN_NAME, liker.getUsername());
        notifications.put(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL, liker.getString(ParseConstants.KEY_PROFILE_IMAGE_URL));
        notifications.put(ParseConstants.KEY_RECIPIENT_ID, postMessage.getString(ParseConstants.KEY_SENDER_ID));
        notifications.put(ParseConstants.KEY_NOTIFICATION_TYPE, "like");
        notifications.put(ParseConstants.KEY_POST_MESSAGE_COLUMN, postMessage.getString(ParseConstants.KEY_POST_MESSAGE_COLUMN));
        notifications.put(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, postMessage.getObjectId());
        notifications.put("likesPostMessage", likes);
        notifications.put(ParseConstants.KEY_POST_MESSAGE_CREATED_AT, postMessage.getCreatedAt());
        notifications.put("opened", false);


        return notifications;
    }

}
