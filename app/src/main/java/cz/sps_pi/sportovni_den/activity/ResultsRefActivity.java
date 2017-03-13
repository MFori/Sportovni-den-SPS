package cz.sps_pi.sportovni_den.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import cz.sps_pi.sportovni_den.Fragment.IndividualRefereeFragment;
import cz.sps_pi.sportovni_den.Fragment.TeamRefereeFragment;
import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.listener.MainActivityCallBack;
import cz.sps_pi.sportovni_den.util.SportResult;

/**
 * Created by Martin Forejt on 16.01.2017.
 * forejt.martin97@gmail.com
 */

public class ResultsRefActivity extends SportDenActivity {

    public static final String CLASS = "cz.sps_pi.sportovni_den.activity.ResultsRefActivity";
    private Sport sport;
    private int sportId;
    private MainActivityCallBack callBack;
    private boolean exist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_ref);

        sportId = getIntent().getIntExtra("sport", 0);

        TypeLoader typeLoader = new TypeLoader();
        typeLoader.execute(sportId);

        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Change fragment
     *
     * @param fragment new Fragment
     */
    public void changeFragment(Fragment fragment) {
        changeFragment(fragment, true);
    }

    /**
     * Change fragment with new/ from backStack
     *
     * @param fragment     new Fragment
     * @param popBackStack use from backStack?
     */
    public void changeFragment(Fragment fragment, boolean popBackStack) {
        callBack = (MainActivityCallBack) fragment;
        FragmentManager manager = getSupportFragmentManager();
        if (popBackStack && manager.getBackStackEntryCount() > 1) {
            manager.popBackStackImmediate();
        } else {
            if (!popBackStack) {
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.results_referee_container, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * Task for getting sport and sportType from db
     */
    private class TypeLoader extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... sports) {
            SportRepository sportRepository = new SportRepository();
            ResultsRefActivity.this.sport = sportRepository.getSport(sports[0]);
            return ResultsRefActivity.this.sport.getScoring();
        }

        @Override
        protected void onPostExecute(Integer type) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(sport.getName());
            }
            if (type != null) {
                switch (type) {
                    case SportResult.TYPE_INDIVIDUALS:
                        changeFragment(IndividualRefereeFragment.newInstance(sportId));
                        break;
                    case SportResult.TYPE_GROUP_FINALE:
                    case SportResult.TYPE_GROUP_GROUP:
                    case SportResult.TYPE_ROBIN:
                        changeFragment(TeamRefereeFragment.newInstance(sportId));
                        break;
                }
            }
        }
    }

    /**
     * Get sport
     *
     * @return Sport
     */
    public Sport getSport() {
        return sport;
    }

    @Override
    public void onBackPressed() {
        if (callBack == null || !callBack.onBackPressed()) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.nothing, R.anim.to_right);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Can activity/fragment access ui
     *
     * @return can?
     */
    public boolean canAccessUi() {
        return exist;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exist = false;
    }

}