package com.paveynganpi.ballonor.pojo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.ui.NotificationsActivity;

/**
 * Created by paveynganpi on 7/31/15.
 */
public class MyPushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);

        //To track "App Opens"
        ParseAnalytics.trackAppOpenedInBackground(intent);


        Intent notificationsIntent = new Intent(context, NotificationsActivity.class);
        notificationsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notificationsIntent);

    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return R.drawable.ic_launcher;
    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
    }
}
