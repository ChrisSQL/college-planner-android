/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.chris.collegeplanner.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

public class SQLiteHandlerProjects extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_PROJECTS = "projects";

    // Login Table Columns names
    private static final String KEY_ID = "ProjectID";
    private static final String KEY_SUBJECT = "ProjectSubject";
    private static final String KEY_TYPE = "ProjectType";
    private static final String KEY_TITLE = "ProjectTitle";
    private static final String KEY_WORTH = "ProjectWorth";
    private static final String KEY_DUEDATE = "ProjectDueDate";
    private static final String KEY_DETAILS = "ProjectDetails";

    public SQLiteHandlerProjects(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROJECTS_TABLE =
                "CREATE TABLE "
                        + TABLE_PROJECTS + "("
                        + KEY_ID + " as _id INTEGER PRIMARY KEY,  "
                        + KEY_SUBJECT + " TEXT,"
                        + KEY_TYPE + " TEXT,"
                        + KEY_TITLE + " TEXT,"
                        + KEY_WORTH + " TEXT,"
                        + KEY_DUEDATE + " DATE,"
                        + KEY_DETAILS + " TEXT"
                        + ")";

        db.execSQL(CREATE_PROJECTS_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addProject(int id, String subject, String type, String title, String worth, String date, String details) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // Email
        values.put(KEY_SUBJECT, subject); // Email
        values.put(KEY_TYPE, type); // Email
        values.put(KEY_TITLE, title); // Email
        values.put(KEY_WORTH, worth); // Email
        values.put(KEY_DUEDATE, date); // Email
        values.put(KEY_DETAILS, details); // Email

        // Inserting Row
        long idInsert = db.insert(TABLE_PROJECTS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New project inserted into sqlite: " + idInsert);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getProjectDetails() {
        HashMap<String, String> project = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_PROJECTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

            project.put("subject", cursor.getString(1));
            project.put("type", cursor.getString(2));
            project.put("title", cursor.getString(3));
            project.put("worth", cursor.getString(4));
            project.put("date", cursor.getString(5));
            project.put("details", cursor.getString(6));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching project from SQLite: " + project.toString());

        return project;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PROJECTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PROJECTS, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}

