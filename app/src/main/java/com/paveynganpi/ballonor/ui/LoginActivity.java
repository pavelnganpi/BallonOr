package com.paveynganpi.ballonor.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.paveynganpi.ballonor.BallonDorApplication;
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
    public StringBuilder sb = new StringBuilder();
    public Twitter currentTwitterUser;
    public TwitterUserPojo twitterUserpojo;
    public HttpGet verifyGet;


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
                            navigateToLogin();
                        } else if (user.isNew()) {
                               mParseCurrentUser = ParseUser.getCurrentUser();
                               currentTwitterUser = ParseTwitterUtils.getTwitter();
                               BallonDorApplication.updateParseInstallation(mParseCurrentUser);

                               GetTwitterUserDataTask getTwitterUserDataTask = new GetTwitterUserDataTask();
                               getTwitterUserDataTask.execute();
                        } else {
                            BallonDorApplication.updateParseInstallation(ParseUser.getCurrentUser());
                            startMainActivity();
                        }
                    }
                });
            }
        });

    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
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



        @Override
        protected TwitterUserPojo doInBackground(Object... arg0) {
            HttpClient client = new DefaultHttpClient();
            verifyGet = new HttpGet(
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
            finally {
                verifyGet.abort();
            }
            return twitterUserpojo;
        }

        @Override
        protected void onPostExecute(final TwitterUserPojo twitterUserpojo) {
            super.onPostExecute(twitterUserpojo);

            mParseCurrentUser.put(ParseConstants.KEY_TWITTER_FULL_NAME, twitterUserpojo.getName());
            mParseCurrentUser.setUsername(currentTwitterUser.getScreenName());
            String profileImageUrlNormalSize  = twitterUserpojo.getProfileImageUrl();

            String profileImageUrl = getRealImage(profileImageUrlNormalSize);
            mParseCurrentUser.put(ParseConstants.KEY_PROFILE_IMAGE_URL, profileImageUrl);
            mParseCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        startMainActivity();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Sorry, Please try logging in again");//creates a dialog with this message
                        builder.setTitle("Opps , error loging in with twitter");
                        builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog
                        navigateToLogin();
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

    public String getRealImage(String profileImageUrl){
        String removeString = "_normal";
        int removeStringSize = removeString.length();
        int index = profileImageUrl.indexOf(removeString);
        return profileImageUrl.substring(0, index) + profileImageUrl.substring(index + removeStringSize, profileImageUrl.length());

    }

}