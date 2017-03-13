package cz.sps_pi.sportovni_den.util;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martin Forejt on 08.01.2017.
 * forejt.martin97@gmail.com
 */

public class Route {

    public static final int LOGIN = 1;
    public static final int TOURNAMENT = 2;
    public static final int SPORTS = 3;
    public static final int GET_TEAMS = 4;
    public static final int GET_RULES = 5;

    public static final int COMPLETE_RESULTS = 6;
    public static final int SPORT_RESULTS = 7;
    public static final int TIME_RESULTS = 8;

    public static final int GET_MATCHES = 9;
    public static final int UPDATE_MATCH = 10;
    public static final int GET_PERFORMANCES = 11;
    public static final int ADD_PERFORMANCE = 12;
    public static final int UPDATE_PERFORMANCE = 13;
    public static final int DELETE_PERFORMANCE = 14;

    public static final int SEND_NOTIFICATION = 15;

    public static class CreateUrlData {
        public Map<String, String> data;
        public String url;
    }

    public static Route get(int route) {
        switch (route) {
            case LOGIN:
                return new Route("login", Route.POST, 200, LOGIN);
            case TOURNAMENT:
                return new Route("tournaments/active", Route.GET, 200, TOURNAMENT);
            case SPORTS:
                return new Route("sports", Route.GET, 200, SPORTS);
            case GET_TEAMS:
                return new Route("teams", Route.GET, 200, GET_TEAMS);
            case GET_RULES:
                return new Route("rules/{sport}", Route.GET, 200, GET_RULES);

            case COMPLETE_RESULTS:
                return new Route("results/complete", Route.GET, 200, COMPLETE_RESULTS);
            case SPORT_RESULTS:
                return new Route("results/{sport}", Route.GET, 200, SPORT_RESULTS);
            case TIME_RESULTS:
                return new Route("results/timeline", Route.GET, 200, TIME_RESULTS);

            case GET_MATCHES:
                return new Route("matches", Route.GET, 200, GET_MATCHES);
            case UPDATE_MATCH:
                return new Route("matches/{id}", Route.PUT, 200, UPDATE_MATCH);
            case GET_PERFORMANCES:
                return new Route("performances", Route.GET, 200, GET_PERFORMANCES);
            case ADD_PERFORMANCE:
                return new Route("performances", Route.POST, 201, ADD_PERFORMANCE);
            case UPDATE_PERFORMANCE:
                return new Route("performances/{id}", Route.PUT, 200, UPDATE_PERFORMANCE);
            case DELETE_PERFORMANCE:
                return new Route("performances/{id}", Route.DELETE, 204, DELETE_PERFORMANCE);

            case SEND_NOTIFICATION:
                return new Route("notifications", Route.POST, 201, SEND_NOTIFICATION);
        }

        return null;
    }

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private static final String PREFIX_DEV = "http://192.168.0.101/sd/web/app_dev.php/api/v1/";
    private static final String PREFIX = "http://172.16.41.141/sd/web/api/v1/";

    private int id;
    private String url;
    private String method;
    private int result;

    private Route(String url, String method, int result, int id) {
        this.url = url;
        this.method = method;
        this.result = result;
        this.id = id;
    }

    public CreateUrlData getUrl(Map<String, String> paramsAll) {
        Map<String, String> params = new HashMap<>();
        if (paramsAll != null)
            params.putAll(paramsAll);

        Boolean start = false;
        String paramName = "";
        String[] uri = url.split("");
        for (int i = 0; i < url.length() + 1; i++) {
            if (uri[i].equals("{")) {
                start = true;
            } else if (uri[i].equals("}")) {
                if (params.get(paramName) != null) {

                    StringBuilder builder = new StringBuilder();
                    for (String s : uri) {
                        builder.append(s);
                    }

                    this.url = builder.toString().replaceAll("\\{" + paramName + "\\}", params.get(paramName));

                    params.remove(paramName);
                    return getUrl(params);
                }
            } else if (start) {
                paramName += uri[i];
            } else {
                paramName = "";
            }
        }

        if (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }

        if (method.equals(GET)) {
            if (params != null && params.size() > 0) {
                url += "?";
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    url += entry.getKey() + "=" + entry.getValue() + "&";
                }
            }

            if (url.charAt(url.length() - 1) == '&') {
                url = url.substring(0, url.length() - 1);
            }
        }

        CreateUrlData urlData = new CreateUrlData();
        urlData.data = params;
        urlData.url = PREFIX + url;
        return urlData;
    }

    public String getMethod() {
        return method;
    }

    public int getResult() {
        return result;
    }

    public boolean withOutput() {
        return method.equals(POST) || method.equals(PUT);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
