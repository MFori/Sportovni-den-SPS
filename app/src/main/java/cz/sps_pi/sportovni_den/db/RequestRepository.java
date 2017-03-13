package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.sps_pi.sportovni_den.util.Request;
import cz.sps_pi.sportovni_den.util.Route;

/**
 * Created by Martin Forejt on 13.01.2017.
 * forejt.martin97@gmail.com
 */

public class RequestRepository extends Repository {

    public static final String TABLE = "requests";
    public static final String KEY_ID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_ROUTE = "route";
    public static final String KEY_DATA = "data";

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ROUTE + " INTEGER, "
                + KEY_DATA + " TEXT, "
                + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP )";
    }

    @Override
    public void initInsert(SQLiteDatabase db) {

    }

    public int addRequest(Request request) {
        SQLiteDatabase db = openDb();//DatabaseManager.getInstance().openDatabase();
        if (db == null) return 0;

        ContentValues values = new ContentValues();
        values.put(KEY_ROUTE, request.getRoute().getId());
        values.put(KEY_DATA, mapToString(request.getData()));

        int id = (int) db.insert(TABLE, null, values);

        closeDb();

        return id;
    }

    public void deleteRequest(Request request) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        db.delete(TABLE, KEY_ID + "=?", new String[]{String.valueOf(request.getId())});

        closeDb();
    }

    public List<Request> getRequest() {
        List<Request> requests = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return requests;

        String selectQuery = "SELECT * FROM " + TABLE + " ORDER BY " + KEY_TIMESTAMP;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Request request = new Request();
                request.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                request.setRoute(Route.get(c.getInt(c.getColumnIndex(KEY_ROUTE))));
                request.setData(stringToMap(c.getString(c.getColumnIndex(KEY_DATA))));

                requests.add(request);
            } while (c.moveToNext());
        }
        c.close();

        closeDb();

        return requests;
    }

    private String mapToString(Map<String, String> data) {
        String result = "";
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!result.equals("")) result += ";";
            result += entry.getKey() + "=" + entry.getValue();
        }

        return result;
    }

    private Map<String, String> stringToMap(String data) {
        List<String> params = Arrays.asList(data.split("\\s*;\\s*"));
        Map<String, String> map = new HashMap<>();

        for (String s : params) {
            String[] p = s.split("=");
            if (p.length >= 2)
                map.put(p[0], p[1]);
        }

        return map;
    }

}
