package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cz.sps_pi.sportovni_den.App;
import cz.sps_pi.sportovni_den.activity.ResultsRefActivity;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.listener.MainActivityCallBack;
import cz.sps_pi.sportovni_den.listener.FragmentCallback;

import static cz.sps_pi.sportovni_den.activity.MainActivity.POSITION_MENU;
import static cz.sps_pi.sportovni_den.activity.MainActivity.POSITION_MESSAGES;
import static cz.sps_pi.sportovni_den.activity.MainActivity.POSITION_NOTIFICATIONS;
import static cz.sps_pi.sportovni_den.activity.MainActivity.POSITION_RESULTS;
import static cz.sps_pi.sportovni_den.activity.MainActivity.POSITION_RESULTS_REF;
import static cz.sps_pi.sportovni_den.activity.MainActivity.POSITION_RULES;

/**
 * Created by Martin Forejt on 09.01.2017.
 * forejt.martin97@gmail.com
 */

public abstract class SportDenFragment extends Fragment implements MainActivityCallBack {

    private static FragmentCallback callback = null;
    private User user;

    public SportDenFragment() {
        this("Sportovní den", false);
    }

    public SportDenFragment(boolean withArrow) {
        this("Sportovní den", withArrow);
    }

    public SportDenFragment(String title) {
        this(title, false);
    }

    public SportDenFragment(String title, boolean withArrow) {
        if (callback != null) {
            callback.setTitle(title);
            callback.fragmentWithArrow(withArrow);
        }
    }

    public static Fragment getFragment(int position, FragmentCallback callback) {
        SportDenFragment.callback = callback;

        switch (position) {
            case POSITION_MENU:
                return new MenuFragment();
            case POSITION_RESULTS:
                return new ResultsMenuFragment();
            case POSITION_RULES:
                return ChooseSportFragment.newInstance(RulesFragment.CLASS, "Pravidla", position);
            case POSITION_MESSAGES:
                return new MessagesFragment();
            case POSITION_RESULTS_REF:
                return ChooseSportFragment.newInstance(ResultsRefActivity.CLASS, "Zápis výsledků", position, true);
            case POSITION_NOTIFICATIONS:
                return new NotificationsFragment();
            default:
                return null;
        }
    }

    public static void setCallback(FragmentCallback callback) {
        SportDenFragment.callback = callback;
    }

    public static Fragment getFragment(int position, FragmentCallback callback, Bundle bundle) {
        Fragment fragment = getFragment(position, callback);

        if (fragment != null) {
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    protected static FragmentCallback getCallback() {
        return callback;
    }

    /**
     * Get user
     *
     * @return user
     */
    protected User getUser() {
        if (user == null)
            user = App.getUser();

        return user;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
