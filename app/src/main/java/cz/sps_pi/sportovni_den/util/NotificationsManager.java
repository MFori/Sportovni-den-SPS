package cz.sps_pi.sportovni_den.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.activity.MainActivity;
import cz.sps_pi.sportovni_den.entity.Message;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.listener.RequestListener;

/**
 * Created by Martin Forejt on 29.01.2017.
 * forejt.martin97@gmail.com
 */

public class NotificationsManager {

    public static final String TOPIC_ALL = "everybody";
    public static final String TOPIC_REFEREES = "referees";
    public static final String TOPIC_ATHLETES = "athletes";
    public static final String TOPIC_TEAM_A1 = "team_A1";
    public static final String TOPIC_TEAM_B1 = "team_B1";
    public static final String TOPIC_TEAM_C1 = "team_C1";
    public static final String TOPIC_TEAM_A2 = "team_A2";
    public static final String TOPIC_TEAM_B2 = "team_B2";
    public static final String TOPIC_TEAM_C2 = "team_C2";
    public static final String TOPIC_TEAM_A3 = "team_A3";
    public static final String TOPIC_TEAM_B3 = "team_B3";
    public static final String TOPIC_TEAM_C3 = "team_C3";
    public static final String TOPIC_TEAM_A4 = "team_A4";
    public static final String TOPIC_TEAM_B4 = "team_B4";
    public static final String TOPIC_TEAM_C4 = "team_C4";

    public static final int ADDRESSEE_ALL = 1;
    public static final int ADDRESSEE_REFEREES = 2;
    public static final int ADDRESSEE_ATHLETES = 3;
    public static final int ADDRESSEE_TEAM_A1 = 4;
    public static final int ADDRESSEE_TEAM_B1 = 5;
    public static final int ADDRESSEE_TEAM_C1 = 6;
    public static final int ADDRESSEE_TEAM_A2 = 7;
    public static final int ADDRESSEE_TEAM_B2 = 8;
    public static final int ADDRESSEE_TEAM_C2 = 9;
    public static final int ADDRESSEE_TEAM_A3 = 10;
    public static final int ADDRESSEE_TEAM_B3 = 11;
    public static final int ADDRESSEE_TEAM_C3 = 12;
    public static final int ADDRESSEE_TEAM_A4 = 13;
    public static final int ADDRESSEE_TEAM_B4 = 14;
    public static final int ADDRESSEE_TEAM_C4 = 15;

    public static final String TITLE_ALL = "Všichni";
    public static final String TITLE_REFEREES = "Rozhodčí";
    public static final String TITLE_ATHLETES = "Všichni sportovci";
    public static final String TITLE_TEAM_A1 = "Sportovci A1";
    public static final String TITLE_TEAM_B1 = "Sportovci B1";
    public static final String TITLE_TEAM_C1 = "Sportovci C1";
    public static final String TITLE_TEAM_A2 = "Sportovci A2";
    public static final String TITLE_TEAM_B2 = "Sportovci B2";
    public static final String TITLE_TEAM_C2 = "Sportovci C2";
    public static final String TITLE_TEAM_A3 = "Sportovci A3";
    public static final String TITLE_TEAM_B3 = "Sportovci B3";
    public static final String TITLE_TEAM_C3 = "Sportovci C3";
    public static final String TITLE_TEAM_A4 = "Sportovci A4";
    public static final String TITLE_TEAM_B4 = "Sportovci B4";
    public static final String TITLE_TEAM_C4 = "Sportovci C4";

    public static Map<String, Integer> getAsMap() {
        Map<String, Integer> res = new HashMap<>();

        res.put(TITLE_ALL, ADDRESSEE_ALL);
        res.put(TITLE_REFEREES, ADDRESSEE_REFEREES);
        res.put(TITLE_ATHLETES, ADDRESSEE_ATHLETES);
        res.put(TITLE_TEAM_A1, ADDRESSEE_TEAM_A1);
        res.put(TITLE_TEAM_B1, ADDRESSEE_TEAM_B1);
        res.put(TITLE_TEAM_C1, ADDRESSEE_TEAM_C1);
        res.put(TITLE_TEAM_A2, ADDRESSEE_TEAM_A2);
        res.put(TITLE_TEAM_B2, ADDRESSEE_TEAM_B2);
        res.put(TITLE_TEAM_C2, ADDRESSEE_TEAM_C2);
        res.put(TITLE_TEAM_A3, ADDRESSEE_TEAM_A3);
        res.put(TITLE_TEAM_B3, ADDRESSEE_TEAM_B3);
        res.put(TITLE_TEAM_C3, ADDRESSEE_TEAM_C3);
        res.put(TITLE_TEAM_A4, ADDRESSEE_TEAM_A4);
        res.put(TITLE_TEAM_B4, ADDRESSEE_TEAM_B4);
        res.put(TITLE_TEAM_C4, ADDRESSEE_TEAM_C4);

        return res;
    }

    public static String[] getAddressesAsArray() {
        return new String[]{
                TITLE_ALL,
                TITLE_REFEREES,
                TITLE_ATHLETES,
                TITLE_TEAM_A1,
                TITLE_TEAM_B1,
                TITLE_TEAM_C1,
                TITLE_TEAM_A2,
                TITLE_TEAM_B2,
                TITLE_TEAM_C2,
                TITLE_TEAM_A3,
                TITLE_TEAM_B3,
                TITLE_TEAM_C3,
                TITLE_TEAM_A4,
                TITLE_TEAM_B4,
                TITLE_TEAM_C4,
        };
    }

    public static void registerFCM(User user) {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL);
        if (user.getType() == User.TYPE_REFEREE) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_REFEREES);
        } else if (user.getType() == User.TYPE_PLAYER) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ATHLETES);
            FirebaseMessaging.getInstance().subscribeToTopic("team_" + user.getTeam().getName());
        }
    }

    public static void unRegisterFCM(User user) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ALL);
        if (user.getType() == User.TYPE_REFEREE) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_REFEREES);
        } else if (user.getType() == User.TYPE_PLAYER) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ATHLETES);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("team_" + user.getTeam().getName());
        }
    }

    public static void sendNotification(Message message, Integer[] addresses, final SendListener listener) {
        final RequestManager.Request request = RequestManager.createRequest(Route.get(Route.SEND_NOTIFICATION))
                .addParameter("title", message.getTitle())
                .addParameter("text", message.getMessage());
        for (Integer addressee : addresses) {
            request.addParameter("addressees", addressee);
        }
        request.saveIfNoConn(true);
        request.setListener(new RequestListener() {
            @Override
            public void onRequestError(Response response) {
                listener.onError();
            }

            @Override
            public void onRequestSuccess(Response response) {
                Integer id = null;
                try {
                    JSONObject notification = response.getData("notification");
                    id = notification.getInt("id");
                } catch (JSONException e) {
                }
                listener.onSend(id);
            }

            @Override
            public void onNoConnection(boolean saved) {
                listener.onError();
            }
        });
        request.execute();
    }

    public static interface SendListener {
        void onSend(Integer id);

        void onError();
    }

    public static void showNotification(Message message, Context context) {
        if (!getAllowNotifications(context)) return;

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_NOTIFICATION, message.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(message.getTitle())
                .setContentText(message.getMessage())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{500, 1000, 1000, 1000, 1000})
                .setLights(Color.BLUE, 1500, 700);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(message.getId(), builder.build());
    }

    public static void allowNotifications(boolean allow, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notifications", allow);
        editor.apply();
    }

    public static boolean getAllowNotifications(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        return preferences.getBoolean("notifications", true);
    }

}
