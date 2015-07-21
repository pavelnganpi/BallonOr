package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.LeagueTeamsAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LeagueTeamsActivity extends AppCompatActivity {
    protected String mLeageName;
    protected RecyclerView.LayoutManager layoutManager;
    @InjectView(R.id.leagueTeamsRecyclerView) RecyclerView mLeagueTeamsRecyclerView;
    private Toolbar mToolbar;
    protected MenuItem mDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_teams);
        ButterKnife.inject(this);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mLeageName = getIntent().getStringExtra("LeagueName");

        layoutManager = new LinearLayoutManager(LeagueTeamsActivity.this);

        LeagueTeamsAdapter leaguesAdapter = new LeagueTeamsAdapter(LeagueTeamsActivity.this, mLeageName);
        mLeagueTeamsRecyclerView.setAdapter(leaguesAdapter);

        mLeagueTeamsRecyclerView.setLayoutManager(layoutManager);
        mLeagueTeamsRecyclerView.setHasFixedSize(true);

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
            Toast.makeText(this, "HEllo World", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
