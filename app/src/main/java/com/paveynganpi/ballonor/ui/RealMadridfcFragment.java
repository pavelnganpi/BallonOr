package com.paveynganpi.ballonor.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.PostMessageAdapter;
import com.paveynganpi.ballonor.utils.DividerItemDecoration;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class RealMadridfcFragment extends android.support.v4.app.Fragment {
    @InjectView(R.id.PostMessageRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.empty_view) TextView mEmptyView;
    private FloatingActionButton fab;
    private  RecyclerView.LayoutManager layoutManager;
    protected TextView mPostMessageLikeLabel;
    private static String mTeam;
    protected PostMessageAdapter postMessageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_realmadridfc, container, false);
        ButterKnife.inject(this, rootView);

        Bundle bundle = this.getArguments();
        mTeam = bundle.getString("TeamName");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton) view.findViewById(R.id.teamTwoFloatingButton);
        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                intent.putExtra("teamName", mTeam);
                startActivity(intent);
            }
        });
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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Teams");
        query.whereExists(mTeam);
        //Log.d("teamName", mTeam);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
//                    PostMessageAdapter postMessageAdapter = new PostMessageAdapter(getActivity(), list, mTeam);
//                    mRecyclerView.setAdapter(postMessageAdapter);

                    if (mRecyclerView.getAdapter() == null) {
                        postMessageAdapter =
                                new PostMessageAdapter(getActivity(),list, mTeam);
                        if (postMessageAdapter.getItemCount() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        mRecyclerView.setAdapter(postMessageAdapter);
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

                } else {
                    Log.d("parse query", "error with parseQuery");
                }
            }
        });


    }
    public static void setTeam(String team){
        mTeam = team;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
