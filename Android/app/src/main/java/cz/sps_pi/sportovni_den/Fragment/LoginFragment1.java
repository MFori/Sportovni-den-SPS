package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cz.sps_pi.sportovni_den.R;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class LoginFragment1 extends LoginFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment1, container, false);

        Button player = (Button) view.findViewById(R.id.player);
        Button referee = (Button) view.findViewById(R.id.referee);

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(LoginFragment.POSITION_TEAMS);
            }
        });
        referee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(LoginFragment.POSITION_REFEREE);
            }
        });

        return view;
    }
}
