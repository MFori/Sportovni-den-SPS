package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.sps_pi.sportovni_den.R;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class LoginFragmentRoot extends LoginFragment {

    private int current;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment_root, container, false);
    }

    public void changeRoot(int item) {
        if (current == item) return;
        current = item;

        LoginFragment fragment;
        if (item == LoginFragment.POSITION_TEAMS) fragment = new LoginFragment2();
        else if (item == LoginFragment.POSITION_REFEREE) fragment = new LoginFragment3();
        else return;

        fragment.setCallback(getCallback());

        FragmentTransaction trans = getFragmentManager().beginTransaction();

        trans.replace(R.id.root_frame, fragment);

        trans.addToBackStack(null);
        trans.commit();
    }

}
