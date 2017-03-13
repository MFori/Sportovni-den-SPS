package cz.sps_pi.sportovni_den.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import cz.sps_pi.sportovni_den.App;
import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.util.NotificationsManager;

public class SettingsActivity extends SportDenActivity {

    private boolean notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Notification setting
        SwitchCompat switchN = (SwitchCompat) findViewById(R.id.settings_notifications_switch);
        switchN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notifications = b;
            }
        });
        switchN.setChecked(NotificationsManager.getAllowNotifications(this));

        // About dialog button
        FrameLayout about = (FrameLayout) findViewById(R.id.settings_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutDialog dialog = new AboutDialog(SettingsActivity.this);
                dialog.show();
            }
        });

        // Logout button
        FrameLayout logout = (FrameLayout) findViewById(R.id.settings_logout);
        setLogoutText(logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    /**
     * Set text to logout button (can be login button..)
     *
     * @param logout TextView
     */
    private void setLogoutText(View logout) {
        switch (getUser().getType()) {
            case User.TYPE_ANONYM:
                ((TextView) logout.findViewById(R.id.settings_logout_text)).setText(
                        getResources().getString(R.string.settings_log_in)
                );
                break;
            case User.TYPE_PLAYER:
            case User.TYPE_REFEREE:
                ((TextView) logout.findViewById(R.id.settings_logout_text)).setText(
                        getResources().getString(R.string.settings_log_out)
                );
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Thread(new SettingsSave()).start();
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.nothing, R.anim.to_right);
    }

    /**
     * Logout current User - show login activity and clear all data
     */
    private void logout() {
        App.logout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private class SettingsSave implements Runnable {
        @Override
        public void run() {
            NotificationsManager.allowNotifications(notifications, SettingsActivity.this);
        }
    }

    /**
     * Dialog about app
     */
    private class AboutDialog extends Dialog {
        private AboutDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.settings_about);
            if (getWindow() != null) {
                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }
            findViewById(R.id.settings_about_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }
}
