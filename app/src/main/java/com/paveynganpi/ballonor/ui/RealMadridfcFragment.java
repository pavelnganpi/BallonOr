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
    private FloatingActionButton fab;
    private  RecyclerView.LayoutManager layoutManager;
    protected TextView mPostMessageLikeLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_realmadridfc, container, false);
        ButterKnife.inject(this, rootView);

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
                intent.putExtra("teamName", "Chelseafc");
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
        query.whereExists("Chelseafc");
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    String[] postMessages = new String[list.size()];
                    int i = 0;
                    for (ParseObject postMessage : list) {
                        postMessages[i] = postMessage.getString("Chelseafc");
                        i++;
                    }
                    PostMessageAdapter postMessageAdapter = new PostMessageAdapter(getActivity(), list);
                    mRecyclerView.setAdapter(postMessageAdapter);
                } else {
                    Log.d("parse query", "error with parseQuery");
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
