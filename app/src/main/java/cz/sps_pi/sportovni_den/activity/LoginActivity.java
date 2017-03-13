package cz.sps_pi.sportovni_den.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import cz.sps_pi.sportovni_den.Fragment.LoginFragment;
import cz.sps_pi.sportovni_den.Fragment.LoginFragment1;
import cz.sps_pi.sportovni_den.Fragment.LoginFragmentRoot;
import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.listener.LoginListener;
import cz.sps_pi.sportovni_den.listener.LoginPagerCallback;
import cz.sps_pi.sportovni_den.util.LoginManager;

public class LoginActivity extends SportDenActivity implements LoginPagerCallback, LoginListener {

    private ViewPager pager;
    private LoginFragmentRoot root;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        root = new LoginFragmentRoot();
        root.setCallback(this);

        pager = (ViewPager) findViewById(R.id.loginViewPager);
        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(position != 0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * On skip button click
     *
     * @param view button
     */
    public void skipLogin(View view) {
        showDialog(getResources().getString(R.string.login_loading));
        User user = new User();
        user.setType(User.TYPE_ANONYM);
        LoginManager.login(user, new LoginListener[]{this});
    }

    @Override
    public void loginPlayer(Team team, LoginListener listener) {
        showDialog(getResources().getString(R.string.login_loading));
        User user = new User();
        user.setType(User.TYPE_PLAYER).setTeamName(team.getName());
        LoginManager.login(user, new LoginListener[]{this, listener});
    }

    @Override
    public void loginReferee(String name, String password, LoginListener listener) {
        showDialog(getResources().getString(R.string.login_logining));
        User user = new User();
        user.setType(User.TYPE_REFEREE).setName(name).setPassword(password);
        LoginManager.login(user, new LoginListener[]{this, listener});
    }

    @Override
    public void onLoginSuccess(User user) {
        pDialog.dismiss();
        startActivity(MainActivity.class);
        finish();
    }

    @Override
    public void onLoginError() {
        pDialog.dismiss();
    }

    @Override
    public void setPosition(int position) {
        switch (position) {
            case LoginFragment.POSITION_MENU:
                pager.setCurrentItem(0, true);
                break;
            case LoginFragment.POSITION_TEAMS:
            case LoginFragment.POSITION_REFEREE:
                root.changeRoot(position);
                pager.setCurrentItem(1, true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            finish();
        } else {
            pager.setCurrentItem(0, true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setPosition(LoginFragment.POSITION_MENU);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show loading dialog
     *
     * @param message msg
     */
    private void showDialog(String message) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * Custom pager adapter for login viewPager
     */
    public class PagerAdapter extends FragmentPagerAdapter {
        private static final int ITEMS_COUNT = 2;

        private PagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    LoginFragment f = new LoginFragment1();
                    f.setCallback(LoginActivity.this);
                    return f;
                case 1:
                    return root;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return ITEMS_COUNT;
        }
    }
}
