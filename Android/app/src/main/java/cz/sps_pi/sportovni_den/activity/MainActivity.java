package cz.sps_pi.sportovni_den.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cz.sps_pi.sportovni_den.App;
import cz.sps_pi.sportovni_den.Fragment.MessagesFragment;
import cz.sps_pi.sportovni_den.Fragment.SportDenFragment;
import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.listener.MainActivityCallBack;
import cz.sps_pi.sportovni_den.listener.FragmentCallback;
import cz.sps_pi.sportovni_den.view.TeamTextView;

public class MainActivity extends SportDenActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentCallback {

    public static final int POSITION_MENU = 0;
    public static final int POSITION_RESULTS = 1;
    public static final int POSITION_RULES = 2;
    public static final int POSITION_MESSAGES = 3;
    public static final int POSITION_SETTINGS = 4;
    public static final int POSITION_RESULTS_REF = 5;
    public static final int POSITION_NOTIFICATIONS = 6;
    private static final int REQUEST_SETTINGS = 1;

    public static final String EXTRA_NOTIFICATION = "notification";

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ActionBar actionBar;
    private int currentPosition = -1;

    private MainActivityCallBack callBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initPosition();

        View header = navigationView.getHeaderView(0);
        initHeader(header, navigationView.getMenu());

        toggle.setToolbarNavigationClickListener(backArrow);
    }

    private void initPosition() {
        SportDenFragment.setCallback(this);
        Intent intent = getIntent();
        int notification = intent.getIntExtra(EXTRA_NOTIFICATION, -1);
        if (notification != -1) {
            changeFragment(MessagesFragment.DetailFragment.newInstance(notification, Integer.MAX_VALUE));
            navigationView.setCheckedItem(getMenuIdByPosition(POSITION_MESSAGES));
            return;
        }
        changePosition(POSITION_MENU);
        navigationView.setCheckedItem(getMenuIdByPosition(POSITION_MENU));
    }

    private void initHeader(View header, Menu menu) {
        TeamTextView shortcut = (TeamTextView) header.findViewById(R.id.header_user_shortcut);
        TextView name = (TextView) header.findViewById(R.id.header_referee_name);
        switch (getUser().getType()) {
            case User.TYPE_ANONYM:
                shortcut.isAnonym();
                menu.setGroupVisible(R.id.menu_referee, false);
                break;
            case User.TYPE_PLAYER:
                shortcut.setTeam(getUser().getTeam());
                menu.setGroupVisible(R.id.menu_referee, false);
                break;
            case User.TYPE_REFEREE:
                shortcut.isReferee();
                shortcut.setOnClickListener(RefereeIconClickListener);
                name.setText(getUser().getName());
                break;
        }
    }

    private View.OnClickListener RefereeIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getResources().getString(R.string.ref_dialog_name) + getUser().getName() + "\n" +
                            getResources().getString(R.string.ref_dialog_email) + getUser().getEmail())
                    .setTitle(getResources().getString(R.string.ref_dialog_title))
                    .setNegativeButton(getResources().getString(R.string.ref_dialog_logout), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            App.logout();
                        }
                    })
                    .setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            TextView message = (TextView) dialog.findViewById(android.R.id.message);
            message.setTextSize(17);
        }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (callBack != null && !callBack.onBackPressed()) {
                if (currentPosition == POSITION_MENU) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle(getResources().getString(R.string.exit_dialog_title))
                            .setPositiveButton(getResources().getString(R.string.exit_dialog_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).setNegativeButton(getResources().getString(R.string.exit_dialog_no), null);
                    builder.create().show();
                } else
                    changePosition(POSITION_MENU);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTINGS:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    if (res.getBoolean("finish")) {
                        finish();
                    }
                }
                break;
        }
    }

    View.OnClickListener backArrow = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    /**
     * Change main fragment
     *
     * @param fragment new Fragment instance
     */
    private void changeFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.navigation_content, fragment)
                .commit();

        if (fragment instanceof MainActivityCallBack) {
            callBack = (MainActivityCallBack) fragment;
        } else {
            callBack = null;
        }
    }

    @Override
    public void changeFragmentFromFragment(Fragment fragment) {
        changeFragment(fragment);
    }

    @Override
    public void changePositionFromFragment(int position, boolean refresh) {
        changePosition(position, refresh);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (changePosition(getPositionByMenu(item))) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            return false;
        }
        return true;
    }

    private boolean changePosition(int position) {
        return changePosition(position, false);
    }

    /**
     * @param position pos
     * @return close drawer?
     */
    private boolean changePosition(int position, boolean refresh) {
        if (!refresh && currentPosition == position) return true;
        switch (position) {
            case POSITION_MENU:
            case POSITION_RESULTS:
            case POSITION_RULES:
            case POSITION_MESSAGES:
            case POSITION_RESULTS_REF:
            case POSITION_NOTIFICATIONS:
                changeFragment(SportDenFragment.getFragment(position, this));
                break;
            case POSITION_SETTINGS:
                startActivityForResult(SettingsActivity.class, REQUEST_SETTINGS);
                return false;
            default:
                return false;
        }

        currentPosition = position;
        navigationView.setCheckedItem(getMenuIdByPosition(currentPosition));

        return true;
    }

    /**
     * @param menu item
     * @return item position
     */
    private int getPositionByMenu(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.nav_menu:
                return POSITION_MENU;
            case R.id.nav_results:
                return POSITION_RESULTS;
            case R.id.nav_rules:
                return POSITION_RULES;
            case R.id.nav_messages:
                return POSITION_MESSAGES;
            case R.id.nav_settings:
                return POSITION_SETTINGS;
            case R.id.nav_results_ref:
                return POSITION_RESULTS_REF;
            case R.id.nav_notifications:
                return POSITION_NOTIFICATIONS;
        }

        return 0;
    }

    /**
     * @param position item Position
     * @return menu item id
     */
    private int getMenuIdByPosition(int position) {
        switch (position) {
            case POSITION_MENU:
                return R.id.nav_menu;
            case POSITION_RESULTS:
                return R.id.nav_results;
            case POSITION_RULES:
                return R.id.nav_rules;
            case POSITION_MESSAGES:
                return R.id.nav_messages;
            case POSITION_SETTINGS:
                return R.id.nav_settings;
            case POSITION_RESULTS_REF:
                return R.id.nav_results_ref;
            case POSITION_NOTIFICATIONS:
                return R.id.nav_notifications;
        }

        return 0;
    }

    @Override
    public void setTitle(String title) {
        actionBar.setTitle(title);
    }

    @Override
    public void fragmentWithArrow(boolean withArrow) {
        showBackArrow(withArrow);
    }

    private void showBackArrow(boolean show) {
        if (show) {
            toggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        }
    }
}
