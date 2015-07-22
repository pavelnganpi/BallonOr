package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.utils.TeamsConstants;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by paveynganpi on 7/20/15.
 */
public class LeagueTeamsAdapter extends ArrayAdapter<String> {
    protected Context mContext;
    protected String[] mLeagueTeamsNames;

    public LeagueTeamsAdapter(Context context, String league) {
        super(context, R.layout.league_teams_item, TeamsConstants.getTeam(league));
        this.mContext = context;
        mLeagueTeamsNames = TeamsConstants.getTeam(league);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.league_teams_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String team = mLeagueTeamsNames[position];

        viewHolder.mLeagueTeamsTextView.setText(team);
        Picasso.with(mContext)
                .load(TeamsConstants.getTeamImageUrl())
                .resize(110, 110)
                .into(viewHolder.mLeagueTeamsImageView);

        ListView listView = (ListView)parent;
        if(listView.isItemChecked(position)){
            viewHolder.mLeagueTeamsCheckbox.setChecked(true);
        }
        else{
            viewHolder.mLeagueTeamsCheckbox.setChecked(false);
        }


        return convertView;

    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'league_teams_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.leagueTeamsImageView) ImageView mLeagueTeamsImageView;
        @InjectView(R.id.leagueTeamsTextView) TextView mLeagueTeamsTextView;
        @InjectView(R.id.leagueTeamsCheckbox) CheckBox mLeagueTeamsCheckbox;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
