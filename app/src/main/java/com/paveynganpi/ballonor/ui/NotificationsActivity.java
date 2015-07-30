package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.NotificationsAdapter;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationsActivity extends AppCompatActivity {

    @InjectView(R.id.notificationsRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.notificationsSwipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.empty_view) TextView mEmptyView;
    private Toolbar mToolbar;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ParseUser mCurrentUser;
    protected NotificationsAdapter mNotificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.inject(this);

        mCurrentUser = ParseUser.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.notifications_app_bar);
        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryLight,
                R.color.colorPrimary);

        mLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        retrieveNotifications();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
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

    public void retrieveNotifications(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_NOTIFICATIONS_CLASS);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_ID, mCurrentUser.getObjectId());
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    //success
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    setNotificationsAdapter(list);
                }
                else{
                    //error
                }
            }
        });

    }

    public void setNotificationsAdapter(List<ParseObject> list){
        if (mRecyclerView.getAdapter() == null) {
            mNotificationsAdapter =
                    new NotificationsAdapter(this,list);
            if (mNotificationsAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            mRecyclerView.setAdapter(mNotificationsAdapter);
        } else {
            //if it exists, no need to recreate it,
            //just set the data on the recyclerView
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            ((NotificationsAdapter) mRecyclerView.getAdapter()).refill(list);
        }
    }


    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveNotifications();
        }
    };
}
