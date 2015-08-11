package com.paveynganpi.ballonor;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.paveynganpi.ballonor.utils.ParseConfig;
import com.paveynganpi.ballonor.utils.ParseConstants;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class BallonDorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate(); //doing since overing from the parent, so we want o inherit from the base class too
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, ParseConfig.ParseApplicationId, ParseConfig.ParseClientKey);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        //facebook initialization
        //ParseFacebookUtils.initialize(this);

        //twitter initialization
        ParseTwitterUtils.initialize(ParseConfig.TwitterConsumerKey, ParseConfig.ConsumerSecret);
    }
    public static void updateParseInstallation(ParseUser parseUser){
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put(ParseConstants.KEY_USER_ID, parseUser.getObjectId());
        parseInstallation.saveInBackground();
    }
}
