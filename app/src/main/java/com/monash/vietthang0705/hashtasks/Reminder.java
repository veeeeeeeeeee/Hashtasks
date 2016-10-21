package com.monash.vietthang0705.hashtasks;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.SimpleTimeZone;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class Reminder implements Parcelable, Comparable<Reminder> {
    public static final SimpleDateFormat df = new SimpleDateFormat("MMM dd");
    public static final SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat tf = new SimpleDateFormat("hh:mm");

    public static final String TABLE = "reminders";
    public static final String CN_ID = "id";
    public static final String CN_DATE = "date";
    public static final String CN_START = "start";
    public static final String CN_FINISH = "finish";
    public static final String CN_LAT = "lat";
    public static final String CN_LNG = "lng";
    public static final String CN_DESC = "desc";
    public static final String CN_TAGS = "tags";
    public static final String CN_STATUS = "status";

    public static final String CREATE_STATEMENT =
            "CREATE TABLE " + TABLE + "(" +
                CN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                CN_DATE + " DATE NOT NULL, " +
                CN_START + " TEXT NOT NULL, " +
                CN_FINISH + " TEXT NOT NULL, " +
                CN_LAT + " DECIMAL(10, 7) NOT NULL, " +
                CN_LNG + " DECIMAL(10, 7) NOT NULL, " +
                CN_DESC + " TEXT NOT NULL, " +
                CN_TAGS + " TEXT NOT NULL, " +
                CN_STATUS + " TEXT NOT NULL" +
            ")";

    public int listId;

    long id;
    private boolean rStatus;
    private Date rDate;
    private Calendar rStart;
    private Calendar rFinish;
    private LatLng rLoc;
    private String rDesc;
    private ArrayList<String> rTags;

    // default Reminder, also testing purposes
    public Reminder() {
        /*
        try {
            rDate = df2.parse("01/01/2000");
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        */

        rDate = Calendar.getInstance().getTime();
        rStart = Calendar.getInstance();
        rFinish = Calendar.getInstance();
        rLoc = new LatLng(-37.8773836, 145.0455378);
        rDesc = "Description";

        rTags = new ArrayList<>();
        rStatus = false;
    }

    public Reminder(Date _date, Calendar _start, Calendar _finish, LatLng _loc, String _desc, ArrayList<String> _tags, boolean _status) {
        rDate = _date;
        rStart = _start;
        rFinish = _finish;
        rLoc = _loc;
        rDesc = _desc;
        rTags = _tags;
        rStatus = _status;
    }

    public Reminder(long _id, Date _date, Calendar _start, Calendar _finish, LatLng _loc, String _desc, ArrayList<String> _tags, boolean _status) {
        id = _id;
        rDate = _date;
        rStart = _start;
        rFinish = _finish;
        rLoc = _loc;
        rDesc = _desc;
        rTags = _tags;
        rStatus = _status;
    }

    public int compareTo(Reminder rhs) {
        if (this.getDate().before(rhs.getDate()))
            return -1;
        if (this.getDate().after(rhs.getDate()))
            return 1;
        return 0;
    }

    // get set methods
    public long getId() {return id;}

    public int getListId() {return listId;}
    public void setListId(int _listId) {listId = _listId;}

    public Date getDate() {return rDate;}
    public void setDate(Date _date) {rDate = _date;}

    public Calendar getStart() {return rStart;}
    public void setStart(Calendar _start) {rStart = _start;}

    public Calendar getFinish() {return rFinish;}
    public void setFinish(Calendar _finish) {rFinish = _finish;}

    public LatLng getLoc() {return rLoc;}
    public void setLoc(LatLng _loc) {rLoc = _loc;}

    public String getDesc() {return rDesc;}
    public void setDesc(String _desc) {rDesc = _desc;}

    public ArrayList<String> getTags() {return rTags;}
    public void addTag(String _tag) {rTags.add(0, _tag);}
    public void setTags(ArrayList<String> _tags) {rTags = _tags;}

    public boolean getStatus() {return rStatus;}
    public void setStatus(boolean _status) {rStatus = _status;}

    public boolean containAll(ArrayList<String> tags) {
        boolean found = false;
        for (int i=0; i<tags.size(); i++) {
            String search = tags.get(i);
            for (int j=0; j<rTags.size(); j++) {
                String cmp = rTags.get(j);

                Log.d("contain", ""+ search + "|" + cmp);
                if (search.equals(cmp)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public boolean containOne(ArrayList<String> tags) {
        for (int i=0; i<tags.size(); i++) {
            String search = tags.get(i);
            for (int j = 0; j < rTags.size(); j++) {
                String cmp = rTags.get(i);

                if (search.equals(cmp))
                    return true;
            }
        }
        return false;
    }

    // testing purposes
    public void randomise() {
        Calendar c = Calendar.getInstance();

        c.add(Calendar.DAY_OF_MONTH, listId);
        rDate = c.getTime();

        c.add(Calendar.HOUR, listId);
        rStart = c;
        rFinish = rStart;
        rFinish.add(Calendar.HOUR, 1);

        rTags = new ArrayList<>();
        for (int i=0; i<7; i++) {
            rTags.add("assignment");
        }
    }

    public static ArrayList<Reminder> generateList(int numReminders) {
        ArrayList<Reminder> reminders = new ArrayList<>();

        for (int i=0; i<numReminders; i++) {
            reminders.add(new Reminder());
        }

        return reminders;
    }

    // Parcelable overrides / abstract
    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel source) {
            return new Reminder(source);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public Reminder(Parcel in) {
        id = in.readLong();
        listId = in.readInt();
        rDate = (Date) in.readSerializable();
        rStart = (Calendar) in.readSerializable();
        rFinish = (Calendar) in.readSerializable();
        rDesc = in.readString();
        rTags = (ArrayList<String>) in.readSerializable();

        rStatus = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeLong(id);
        p.writeInt(listId);
        p.writeSerializable(rDate);
        p.writeSerializable(rStart);
        p.writeSerializable(rFinish);
        p.writeString(rDesc);
        p.writeSerializable(rTags);
        p.writeByte((byte) (rStatus? 1:0));
    }
}
