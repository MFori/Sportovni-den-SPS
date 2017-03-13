package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Match;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 07.02.2017.
 * forejt.martin97@gmail.com
 */

public class MatchesLinesAdapter extends BaseAdapter {

    private List<Match> matches;
    private Context context;

    public MatchesLinesAdapter(Context context, List<Match> matches) {
        this.matches = matches;
        this.context = context;
    }

    public static class ViewHolder {
        public TeamTextView team1;
        public TeamTextView team2;
        public TextView score1;
        public TextView score2;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.matches_lines_list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.team1 = (TeamTextView) view.findViewById(R.id.matches_lines_item_team1);
            holder.team2 = (TeamTextView) view.findViewById(R.id.matches_lines_item_team2);
            holder.score1 = (TextView) view.findViewById(R.id.matches_lines_item_score1);
            holder.score2 = (TextView) view.findViewById(R.id.matches_lines_item_score2);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (matches.size() > 0) {
            Match match = matches.get(i);

            if (match.getTeam1() != null)
                holder.team1.setTeam(match.getTeam1());
            else
                holder.team1.noTeam();

            if (match.getTeam2() != null)
                holder.team2.setTeam(match.getTeam2());
            else
                holder.team2.noTeam();

            holder.score1.setText(match.getScore1() != null ? String.valueOf(match.getScore1()) : "");
            holder.score2.setText(match.getScore2() != null ? String.valueOf(match.getScore2()) : "");
        }

        return view;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public int getCount() {
        return matches.size();
    }
}
