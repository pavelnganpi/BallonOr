package com.paveynganpi.ballonor.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.utils.ParseConstants;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CreatePostActivity extends AppCompatActivity {

    @InjectView(R.id.createPostTextField) EditText mCreatePostTextField;
    @InjectView(R.id.createPostButton) Button mCreatePostButton;
    public static final String TAG = CreatePostActivity.class.getSimpleName();
    public static String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.inject(this);

        teamName  = getIntent().getStringExtra("teamName");
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if(mCreatePostTextField.getText().toString().isEmpty()){
//            mCreatePostButton.setEnabled(false);
//        }
            mCreatePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("dialog test", mCreatePostTextField.getText().toString());
                    //Log.d("dialog test",TAG.substring(TAG.length()-8));
                    if(mCreatePostTextField.getText().toString().isEmpty()){
                        Toast.makeText(CreatePostActivity.this,"You have to post something", Toast.LENGTH_LONG).show();
                    }
                    else {
                        send(createPost(mCreatePostTextField));
                        finish();
                    }
                }
            });
    }

    public ParseObject createPost(EditText postMessage){
        ParseObject teams = new ParseObject("Teams");
        teams.put(teamName, postMessage.getText().toString().trim());
        teams.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser());
        teams.put("screenName", ParseTwitterUtils.getTwitter().getScreenName());
        teams.put(ParseConstants.KEY_SENDER_PROFILE_IMAGE_URL, ParseUser.getCurrentUser().getString(ParseConstants.KEY_PROFILE_IMAGE_URL));

        return teams;
    }

    public void send(ParseObject teams){

        teams.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                //success
                    Toast.makeText(CreatePostActivity.this,"Message sent", Toast.LENGTH_LONG).show();
                }
                else{
                    //error
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
                    builder.setMessage("Sorry, an error occured, Please try again")
                            .setTitle("Opps, Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
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
