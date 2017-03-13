package cz.sps_pi.sportovni_den.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.CompleteResultRepository;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.CompleteResultsAdapter;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;
import cz.sps_pi.sportovni_den.util.Route;

/**
 * Created by Martin Forejt on 16.01.2017.
 * forejt.martin97@gmail.com
 */

public class ResultsCompleteActivity extends SportDenActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout layout;
    private List<CompleteResultsAdapter.Result> results = new ArrayList<>();
    private CompleteResultsAdapter adapter;
    private boolean exist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_complete);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.complete_results_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        DbLoader dbLoader = new DbLoader();
        dbLoader.execute();

        new Thread(new WebLoader()).start();

        layout = (SwipeRefreshLayout) findViewById(R.id.complete_results_refresh);
        layout.setOnRefreshListener(this);
        layout.post(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(true);
            }
        });

        adapter = new CompleteResultsAdapter(results, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.nothing, R.anim.to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Thread(new WebLoader()).start();
    }

    private class WebLoader implements Runnable {

        @Override
        public void run() {
            RequestManager.createRequest(Route.get(Route.COMPLETE_RESULTS))
                    .setListener(new RequestListener() {
                        @Override
                        public void onRequestError(Response response) {
                            layout.setRefreshing(false);
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            saveResults(getResults(response));
                            if (exist)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DbLoader dbLoader = new DbLoader();
                                        dbLoader.execute();
                                        layout.setRefreshing(false);
                                    }
                                });
                        }

                        @Override
                        public void onNoConnection(boolean saved) {
                            layout.setRefreshing(false);
                        }
                    }).execute();
        }

        private void saveResults(List<CompleteResultsAdapter.Result> results) {
            CompleteResultRepository repo = new CompleteResultRepository();
            for (CompleteResultsAdapter.Result result : results) {
                repo.updateTeamResult(result);
            }
        }

        private List<CompleteResultsAdapter.Result> getResults(Response response) {
            List<CompleteResultsAdapter.Result> results = new ArrayList<>();

            JSONArray array = response.getArray("results");
            for (int i = 0; i < array.length(); i++) {
                CompleteResultsAdapter.Result RESULT = new CompleteResultsAdapter.Result();
                try {

                    JSONObject result = array.getJSONObject(i);
                    JSONObject team = result.getJSONObject("team");
                    int position = result.getInt("position");

                    RESULT.setTeam(Team.getById(team.getInt("id")));
                    RESULT.setPosition(position);

                    Map<Sport, Integer> sportResult = new HashMap<>();

                    JSONArray sports = result.getJSONArray("sports");
                    for (int j = 0; j < sports.length(); j++) {
                        JSONObject sportRes = sports.getJSONObject(j);
                        JSONObject sport = sportRes.getJSONObject("sport");
                        int points = sportRes.getInt("points");

                        sportResult.put(new Sport(
                                sport.getInt("id"),
                                sport.getString("name"),
                                sport.getBoolean("active")
                        ), points);
                    }

                    RESULT.setResults(sportResult);
                    results.add(RESULT);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return results;
        }
    }

    private class DbLoader extends AsyncTask<Void, Void, List<CompleteResultsAdapter.Result>> {

        @Override
        protected List<CompleteResultsAdapter.Result> doInBackground(Void... voids) {
            CompleteResultRepository repo = new CompleteResultRepository();

            List<CompleteResultsAdapter.Result> results = repo.getResults();

            Collections.sort(results, new TeamComparator());

            return results;
        }

        private class TeamComparator implements Comparator<CompleteResultsAdapter.Result> {
            @Override
            public int compare(CompleteResultsAdapter.Result r1, CompleteResultsAdapter.Result r2) {
                if (r1.getPosition() == r2.getPosition()) return 0;
                else return r1.getPosition() < r2.getPosition() ? 1 : -1;
            }
        }

        @Override
        protected void onPostExecute(List<CompleteResultsAdapter.Result> results) {
            ResultsCompleteActivity.this.results.clear();
            ResultsCompleteActivity.this.results.addAll(results);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exist = false;
    }
}
