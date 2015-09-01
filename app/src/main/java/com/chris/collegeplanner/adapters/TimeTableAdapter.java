package com.chris.collegeplanner.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chris.collegeplanner.model.TimeTable;

public class TimeTableAdapter {

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORESLASH = "/";
    private static final String KEY_ID = " _id";
    private static final String KEY_URL = "Url";
    private static final String TAG = "TimeTableDbAdapter";
    private static final String DATABASE_NAME = "chrinrim_ccc";
    private static final String SQLITE_TABLE = "TimeTables";
    private static final int DATABASE_VERSION = 3;
    private static final String CREATE_TIMETABLE_TABLE =
            "CREATE TABLE "
                    + SQLITE_TABLE + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY, "
                    + KEY_URL + " TEXT"


                    + ")";
    private final Context mCtx;
    // Login Table Columns names
    private TimeTable timetable;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    public TimeTableAdapter(Context ctx) {


        this.mCtx = ctx;
    }

    public TimeTableAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public void createTimeTable(int id, String url) {


        ContentValues values = new ContentValues();

        values.put(KEY_ID, id); // Email
        values.put(KEY_URL, url); // Email


        // Inserting Row
        mDb.insert(SQLITE_TABLE, null, values);
        mDb.close(); // Closing database connection


    }

    public int updateTimeTable(TimeTable timeTable) {

        ContentValues values = new ContentValues();

        values.put(KEY_ID, timeTable.get_id()); // Email
        values.put(KEY_URL, timeTable.getTimetableURL()); // Email

        // updating row
        return mDb.update(SQLITE_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(timeTable.get_id())});

    }

    public void deleteAllUsers() {

//        int doneDelete = 0;
//        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
//        Log.w(TAG, Integer.toString(doneDelete));
//        return doneDelete > 0;

    }

    public Cursor fetchAllTimeTables() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{
                        KEY_ID,
                        KEY_URL},
                null, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;


    }

    // Getting single contact
    public TimeTable getTimeTable(int id) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ID,
                        KEY_URL}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        TimeTable timeTable = new TimeTable(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return contact
        return timeTable;
    }

    public void insertSomeTimeTables() {

        createTimeTable(1, "Add an image of your timetable.");

    }

    //---deletes a particular title---
    public boolean deleteTitle(String id) {

        return mDb.delete(SQLITE_TABLE, KEY_ID + "=" + id, null) > 0;

    }

    public boolean exists(int id) {
        String Query = "Select * from " + SQLITE_TABLE + " where " + KEY_ID + " = " + id;
        Cursor cursor = mDb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, CREATE_TIMETABLE_TABLE);
            db.execSQL(CREATE_TIMETABLE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }
}

