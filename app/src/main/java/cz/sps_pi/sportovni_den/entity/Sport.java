package cz.sps_pi.sportovni_den.entity;

import java.io.Serializable;

import cz.sps_pi.sportovni_den.R;

/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

public class Sport implements Serializable {
    private int id;
    private String name;
    private boolean active;
    private Integer scoring;
    private Integer sets;
    private Integer setPoints;
    private Integer time;

    public Sport() {

    }

    public Sport(int id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public Sport(int id, String name, boolean active, Integer scoring, Integer sets, Integer setPoints, Integer time) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.scoring = scoring;
        this.sets = sets;
        this.setPoints = setPoints;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getScoring() {
        return scoring;
    }

    public void setScoring(Integer scoring) {
        this.scoring = scoring;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getSetPoints() {
        return setPoints;
    }

    public void setSetPoints(Integer setPoints) {
        this.setPoints = setPoints;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public static int iconById(int sportId) {
        switch (sportId) {
            case 1:
                return R.drawable.sport_football;
            case 2:
                return R.drawable.sport_football_2;
            case 3:
                return R.drawable.sport_basketball;
            case 4:
                return R.drawable.sport_volleyball;
            case 5:
                return R.drawable.sport_ringo;
            case 6:
                return R.drawable.sport_rope;
            case 7:
                return R.drawable.sport_ping_pong;
            case 8:
                return R.drawable.sport_pull;
            case 9:
                return R.drawable.sport_dart;
            case 10:
                return R.drawable.sport_jump;
        }

        return 0;
    }
}
