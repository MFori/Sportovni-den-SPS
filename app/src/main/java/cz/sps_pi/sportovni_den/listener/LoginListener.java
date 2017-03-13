package cz.sps_pi.sportovni_den.listener;

import cz.sps_pi.sportovni_den.entity.User;

/**
 * Created by Martin Forejt on 08.01.2017.
 * forejt.martin97@gmail.com
 */

public interface LoginListener {

    void onLoginSuccess(User user);

    void onLoginError();

}
