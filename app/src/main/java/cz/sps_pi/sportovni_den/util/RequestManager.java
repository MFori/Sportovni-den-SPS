package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cz.sps_pi.sportovni_den.App;
import cz.sps_pi.sportovni_den.db.RequestRepository;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.db.TeamRepository;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.listener.RequestListener;

/**
 * Created by Martin Forejt on 08.01.2017.
 * forejt.martin97@gmail.com
 */

public class RequestManager {

    public static Request createRequest(Route route) {

        Request request = new Request(App.get().getApplicationContext());
        request.setRoute(route);

        return request;
    }

    public static Request createRequest(cz.sps_pi.sportovni_den.util.Request r) {
        Request request = new Request(App.get().getApplicationContext());
        request.setRoute(r.getRoute());
        request.setData(r.getData());

        return request;
    }

    public static class Request {
        private Context context;
        private Route route;
        private RequestTask task;
        private boolean save = false;
        private Map<String, String> data = new HashMap<>();

        private Request(Context context) {
            this.context = context;
            task = new RequestTask();
        }

        private void setRoute(Route route) {
            this.route = route;
        }

        public Request saveIfNoConn(boolean save) {
            this.save = save;
            return this;
        }

        private boolean saveIfNoConn() {
            return save;
        }

        public Request addNullParameter(String paramName) {
            data.put(paramName, null);
            return this;
        }

        public Request addParameter(String paramName, String value) {
            data.put(paramName, value);
            return this;
        }

        public Request addParameter(String paramName, int value) {
            return addParameter(paramName, String.valueOf(value));
        }

        public Request addParameter(String paramName, boolean value) {
            return addParameter(paramName, value ? "true" : "false");
        }

        public Request setListener(RequestListener listener) {
            task.setListener(listener);
            return this;
        }

        private Request setData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public void execute() {
            if (App.getTournamentId() == 0) {
                getTournament();
            } else {
                task.setData(data);
                task.setRoute(route);
                task.setUser(App.getUser());
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
                if (!App.hasSports() && route.getId() != Route.SPORTS && route.getId() != Route.GET_TEAMS) {
                    getSports();
                }
                if (!App.hasTeams() && route.getId() != Route.GET_TEAMS && route.getId() != Route.SPORTS) {
                    getTeams();
                }
            }
        }

        public void cancel() {
            if (task != null) {
                task.cancel(true);
            }
        }

        /**
         * Get active tournament
         */
        private void getTournament() {
            RequestTask t = new RequestTask();
            t.setRoute(Route.get(Route.TOURNAMENT));
            t.setUser(App.getUser());
            t.setListener(new RequestListener() {
                @Override
                public void onRequestError(Response response) {
                    if (task.getListener() != null)
                        task.getListener().onRequestError(response);
                }

                @Override
                public void onRequestSuccess(Response response) {
                    JSONObject tournament = response.getData("tournament");
                    try {
                        App.saveTournament(tournament.getInt("id"));
                        execute();
                    } catch (JSONException e) {
                        if (task.getListener() != null)
                            task.getListener().onRequestError(response);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNoConnection(boolean saved) {
                }
            });
            t.execute(this);
        }

        /**
         * Get all sports and save to db
         */
        private void getSports() {
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
                                            int sets = o.optInt("sets", -1);
                                            int setPoints = o.optInt("sets_points", -1);
                                            int time = o.optInt("time", -1);
                                            Sport sport = new Sport(
                                                    o.getInt("id"),
                                                    o.getString("name"),
                                                    o.getBoolean("active"),
                                                    o.optInt("scoring"),
                                                    sets != -1 ? sets : null,
                                                    setPoints != -1 ? setPoints : null,
                                                    time != -1 ? time : null
                                            );
                                            repository.updateSport(sport);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    App.setSports(true);
                                }

                                @Override
                                public void onNoConnection(boolean saved) {
                                }
                            }).execute();
                }
            }).start();
        }

        /**
         * Get all teams and save to db
         */
        private void getTeams() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestManager.createRequest(Route.get(Route.GET_TEAMS))
                            .setListener(new RequestListener() {
                                @Override
                                public void onRequestError(Response response) {
                                }

                                @Override
                                public void onRequestSuccess(Response response) {
                                    JSONArray teams = response.getArray("teams");
                                    TeamRepository repository = new TeamRepository();
                                    for (int i = 0; i < teams.length(); i++) {
                                        try {
                                            JSONObject o = teams.getJSONObject(i);
                                            Team team = Team.getById(o.getInt("id"));
                                            team.setActive(o.getBoolean("active"));
                                            repository.updateTeam(team);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    App.setTeams(true);
                                }

                                @Override
                                public void onNoConnection(boolean saved) {
                                }
                            }).execute();
                }
            }).start();
        }
    }

    private static class RequestTask extends AsyncTask<Request, Void, Response> {

        private RequestListener listener;
        private Map<String, String> data;
        private Route route;
        private User user;
        private Request request;

        @Override
        protected Response doInBackground(Request... requests) {

            request = requests[0];
            Response response = new Response();

            try {
                Route.CreateUrlData createUrlData = route.getUrl(data);
                URL url = new URL(createUrlData.url);
                data = createUrlData.data;
                Log.d("URL", url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(route.getMethod());
                connection.setDoInput(true);
                connection.setDoOutput(route.withOutput());
                connection.setConnectTimeout(5000);

                connection.setRequestProperty("Tournament", String.valueOf(App.getTournamentId()));
                if (user != null && user.getType() == User.TYPE_REFEREE) {
                    connection.setRequestProperty("Authorization", "key=" + user.getApiKey());
                }

                if (route.withOutput()) {
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(createData(data));
                    writer.flush();
                    writer.close();
                    os.close();
                }

                connection.connect();

                BufferedReader reader;

                if (connection.getResponseCode() == route.getResult()) {
                    response.setSuccess(true);
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    response.setSuccess(false);
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String output;

                while ((output = reader.readLine()) != null) {
                    sb.append(output);
                }

                response.setResponse(String.valueOf(sb));

            } catch (Exception e) {
                e.printStackTrace();
                Error error = new Error(Error.NO_SERVER, "Server je nedostupný");
                response.addError(error);
                response.setSuccess(false);
            }

            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            if (response.hasError(Error.OLD_DATA)) {
                App.logout();
                return;
            }


            if (response.isSuccess()) {
                if (listener != null)
                    listener.onRequestSuccess(response);
            } else {
                if (request.saveIfNoConn() && response.hasError(Error.NO_SERVER))
                    saveRequest(createRequestToSave(request));
                if (listener != null)
                    listener.onRequestError(response);
            }
        }

        private void setListener(RequestListener listener) {
            this.listener = listener;
        }

        private RequestListener getListener() {
            return listener;
        }

        private void setData(Map<String, String> data) {
            this.data = data;
        }

        private void setUser(User user) {
            this.user = user;
        }

        private void setRoute(Route route) {
            this.route = route;
        }

        private String createData(Map<String, String> data) {
            String d = "";
            Iterator it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                d += pair.getKey() + "=" + pair.getValue() + "&";
                it.remove();
            }

            if (d.length() > 0) {
                d = d.substring(0, d.length() - 1);
            }
            return d;
        }

        private cz.sps_pi.sportovni_den.util.Request createRequestToSave(Request request) {
            cz.sps_pi.sportovni_den.util.Request r = new cz.sps_pi.sportovni_den.util.Request();
            r.setData(request.data);
            r.setRoute(request.route);
            return r;
        }

        private void saveRequest(final cz.sps_pi.sportovni_den.util.Request request) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("request", "save");
                    RequestRepository repository = new RequestRepository();
                    repository.addRequest(request);
                }
            }).start();
        }
    }

}
