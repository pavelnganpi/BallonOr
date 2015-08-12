package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.ui.LeagueTeamsActivity;
import com.paveynganpi.ballonor.utils.LeaguesConstants;

/**
 * Created by paveynganpi on 7/19/15.
 */
public class LeaguesAdapter extends RecyclerView.Adapter<LeaguesAdapter.LeaguesViewHolder>{

    protected Context mContext;
    protected String[] mLeageNames;

    public LeaguesAdapter(Context context){
        this.mContext = context;
        mLeageNames = LeaguesConstants.getLeagueNames();
    }

    @Override
    public LeaguesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.leagues_item, parent, false);
        return new LeaguesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaguesViewHolder leaguesViewHolder, int position) {
        leaguesViewHolder.bindLeagues(mLeageNames[position], position);
    }

    @Override
    public int getItemCount() {
        return mLeageNames.length;
    }

    public class LeaguesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mLeaguesImageView;
        public TextView mLeaguesTextView;
        int mPosition;


        public LeaguesViewHolder(View itemView){
            super(itemView);
            mLeaguesImageView = (ImageView) itemView.findViewById(R.id.leaguesImageView);
            mLeaguesTextView = (TextView) itemView.findViewById(R.id.leaguesTextView);
            itemView.setOnClickListener(this);

        }

        public void bindLeagues(String leagueName, int postion){
            mPosition = postion;
            mLeaguesTextView.setText(leagueName);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, LeagueTeamsActivity.class);
            intent.putExtra("LeagueName", mLeageNames[mPosition]);
            mContext.startActivity(intent);
        }
    }
}
