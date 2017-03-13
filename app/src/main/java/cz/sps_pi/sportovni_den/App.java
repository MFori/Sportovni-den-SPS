package cz.sps_pi.sportovni_den;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

import cz.sps_pi.sportovni_den.activity.LoginActivity;
import cz.sps_pi.sportovni_den.db.DBHelper;
import cz.sps_pi.sportovni_den.db.DatabaseManager;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.service.JobSchedulerService;
import cz.sps_pi.sportovni_den.util.ConnectionManager;
import cz.sps_pi.sportovni_den.util.LoginManager;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class App extends Application {
    private static App instance;
    private int activities = 0;
    private WeakReference<Activity> currentActivity = null;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        registerActivityLifecycleCallbacks(new MyLifeCycleCallbacks());

        Log.d("App", "onCreate");
    }

    /**
     * Clear all data and start login activity
     */
    public static void logout() {
        final ProgressDialog dialog = new ProgressDialog(App.get().currentActivity.get());
        dialog.setMessage("Odhlašování...");
        dialog.setCancelable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                get().currentActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });

                DatabaseManager.getInstance().clearAll();
                App.saveTournament(0);
                App.setSports(false);
                App.setTeams(false);
                LoginManager.logout();

                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                get().currentActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Intent i = new Intent(App.get().getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                                IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        App.get().getApplicationContext().startActivity(i);
                    }
                });
            }
        }).start();
    }

    /**
     * Get app shared preferences
     *
     * @param name name
     * @return preferences
     */
    public SharedPreferences getPreferences(String name) {
        return getSharedPreferences(name, MODE_PRIVATE);
    }

    /**
     * Get logged user
     *
     * @return User
     */
    public static User getUser() {
        return LoginManager.getUser();
    }

    /**
     * Get current tournament id
     *
     * @return id
     */
    public static int getTournamentId() {
        SharedPreferences preferences = instance.getPreferences("app");
        return preferences.getInt("tournament", 0);
    }

    /**
     * Store current tournament id to shared preferences
     *
     * @param id tournament id
     */
    public static void saveTournament(int id) {
        SharedPreferences preferences = instance.getPreferences("app");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("tournament", id);
        editor.apply();
    }

    /**
     * Check if sports are stored in db (key in shared pref.)
     *
     * @return are stored?
     */
    public static boolean hasSports() {
        SharedPreferences preferences = instance.getPreferences("app");
        return preferences.getBoolean("sports", false);
    }

    /**
     * Save sports in db and store value in shared preferences
     *
     * @param hasSports value to pref.
     */
    public static void setSports(boolean hasSports) {
        SharedPreferences preferences = instance.getPreferences("app");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sports", hasSports);
        editor.apply();
    }

    /**
     * Check if teams are stored in db (key in shared pref.)
     *
     * @return are stored?
     */
    public static boolean hasTeams() {
        SharedPreferences preferences = instance.getPreferences("app");
        return preferences.getBoolean("teams", false);
    }

    /**
     * Save teams in db and store value in shared preferences
     *
     * @param hasTeams value to pref.
     */
    public static void setTeams(boolean hasTeams) {
        SharedPreferences preferences = instance.getPreferences("app");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("teams", hasTeams);
        editor.apply();
    }

    /**
     * Init job scheduler for internet conn change
     */
    @TargetApi(21)
    private void initScheduler() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(JobSchedulerService.UPLOAD_JOB_ID, new ComponentName(getPackageName(),
                JobSchedulerService.class.getName()));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setMinimumLatency(5000);
        jobScheduler.schedule(builder.build());
    }

    /**
     * Disable job scheduler for internet conn change
     */
    @TargetApi(21)
    private void disableScheduler() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JobSchedulerService.UPLOAD_JOB_ID);
    }

    /**
     * Activities callbacks listen
     */
    private final class MyLifeCycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            if (activities == 0) {
                DatabaseManager.initInstance(new DBHelper(App.this));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ConnectionManager.registerReceiver();
                    disableScheduler();
                }
            }
            activities++;
            Log.d("App", "onActivityCreated: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d("App", "onActivityStarted: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityResumed(Activity activity) {
            currentActivity = new WeakReference<Activity>(activity);
            Log.d("App", "onActivityResumed: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d("App", "onActivityPaused: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d("App", "onActivityStopped: " + activity.getLocalClassName());
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            Log.d("App", "onActivitySaveInstanceState: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activities--;
            if (activities == 0) {
                DatabaseManager.close();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ConnectionManager.unregisterReceiver();
                    initScheduler();
                }
                currentActivity.clear();
            }
            Log.d("App", "onActivityDestroyed: " + activity.getLocalClassName());
        }
    }
}
