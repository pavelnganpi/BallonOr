package com.paveynganpi.ballonor.ui;

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
import com.paveynganpi.ballonor.adapter.AllPostsAdapter;
import com.paveynganpi.ballonor.utils.DividerItemDecoration;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserFavouritePostsFragment extends Fragment {

    @InjectView(R.id.allPostsRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.allPostsSwipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.empty_view) TextView mEmptyView;
    protected   RecyclerView.LayoutManager layoutManager;
    protected ParseUser mCurrentUser;
    protected AllPostsAdapter mAllPostsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_all_posts, container, false);
        ButterKnife.inject(this, view);

        mCurrentUser = ParseUser.getCurrentUser();

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
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mRecyclerView.setLayoutManager(layoutManager);

        retrievePosts();
    }

    public void retrievePosts() {

        ArrayList<String> likedPosts = (ArrayList<String>) mCurrentUser.get("likedPosts");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Teams");
        query.whereContainedIn(ParseConstants.KEY_OBJECT_ID, likedPosts);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if(e == null){
                    //success
                    setAllPostsMessageAdapter(list);

                }
                else{
                    //TODO: put alert dialog below
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
