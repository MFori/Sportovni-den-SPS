package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

public class TeamRepository extends Repository {

    public static final String TABLE = "team";
    public static final String KEY_ID = "id";
    public static final String KEY_ACTIVE = "active";

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_ACTIVE + " INTEGER )";
    }

    @Override
    public void initInsert(SQLiteDatabase db) {
        for (Team team : Team.values()) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, team.getId());
            values.put(KEY_ACTIVE, team.isActive() ? 1 : 0);
            db.insert(TABLE, null, values);
        }
    }

    public List<Team> getActiveTeams() {
        List<Team> teamList = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return teamList;

        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_ACTIVE + " = 1";

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                teamList.add(Team.getById(c.getInt(c.getColumnIndex(KEY_ID))));
            } while (c.moveToNext());
        }
        c.close();
        closeDb();

        return teamList;
    }

    public void updateTeam(Team team) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVE, team.isActive() ? 1 : 0);

        String[] args = new String[]{String.valueOf(team.getId())};
        db.update(TABLE, values, KEY_ID + "=?", args);

        closeDb();
    }
}
