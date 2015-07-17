package com.paveynganpi.ballonor.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.pojo.TwitterUserPojo;
import com.paveynganpi.ballonor.utils.ParseConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends ActionBarActivity {

    @InjectView(R.id.loginButton) Button mLoginButton;
    protected ParseUser mParseCurrentUser;
    protected String TWITTER_USERS_SHOW_URL ="https://api.twitter.com/1.1/users/show.json?screen_name=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseTwitterUtils.logIn(LoginActivity.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Sorry, Please try logging in again");//creates a dialog with this message
                            builder.setTitle("Opps , error logging in with twitter");
                            builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog

                            AlertDialog dialog = builder.create();//create a dialog
                            dialog.show();//show the dialog
                        } else if (user.isNew()) {
                            mParseCurrentUser = ParseUser.getCurrentUser();

                            GetTwitterUserDataTask getTwitterUserDataTask = new GetTwitterUserDataTask();
                            getTwitterUserDataTask.execute();

                            user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //great

                                    } else {
                                        //error
                                        Log.d("Twitter Login error", e.getMessage());
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("Sorry, Please try logging in again");//creates a dialog with this message
                                        builder.setTitle("Opps , error loging in with twitter");
                                        builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog
                                        navigateToLogin();
                                    }
                                }
                            });

                            //move to mainActicvity on successfull signup
                            startMainActivity();
                        } else {
                            startMainActivity();
                        }
                    }
                });
            }
        });

    }

    public void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToLogin() {
        //start the loginActivity
        Intent intent = new Intent(this, LoginActivity.class);

        //add the loginActivity to the top of stack, and clear the inbox page
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//add the loginActivity task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear the previous task
        startActivity(intent);
    }

    public class GetTwitterUserDataTask extends AsyncTask<Object, Void, TwitterUserPojo> {

        public StringBuilder sb = new StringBuilder();
        private Twitter currentTwitterUser = ParseTwitterUtils.getTwitter();
        public TwitterUserPojo twitterUserpojo;

        @Override
        protected TwitterUserPojo doInBackground(Object... arg0) {
            HttpClient client = new DefaultHttpClient();
            HttpGet verifyGet = new HttpGet(
                    TWITTER_USERS_SHOW_URL + currentTwitterUser.getScreenName());
            currentTwitterUser.signRequest(verifyGet);
            try {
                HttpResponse response = client.execute(verifyGet);

                //gets response body from response object
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                twitterUserpojo = mapper.readValue(sb.toString(), TwitterUserPojo.class);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return twitterUserpojo;
        }

        @Override
        protected void onPostExecute(final TwitterUserPojo twitterUserpojo) {
            super.onPostExecute(twitterUserpojo);

            mParseCurrentUser.put(ParseConstants.KEY_TWITTER_ID, currentTwitterUser.getUserId());
            mParseCurrentUser.put(ParseConstants.KEY_TWITTER_FULL_NAME, twitterUserpojo.getName());
            String profileImageUrlNormalSize  = twitterUserpojo.getProfileImageUrl();

            String profileImageUrl = !twitterUserpojo.getDefaulProfileImage()
                    ? profileImageUrlNormalSize.substring(0, profileImageUrlNormalSize.length() - 12) + ".jpeg" : "http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3.png";
            mParseCurrentUser.put(ParseConstants.KEY_PROFILE_IMAGE_URL, profileImageUrl);
            mParseCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("post execute", "successfully saved user in parse");

                    } else {
                        Log.d("post execute", "error saving user in parse " + e.getMessage());
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
