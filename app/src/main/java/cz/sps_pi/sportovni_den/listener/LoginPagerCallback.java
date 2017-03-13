package cz.sps_pi.sportovni_den.listener;

import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public interface LoginPagerCallback {
    void setPosition(int position);

    void loginPlayer(Team team, LoginListener listener);

    void loginReferee(String name, String password, LoginListener listener);
}
