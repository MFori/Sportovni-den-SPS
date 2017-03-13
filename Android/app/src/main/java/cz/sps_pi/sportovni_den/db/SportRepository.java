package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.entity.Sport;

/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

public class SportRepository extends Repository {

    public static final String TABLE = "sport";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_SCORING = "scoring";
    public static final String KEY_SETS = "sets";
    public static final String KEY_SET_POINTS = "set_points";
    public static final String KEY_TIME = "time";

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_SCORING + " INTEGER, "
                + KEY_SETS + " INTEGER, "
                + KEY_SET_POINTS + " INTEGER, "
                + KEY_TIME + " INTEGER, "
                + KEY_ACTIVE + " INTEGER )";
    }

    @Override
    public void initInsert(SQLiteDatabase db) {
        for (Sport sport : getDefaultSports()) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, sport.getId());
            values.put(KEY_NAME, sport.getName());
            values.put(KEY_ACTIVE, sport.isActive() ? 1 : 0);
            values.putNull(KEY_SCORING);
            values.putNull(KEY_SETS);
            values.putNull(KEY_SET_POINTS);
            values.putNull(KEY_TIME);
            db.insert(TABLE, null, values);
        }
    }

    public List<Sport> getAllSports() {
        List<Sport> sportList = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return sportList;

        String selectQuery = "SELECT * FROM " + TABLE;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Sport sport = new Sport();
                sport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                sport.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                sport.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)) == 1);
                sport.setScoring(c.getInt(c.getColumnIndex(KEY_SCORING)));
                sport.setSets(c.getInt(c.getColumnIndex(KEY_SETS)));
                sport.setSetPoints(c.getInt(c.getColumnIndex(KEY_SET_POINTS)));
                sport.setTime(c.getInt(c.getColumnIndex(KEY_TIME)));

                sportList.add(sport);
            } while (c.moveToNext());
        }
        c.close();

        closeDb();

        return sportList;
    }

    public List<Sport> getActiveSports() {
        List<Sport> sportList = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return sportList;

        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_ACTIVE + " = 1";

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Sport sport = new Sport();
                sport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                sport.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                sport.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)) == 1);
                sport.setScoring(c.getInt(c.getColumnIndex(KEY_SCORING)));

                if (!c.isNull(c.getColumnIndex(KEY_SETS)))
                    sport.setSets(c.getInt(c.getColumnIndex(KEY_SETS)));
                if (!c.isNull(c.getColumnIndex(KEY_SET_POINTS)))
                    sport.setSetPoints(c.getInt(c.getColumnIndex(KEY_SET_POINTS)));
                if (!c.isNull(c.getColumnIndex(KEY_TIME)))
                    sport.setTime(c.getInt(c.getColumnIndex(KEY_TIME)));

                sportList.add(sport);
            } while (c.moveToNext());
        }
        c.close();

        closeDb();

        return sportList;
    }

    public void updateSport(Sport sport) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVE, sport.isActive() ? 1 : 0);
        values.put(KEY_SCORING, sport.getScoring());

        if (sport.getSets() != null) {
            values.put(KEY_SETS, sport.getSets());
        } else {
            values.putNull(KEY_SETS);
        }
        if (sport.getSetPoints() != null) {
            values.put(KEY_SET_POINTS, sport.getSetPoints());
        } else {
            values.putNull(KEY_SET_POINTS);
        }
        if (sport.getTime() != null) {
            values.put(KEY_TIME, sport.getTime());
        } else {
            values.putNull(KEY_TIME);
        }

        String[] args = new String[]{String.valueOf(sport.getId())};
        db.update(TABLE, values, KEY_ID + "=?", args);

        closeDb();
    }

    public Sport getSport(int id) {
        SQLiteDatabase db = openDb();
        if (db == null) return null;

        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_ID + " = " + String.valueOf(id);

        Sport sport = null;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            sport = new Sport();
            sport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            sport.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            sport.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)) == 1);
            sport.setScoring(c.getInt(c.getColumnIndex(KEY_SCORING)));

            if (!c.isNull(c.getColumnIndex(KEY_SETS)))
                sport.setSets(c.getInt(c.getColumnIndex(KEY_SETS)));
            if (!c.isNull(c.getColumnIndex(KEY_SET_POINTS)))
                sport.setSetPoints(c.getInt(c.getColumnIndex(KEY_SET_POINTS)));
            if (!c.isNull(c.getColumnIndex(KEY_TIME)))
                sport.setTime(c.getInt(c.getColumnIndex(KEY_TIME)));
        }
        c.close();

        closeDb();

        return sport;
    }

    public Sport getSport(int id, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_ID + " = " + String.valueOf(id);

        Sport sport = null;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            sport = new Sport();
            sport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            sport.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            sport.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)) == 1);
            sport.setScoring(c.getInt(c.getColumnIndex(KEY_SCORING)));

            if (!c.isNull(c.getColumnIndex(KEY_SETS)))
                sport.setSets(c.getInt(c.getColumnIndex(KEY_SETS)));
            if (!c.isNull(c.getColumnIndex(KEY_SET_POINTS)))
                sport.setSetPoints(c.getInt(c.getColumnIndex(KEY_SET_POINTS)));
            if (!c.isNull(c.getColumnIndex(KEY_TIME)))
                sport.setTime(c.getInt(c.getColumnIndex(KEY_TIME)));
        }
        c.close();

        return sport;
    }

    private Sport[] getDefaultSports() {
        Sport[] sports = new Sport[10];

        sports[0] = new Sport(1, "Fotbal", true);
        sports[1] = new Sport(2, "Nohejbal", true);
        sports[2] = new Sport(3, "Basketbal", true);
        sports[3] = new Sport(4, "Volejbal", true);
        sports[4] = new Sport(5, "Ringo", true);
        sports[5] = new Sport(6, "Přetah lanem", true);
        sports[6] = new Sport(7, "Ping pong", true);
        sports[7] = new Sport(8, "Shyby", true);
        sports[8] = new Sport(9, "Šipky", true);
        sports[9] = new Sport(10, "Trojskok", true);

        return sports;
    }
}
