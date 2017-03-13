package cz.sps_pi.sportovni_den.Fragment;

import android.support.v4.app.Fragment;

import cz.sps_pi.sportovni_den.activity.ResultsRefActivity;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.listener.MainActivityCallBack;

/**
 * Created by Martin Forejt on 22.01.2017.
 * forejt.martin97@gmail.com
 */

public abstract class ResultsRefFragment extends Fragment implements MainActivityCallBack {

    private Sport sport;

    protected Sport getSport() {
        if (sport == null) {
            if (getActivity() == null) return null;
            sport = ((ResultsRefActivity) getActivity()).getSport();
        }
        return sport;
    }

    protected boolean canAccessUi() {
        return getActivity() != null && ((ResultsRefActivity) getActivity()).canAccessUi();
    }

    protected void changeFragment(ResultsRefFragment fragment) {
        if (getActivity() != null)
            ((ResultsRefActivity) getActivity()).changeFragment(fragment);
    }

    protected void changeFragment(ResultsRefFragment fragment, boolean popBackStack) {
        if (getActivity() != null)
            ((ResultsRefActivity) getActivity()).changeFragment(fragment, popBackStack);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}