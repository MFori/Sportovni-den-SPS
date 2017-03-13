package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Sport;

/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

public class SportAdapter extends BaseAdapter {

    private Context context;
    private final List<Sport> sports;

    public SportAdapter(Context context, List<Sport> sports) {
        this.context = context;
        this.sports = sports;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.sport_grid_item, parent, false);
            TextView textView = (TextView) gridView.findViewById(R.id.grid_item_sport_text);
            ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_sport_icon);
            textView.setText(sports.get(position).getName());
            imageView.setImageResource(Sport.iconById(sports.get(position).getId()));
        } else {
            gridView = convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return sports.size();
    }

    @Override
    public Object getItem(int position) {
        return sports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
