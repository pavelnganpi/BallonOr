package com.paveynganpi.ballonor.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.utils.TeamsConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private static final String PREF_FILE_NAME = "testpref";
    private static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private static final String KEY_TEAM_NAME = "team_name";
    @InjectView(R.id.addTeamButton) Button mAddTeamButton;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    private ArrayAdapter<String> mTeamsAdapter;
    private ListView mTeamsListView;
    public ArrayList<String> mTeams;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromSharedPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mTeamsListView = (ListView) view.findViewById(R.id.teamsListView);
        ButterKnife.inject(this, view);

        mAddTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeaguesActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void setUpDrawer(int fragmentView, DrawerLayout drawerLayout, Toolbar toolbar) {

        containerView = getActivity().findViewById(fragmentView);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    sendToSharedPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                //getSupportActionBar().setTitle(mActivityTitle);
                getActivity().invalidateOptionsMenu();// redraw action bar
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //doing this since we dont have a postOnCreate
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static void sendToSharedPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, defaultValue);
        editor.apply();//async
    }

    public static String readFromSharedPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public void addDrawerTeams() {

        mTeams = new ArrayList<>();

        Arrays.sort(TeamsConstants.eplTeams);
        Arrays.sort(TeamsConstants.laLigaTeams);

        for (int i = 0; i < TeamsConstants.eplTeams.length; i++) {
            mTeams.add(TeamsConstants.eplTeams[i]);
        }
        for (int i = 0; i < TeamsConstants.laLigaTeams.length; i++) {
            mTeams.add(TeamsConstants.laLigaTeams[i]);
        }

        mTeamsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mTeams);
        mTeamsListView.setAdapter(mTeamsAdapter);
        HashMap<String, String> teamsConvert = new HashMap<>();


        //set onClickListerner
        mTeamsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTeamsListView.setItemChecked(position, true);
                ((MainActivity) getActivity()).onDrawerItemClicked(mTeams.get(position));
                //RealMadridfcFragment.setTeam(mTeams.get(position));
//               Intent intent = new Intent(getActivity(), MainActivity.class);
//               startActivity(intent);
                mDrawerLayout.closeDrawer(GravityCompat.START);

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
