package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class TeamAdapter extends BaseAdapter {

    private Context context;
    private final Team[] teams;

    public TeamAdapter(Context context, Team[] teams) {
        this.context = context;
        this.teams = teams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.team_grid_item, parent, false);
            TeamTextView textView = (TeamTextView) gridView.findViewById(R.id.grid_item_team);
            textView.setTeam(teams[position]);
        } else {
            gridView = convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return teams.length;
    }

    @Override
    public Object getItem(int position) {
        return teams[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
