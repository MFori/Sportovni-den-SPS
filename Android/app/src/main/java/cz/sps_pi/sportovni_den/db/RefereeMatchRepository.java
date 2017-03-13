package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.entity.Match;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 21.01.2017.
 * forejt.martin97@gmail.com
 */

public class RefereeMatchRepository extends Repository {

    public static final String TABLE = "referee_match";
    public static final String KEY_ID = "id";
    public static final String KEY_SPORT = "sport";
    public static final String KEY_TEAM1 = "team_1";
    public static final String KEY_TEAM2 = "team_2";
    public static final String KEY_SCORE1 = "score_1";
    public static final String KEY_SCORE2 = "score_2";
    public static final String KEY_GROUP = "group_id";
    public static final String KEY_STATUS = "status";

    @Override
    String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_SPORT + " INTEGER, "
                + KEY_TEAM1 + " INTEGER, "
                + KEY_TEAM2 + " INTEGER, "
                + KEY_SCORE1 + " INTEGER, "
                + KEY_SCORE2 + " INTEGER, "
                + KEY_GROUP + " INTEGER, "
                + KEY_STATUS + " INTEGER )";
    }

    public void insertMatch(Match match) {
        SQLiteDatabase db = openDb();
        if (db == null) return;

        ContentValues values = new ContentValues();
        if (match.getTeam1() != null)
            values.put(KEY_TEAM1, match.getTeam1().getId());
        if (match.getTeam2() != null)
            values.put(KEY_TEAM2, match.getTeam2().getId());
        values.put(KEY_SCORE1, match.getScore1());
        values.put(KEY_SCORE2, match.getScore2());
        values.put(KEY_STATUS, match.getStatus());

        if (getMatch(match.getId()) == null) {
            values.put(KEY_ID, match.getId());
            values.put(KEY_SPORT, match.getSport().getId());
            values.put(KEY_GROUP, match.getGroup());
            db.insert(TABLE, null, values);
        } else {
            String where = KEY_ID + "=?";
            String[] args = new String[]{String.valueOf(match.getId())};
            db.update(TABLE, values, where, args);
        }

        closeDb();
    }

    public Match getMatch(int id) {
        SQLiteDatabase db = openDb();
        if (db == null) return null;

        String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_ID + "=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(id)});

        Match match = null;

        if (c.getCount() > 0) {
            c.moveToFirst();

            match = new Match();
            match.setId(id);
            match.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
            match.setGroup(c.getInt(c.getColumnIndex(KEY_GROUP)));
            match.setTeam1(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM1))));
            match.setTeam2(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM2))));

            if (!c.isNull(c.getColumnIndex(KEY_SCORE1)))
                match.setScore1(c.getInt(c.getColumnIndex(KEY_SCORE1)));
            if (!c.isNull(c.getColumnIndex(KEY_SCORE2)))
                match.setScore2(c.getInt(c.getColumnIndex(KEY_SCORE2)));

            SportRepository sportRepository = new SportRepository();
            Sport sport = sportRepository.getSport(c.getInt(c.getColumnIndex(KEY_SPORT)), db);
            match.setSport(sport);
        }

        c.close();
        closeDb();

        return match;
    }

    public List<Match> getMatches(Sport sport, int group) {
        List<Match> matches = new ArrayList<>();
        SQLiteDatabase db = openDb();
        if (db == null) return matches;

        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_SPORT + "=? AND " + KEY_GROUP + "=? ";
        String[] args = new String[]{
                String.valueOf(sport.getId()),
                String.valueOf(group)
        };

        Cursor c = db.rawQuery(selectQuery, args);
        if (c.moveToFirst()) {
            do {
                Match match = new Match();
                match.setSport(sport);

                match.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                match.setGroup(c.getInt(c.getColumnIndex(KEY_GROUP)));

                if (!c.isNull(c.getColumnIndex(KEY_SCORE1)))
                    match.setScore1(c.getInt(c.getColumnIndex(KEY_SCORE1)));
                if (!c.isNull(c.getColumnIndex(KEY_SCORE2)))
                    match.setScore2(c.getInt(c.getColumnIndex(KEY_SCORE2)));

                match.setTeam1(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM1))));
                match.setTeam2(Team.getById(c.getInt(c.getColumnIndex(KEY_TEAM2))));
                match.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));

                matches.add(match);
            } while (c.moveToNext());
        }
        c.close();

        closeDb();

        return matches;
    }

    @Override
    void initInsert(SQLiteDatabase db) {

    }
}
