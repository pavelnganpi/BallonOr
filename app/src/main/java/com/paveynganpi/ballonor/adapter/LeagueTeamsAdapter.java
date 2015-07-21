package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.utils.TeamsConstants;
import com.squareup.picasso.Picasso;

/**
 * Created by paveynganpi on 7/20/15.
 */
public class LeagueTeamsAdapter extends RecyclerView.Adapter<LeagueTeamsAdapter.LeagueTeamsViewHolder> {
    protected Context mContext;
    protected String[] mLeageTeamsNames;

    public LeagueTeamsAdapter(Context context, String league){
        this.mContext = context;
        mLeageTeamsNames = TeamsConstants.getTeam(league);
    }

    @Override
    public LeagueTeamsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.league_teams_item, parent, false);
        return new LeagueTeamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeagueTeamsViewHolder leagueTeamsViewHolder, int position) {
        leagueTeamsViewHolder.bindLeagueTeams(mLeageTeamsNames[position], position);

    }

    @Override
    public int getItemCount() {
        return mLeageTeamsNames.length;
    }

    public class LeagueTeamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mLeagueTeamsImageView;
        public TextView mLeagueTeamsTextView;
        public int mPosition;

        public LeagueTeamsViewHolder(View itemView) {
            super(itemView);
            mLeagueTeamsImageView = (ImageView) itemView.findViewById(R.id.leagueTeamsImageView);
            mLeagueTeamsTextView = (TextView) itemView.findViewById(R.id.leagueTeamsTextView);

            itemView.setOnClickListener(this);
        }

        public void bindLeagueTeams(String team, int position){
            mLeagueTeamsTextView.setText(team);
            mPosition = position;
            Picasso.with(mContext)
                    .load(TeamsConstants.getTeamImageUrl())
                    .resize(110, 110)
                    .into(mLeagueTeamsImageView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
