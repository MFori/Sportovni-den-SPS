package cz.sps_pi.sportovni_den.util;

import java.util.Date;
import java.util.Map;

/**
 * Created by Martin Forejt on 13.01.2017.
 * forejt.martin97@gmail.com
 */

public class Request {

    private int id;
    private Route route;
    private Map<String, String> data;
    private Date timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
