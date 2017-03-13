package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cz.sps_pi.sportovni_den.App;
import cz.sps_pi.sportovni_den.service.NetworkChangeReceiver;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class ConnectionManager {

    private static NetworkChangeReceiver receiver = null;

    public static boolean isOnline() {
        return isOnline(App.get().getApplicationContext());
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null;
    }

    public static void registerReceiver() {
        if (receiver == null) {
            receiver = new NetworkChangeReceiver();
            App.get().getApplicationContext().registerReceiver(receiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public static void unregisterReceiver() {
        try {
            App.get().getApplicationContext().unregisterReceiver(receiver);
            receiver = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
