package cz.sps_pi.sportovni_den.Fragment;

import android.support.v4.app.Fragment;

import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.listener.LoginListener;
import cz.sps_pi.sportovni_den.listener.LoginPagerCallback;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public abstract class LoginFragment extends Fragment implements LoginListener {

    public static final int POSITION_MENU = 1;
    public static final int POSITION_TEAMS = 2;
    public static final int POSITION_REFEREE = 3;

    private LoginPagerCallback callback;

    public void setCallback(LoginPagerCallback callback) {
        this.callback = callback;
    }

    protected LoginPagerCallback getCallback() {
        return callback;
    }

    protected void changeFragment(int position) {
        callback.setPosition(position);
    }

    @Override
    public void onLoginSuccess(User user) {

    }

    @Override
    public void onLoginError() {

    }
}
