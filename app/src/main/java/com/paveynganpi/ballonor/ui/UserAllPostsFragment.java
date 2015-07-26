package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.paveynganpi.ballonor.adapter.AllPostsAdapter;
import com.paveynganpi.ballonor.utils.DividerItemDecoration;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserAllPostsFragment extends android.support.v4.app.Fragment {
    @InjectView(R.id.allPostsRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.empty_view) TextView mEmptyView;
    @InjectView(R.id.allPostsSwipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    protected   RecyclerView.LayoutManager layoutManager;
    protected ParseUser mCurrentUser;
    protected AllPostsAdapter mAllPostsAdapter;
    List<ParseObject> posts = new ArrayList<>();
    //protected RecyclerView mRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_all_posts, container, false);
        ButterKnife.inject(this, rootView);
        mCurrentUser = ParseUser.getCurrentUser();

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryLight,
                R.color.colorPrimary);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mRecyclerView.setLayoutManager(layoutManager);

        retrievePosts();
    }

    public void retrievePosts() {

        Log.d("comments", "inside retrive posts");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Teams");
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, mCurrentUser.getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    //success
                    setAllPostsMessageAdapter(list);
                    Log.d("comments", list.size() + "");

                }
                else{
                    //TODO: put alert dialog below
                    Log.d("parsechecking", e.getMessage());
                }
            }
        });
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrievePosts();
        }
    };

    public void setAllPostsMessageAdapter(List<ParseObject> list){
        if (mRecyclerView.getAdapter() == null) {
            mAllPostsAdapter =
                    new AllPostsAdapter(getActivity(),list);
            if (mAllPostsAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            mRecyclerView.setAdapter(mAllPostsAdapter);
        } else {
            //if it exists, no need to recreate it,
            //just set the data on the recyclerView
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            ((AllPostsAdapter) mRecyclerView.getAdapter()).refill(list);
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.reset(this);
//    }
}
