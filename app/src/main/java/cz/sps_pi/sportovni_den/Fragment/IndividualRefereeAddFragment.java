package cz.sps_pi.sportovni_den.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.PerformanceRepository;
import cz.sps_pi.sportovni_den.db.TeamRepository;
import cz.sps_pi.sportovni_den.entity.Performance;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;
import cz.sps_pi.sportovni_den.util.Route;

/**
 * Created by Martin Forejt on 20.01.2017.
 * forejt.martin97@gmail.com
 */

public class IndividualRefereeAddFragment extends ResultsRefFragment {

    private Performance performance = null;
    private ProgressDialog pDialog;
    private Spinner teamsSpinner;
    private EditText pointsInput;

    public static IndividualRefereeAddFragment newInstance() {
        return new IndividualRefereeAddFragment();
    }

    public static IndividualRefereeAddFragment newInstance(int id) {
        IndividualRefereeAddFragment fragment = new IndividualRefereeAddFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("performance", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            PerformanceRepository performanceRepository = new PerformanceRepository();
            performance = performanceRepository.getPerformance(bundle.getInt("performance"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_referee_add, container, false);

        teamsSpinner = (Spinner) view.findViewById(R.id.individual_referee_add_spinner);
        pointsInput = (EditText) view.findViewById(R.id.individual_referee_add_points);

        TeamRepository teamRepository = new TeamRepository();
        List<Team> teams = teamRepository.getActiveTeams();
        TeamsAdapter adapter = new TeamsAdapter(getContext(), R.layout.teams_spinner_item, teams);
        teamsSpinner.setAdapter(adapter);

        if (performance != null) {
            pointsInput.setText(String.valueOf(performance.getPoints()));
            teamsSpinner.setSelection(adapter.getPosition(performance.getTeam()));
        }

        Button submit = (Button) view.findViewById(R.id.individual_referee_add_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        return view;
    }

    private void submit() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Zapisování...");
        pDialog.setCancelable(false);
        pDialog.show();

        Performance performance = this.performance;
        Route route;
        String id;
        if (performance == null) {
            route = Route.get(Route.ADD_PERFORMANCE);
            performance = new Performance();
            performance.setSport(getSport());
            performance.setTeam(Team.valueOf(teamsSpinner.getSelectedItem().toString()));
            id = null;
        } else {
            route = Route.get(Route.UPDATE_PERFORMANCE);
            id = String.valueOf(performance.getId());
        }
        try {
            performance.setPoints(Integer.valueOf(pointsInput.getText().toString()));
        } catch (NumberFormatException e) {
            pDialog.dismiss();
            Toast.makeText(getContext(), "Body jsou špatně zadané", Toast.LENGTH_LONG).show();
            return;
        }

        RequestManager.createRequest(route)
                .addParameter("team", Team.valueOf(teamsSpinner.getSelectedItem().toString()).getId())
                .addParameter("sport", getSport().getId())
                .addParameter("points", pointsInput.getText().toString())
                .addParameter("id", id)
                .saveIfNoConn(true)
                .setListener(new RequestListener() {
                    @Override
                    public void onRequestError(Response response) {
                        pDialog.dismiss();
                        changeFragment(IndividualRefereeFragment.newInstance(getSport().getId()));
                    }

                    @Override
                    public void onRequestSuccess(Response response) {
                        pDialog.dismiss();
                        changeFragment(IndividualRefereeFragment.newInstance(getSport().getId()));
                    }

                    @Override
                    public void onNoConnection(boolean saved) {
                        pDialog.dismiss();
                        changeFragment(IndividualRefereeFragment.newInstance(getSport().getId()));
                    }
                }).execute();

        PerformanceRepository performanceRepository = new PerformanceRepository();
        performanceRepository.insertPerformance(performance);
    }

    @Override
    public boolean onBackPressed() {
        changeFragment(IndividualRefereeFragment.newInstance(getSport().getId()));
        return true;
    }

    private class TeamsAdapter extends ArrayAdapter {
        private List<Team> teams;

        private TeamsAdapter(Context context, int resId, List<Team> teams) {
            super(context, resId, teams);
            this.teams = teams;
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.teams_spinner_item, parent, false);
            TextView name = (TextView) view.findViewById(R.id.teams_spinner_item_textView);
            name.setText(teams.get(position).getName());
            name.setTextColor(ContextCompat.getColor(getContext(), teams.get(position).getColor()));
            return view;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }
}
