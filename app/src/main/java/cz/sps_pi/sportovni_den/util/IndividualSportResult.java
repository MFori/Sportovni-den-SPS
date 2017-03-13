package cz.sps_pi.sportovni_den.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.sps_pi.sportovni_den.entity.Table;
import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 19.01.2017.
 * forejt.martin97@gmail.com
 */

public class IndividualSportResult extends SportResult {

    public IndividualSportResult() {
        scoring = SportResult.TYPE_INDIVIDUALS;
    }

    private Table table;

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    protected void fromJSON(JSONObject json) {
        this.table = new Table();

        try {
            JSONArray lines = json.getJSONArray("table");

            for (int i = 0; i < lines.length(); i++) {
                JSONObject result = lines.getJSONObject(i);
                JSONObject teamO = result.getJSONObject("team");

                Table.Line line = new Table.Line();
                line.setTeam(Team.getById(teamO.getInt("id")));
                line.setPosition(result.getInt("position"));
                line.setPoints(result.getInt("points"));

                table.addLine(line);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
