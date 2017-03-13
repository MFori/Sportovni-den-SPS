package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.util.CompleteResultsAdapter;

/**
 * Created by Martin Forejt on 18.01.2017.
 * forejt.martin97@gmail.com
 */

public class CompleteResultRepository extends Repository {

    public static final String TABLE = "complete_results";
    public static final String KEY_TEAM = "team";
    public static final String KEY_SPORT = "sport";
    public static final String KEY_POINTS = "points";

    @Override
    String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_TEAM + " INTEGER, "
                + KEY_SPORT + " INTEGER, "
                + KEY_POINTS + " INTEGER, "
                + "PRIMARY KEY (" + KEY_TEAM + ", " + KEY_SPORT + ")"
                + ")";
    }

    /**
     * Update/create complete team results
     *
     * @param result Result
     */
    public void updateTeamResult(CompleteResultsAdapter.Result result) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        int team = result.getTeam().getId();
        Map<Sport, Integer> results = result.getResults();

        for (Map.Entry<Sport, Integer> entry : results.entrySet()) {
            int sport = entry.getKey().getId();
            int points = entry.getValue();

            ContentValues values = new ContentValues();
            values.put(KEY_TEAM, team);
            values.put(KEY_SPORT, sport);
            values.put(KEY_POINTS, points);

            if (existResult(sport, team, db)) {
                String[] args = new String[]{
                        String.valueOf(sport),
                        String.valueOf(team)
                };
                db.update(TABLE, values, KEY_SPORT + "=? AND " + KEY_TEAM + "=?", args);
            } else {
                db.insert(TABLE, null, values);
            }

        }

        closeDb();
    }

    /**
     * Check if result exist
     *
     * @param sport sport
     * @param team  team
     * @param db    db
     * @return exist?
     */
    private boolean existResult(int sport, int team, SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE + " WHERE sport=? AND team=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(sport), String.valueOf(team)});

        boolean res = c.getCount() > 0;

        c.close();

        return res;
    }

    /**
     * Get all results
     *
     * @return list
     */
    public List<CompleteResultsAdapter.Result> getResults() {
        List<CompleteResultsAdapter.Result> results = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return results;

        String query = "SELECT * FROM " + TABLE + " ORDER BY " + KEY_TEAM + ", " + KEY_SPORT;
        Cursor c = db.rawQuery(query, null);

        int team = -1;
        CompleteResultsAdapter.Result result = null;
        Map<Sport, Integer> sportRes = new TreeMap<>();
        SportRepository sportRepository = new SportRepository();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(c.getColumnIndex(KEY_TEAM)) != team) {
                    if (result != null) {
                        result.setResults(sportRes);
                        results.add(result);
                    }
                    team = c.getInt(c.getColumnIndex(KEY_TEAM));
                    result = new CompleteResultsAdapter.Result();
                    result.setTeam(Team.getById(team));
                    result.setPosition(0);
                    sportRes = new HashMap<>();
                }

                Sport sport = sportRepository.getSport(c.getInt(c.getColumnIndex(KEY_SPORT)), db);
                int points = c.getInt(c.getColumnIndex(KEY_POINTS));
                sportRes.put(sport, points);
                if (result != null) {
                    result.setPosition(result.getPosition() + points);
                }

            } while (c.moveToNext());
        }

        c.close();
        closeDb();

        return results;
    }

    @Override
    void initInsert(SQLiteDatabase db) {

    }
}
