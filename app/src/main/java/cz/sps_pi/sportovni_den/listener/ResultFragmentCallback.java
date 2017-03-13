package cz.sps_pi.sportovni_den.listener;

import android.support.v4.widget.SwipeRefreshLayout;

import cz.sps_pi.sportovni_den.util.SportResult;

/**
 * Created by Martin Forejt on 20.01.2017.
 * forejt.martin97@gmail.com
 */

public interface ResultFragmentCallback {

    SportResult getResult();

    SwipeRefreshLayout getRefreshLayout();
}
