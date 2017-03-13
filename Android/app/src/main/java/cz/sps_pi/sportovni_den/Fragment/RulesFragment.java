package cz.sps_pi.sportovni_den.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.activity.MainActivity;
import cz.sps_pi.sportovni_den.db.RulesRepository;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;
import cz.sps_pi.sportovni_den.util.Route;

public class RulesFragment extends SportDenFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String CLASS = "cz.sps_pi.sportovni_den.Fragment.RulesFragment";
    private int sportId;
    private TextView content;
    private SwipeRefreshLayout layout;
    private boolean firstLoad = true;

    public RulesFragment() {
        super("Pravidla", true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            sportId = bundle.getInt("sport");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules, container, false);

        content = (TextView) view.findViewById(R.id.rules_content);

        SportLoader sportLoader = new SportLoader();
        sportLoader.execute(sportId);
        RulesDbLoader loader = new RulesDbLoader();
        loader.execute(sportId);

        layout = (SwipeRefreshLayout) view.findViewById(R.id.rules_swipe_r_layout);
        layout.setOnRefreshListener(this);

        return view;
    }

    private void changeRules(String rules) {
        content.setText(rules);
    }

    @Override
    public void onRefresh() {
        new Thread(new RulesWebLoader()).start();
    }

    @Override
    public boolean onBackPressed() {
        Fragment fragment = SportDenFragment.getFragment(MainActivity.POSITION_RULES, getCallback());
        getCallback().changeFragmentFromFragment(fragment);
        return true;
    }

    private class RulesDbLoader extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... sports) {

            RulesRepository repo = new RulesRepository();
            return repo.getRules(sports[0]);
        }

        @Override
        protected void onPostExecute(String rules) {
            if (firstLoad && rules == null) {
                firstLoad = false;
                layout.setRefreshing(true);
                new Thread(new RulesWebLoader()).start();
            }
            changeRules(rules);
        }
    }

    private class SportLoader extends AsyncTask<Integer, Void, Sport> {
        @Override
        protected Sport doInBackground(Integer... sports) {
            SportRepository repo = new SportRepository();
            return repo.getSport(sports[0]);
        }

        @Override
        protected void onPostExecute(Sport sport) {
            if (sport != null) {
                getCallback().setTitle(sport.getName());
            }
        }
    }

    private class RulesWebLoader implements Runnable {

        @Override
        public void run() {
            RequestManager.createRequest(Route.get(Route.GET_RULES))
                    .addParameter("sport", "")
                    .setListener(new RequestListener() {
                        @Override
                        public void onRequestError(Response response) {
                            layout.setRefreshing(false);
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            final String rules = saveResponse(response);
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        changeRules(rules);
                                    }
                                });
                            } catch (NullPointerException e) {
                                //
                            }
                            layout.setRefreshing(false);
                        }

                        @Override
                        public void onNoConnection(boolean saved) {
                            layout.setRefreshing(false);
                        }
                    }).execute();
        }

        private String saveResponse(Response response) {
            String result = null;
            JSONArray rules = response.getArray("rules");
            RulesRepository repo = new RulesRepository();
            for (int i = 0; i < rules.length(); i++) {
                try {
                    JSONObject rule = rules.getJSONObject(i);
                    JSONObject sport = rule.getJSONObject("sport");

                    int sportId = sport.getInt("id");
                    String sRules = rule.getString("text");
                    if (sportId == RulesFragment.this.sportId) {
                        result = sRules;
                    }

                    try {
                        repo.updateRules(sportId, sRules);
                    } catch (IllegalStateException e) {
                        //
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }
    }
}
