package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.entity.Group;

/**
 * Created by Martin Forejt on 21.01.2017.
 * forejt.martin97@gmail.com
 */

public class GroupRepository extends Repository {

    public static final String TABLE = "group_table";
    public static final String KEY_GROUP = "group_id";
    public static final String KEY_SPORT = "sport";
    public static final String KEY_NAME = "name";

    @Override
    String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_GROUP + " INTEGER, "
                + KEY_SPORT + " INTEGER, "
                + KEY_NAME + " TEXT, "
                + "PRIMARY KEY (" + KEY_GROUP + ", " + KEY_SPORT + ")"
                + ")";
    }

    public void insert(int group, int sport, String name) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);

        if (getGroup(group, sport) == null) {
            values.put(KEY_GROUP, group);
            values.put(KEY_SPORT, sport);
            db.insert(TABLE, null, values);
        } else {
            String where = KEY_GROUP + "=? AND " + KEY_SPORT + "=?";
            String[] args = new String[]{
                    String.valueOf(group),
                    String.valueOf(sport)
            };
            db.update(TABLE, values, where, args);
        }

        closeDb();
    }

    public Group getGroup(int group, int sport) {
        SQLiteDatabase db = openDb();
        if (db == null) return null;

        String query = "SELECT " + KEY_NAME + " FROM " + TABLE + " WHERE " + KEY_GROUP + "=? AND " + KEY_SPORT + "=?";
        Cursor c = db.rawQuery(query, new String[]{
                String.valueOf(group),
                String.valueOf(sport)
        });

        Group group1 = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            group1 = new Group();

            group1.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            group1.setGroup(group);
        }

        c.close();
        closeDb();

        return group1;
    }

    public List<Group> getGroups(int sport) {
        List<Group> groups = new ArrayList<>();
        SQLiteDatabase db = openDb();
        if (db == null) return groups;

        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_SPORT + "=?";

        Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(sport)});
        if (c.moveToFirst()) {
            do {
                Group group = new Group();
                group.setGroup(c.getInt(c.getColumnIndex(KEY_GROUP)));
                group.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                groups.add(group);
            } while (c.moveToNext());
        }
        c.close();

        closeDb();

        return groups;
    }

    @Override
    void initInsert(SQLiteDatabase db) {

    }
}
