package cz.sps_pi.sportovni_den.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Martin Forejt on 19.01.2017.
 * forejt.martin97@gmail.com
 */

public class Group {
    private int group;
    private String name;
    private List<Match> matches;
    private Table table;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
        Collections.sort(this.matches, new Comparator<Match>() {
            @Override
            public int compare(Match match, Match t1) {
                return match.getId() < t1.getId() ? 1 : -1;
            }
        });
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
