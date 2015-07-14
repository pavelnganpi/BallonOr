package com.paveynganpi.ballonor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.List;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class ChelseafcFragment extends ListFragment {
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chelseafc, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton) view.findViewById(R.id.teamOneFloatingButton);
        fab.attachToListView(getListView());


    }

    @Override
    public void onResume() {
        super.onResume();
        retrievePosts();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        String[] test = {"hello","now","awesome", "nice", "pool","come","find","you","poap","gone","man","woman","whatever","good","bad",
//        "computer","laptop","home","test","float", "hello","now","awesome", "nice", "pool","come","find","you","poap","gone","man","woman","whatever","good","bad",
//                "computer","laptop","home","test","float"};

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,test);
//        getListView().setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                intent.putExtra("teamName", "Chelseafc");
                startActivity(intent);
            }
        });
    }

    public void retrievePosts(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Teams");
        query.whereExists("Chelseafc");
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    Log.d("parse query","successfull");
                    Log.d("parse query", list.get(0).getString("Chelseafc"));
                    String[] postMessages = new String[list.size()];
                    int i =0;
                    for(ParseObject postMessage: list){
                        Log.d("parse query", postMessage.getString("Chelseafc"));
                        postMessages[i] = postMessage.getString("Chelseafc");
                        i++;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,postMessages);
                    getListView().setAdapter(adapter);
                }
                else{
                    Log.d("parse query","error with parseQuery");
                }
            }
        });



    }
}
