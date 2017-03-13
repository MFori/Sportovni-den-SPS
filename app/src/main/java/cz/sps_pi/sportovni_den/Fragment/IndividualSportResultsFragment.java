package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Table;
import cz.sps_pi.sportovni_den.listener.ResultFragmentCallback;
import cz.sps_pi.sportovni_den.listener.ResultsRefreshListener;
import cz.sps_pi.sportovni_den.util.IndividualSportResult;
import cz.sps_pi.sportovni_den.util.SportResult;
import cz.sps_pi.sportovni_den.view.ResultRecyclerView;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 20.01.2017.
 * forejt.martin97@gmail.com
 */

public class IndividualSportResultsFragment extends Fragment implements ResultsRefreshListener {

    private MyAdapter adapter;
    private List<Table.Line> lines;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_sport_results, container, false);

        final ResultRecyclerView recyclerView = new ResultRecyclerView(getContext(),
                ((ResultFragmentCallback) getActivity()).getRefreshLayout());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        IndividualSportResult result = (IndividualSportResult) ((ResultFragmentCallback) getActivity()).getResult();
        lines = result.getTable().getLines();
        adapter = new MyAdapter(lines);
        recyclerView.setAdapter(adapter);

        ((LinearLayout) view.findViewById(R.id.individual_sport_result_container)).addView(recyclerView);

        return view;
    }

    @Override
    public void onUpdate(SportResult result) {
        lines.clear();
        lines.addAll(((IndividualSportResult) result).getTable().getLines());
        adapter.notifyDataSetChanged();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.LineHolder> {
        private List<Table.Line> lines;

        private MyAdapter(List<Table.Line> lines) {
            this.lines = lines;
        }

        public class LineHolder extends RecyclerView.ViewHolder {
            TextView position;
            TeamTextView team;
            TextView points;

            private LineHolder(View v) {
                super(v);
                position = (TextView) v.findViewById(R.id.individual_results_item_position);
                team = (TeamTextView) v.findViewById(R.id.individual_results_item_team);
                points = (TextView) v.findViewById(R.id.individual_results_item_points);
            }
        }

        @Override
        public LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.individual_results_list_item, parent, false);
            return new LineHolder(v);
        }

        @Override
        public void onBindViewHolder(LineHolder holder, int position) {
            Table.Line line = lines.get(position);

            String posStr = String.valueOf(line.getPosition()) + ".";

            holder.position.setText(posStr);
            holder.team.setTeam(line.getTeam());
            holder.points.setText(String.valueOf(line.getPoints()));
        }

        @Override
        public int getItemCount() {
            return lines.size();
        }
    }
}
