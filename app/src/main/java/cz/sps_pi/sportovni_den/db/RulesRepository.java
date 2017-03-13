package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

public class RulesRepository extends Repository {

    public static final String TABLE = "rules";
    public static final String KEY_SPORT = "sport";
    public static final String KEY_RULES = "rules";

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_SPORT + " INTEGER PRIMARY KEY, "
                + KEY_RULES + " TEXT )";
    }

    @Override
    public void initInsert(SQLiteDatabase db) {

    }

    public String getRules(int sport) {
        return getRules(sport, null);
    }

    public String getRules(int sport, SQLiteDatabase db) {
        boolean withDb = (db != null);

        if (!withDb) {
            db = openDb();
            if (db == null) return null;
        }

        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_SPORT + "=" + String.valueOf(sport);

        String rules = null;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.getCount() > 0) {
            c.moveToFirst();

            rules = c.getString(c.getColumnIndex(KEY_RULES));
            if (rules == null) rules = "";
        }

        c.close();

        if (!withDb) closeDb();

        return rules;
    }

    public void updateRules(int sport, String rules) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        if (getRules(sport, db) == null) {
            ContentValues values = new ContentValues();
            values.put(KEY_SPORT, sport);
            values.put(KEY_RULES, rules);

            db.insert(TABLE, null, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_RULES, rules);

            String[] args = new String[]{String.valueOf(sport)};
            db.update(TABLE, values, KEY_SPORT + "=?", args);
        }

        closeDb();
    }

}
