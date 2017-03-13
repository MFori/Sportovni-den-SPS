package cz.sps_pi.sportovni_den.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cz.sps_pi.sportovni_den.App;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.util.ConnectionManager;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */
public abstract class SportDenActivity extends AppCompatActivity {

    protected SharedPreferences sharedPreferences;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.onCreate(savedInstanceState, false);
    }

    /**
     * @param savedInstanceState Bundle
     * @param fullScreen         hide action bar?
     */
    protected void onCreate(@Nullable Bundle savedInstanceState, boolean fullScreen) {
        super.onCreate(savedInstanceState);

        if (fullScreen && getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sharedPreferences = getSharedPreferences("activity", MODE_PRIVATE);
    }

    /**
     * Get value from shared preferences
     *
     * @param name item key
     * @param def  default value
     * @return value|def
     */
    protected String getPreference(String name, String def) {
        return sharedPreferences.getString(name, def);
    }

    /**
     * Get value from shared preferences
     *
     * @param name item key
     * @return value|null
     */
    protected String getPreference(String name) {
        return this.getPreference(name, null);
    }

    /**
     * Start activity
     *
     * @param cls activity class
     */
    protected void startActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * Start activity for result
     *
     * @param cls         activity class
     * @param requestCode request code for result
     */
    protected void startActivityForResult(Class cls, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    /**
     * Check if is internet connection
     *
     * @return online
     */
    public boolean isOnline() {
        return ConnectionManager.isOnline();
    }

    /**
     * Return logged user
     *
     * @return User
     */
    public User getUser() {
        if (user == null)
            user = App.getUser();

        return user;
    }
}
