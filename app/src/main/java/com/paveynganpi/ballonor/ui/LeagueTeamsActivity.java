package com.paveynganpi.ballonor.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.LeagueTeamsAdapter;
import com.paveynganpi.ballonor.utils.ParseConstants;
import com.paveynganpi.ballonor.utils.TeamsConstants;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LeagueTeamsActivity extends AppCompatActivity {
    protected String mLeageName;
    protected RecyclerView.LayoutManager layoutManager;
    @InjectView(R.id.leagueTeamsListView) ListView mListView;
    private Toolbar mToolbar;
    protected MenuItem mDone;
    protected String[] mLeagueTeams;
    protected ParseUser mCurrentUser;
    protected ArrayList<String> mFavouriteTeams;
    protected ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_teams);
        ButterKnife.inject(this);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mLeageName = getIntent().getStringExtra("LeagueName");
        mLeagueTeams = TeamsConstants.getTeam(mLeageName);

        mCurrentUser = ParseUser.getCurrentUser();
        mFavouriteTeams =  (ArrayList<String>) mCurrentUser.get(ParseConstants.KEY_FAVOURITE_TEAMS);

        LeagueTeamsAdapter leaguesAdapter = new LeagueTeamsAdapter(LeagueTeamsActivity.this, mLeageName);
        mListView.setAdapter(leaguesAdapter);
        if(mFavouriteTeams!=null) {
            addTeamsCheckMarks(mFavouriteTeams);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckBox checkBox = (CheckBox)findViewById(R.id.leagueTeamsCheckbox);
                if(mListView.isItemChecked(position)){
                    mListView.setItemChecked(position, true);
                    checkBox.setChecked(true);
                }
                else{
                    mListView.setItemChecked(position, false);
                    checkBox.setChecked(false);
                }

            }
        });

    }

    public void addTeamsCheckMarks(ArrayList<String> favouriteTeams){

        CheckBox checkBox = (CheckBox)findViewById(R.id.leagueTeamsCheckbox);
        for(int i = 0; i< mLeagueTeams.length; i++){

            if(favouriteTeams.contains(mLeagueTeams[i])){
                mListView.setItemChecked(i, true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_league_teams, menu);
        mDone = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            ArrayList<String> favouriteTeams = new ArrayList<>();
            ArrayList<String> nonFavouriteTeams = new ArrayList<>();

            for(int i =0; i< mListView.getCount(); i++){
                if(mListView.isItemChecked(i)){
                    favouriteTeams.add(mLeagueTeams[i]);
                }
                else{
                    nonFavouriteTeams.add(mLeagueTeams[i]);
                }
            }

            ArrayList<String> userFavouriteTeams =  mCurrentUser.get(ParseConstants.KEY_FAVOURITE_TEAMS) != null
                    ? (ArrayList<String>) mCurrentUser.get(ParseConstants.KEY_FAVOURITE_TEAMS) : new ArrayList<String>();

            for(String team : nonFavouriteTeams){
                if(userFavouriteTeams.contains(team)){
                    userFavouriteTeams.remove(team);
                }
            }

            mCurrentUser.put(ParseConstants.KEY_FAVOURITE_TEAMS, userFavouriteTeams);
            mCurrentUser.addAllUnique(ParseConstants.KEY_FAVOURITE_TEAMS, favouriteTeams);


            progress = ProgressDialog.show(this, "Loading...",
                    "Please wait...", true);
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    progress.dismiss();
                    if (e == null) {
                        //success
                        Toast.makeText(LeagueTeamsActivity.this, "Successfully updated your favorites teams", Toast.LENGTH_SHORT).show();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LeagueTeamsActivity.this);
                        builder.setMessage("Sorry, Please try again");
                        builder.setTitle("Opps , error saving favourite teams");
                        builder.setPositiveButton(android.R.string.ok, null);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
            });
            Intent intent = new Intent(LeagueTeamsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
