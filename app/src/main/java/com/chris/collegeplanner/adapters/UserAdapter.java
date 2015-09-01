package com.chris.collegeplanner.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chris.collegeplanner.model.Project;
import com.chris.collegeplanner.model.User;

import java.text.SimpleDateFormat;

public class UserAdapter {

    private static final String KEY_ID = " _id";
    private static final String KEY_EMAIL = "UserEmail";
    private static final String KEY_PASSWORD = "UserPassword";
    private static final String KEY_NAME = "UserName";
    private static final String KEY_REGISTRATION_DATE = "UserRegistrationDate";
    private static final String TAG = "UserDbAdapter";
    private static final String DATABASE_NAME = "chrinrim_bbb";
    private static final String SQLITE_TABLE = "Users";
    private static final int DATABASE_VERSION = 3;
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE "
                    + SQLITE_TABLE + "(" +
                    " _id    INTEGER PRIMARY KEY,  "
                    + KEY_EMAIL + " TEXT,"
                    + KEY_PASSWORD + " TEXT,"
                    + KEY_NAME + " TEXT,"
                    + KEY_REGISTRATION_DATE + " TEXTz "

                    + ")";
    private final Context mCtx;
    // Login Table Columns names
    private User user;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public UserAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public UserAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public void createUser(User user) {


        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(user.getRegistrationDate());

        // values.put(KEY_ID, project.get_id()); // Email
        values.put(KEY_EMAIL, user.getEmail()); // Email
        values.put(KEY_PASSWORD, user.getPassword()); // Email
        values.put(KEY_NAME, user.getName()); // Email
        values.put(KEY_REGISTRATION_DATE, date); // Email

        // Inserting Row
        mDb.insert(SQLITE_TABLE, null, values);
        mDb.close(); // Closing database connection


    }

    public int updateUser(Project project) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(project.getProjectDueDate());

        ContentValues values = new ContentValues();

        values.put(KEY_EMAIL, user.getEmail()); // Email
        values.put(KEY_PASSWORD, user.getPassword()); // Email
        values.put(KEY_NAME, user.getName()); // Email

        // updating row
        return mDb.update(SQLITE_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(project.get_id())});

    }

    public void deleteAllUsers() {

//        int doneDelete = 0;
//        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
//        Log.w(TAG, Integer.toString(doneDelete));
//        return doneDelete > 0;

    }

    public boolean checkLoginOffline(String email, String password) {

        String selection = KEY_EMAIL + " = \'" + email + "\' AND " + KEY_PASSWORD + " = \'" + password + "\'";

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{
                        KEY_EMAIL},
                selection, null, null, null, null);
        if (cursor != null)
            return true;

        // return contact
        return false;
    }

    public Cursor fetchAllUsers() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_REGISTRATION_DATE,
                        KEY_EMAIL},
                null, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;


    }

    public Cursor fetchUserByID(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null || inputText.length() == 0) {
            mCursor = mDb.query(SQLITE_TABLE, new String[]{
                            KEY_ID, KEY_NAME, KEY_REGISTRATION_DATE, KEY_EMAIL},
                    null, null, null, null, null, null);

        } else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[]{
                            KEY_ID, KEY_NAME, KEY_REGISTRATION_DATE, KEY_EMAIL},
                    KEY_ID + " = " + inputText + " ", null, null, null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public void insertSomeProjects() {

//        createProject(0, "Android", "Project", "Group Project", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
//        createProject(0, "Java", "Project", "Group Project", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
//        createProject(0, "Software Enterprise", "Project", "Group Project", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
//        createProject(0, "Android", "Project", "Group Project", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
//        createProject(0, "Java", "Project", "Group Project", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");


    }

    //---deletes a particular title---
    public boolean deleteTitle(String id) {

        return mDb.delete(SQLITE_TABLE, KEY_ID + "=" + id, null) > 0;

    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, CREATE_USERS_TABLE);
            db.execSQL(CREATE_USERS_TABLE);
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

