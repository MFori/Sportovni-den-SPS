package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.activity.MainActivity;
import cz.sps_pi.sportovni_den.entity.Match;
import cz.sps_pi.sportovni_den.entity.Performance;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;
import cz.sps_pi.sportovni_den.util.Route;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 10.01.2017.
 * forejt.martin97@gmail.com
 */

public class TimeResultsFragment extends SportDenFragment implements SwipeRefreshLayout.OnRefreshListener {
    private ResultsAdapter adapter;
    private List<TimeResult> results = new ArrayList<>();
    private SwipeRefreshLayout refresh;

    public TimeResultsFragment() {
        super("Časová osa", true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_results, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.time_results_recycler_view);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.time_results_swipe_refresh);
        refresh.setOnRefreshListener(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new ResultsAdapter(results);
        recyclerView.setAdapter(adapter);

        new Thread(new WebLoader()).start();
        refresh.setRefreshing(true);

        return view;
    }

    @Override
    public boolean onBackPressed() {
        getCallback().changePositionFromFragment(MainActivity.POSITION_RESULTS, true);
        return true;
    }

    @Override
    public void onRefresh() {
        new Thread(new WebLoader()).start();
    }

    private void showResults(List<TimeResult> results) {
        this.results.clear();
        this.results.addAll(results);
        adapter.notifyDataSetChanged();
    }

    private class WebLoader implements Runnable {
        @Override
        public void run() {
            RequestManager.createRequest(Route.get(Route.TIME_RESULTS))
                    .setListener(new RequestListener() {
                        @Override
                        public void onRequestError(Response response) {
                            refresh.setRefreshing(false);
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            final List<TimeResult> results = fromResponse(response);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showResults(results);
                                        refresh.setRefreshing(false);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onNoConnection(boolean saved) {
                            refresh.setRefreshing(false);
                        }
                    }).execute();
        }

        private List<TimeResult> fromResponse(Response response) {
            List<TimeResult> results = new ArrayList<>();
            JSONArray jsonResults = response.getArray("results");
            try {
                for (int i = 0; i < jsonResults.length(); i++) {
                    JSONObject item = jsonResults.getJSONObject(i);
                    TimeResult result = new TimeResult();
                    if (item.opt("points") != null) {
                        result.setPerformance(createPerformance(item));
                    } else {
                        result.setMatch(createMatch(item));
                    }
                    results.add(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return results;
        }

        private Match createMatch(JSONObject json) {
            Match match = new Match();
            try {
                match.setId(json.getInt("id"));
                match.setGroup(json.optInt("group", 1));

                int score1 = json.optInt("score_1", -1);
                int score2 = json.optInt("score_2", -1);

                match.setScore1(score1 == -1 ? null : score1);
                match.setScore2(score2 == -1 ? null : score2);

                JSONObject sportO = json.getJSONObject("sport");
                JSONObject team1O = json.optJSONObject("team_1");
                JSONObject team2O = json.optJSONObject("team_2");
                JSONObject statusO = json.getJSONObject("status");

                match.setSport(new Sport(
                        sportO.getInt("id"),
                        sportO.getString("name"),
                        sportO.getBoolean("active")
                ));

                if (team1O != null)
                    match.setTeam1(Team.getById(team1O.getInt("id")));
                if (team2O != null)
                    match.setTeam2(Team.getById(team2O.getInt("id")));
                match.setStatus(statusO.getInt("id"));

                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd hh:mm:ss", Locale.getDefault());
                try {
                    match.setDate(sdf.parse(json.getJSONObject("date").getString("date")));
                } catch (ParseException e) {
                    match.setDate(null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return match;
        }

        private Performance createPerformance(JSONObject json) {
            Performance performance = new Performance();
            try {
                JSONObject team0 = json.getJSONObject("team");
                JSONObject sportO = json.getJSONObject("sport");

                performance.setId(json.getInt("id"));
                performance.setTeam(Team.getById(team0.getInt("id")));
                performance.setSport(new Sport(
                        sportO.getInt("id"),
                        sportO.getString("name"),
                        sportO.getBoolean("active")
                ));
                performance.setPoints(json.getInt("points"));

                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd hh:mm:ss", Locale.getDefault());
                try {
                    performance.setDate(sdf.parse(json.getJSONObject("date").getString("date")));
                } catch (ParseException e) {
                    performance.setDate(null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return performance;
        }

    }

    private class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_MATCH = 1;
        private static final int TYPE_PERFORMANCE = 2;

        private List<TimeResult> results;

        private ResultsAdapter(List<TimeResult> results) {
            this.results = results;
        }

        class MatchViewHolder extends RecyclerView.ViewHolder {
            private TeamTextView team1;
            private TeamTextView team2;
            private TextView score1;
            private TextView score2;
            private TextView sport;
            private TextView date;

            private MatchViewHolder(View v) {
                super(v);
                team1 = (TeamTextView) v.findViewById(R.id.time_results_match_team1);
                team2 = (TeamTextView) v.findViewById(R.id.time_results_match_team2);
                score1 = (TextView) v.findViewById(R.id.time_results_match_score1);
                score2 = (TextView) v.findViewById(R.id.time_results_match_score2);
                sport = (TextView) v.findViewById(R.id.time_results_match_sport);
                date = (TextView) v.findViewById(R.id.time_results_match_date);
            }
        }

        class PerformanceViewHolder extends RecyclerView.ViewHolder {
            private TeamTextView team;
            private TextView points;
            private TextView sport;
            private TextView date;

            private PerformanceViewHolder(View v) {
                super(v);
                team = (TeamTextView) v.findViewById(R.id.time_results_performance_team);
                points = (TextView) v.findViewById(R.id.time_results_performance_points);
                sport = (TextView) v.findViewById(R.id.time_results_performance_sport);
                date = (TextView) v.findViewById(R.id.time_results_performance_date);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_MATCH:
                    View vMatch = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.results_time_list_item_match, parent, false);
                    return new MatchViewHolder(vMatch);
                case TYPE_PERFORMANCE:
                    View vPer = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.results_time_list_item_performance, parent, false);
                    return new PerformanceViewHolder(vPer);
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if (results.get(position).isMatch()) {
                return TYPE_MATCH;
            } else {
                return TYPE_PERFORMANCE;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TimeResult result = results.get(position);
            switch (holder.getItemViewType()) {
                case TYPE_MATCH:
                    MatchViewHolder mHolder = (MatchViewHolder) holder;
                    Match match = result.getMatch();
                    mHolder.team1.setTeam(match.getTeam1());
                    mHolder.team2.setTeam(match.getTeam2());
                    mHolder.score1.setText(String.valueOf(match.getScore1()));
                    mHolder.score2.setText(String.valueOf(match.getScore2()));
                    mHolder.sport.setText(match.getSport().getName());
                    if (match.getDate() != null) {
                        SimpleDateFormat df = new SimpleDateFormat("dd. MM. yyyy HH:mm:ss", Locale.ENGLISH);
                        mHolder.date.setText(df.format(match.getDate()));
                    }
                    break;
                case TYPE_PERFORMANCE:
                    PerformanceViewHolder pHolder = (PerformanceViewHolder) holder;
                    Performance performance = result.getPerformance();
                    pHolder.team.setTeam(performance.getTeam());
                    pHolder.points.setText(String.valueOf(performance.getPoints()));
                    pHolder.sport.setText(performance.getSport().getName());
                    if (performance.getDate() != null) {
                        SimpleDateFormat df = new SimpleDateFormat("dd. MM. yyyy HH:mm:ss", Locale.ENGLISH);
                        pHolder.date.setText(df.format(performance.getDate()));
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
    }

    private class TimeResult {
        private Match match = null;
        private Performance performance = null;

        private boolean isMatch() {
            return match != null;
        }

        public Match getMatch() {
            return match;
        }

        public void setMatch(Match match) {
            this.match = match;
        }

        public Performance getPerformance() {
            return performance;
        }

        public void setPerformance(Performance performance) {
            this.performance = performance;
        }
    }

}
