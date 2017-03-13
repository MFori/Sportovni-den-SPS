package cz.sps_pi.sportovni_den.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cz.sps_pi.sportovni_den.util.ConnectionManager;

/**
 * Created by Martin Forejt on 13.01.2017.
 * forejt.martin97@gmail.com
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectionManager.isOnline(context))
            context.startService(new Intent(context, UploadDataService.class));
    }

}
