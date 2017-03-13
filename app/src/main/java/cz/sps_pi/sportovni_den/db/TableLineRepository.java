package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.entity.Table;
import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 21.01.2017.
 * forejt.martin97@gmail.com
 */

public class TableLineRepository extends Repository {

    public static final String TABLE = "lines";
    public static final String KEY_ID = "id";
    public static final String KEY_POSITION = "position";
    public static final String KEY_SPORT = "sport";
    public static final String KEY_GROUP = "group_id";
    public static final String KEY_TEAM = "team";
    public static final String KEY_POINTS = "points";

    @Override
    String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_POSITION + " INTEGER, "
                + KEY_TEAM + " INTEGER, "
                + KEY_SPORT + " INTEGER, "
                + KEY_GROUP + " INTEGER, "
                + KEY_POINTS + " INTEGER ) ";
    }

    public void insert(Table.Line line, int sport) {
        insert(line, sport, 0);
    }

    public void insert(Table.Line line, int sport, int group) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        ContentValues values = new ContentValues();
        values.put(KEY_POINTS, line.getPoints());
        values.put(KEY_POSITION, line.getPosition());

        boolean update;
        if (line.getTeam() == null) {
            update = isLine(line, sport, group);
        } else {
            update = getLine(sport, group, line.getTeam().getId()) != null;
        }

        if (update) {
            String where = KEY_SPORT + "=? AND " + KEY_GROUP + "=? AND " + KEY_TEAM + "=?";
            String[] args = new String[]{
                    String.valueOf(sport),
                    String.valueOf(group),
                    line.getTeam() != null ? String.valueOf(line.getTeam().getId()) : null
            };
            db.update(TABLE, values, where, args);
        } else {
            values.put(KEY_GROUP, group);
            values.put(KEY_SPORT, sport);
            values.put(KEY_TEAM, line.getTeam() != null ? line.getTeam().getId() : null);
            db.insert(TABLE, null, values);
        }

        closeDb();
    }

    public boolean isLine(Table.Line line, int sport, int group) {
        SQLiteDatabase db = openDb();
        if (db == null) return false;

        String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_SPORT + "=? AND " + KEY_GROUP + "=? AND " + KEY_POSITION + "=?";
        Cursor c = db.rawQuery(query, new String[]{
                String.valueOf(sport),
                String.valueOf(group),
                String.valueOf(line.getPosition())
        });

        boolean is = false;

        if (c.getCount() > 0) {
            is = true;
        }

        c.close();
        closeDb();

        return is;
    }

    public Table.Line getLine(int sport, int group, Integer team) {
        SQLiteDatabase db = openDb();
        if (db == null) return null;

        String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_SPORT + "=? AND " + KEY_GROUP + "=? AND " + KEY_TEAM + "=?";
        Cursor c = db.rawQuery(query, new String[]{
                String.valueOf(sport),
                String.valueOf(group),
                String.valueOf(team)
        });

        Table.Line line = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            line = new Table.Line();
            line.setPoints(c.getInt(c.getColumnIndex(KEY_POINTS)));
            line.setPosition(c.getInt(c.getColumnIndex(KEY_POSITION)));
            line.setTeam(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM))));
        }

        c.close();
        closeDb();

        return line;
    }

    public List<Table.Line> getLines(int sport) {
        return getLines(sport, 0);
    }

    public List<Table.Line> getLines(int sport, int group) {
        List<Table.Line> lines = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return lines;

        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_SPORT + "=? AND " + KEY_GROUP + "=?";
        String[] args = new String[]{
                String.valueOf(sport),
                String.valueOf(group)
        };

        Cursor c = db.rawQuery(selectQuery, args);
        if (c.moveToFirst()) {
            do {
                Table.Line line = new Table.Line();

                line.setTeam(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM))));
                line.setPosition(c.getInt(c.getColumnIndex(KEY_POSITION)));
                line.setPoints(c.getInt(c.getColumnIndex(KEY_POINTS)));

                lines.add(line);
            } while (c.moveToNext());
        }
        c.close();
        closeDb();

        return lines;
    }

    public void delete(int sport, int group) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        db.delete(TABLE, KEY_SPORT + "=? AND " + KEY_GROUP + "=?", new String[]{
                String.valueOf(sport),
                String.valueOf(group)
        });

        closeDb();
    }

    @Override
    void initInsert(SQLiteDatabase db) {

    }

}
