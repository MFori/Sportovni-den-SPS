package cz.sps_pi.sportovni_den.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Martin Forejt on 19.01.2017.
 * forejt.martin97@gmail.com
 */

public class Table {

    private List<Line> lines;

    public Table() {
        lines = new ArrayList<>();
    }

    public List<Line> getLines() {

        Collections.sort(lines, new Comparator<Line>() {
            @Override
            public int compare(Line line, Line t1) {
                return line.getPosition() < t1.getPosition() ? -1 : 1;
            }
        });

        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public void addLine(Line line) {
        this.lines.add(line);
    }

    public Line getLine(int i) {
        return this.lines.get(i);
    }

    public static class Line {
        private int position;
        private Team team;
        private int points;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Team getTeam() {
            return team;
        }

        public void setTeam(Team team) {
            this.team = team;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }

}
