package com.monash.vietthang0705.hashtasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vietthang.0705 on 16-May-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "HashtasksDB";
    public static final int DB_VERSION = 1;

    public static int lastItem;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        lastItem = 0;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Reminder.CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Reminder.TABLE);
        onCreate(db);
    }

    ContentValues putValues(Reminder r) {
        ContentValues v = new ContentValues();

        v.put(Reminder.CN_DATE, Reminder.df2.format(r.getDate()));
        v.put(Reminder.CN_START, Reminder.tf.format(r.getStart().getTime()));
        v.put(Reminder.CN_FINISH, Reminder.tf.format(r.getFinish().getTime()));
        v.put(Reminder.CN_LAT, r.getLoc().latitude);
        v.put(Reminder.CN_LNG, r.getLoc().longitude);
        v.put(Reminder.CN_DESC, r.getDesc());

        String v_tags = "";
        ArrayList<String> r_tags = r.getTags();
        for (int i=0; i<r_tags.size(); i++) {
            v_tags += (r_tags.get(i) + ", ");
        }
        v.put(Reminder.CN_TAGS, v_tags);
        v.put(Reminder.CN_STATUS, r.getStatus()? 1:0);

        return v;
    }

    public void addReminder(Reminder r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = putValues(r);

        db.insert(Reminder.TABLE, null, v);
        db.close();
    }

    public void updateReminder(long id, Reminder new_r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = putValues(new_r);

        db.update(Reminder.TABLE, v, Reminder.CN_ID + "=" + new_r.getId(), null);
    }

    public void deleteReminder(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Reminder.TABLE, Reminder.CN_ID + "=" + id, null);
    }

    public ArrayList<Reminder> getReminders() {
        ArrayList<Reminder> reminders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c;
        c = db.rawQuery("SELECT * FROM " + Reminder.TABLE, null);

        int cnt = 10;
        if (c.moveToPosition(lastItem)) {
            do {
                // TODO: get the row into a Reminder object and add to reminders
                int col = 0;
                long q_id = c.getLong(col++);

                Date q_date = new Date();

                Calendar q_start = Calendar.getInstance();
                Calendar q_finish = Calendar.getInstance();
                Date tmp = new Date();

                try {
                    q_date = Reminder.df2.parse(c.getString(col++));

                    tmp = Reminder.tf.parse(c.getString(col++));
                    q_start.setTime(tmp);

                    tmp = Reminder.tf.parse(c.getString(col++));
                    q_finish.setTime(tmp);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

                LatLng q_latlng = new LatLng(c.getFloat(col++), c.getFloat(col++));

                String q_desc = c.getString(col++);

                String tags = c.getString(col++);
                ArrayList<String> q_tags = new ArrayList<String>(Arrays.asList(tags.split(", ")));
                // strip string

                boolean q_status = c.getInt(col) != 0;
                Log.d("DB_READ", ""+ q_status);

                Reminder r = new Reminder(q_id, q_date, q_start, q_finish, q_latlng, q_desc, q_tags, q_status);
                reminders.add(r);

                lastItem ++;
            } while (cnt -- != 0 && c.moveToNext());
        }

        c.close();
        return reminders;
    }
}
