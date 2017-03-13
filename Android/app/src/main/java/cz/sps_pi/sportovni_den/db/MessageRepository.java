package cz.sps_pi.sportovni_den.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.sps_pi.sportovni_den.entity.Message;

/**
 * Created by Martin Forejt on 14.01.2017.
 * forejt.martin97@gmail.com
 */

public class MessageRepository extends Repository {

    public static final String TABLE = "message";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_DATE = "date_t";
    public static final String KEY_FROM = "sender";

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT, "
                + KEY_MESSAGE + " TEXT, "
                + KEY_FROM + " INTEGER, "
                + KEY_DATE + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) )";
    }

    @Override
    public void initInsert(SQLiteDatabase db) {

    }

    public int addMessage(Message message) {
        SQLiteDatabase db = openDb();
        if (db == null) return 0;

        ContentValues values = new ContentValues();
        values.put(KEY_ID, message.getId());
        values.put(KEY_TITLE, message.getTitle());
        values.put(KEY_MESSAGE, message.getMessage());
        values.put(KEY_FROM, message.getSender());

        int id = (int) db.insert(TABLE, null, values);

        closeDb();

        return id;
    }

    public Message getMessage(int id) {
        SQLiteDatabase db = openDb();
        if (db == null) return null;

        String query = "SELECT * FROM " + TABLE + " WHERE id=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(id)});

        Message message;

        if (c.getCount() > 0) {
            c.moveToFirst();
            message = new Message();
            message.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            message.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
            message.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));
            message.setSender(c.getInt(c.getColumnIndex(KEY_FROM)));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                message.setDate(sdf.parse(c.getString(c.getColumnIndex(KEY_DATE))));
            } catch (ParseException e) {
                message.setDate(null);
            }

        } else {
            return null;
        }

        c.close();
        closeDb();

        return message;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return messages;

        String selectQuery = "SELECT * FROM " + TABLE + " ORDER BY " + KEY_DATE;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                message.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                message.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));
                message.setSender(c.getInt(c.getColumnIndex(KEY_FROM)));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    message.setDate(sdf.parse(c.getString(c.getColumnIndex(KEY_DATE))));
                } catch (ParseException e) {
                    message.setDate(null);
                }
                messages.add(message);
            } while (c.moveToNext());
        }
        c.close();

        closeDb();

        return messages;
    }

    public List<Message> getUsersMessages(boolean users, Integer userId) {
        List<Message> messages = new ArrayList<>();

        SQLiteDatabase db = openDb();
        if (db == null) return messages;

        String selectQuery = "SELECT * FROM " + TABLE;
        if (users) {
            selectQuery += " WHERE " + KEY_FROM + " = " + String.valueOf(userId);
        } else {
            selectQuery += " WHERE " + KEY_FROM + " != " + String.valueOf(userId);
        }
        selectQuery += " ORDER BY " + KEY_DATE;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                message.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                message.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));
                message.setSender(c.getInt(c.getColumnIndex(KEY_FROM)));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    message.setDate(sdf.parse(c.getString(c.getColumnIndex(KEY_DATE))));
                } catch (ParseException e) {
                    message.setDate(null);
                }
                messages.add(message);
            } while (c.moveToNext());
        }
        c.close();

        closeDb();

        return messages;
    }

}
