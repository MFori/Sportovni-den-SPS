package cz.sps_pi.sportovni_den.util;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.sps_pi.sportovni_den.App;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.listener.LoginListener;
import cz.sps_pi.sportovni_den.listener.RequestListener;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class LoginManager {

    private static final String P_NAME = "user";
    private static final String USER_TYPE = "user_type";
    private static final String USER_TEAM = "user_team";
    private static final String USER_NAME = "user_name";
    private static final String USER_KEY = "user_key";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";

    /**
     * Get logged user
     *
     * @return User|null
     */
    public static User getUser() {
        SharedPreferences preferences = App.get().getPreferences(P_NAME);

        if (!preferences.getBoolean("is_logged", false)) return null;

        User user = new User();
        int id = preferences.getInt(USER_ID, -1);
        user.setId(id != -1 ? id : null);
        user.setType(preferences.getInt(USER_TYPE, -1));
        user.setName(preferences.getString(USER_NAME, null));
        user.setTeamName(preferences.getString(USER_TEAM, null));
        user.setApiKey(preferences.getString(USER_KEY, null));
        user.setEmail(preferences.getString(USER_EMAIL, null));

        return user;
    }

    private static void saveUser(User user) {
        SharedPreferences preferences = App.get().getPreferences(P_NAME);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("is_logged", true);
        editor.putInt(USER_TYPE, user.getType());
        switch (user.getType()) {
            case User.TYPE_PLAYER:
                editor.putString(USER_TEAM, user.getTeamName());
                break;
            case User.TYPE_REFEREE:
                editor.putInt(USER_ID, user.getId());
                editor.putString(USER_NAME, user.getName());
                editor.putString(USER_KEY, user.getApiKey());
                editor.putString(USER_EMAIL, user.getEmail());
                break;
        }

        editor.apply();
    }

    public static void logout() {
        NotificationsManager.unRegisterFCM(getUser());

        SharedPreferences preferences = App.get().getPreferences(P_NAME);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("is_logged", false);

        editor.apply();
    }

    /**
     * @param user      User
     * @param listeners login listeners
     */
    public static void login(User user, LoginListener[] listeners) {
        switch (user.getType()) {
            case User.TYPE_REFEREE:
                loginReferee(user, listeners);
                break;
            case User.TYPE_PLAYER:
            case User.TYPE_ANONYM:
                fakeLoginAction(user, listeners);
                break;
        }
        NotificationsManager.registerFCM(user);
    }

    /**
     * @param user
     * @param listeners
     */
    private static void loginReferee(final User user, final LoginListener[] listeners) {
        String initVector = Security.generateRandomIV();

        RequestManager.createRequest(Route.get(Route.LOGIN))
                .addParameter("username", user.getName())
                .addParameter("pass", Security.encrypt(initVector, user.getPassword()))
                .addParameter("iv", initVector)
                .setListener(new RequestListener() {
                    @Override
                    public void onRequestError(Response response) {
                        for (LoginListener listener : listeners) {
                            listener.onLoginError();
                        }
                    }

                    @Override
                    public void onRequestSuccess(Response response) {
                        saveUser(createUserFromResponse(response, user));
                        for (LoginListener listener : listeners) {
                            listener.onLoginSuccess(getUser());
                        }
                    }

                    @Override
                    public void onNoConnection(boolean saved) {
                        for (LoginListener listener : listeners) {
                            listener.onLoginError();
                        }
                    }
                })
                .execute();
    }

    /**
     * @param user
     * @param listeners
     */
    private static void fakeLoginAction(final User user, final LoginListener[] listeners) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestManager.createRequest(Route.get(Route.SPORTS))
                        .setListener(new RequestListener() {
                            @Override
                            public void onRequestError(Response response) {
                            }

                            @Override
                            public void onRequestSuccess(Response response) {
                                JSONArray sports = response.getArray("sports");
                                SportRepository repository = new SportRepository();
                                for (int i = 0; i < sports.length(); i++) {
                                    try {
                                        JSONObject o = sports.getJSONObject(i);
                                        Sport sport = new Sport(
                                                o.getInt("id"),
                                                o.getString("name"),
                                                o.getBoolean("active"),
                                                o.optInt("scoring"),
                                                o.optInt("sets"),
                                                o.optInt("sets_points"),
                                                o.optInt("time")
                                        );
                                        repository.updateSport(sport);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onNoConnection(boolean saved) {
                            }
                        }).execute();
            }
        }).start();
        saveUser(user);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (LoginListener listener : listeners) {
                    listener.onLoginSuccess(getUser());
                }
            }
        }, 2000);
    }

    /**
     * @param response Response
     * @param user     requested user
     * @return user
     */
    private static User createUserFromResponse(Response response, User user) {
        Log.d("login", response.getRawData());
        JSONObject data = response.getData("user");

        try {
            user.setApiKey(data.getString("api_key"));
            user.setId(data.getInt("id"));
            user.setEmail(data.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }


}
