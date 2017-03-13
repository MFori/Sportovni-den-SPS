package cz.sps_pi.sportovni_den.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

public class DatabaseManager {

    private static DatabaseManager instance = null;
    private SQLiteOpenHelper databaseHelper;
    private SQLiteDatabase db;

    private int openCount = 0;

    private DatabaseManager(SQLiteOpenHelper helper) {
        this.databaseHelper = helper;
    }

    /**
     * Init manager - create singleton instance
     *
     * @param helper open helper
     */
    public static synchronized void initInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager(helper);
        }
    }

    /**
     * Clear all data
     */
    public void clearAll() {
        databaseHelper.onUpgrade(openDatabase(), DBHelper.DATABASE_VERSION, DBHelper.DATABASE_VERSION);
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new ManagerNotInitializedException();
        }

        return instance;
    }

    /**
     * Return db object, increment open counter
     *
     * @return db
     */
    public synchronized SQLiteDatabase openDatabase() {
        openCount += 1;
        if (openCount == 1) {
            db = databaseHelper.getWritableDatabase();
        }

        return db;
    }

    /**
     * decrement open counter and close db if is 0
     */
    public synchronized void closeDatabase() {
        openCount -= 1;
        if (openCount == 0) {
            db.close();
        }
    }

    /**
     * destroy singleton instance
     */
    public static void close() {
        if (instance != null) {
            if (instance.db.isOpen()) instance.db.close();
            instance.databaseHelper.close();
            instance = null;
        }
    }

}
