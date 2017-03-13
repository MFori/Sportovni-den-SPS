package cz.sps_pi.sportovni_den.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.activity.MainActivity;
import cz.sps_pi.sportovni_den.activity.ResultsCompleteActivity;
import cz.sps_pi.sportovni_den.activity.ResultsSportActivity;

/**
 * Created by Martin Forejt on 10.01.2017.
 * forejt.martin97@gmail.com
 */

public class ResultsMenuFragment extends SportDenFragment {

    public ResultsMenuFragment() {
        super("Výsledky");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results_menu, container, false);

        Button complete = (Button) view.findViewById(R.id.complete);
        Button sports = (Button) view.findViewById(R.id.sports);
        Button time = (Button) view.findViewById(R.id.time);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ResultsCompleteActivity.class));
            }
        });

        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = ChooseSportFragment.newInstance(
                        ResultsSportActivity.CLASS,
                        "Výsledky dle sportu",
                        MainActivity.POSITION_RESULTS,
                        true);
                getCallback().changeFragmentFromFragment(fragment);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new TimeResultsFragment();
                getCallback().changeFragmentFromFragment(fragment);
            }
        });

        return view;
    }

}
