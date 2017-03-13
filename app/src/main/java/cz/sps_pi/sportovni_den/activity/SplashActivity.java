package cz.sps_pi.sportovni_den.activity;

import android.os.Bundle;
import android.os.Handler;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.util.LoginManager;

public class SplashActivity extends SportDenActivity {

    private static final long DELAY = 3000;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        runnable = new mRunnable();
        handler.postDelayed(runnable, DELAY);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    /**
     * Runnable started after DELAY time
     * Start next activity
     */
    private class mRunnable implements Runnable {
        @Override
        public void run() {
            if (LoginManager.getUser() == null) {
                startActivity(LoginActivity.class);
            } else {
                startActivity(MainActivity.class);
            }
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            finish();
        }
    }
}
