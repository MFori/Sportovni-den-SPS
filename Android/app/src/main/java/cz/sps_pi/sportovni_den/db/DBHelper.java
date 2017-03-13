package cz.sps_pi.sportovni_den.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cz.sps_pi.sportovni_den.App;

/**
 * Created by Martin Forejt on 12.01.2017.
 * forejt.martin97@gmail.com
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sportDen.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper() {
        super(App.get().getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SportRepository sport = new SportRepository();
        db.execSQL(sport.createTable());

        TeamRepository team = new TeamRepository();
        db.execSQL(team.createTable());

        RequestRepository request = new RequestRepository();
        db.execSQL(request.createTable());

        MessageRepository message = new MessageRepository();
        db.execSQL(message.createTable());

        RulesRepository rules = new RulesRepository();
        db.execSQL(rules.createTable());

        CompleteResultRepository completeResults = new CompleteResultRepository();
        db.execSQL(completeResults.createTable());

        GroupRepository groups = new GroupRepository();
        db.execSQL(groups.createTable());

        ResultMatchRepository resultMatches = new ResultMatchRepository();
        db.execSQL(resultMatches.createTable());

        TableLineRepository lines = new TableLineRepository();
        db.execSQL(lines.createTable());

        RefereeMatchRepository refereeMatches = new RefereeMatchRepository();
        db.execSQL(refereeMatches.createTable());

        PerformanceRepository performances = new PerformanceRepository();
        db.execSQL(performances.createTable());

        sport.initInsert(db);
        team.initInsert(db);
        request.initInsert(db);
        message.initInsert(db);
        rules.initInsert(db);
        completeResults.initInsert(db);
        groups.initInsert(db);
        resultMatches.initInsert(db);
        lines.initInsert(db);
        refereeMatches.initInsert(db);
        performances.initInsert(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SportRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TeamRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RequestRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MessageRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RulesRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CompleteResultRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GroupRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ResultMatchRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TableLineRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RefereeMatchRepository.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PerformanceRepository.TABLE);

        onCreate(db);
    }
}
