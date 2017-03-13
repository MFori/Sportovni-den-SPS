package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.util.TeamAdapter;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class LoginFragment2 extends LoginFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment2, container, false);

        final Team[] teams = Team.values();

        GridView grid = (GridView) view.findViewById(R.id.login_teams_grid);
        grid.setAdapter(new TeamAdapter(getContext(), teams));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                getCallback().loginPlayer(teams[position], LoginFragment2.this);
            }
        });

        return view;
    }

}
