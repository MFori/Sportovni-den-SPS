package cz.sps_pi.sportovni_den.listener;

import cz.sps_pi.sportovni_den.util.SportResult;

/**
 * Created by Martin Forejt on 28.01.2017.
 * forejt.martin97@gmail.com
 */

public interface ResultsRefreshListener {

    void onUpdate(SportResult result);

}
