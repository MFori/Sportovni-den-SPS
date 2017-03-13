package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Table;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 07.02.2017.
 * forejt.martin97@gmail.com
 */

public class TablesLinesAdapter extends BaseAdapter {

    private List<Table.Line> lines;
    private Context context;

    public TablesLinesAdapter(Context context, List<Table.Line> lines) {
        this.lines = lines;
        this.context = context;
    }

    public static class ViewHolder {
        public TextView position;
        public TeamTextView team;
        public TextView points;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tables_lines_list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.position = (TextView) view.findViewById(R.id.tables_lines_item_position);
            holder.team = (TeamTextView) view.findViewById(R.id.tables_lines_item_team);
            holder.points = (TextView) view.findViewById(R.id.tables_lines_item_points);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (lines.size() > 0) {
            Table.Line line = lines.get(i);

            holder.position.setText(String.valueOf(line.getPosition()));
            if (line.getTeam() != null)
                holder.team.setTeam(line.getTeam());
            else
                holder.team.noTeam();
            holder.points.setText(String.valueOf(line.getPoints()));
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
        return lines.size();
    }
}
