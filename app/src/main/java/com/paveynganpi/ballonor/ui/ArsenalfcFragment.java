package com.paveynganpi.ballonor.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paveynganpi.ballonor.R;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class ArsenalfcFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_arsenalfc, container, false);
        return rootView;
    }
}
