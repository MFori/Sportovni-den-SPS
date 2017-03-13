package cz.sps_pi.sportovni_den.db;

import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

abstract public class Repository {
    abstract String createTable();

    /**
     * Called when table is created
     *
     * @param db db
     */
    abstract void initInsert(SQLiteDatabase db);

    /**
     * Get db object
     *
     * @return db
     */
    public SQLiteDatabase openDb() {
        try {
            return DatabaseManager.getInstance().openDatabase();
        } catch (ManagerNotInitializedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Close db object
     */
    public void closeDb() {
        try {
            DatabaseManager.getInstance().closeDatabase();
        } catch (ManagerNotInitializedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert Date object to String
     *
     * @param date Date
     * @return string
     */
    protected String DateToString(Date date) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }
}
