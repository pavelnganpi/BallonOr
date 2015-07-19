package com.paveynganpi.ballonor;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseTwitterUtils;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class BallonDorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate(); //doing since overing from the parent, so we want o inherit from the base class too
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Skm8cEztx4rcXdhIGrHPjic7mVVS3xrhMr6Jhuvy", "4FBSGsODwvCMXq9XmGBcbdggfShPaUPAxVPxp5uM");
        //facebook initialization
        //ParseFacebookUtils.initialize(this);

        //twitter initialization
        ParseTwitterUtils.initialize("rwzNdxb3SZ6UZBpuWq2HkyUu6", "bhP9OAvAnkyc13FLSrdrs7ixfcIYIeMeD1v0TqO8a7hJtmjvcp");
    }
}
