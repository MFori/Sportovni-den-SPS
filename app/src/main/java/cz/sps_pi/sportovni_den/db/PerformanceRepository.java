package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.entity.Performance;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 21.01.2017.
 * forejt.martin97@gmail.com
 */

public class PerformanceRepository extends Repository {

    public static final String TABLE = "performance";
    public static final String KEY_ID = "id";
    public static final String KEY_TEAM = "team";
    public static final String KEY_SPORT = "sport";
    public static final String KEY_POINTS = "points";
    public static final String KEY_DATE = "date";

    @Override
    String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TEAM + " INTEGER, "
                + KEY_SPORT + " INTEGER, "
                + KEY_POINTS + " INTEGER, "
                + KEY_DATE + " DATETIME )";
    }

    public void insertPerformance(Performance performance) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        ContentValues values = new ContentValues();
        values.put(KEY_POINTS, performance.getPoints());
        values.put(KEY_DATE, DateToString(performance.getDate()));
        values.put(KEY_SPORT, performance.getSport().getId());
        values.put(KEY_TEAM, performance.getTeam().getId());

        if (getPerformance(performance.getId()) == null) {
            if (performance.getId() == 0) performance.setId(getLastId(db) + 1);
            values.put(KEY_ID, performance.getId());
            db.insert(TABLE, null, values);
        } else {
            db.update(TABLE, values, KEY_ID + "=?", new String[]{String.valueOf(performance.getId())});
        }

        closeDb();
    }

    private Integer getLastId(SQLiteDatabase db) {
        String query = "SELECT id FROM " + TABLE + " ORDER BY " + KEY_ID + " DESC LIMIT 1";
        Cursor c = db.rawQuery(query, null);

        Integer res = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            res = c.getInt(c.getColumnIndex(KEY_ID));
        }
        c.close();

        return res;
    }

    public void deletePerformance(Performance performance) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        db.delete(TABLE, KEY_ID + "=?", new String[]{String.valueOf(performance.getId())});

        closeDb();
    }

    public void deletePerformances(int sport) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        db.delete(TABLE, KEY_SPORT + "=?", new String[]{String.valueOf(sport)});

        closeDb();
    }

    public Performance getPerformance(int id) {
        SQLiteDatabase db = openDb();
        if (db == null) return null;

        String query = "SELECT * FROM " + TABLE + " WHERE id=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(id)});

        Performance performance = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            performance = new Performance();
            performance.setId(id);
            performance.setPoints(c.getInt(c.getColumnIndex(KEY_POINTS)));
            performance.setTeam(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM))));

            SportRepository repository = new SportRepository();
            performance.setSport(repository.getSport(c.getInt(c.getColumnIndex(KEY_SPORT)), db));

            /*SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY", Locale.getDefault());
            try {
                performance.setDate(sdf.parse(c.getString(c.getColumnIndex(KEY_DATE))));
            } catch (ParseException e) {
                performance.setDate(null);
            }*/
        }

        c.close();
        closeDb();

        return performance;
    }

    public List<Performance> getPerformances(Sport sport) {
        List<Performance> performances = new ArrayList<>();
        SQLiteDatabase db = openDb();
        if (db == null) return performances;

        String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_SPORT + "=? ORDER BY " + KEY_ID + " ASC";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(sport.getId())});

        if (c.moveToFirst()) {
            do {
                Performance performance = new Performance();
                performance.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                performance.setPoints(c.getInt(c.getColumnIndex(KEY_POINTS)));
                performance.setTeam(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM))));
                performance.setSport(sport);

                /*SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY", Locale.getDefault());
                try {
                    performance.setDate(sdf.parse(c.getString(c.getColumnIndex(KEY_DATE))));
                } catch (ParseException e) {
                    performance.setDate(null);
                }*/

                performances.add(performance);
            } while (c.moveToNext());
        }
        c.close();
        closeDb();

        return performances;
    }

    @Override
    void initInsert(SQLiteDatabase db) {

    }
}
