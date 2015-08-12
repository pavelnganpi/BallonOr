package com.paveynganpi.ballonor.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.PostMessageAdapter;
import com.paveynganpi.ballonor.utils.ConnectionDetector;
import com.paveynganpi.ballonor.utils.DividerItemDecoration;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class FeedFragment extends Fragment{
    @InjectView(R.id.feedRecyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.feedSwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.empty_view)
    TextView mEmptyView;
    private FloatingActionButton fab;
    private  RecyclerView.LayoutManager layoutManager;
    protected PostMessageAdapter mPostMessageAdapter;
    protected static String mTeam;
    public static final String TAG = FeedFragment.class.getSimpleName();
    private ParseUser mCurrentUser;
    protected List<String> followersIds;
    protected ProgressDialog progress;
    protected ConnectionDetector mConnectionDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.inject(this, rootView);

        Bundle bundle = this.getArguments();
        mTeam = bundle.getString("FeedTeamName");
        mCurrentUser = ParseUser.getCurrentUser();

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryLight,
                R.color.colorPrimary);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mConnectionDetector = new ConnectionDetector(getActivity(), getActivity());
        Boolean isInternetPresent = mConnectionDetector.isConnectingToInternet(); // true or false
        if(isInternetPresent){
            layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
            mRecyclerView.setLayoutManager(layoutManager);

            retrievePosts();
        }
        else {
            mConnectionDetector.showAlertDialog();
        }
    }

    public void retrievePosts() {
        ParseQuery<ParseObject> followQuery = ParseQuery.getQuery(ParseConstants.KEY_FOLLOW_CLASS);
        followQuery.whereEqualTo(ParseConstants.KEY_TO, mCurrentUser);
        try {
            List<ParseObject> followers = followQuery.find();
            followersIds = new ArrayList<>();
            for(ParseObject follower : followers){
                ParseUser user = (ParseUser) follower.get(ParseConstants.KEY_FROM);
                followersIds.add(user.getObjectId());
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Teams");
            query.whereContainedIn(ParseConstants.KEY_SENDER_ID, followersIds);
            query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {

                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    if (e == null) {

                        setPostMessageAdapter(list);

                    } else {
                        Log.d("parse query", "error with parseQuery");
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrievePosts();
        }
    };

    public void setPostMessageAdapter(List<ParseObject> list){
        if (mRecyclerView.getAdapter() == null) {
            mPostMessageAdapter =
                    new PostMessageAdapter(getActivity(),list, mTeam);
            if (mPostMessageAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            mRecyclerView.setAdapter(mPostMessageAdapter);
        } else {
            //if it exists, no need to recreate it,
            //just set the data on the recyclerView
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            ((PostMessageAdapter) mRecyclerView.getAdapter()).refill(list);
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.reset(this);
//    }
}
