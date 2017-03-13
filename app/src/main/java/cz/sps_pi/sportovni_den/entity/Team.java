package cz.sps_pi.sportovni_den.entity;

import java.io.Serializable;

import cz.sps_pi.sportovni_den.R;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public enum Team implements Serializable {

    A1(1, "A1", R.color.team_a1), B1(2, "B1", R.color.team_b1), C1(3, "C1", R.color.team_c1),
    A2(4, "A2", R.color.team_a2), B2(5, "B2", R.color.team_b2), C2(6, "C2", R.color.team_c2),
    A3(7, "A3", R.color.team_a3), B3(8, "B3", R.color.team_b3), C3(9, "C3", R.color.team_c3),
    A4(10, "A4", R.color.team_a4), B4(11, "B4", R.color.team_b4), C4(12, "C4", R.color.team_c4);

    private int id;
    private String name;
    private int color;
    private boolean active;

    Team(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static Team getById(int id) {
        switch (id) {
            case 1:
                return A1;
            case 2:
                return B1;
            case 3:
                return C1;
            case 4:
                return A2;
            case 5:
                return B2;
            case 6:
                return C2;
            case 7:
                return A3;
            case 8:
                return B3;
            case 9:
                return C3;
            case 10:
                return A4;
            case 11:
                return B4;
            case 12:
                return C4;
        }

        return null;
    }
}
