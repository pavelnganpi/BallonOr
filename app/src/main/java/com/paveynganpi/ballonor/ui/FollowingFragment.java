package com.paveynganpi.ballonor.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.FollowAdapter;
import com.paveynganpi.ballonor.utils.ConnectionDetector;
import com.paveynganpi.ballonor.utils.DividerItemDecoration;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FollowingFragment extends Fragment {

    @InjectView(R.id.followingRecyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.followingSwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.empty_view)
    TextView mEmptyView;
    String mUserId;
    protected ProgressDialog progress;
    protected ConnectionDetector mConnectionDetector;

    protected RecyclerView.LayoutManager layoutManager;
    protected ParseUser mCurrentUser;
    protected FollowAdapter mFollowAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        ButterKnife.inject(this, view);

        mCurrentUser = ParseUser.getCurrentUser();
        mUserId = getActivity().getIntent().getStringExtra(ParseConstants.KEY_USER_ID);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryLight,
                R.color.colorPrimary);

        return view;
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

            retrieveFollows();
        }
        else {
            mConnectionDetector.showAlertDialog();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveFollows();
        }
    };

    private void retrieveFollows() {

        progress = ProgressDialog.show(getActivity(), "Loading...",
                "Please wait...", true);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_FOLLOW_CLASS);
        query.whereEqualTo(ParseConstants.KEY_FROM_USER_ID, mUserId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                progress.dismiss();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if(e == null){
                    //success
                    setFollowAdapter(list);

                }
                else{
                    //TODO: put alert dialog below
                }
            }
        });
    }

    public void setFollowAdapter(List<ParseObject> list){
        if (mRecyclerView.getAdapter() == null) {
            mFollowAdapter =
                    new FollowAdapter(getActivity(),list, ParseConstants.KEY_FOLLOW_TYPE_FOLLOWING);
            if (mFollowAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            mRecyclerView.setAdapter(mFollowAdapter);
        } else {
            //if it exists, no need to recreate it,
            //just set the data on the recyclerView
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            ((FollowAdapter) mRecyclerView.getAdapter()).refill(list);
        }
    }

}
