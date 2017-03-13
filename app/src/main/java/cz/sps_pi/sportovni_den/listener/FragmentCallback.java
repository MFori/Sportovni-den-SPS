package cz.sps_pi.sportovni_den.listener;

import android.support.v4.app.Fragment;

/**
 * Created by Martin Forejt on 09.01.2017.
 * forejt.martin97@gmail.com
 */

public interface FragmentCallback {

    void changePositionFromFragment(int position, boolean refresh);

    void changeFragmentFromFragment(Fragment fragment);

    void fragmentWithArrow(boolean withArrow);

    void setTitle(String title);

}
