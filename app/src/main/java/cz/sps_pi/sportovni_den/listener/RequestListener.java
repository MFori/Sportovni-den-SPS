package cz.sps_pi.sportovni_den.listener;

import cz.sps_pi.sportovni_den.util.Response;

/**
 * Created by Martin Forejt on 08.01.2017.
 * forejt.martin97@gmail.com
 */

public interface RequestListener {

    void onRequestError(Response response);

    void onRequestSuccess(Response response);

    void onNoConnection(boolean saved);

}
