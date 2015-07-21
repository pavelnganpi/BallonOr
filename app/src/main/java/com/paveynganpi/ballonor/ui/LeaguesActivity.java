package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.LeaguesAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LeaguesActivity extends AppCompatActivity {
    @InjectView(R.id.leaguesRecyclerView) RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagues);
        ButterKnife.inject(this);

        layoutManager = new LinearLayoutManager(LeaguesActivity.this);

        LeaguesAdapter leaguesAdapter = new LeaguesAdapter(LeaguesActivity.this);
        mRecyclerView.setAdapter(leaguesAdapter);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_leagues, menu);
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
