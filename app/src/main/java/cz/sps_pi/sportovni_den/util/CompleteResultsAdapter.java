package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 18.01.2017.
 * forejt.martin97@gmail.com
 */

public class CompleteResultsAdapter extends RecyclerView.Adapter<CompleteResultsAdapter.ResViewHolder> {

    private List<Result> results;
    private Context context;
    private int[] visibilities;

    public CompleteResultsAdapter(List<Result> results, Context context) {
        this.results = results;
        this.context = context;
        visibilities = new int[results.size()];
        for (int i = 0; i < visibilities.length; i++)
            visibilities[i] = View.GONE;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int[] vis_ = visibilities;
                visibilities = new int[CompleteResultsAdapter.this.results.size()];
                for (int i = 0; i < visibilities.length; i++)
                    if (vis_.length - 1 >= i) visibilities[i] = vis_[i];
                    else visibilities[i] = View.GONE;
            }
        });
    }

    public class ResViewHolder extends RecyclerView.ViewHolder {
        private TeamTextView team;
        private TextView points;
        private TextView position;
        private ListView sports;
        private LinearLayout content;
        private CardView card;
        private View line;

        private ResViewHolder(View v) {
            super(v);
            content = (LinearLayout) v.findViewById(R.id.complete_results_team_content);
            team = (TeamTextView) v.findViewById(R.id.complete_results_team);
            points = (TextView) v.findViewById(R.id.complete_results_points);
            position = (TextView) v.findViewById(R.id.complete_results_position);
            sports = (ListView) v.findViewById(R.id.complete_results_sports);
            line = v.findViewById(R.id.complete_results_bottom_line);
            card = (CardView) v.findViewById(R.id.complete_results_card);
        }
    }

    @Override
    public ResViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complete_results_list_item, parent, false);
        return new ResViewHolder(v);
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onBindViewHolder(final ResViewHolder holder, int position) {
        Result result = results.get(position);

        holder.team.setTeam(result.getTeam());
        holder.points.setText(String.valueOf(result.getPosition()));
        holder.position.setText(String.valueOf(position + 1));
        holder.sports.setAdapter(new SubListAdapter(result.getResults()));

        switch (position) {
            case 0:
                setBackground(holder.card, R.drawable.gold_gradient);
                break;
            case 1:
                setBackground(holder.card, R.drawable.silver_gradient);
                break;
            case 2:
                setBackground(holder.card, R.drawable.bronze_gradient);
                break;
            default:
                setBackground(holder.card, R.drawable.gray_gradient);
                break;
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();

                if (visibilities[pos] == View.GONE) {
                    visibilities[pos] = View.VISIBLE;
                } else {
                    visibilities[pos] = View.GONE;
                }
                holder.sports.setVisibility(visibilities[pos]);
                holder.line.setVisibility(visibilities[pos]);
            }
        });

        holder.sports.setVisibility(visibilities[position]);
        holder.line.setVisibility(visibilities[position]);
    }

    private void setBackground(CardView card, int drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            card.setBackground(ContextCompat.getDrawable(context, drawable));
        } else {
            card.setBackgroundDrawable(ContextCompat.getDrawable(context, drawable));
        }
    }

    public int getItemCount() {
        return results.size();
    }

    public static class Result {
        private int position;
        private Team team;
        private Map<Sport, Integer> results;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Team getTeam() {
            return team;
        }

        public void setTeam(Team team) {
            this.team = team;
        }

        public Map<Sport, Integer> getResults() {
            return results;
        }

        public void setResults(Map<Sport, Integer> results) {
            this.results = results;
        }
    }

    private class SubListAdapter extends BaseAdapter {
        private final ArrayList results;

        private SubListAdapter(Map<Sport, Integer> results) {
            this.results = new ArrayList();

            Map<Sport, Integer> teamResults = new TreeMap<>(new Comparator<Sport>() {
                @Override
                public int compare(Sport sport, Sport t1) {
                    return sport.getId() < t1.getId() ? -1 : 1;
                }
            });
            teamResults.putAll(results);

            this.results.addAll(teamResults.entrySet());
        }

        private class ViewHolder {
            TextView sport;
            TextView points;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.complete_results_sport_item, viewGroup, false);

                holder = new ViewHolder();
                holder.sport = (TextView) view.findViewById(R.id.complete_results_sport_name);
                holder.points = (TextView) view.findViewById(R.id.complete_results_sport_points);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (results.size() > 0) {

                Map.Entry<Sport, Integer> item = getItem(i);

                holder.sport.setText(item.getKey().getName());
                holder.points.setText(String.valueOf(item.getValue()));
            }

            return view;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Map.Entry<Sport, Integer> getItem(int i) {
            return (Map.Entry) results.get(i);
        }

        public int getCount() {
            return results.size();
        }
    }
}
