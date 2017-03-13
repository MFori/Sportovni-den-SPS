package cz.sps_pi.sportovni_den.activity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;

import java.util.List;

import cz.sps_pi.sportovni_den.Fragment.GroupSportResultsFragment;
import cz.sps_pi.sportovni_den.Fragment.IndividualSportResultsFragment;
import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.GroupRepository;
import cz.sps_pi.sportovni_den.db.ResultMatchRepository;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.db.TableLineRepository;
import cz.sps_pi.sportovni_den.entity.Group;
import cz.sps_pi.sportovni_den.entity.Match;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Table;
import cz.sps_pi.sportovni_den.listener.ResultFragmentCallback;
import cz.sps_pi.sportovni_den.listener.ResultsRefreshListener;
import cz.sps_pi.sportovni_den.util.IndividualSportResult;
import cz.sps_pi.sportovni_den.util.SportResult;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;
import cz.sps_pi.sportovni_den.util.Route;
import cz.sps_pi.sportovni_den.util.TeamSportResult;

/**
 * Created by Martin Forejt on 16.01.2017.
 * forejt.martin97@gmail.com
 */

public class ResultsSportActivity extends SportDenActivity implements ResultFragmentCallback,
        SwipeRefreshLayout.OnRefreshListener {

    private int sportId;
    private SportResult result = null;
    private SwipeRefreshLayout swipeRefresh;
    private boolean exist = true;
    private ResultsRefreshListener refreshCallback;

    public static final String CLASS = "cz.sps_pi.sportovni_den.activity.ResultsSportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_sport);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.sport_results_swipe);
        swipeRefresh.setOnRefreshListener(this);

        sportId = getIntent().getIntExtra("sport", 0);

        ResultsLoader loader = new ResultsLoader();
        loader.execute(sportId);

        new Thread(new WebLoader(sportId)).start();

        swipeRefresh.setRefreshing(true);
    }

    @Override
    public SportResult getResult() {
        return result;
    }

    @Override
    public SwipeRefreshLayout getRefreshLayout() {
        return swipeRefresh;
    }

    /**
     * Show results, update current via refreshCallbacks onUpdate method or add new Fragment
     * Update action bar title
     *
     * @param result Sport result
     */
    private void showResults(SportResult result) {
        this.result = result;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(result.getSport().getName());
        }
        if (refreshCallback != null) {
            refreshCallback.onUpdate(result);
            return;
        }
        switch (result.getScoring()) {
            case SportResult.TYPE_INDIVIDUALS:
                changeFragment(new IndividualSportResultsFragment());
                break;
            case SportResult.TYPE_ROBIN:
                changeFragment(new GroupSportResultsFragment());
                break;
            case SportResult.TYPE_GROUP_FINALE:
            case SportResult.TYPE_GROUP_GROUP:
                changeFragment(new GroupSportResultsFragment());
                break;
        }
    }

    /**
     * Replace fragment
     *
     * @param fragment new Fragment
     */
    private void changeFragment(Fragment fragment) {
        refreshCallback = (ResultsRefreshListener) fragment;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.sport_results_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onRefresh() {
        new Thread(new WebLoader(sportId)).start();
    }

    /**
     * Task for getting results from db
     */
    private class ResultsLoader extends AsyncTask<Integer, Void, SportResult> {
        @Override
        protected SportResult doInBackground(Integer... sports) {
            int sportId = sports[0];

            SportRepository sportRepository = new SportRepository();
            Sport sport = sportRepository.getSport(sportId);

            switch (sport.getScoring()) {
                case SportResult.TYPE_INDIVIDUALS:
                    return getIndividuals(sport);
                case SportResult.TYPE_GROUP_FINALE:
                case SportResult.TYPE_GROUP_GROUP:
                case SportResult.TYPE_ROBIN:
                    return getTeams(sport);
            }

            return null;
        }

        /**
         * Get individuals results from db for sport
         *
         * @param sport Sport
         * @return results object
         */
        private SportResult getIndividuals(Sport sport) {
            TableLineRepository repository = new TableLineRepository();
            Table table = new Table();
            table.setLines(repository.getLines(sport.getId()));

            IndividualSportResult result = new IndividualSportResult();
            result.setTable(table);
            result.setSport(sport);
            result.setScoring(SportResult.TYPE_INDIVIDUALS);

            return result;
        }

        /**
         * Get team sport results from db for sport
         *
         * @param sport Sport
         * @return results object
         */
        private SportResult getTeams(Sport sport) {
            TeamSportResult result = new TeamSportResult();

            GroupRepository groupRepository = new GroupRepository();
            List<Group> groups = groupRepository.getGroups(sport.getId());

            ResultMatchRepository matchRepository = new ResultMatchRepository();
            TableLineRepository tableRepository = new TableLineRepository();
            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                group.setMatches(matchRepository.getMatches(sport, group.getGroup()));
                Table table = new Table();
                table.setLines(tableRepository.getLines(sport.getId(), group.getGroup()));
                group.setTable(table);
                if (group.getMatches().size() + table.getLines().size() == 0) {
                    groups.remove(i);
                    i--;
                }
            }

            result.setGroups(groups);
            result.setSport(sport);
            result.setScoring(sport.getScoring());

            return result;
        }

        @Override
        protected void onPostExecute(SportResult sportResult) {
            if (sportResult != null && exist)
                showResults(sportResult);
        }
    }

    /**
     * Loader for getting results from web
     */
    private class WebLoader implements Runnable {

        private int sport;

        private WebLoader(int sport) {
            this.sport = sport;
        }

        @Override
        public void run() {
            RequestManager.createRequest(Route.get(Route.SPORT_RESULTS))
                    .addParameter("sport", sport)
                    .setListener(new RequestListener() {
                        @Override
                        public void onRequestError(Response response) {
                            swipeRefresh.setRefreshing(false);
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            final SportResult result = SportResult.fromResponse(response);
                            SportRepository repository = new SportRepository();
                            result.setSport(repository.getSport(sport));

                            if (exist) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showResults(result);
                                        swipeRefresh.setRefreshing(false);
                                    }
                                });
                            }
                            saveToDb(result);
                        }

                        @Override
                        public void onNoConnection(boolean saved) {
                            swipeRefresh.setRefreshing(false);
                        }
                    })
                    .execute();
        }

        /**
         * Save downloaded data to local db
         *
         * @param result results object
         */
        private void saveToDb(SportResult result) {
            switch (result.getScoring()) {
                case SportResult.TYPE_INDIVIDUALS:
                    saveIndividuals((IndividualSportResult) result);
                    break;
                case SportResult.TYPE_GROUP_FINALE:
                case SportResult.TYPE_GROUP_GROUP:
                case SportResult.TYPE_ROBIN:
                    saveTeams((TeamSportResult) result);
                    break;
            }
        }

        /**
         * Save individuals results to db
         *
         * @param result results object
         */
        private void saveIndividuals(IndividualSportResult result) {
            TableLineRepository repository = new TableLineRepository();
            for (Table.Line line : result.getTable().getLines()) {
                repository.insert(line, result.getSport().getId());
            }
        }

        /**
         * Save teams results to db
         *
         * @param result results object
         */
        private void saveTeams(TeamSportResult result) {
            TableLineRepository lineRepository = new TableLineRepository();
            GroupRepository groupRepository = new GroupRepository();
            ResultMatchRepository matchRepository = new ResultMatchRepository();

            for (Group group : result.getGroups()) {
                groupRepository.insert(group.getGroup(), result.getSport().getId(), group.getName());
                for (Match match : group.getMatches()) {
                    matchRepository.insert(match);
                }
                lineRepository.delete(result.getSport().getId(), group.getGroup());
                for (Table.Line line : group.getTable().getLines()) {
                    lineRepository.insert(line, result.getSport().getId(), group.getGroup());
                }
            }
        }
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
    public void onDestroy() {
        super.onDestroy();
        exist = false;
    }
}
