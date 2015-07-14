package com.paveynganpi.ballonor.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.melnykov.fab.FloatingActionButton;
import com.paveynganpi.ballonor.R;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class ArsenalfcFragment extends ListFragment {
    private FloatingActionButton fab;
    public static final String TAG = ArsenalfcFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_arsenalfc, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton) view.findViewById(R.id.teamThreeFloatingButton);
        fab.attachToListView(getListView());


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] test = {"hello","now","awesome", "nice", "pool","come","find","you","poap","gone","man","woman","whatever","good","bad",
                "computer","laptop","home","test","float", "hello","now","awesome", "nice", "pool","come","find","you","poap","gone","man","woman","whatever","good","bad",
                "computer","laptop","home","test","float"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,test);
        getListView().setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("New Post");
                alert.setMessage("enter message here");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                alert.setView(input);

                alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // Do something with value!
                        Log.d("dialog test",whichButton + "");
                        Log.d("dialog test", TAG.substring(0,TAG.length() - 8));
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }
}
