package cz.sps_pi.sportovni_den.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.activity.OnlineMatchActivity;
import cz.sps_pi.sportovni_den.db.RefereeMatchRepository;
import cz.sps_pi.sportovni_den.entity.Match;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;
import cz.sps_pi.sportovni_den.util.Route;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 20.01.2017.
 * forejt.martin97@gmail.com
 */

public class TeamRefereeDetailFragment extends ResultsRefFragment {

    private static final int REQUEST_ONLINE = 111;
    private Match match;
    private EditText inScore1, inScore2;
    private ProgressDialog pDialog;

    public static TeamRefereeDetailFragment newInstance(int match) {
        TeamRefereeDetailFragment fragment = new TeamRefereeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("match", match);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            RefereeMatchRepository repository = new RefereeMatchRepository();
            match = repository.getMatch(bundle.getInt("match"));
        }

        if (match == null) {
            changeFragment(TeamRefereeFragment.newInstance(getSport().getId()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ONLINE && resultCode == Activity.RESULT_OK) {
            Match match = (Match) data.getExtras().getSerializable(OnlineMatchActivity.EXTRA_MATCH);
            if (match != null) {
                this.match = match;
                inScore1.setText(String.valueOf(match.getScore1()));
                inScore2.setText(String.valueOf(match.getScore2()));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_referee_detail, container, false);

        TeamTextView team1 = (TeamTextView) view.findViewById(R.id.team_referee_detail_team_1);
        TeamTextView team2 = (TeamTextView) view.findViewById(R.id.team_referee_detail_team_2);

        team1.setTeam(match.getTeam1());
        team2.setTeam(match.getTeam2());

        inScore1 = (EditText) view.findViewById(R.id.team_referee_detail_score_1);
        inScore2 = (EditText) view.findViewById(R.id.team_referee_detail_score_2);

        inScore1.setText(match.getScore1() != null ? String.valueOf(match.getScore1()) : null);
        inScore2.setText(match.getScore2() != null ? String.valueOf(match.getScore2()) : null);


        inScore2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((EditText) view).onTouchEvent(motionEvent);
                ((EditText) view).setSelection(((EditText) view).getText().length());
                return true;
            }
        });

        Button save = (Button) view.findViewById(R.id.team_referee_detail_save_score);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMatch();
            }
        });

        Button start = (Button) view.findViewById(R.id.team_referee_detail_start_match);
        if (withoutStart()) start.setVisibility(View.GONE);
        else start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OnlineMatchActivity.class);
                intent.putExtra(OnlineMatchActivity.EXTRA_MATCH, match);
                startActivityForResult(intent, REQUEST_ONLINE);
            }
        });

        return view;
    }

    private boolean withoutStart() {
        return match.getSport().getSets() == null &&
                match.getSport().getSetPoints() == null &&
                match.getSport().getTime() == null;
    }

    private void saveMatch() {
        if(inScore1.getText().toString().trim().isEmpty() ||
                inScore2.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Zadejte výsledek zápasu.", Toast.LENGTH_SHORT).show();
            return;
        }

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Zapisování...");
        pDialog.setCancelable(false);
        pDialog.show();

        match.setScore1(inScore1.getText().toString().equals("") ? null :
                Integer.parseInt(inScore1.getText().toString()));
        match.setScore2(inScore2.getText().toString().equals("") ? null :
                Integer.parseInt(inScore2.getText().toString()));
        final int matchId = match.getId();
        final String score1 = String.valueOf(match.getScore1());
        final String score2 = String.valueOf(match.getScore2());

        RequestManager.createRequest(Route.get(Route.UPDATE_MATCH))
                .addParameter("id", matchId)
                .addParameter("score_1", score1)
                .addParameter("score_2", score2)
                .addParameter("status", Match.STATUS_END)
                .saveIfNoConn(true)
                .setListener(new RequestListener() {
                    @Override
                    public void onRequestError(Response response) {
                        pDialog.dismiss();
                        Toast.makeText(getContext(), "Výsledek se nepodařilo uložit.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRequestSuccess(Response response) {
                        pDialog.dismiss();
                        changeFragment(TeamRefereeFragment.newInstance(match.getSport().getId()), false);
                    }

                    @Override
                    public void onNoConnection(boolean saved) {
                        pDialog.dismiss();
                    }
                }).execute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                RefereeMatchRepository repository = new RefereeMatchRepository();
                repository.insertMatch(match);
            }
        }).start();
    }

    @Override
    public boolean onBackPressed() {
        changeFragment(TeamRefereeFragment.newInstance(getSport().getId()));
        return true;
    }
}
