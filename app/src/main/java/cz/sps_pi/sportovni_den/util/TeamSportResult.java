package cz.sps_pi.sportovni_den.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.entity.Group;
import cz.sps_pi.sportovni_den.entity.Match;
import cz.sps_pi.sportovni_den.entity.Table;
import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 19.01.2017.
 * forejt.martin97@gmail.com
 */

public class TeamSportResult extends SportResult {

    private List<Group> groups;

    public TeamSportResult() {
        groups = new ArrayList<>();
    }

    public List<Group> getGroups() {
        Collections.sort(groups, new Comparator<Group>() {
            @Override
            public int compare(Group group, Group t1) {
                return group.getGroup() < t1.getGroup() ? -1 : 1;
            }
        });
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    protected void fromJSON(JSONObject json) {
        switch (scoring) {
            case TYPE_GROUP_FINALE:
            case TYPE_GROUP_GROUP:
                fromJSONGroup(json);
                break;
            case TYPE_ROBIN:
                fromJSONRobin(json);
                break;
        }
    }

    private void fromJSONGroup(JSONObject json) {
        try {
            JSONArray arrGroups = json.getJSONArray("groups");

            for (int i = 0; i < arrGroups.length(); i++) {
                JSONObject groupO = arrGroups.getJSONObject(i);
                Group group = new Group();
                group.setGroup(groupO.getInt("group"));
                group.setName(groupO.getString("name"));

                JSONArray arrMatches = groupO.getJSONArray("matches");
                List<Match> matches = new ArrayList<>();

                for (int j = 0; j < arrMatches.length(); j++) {
                    JSONObject matchO = arrMatches.getJSONObject(j);
                    Match match = new Match();
                    match.setGroup(group.getGroup());
                    match.setId(matchO.getInt("id"));

                    int score1 = matchO.optInt("score_1", -1);
                    int score2 = matchO.optInt("score_2", -1);

                    match.setScore1(score1 == -1 ? null : score1);
                    match.setScore2(score2 == -1 ? null : score2);

                    JSONObject sportO = matchO.getJSONObject("sport");
                    JSONObject team1O = matchO.optJSONObject("team_1");
                    JSONObject team2O = matchO.optJSONObject("team_2");
                    JSONObject statusO = matchO.getJSONObject("status");

                    SportRepository repo = new SportRepository();
                    match.setSport(repo.getSport(sportO.getInt("id")));

                    if (team1O != null)
                        match.setTeam1(Team.getById(team1O.getInt("id")));
                    if (team2O != null)
                        match.setTeam2(Team.getById(team2O.getInt("id")));
                    match.setStatus(statusO.getInt("id"));

                    matches.add(match);
                }
                group.setMatches(matches);

                JSONArray arrLines = groupO.getJSONArray("table");
                Table table = new Table();

                for (int k = 0; k < arrLines.length(); k++) {
                    JSONObject lineO = arrLines.getJSONObject(k);
                    JSONObject teamO = lineO.optJSONObject("team");

                    Table.Line line = new Table.Line();
                    if (teamO != null)
                        line.setTeam(Team.getById(teamO.getInt("id")));
                    line.setPoints(lineO.getInt("points"));
                    line.setPosition(lineO.getInt("position"));

                    table.addLine(line);
                }
                group.setTable(table);

                groups.add(group);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fromJSONRobin(JSONObject json) {
        try {
            Group group = new Group();
            group.setGroup(0);
            group.setName(null);

            JSONArray arrMatches = json.getJSONArray("matches");
            List<Match> matches = new ArrayList<>();

            for (int j = 0; j < arrMatches.length(); j++) {
                JSONObject matchO = arrMatches.getJSONObject(j);
                Match match = new Match();
                match.setGroup(group.getGroup());
                match.setId(matchO.getInt("id"));

                int score1 = matchO.optInt("score_1", -1);
                int score2 = matchO.optInt("score_2", -1);

                match.setScore1(score1 == -1 ? null : score1);
                match.setScore2(score2 == -1 ? null : score2);

                JSONObject sportO = matchO.getJSONObject("sport");
                JSONObject team1O = matchO.optJSONObject("team_1");
                JSONObject team2O = matchO.optJSONObject("team_2");
                JSONObject statusO = matchO.getJSONObject("status");

                SportRepository repo = new SportRepository();
                match.setSport(repo.getSport(sportO.getInt("id")));

                if (team1O != null)
                    match.setTeam1(Team.getById(team1O.getInt("id")));
                if (team2O != null)
                    match.setTeam2(Team.getById(team2O.getInt("id")));
                match.setStatus(statusO.getInt("id"));

                matches.add(match);
            }
            group.setMatches(matches);

            JSONArray arrLines = json.getJSONArray("table");
            Table table = new Table();

            for (int k = 0; k < arrLines.length(); k++) {
                JSONObject lineO = arrLines.getJSONObject(k);
                JSONObject teamO = lineO.optJSONObject("team");

                Table.Line line = new Table.Line();
                if (teamO != null)
                    line.setTeam(Team.getById(teamO.getInt("id")));
                line.setPoints(lineO.getInt("points"));
                line.setPosition(lineO.getInt("position"));

                table.addLine(line);
            }
            group.setTable(table);

            groups.add(group);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
