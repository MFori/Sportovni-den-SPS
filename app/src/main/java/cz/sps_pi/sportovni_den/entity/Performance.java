package cz.sps_pi.sportovni_den.entity;

import java.util.Date;

/**
 * Created by Martin Forejt on 19.01.2017.
 * forejt.martin97@gmail.com
 */

public class Performance {
    private int id;
    private Team team;
    private Sport sport;
    private int points;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
