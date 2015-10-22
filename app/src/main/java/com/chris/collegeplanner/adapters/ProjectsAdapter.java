package com.chris.collegeplanner.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chris.collegeplanner.model.Project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectsAdapter {

    // Login Table Columns names


    private static final String KEY_ID = " _id";
    private static final String KEY_SUBJECT = "ProjectSubject";
    private static final String KEY_TYPE = "ProjectType";
    private static final String KEY_TITLE = "ProjectTitle";
    private static final String KEY_WORTH = "ProjectWorth";
    private static final String KEY_DUEDATE = "ProjectDueDate";
    private static final String KEY_DETAILS = "ProjectDetails";
    private static final String KEY_EMAIL = "ProjectEmail";

    private static final String TAG = "ProjectsDbAdapter";
    private static final String DATABASE_NAME = "chrinrim_bbb";
    private static final String SQLITE_TABLE = "Projects";
    private static final int DATABASE_VERSION = 3;
    private static final String CREATE_PROJECTS_TABLE =
            "CREATE TABLE "
                    + SQLITE_TABLE + "(" +
                    " _id    INTEGER PRIMARY KEY,  "
                    + KEY_SUBJECT + " TEXT,"
                    + KEY_TYPE + " TEXT,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_WORTH + " TEXT,"
                    + KEY_DUEDATE + " DATE,"
                    + KEY_DETAILS + " TEXT,"
                    + KEY_EMAIL + " TEXT"
                    + ")";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public ProjectsAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public ProjectsAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public void createProject(Project project) {


        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(project.getProjectDueDate());

        // values.put(KEY_ID, project.get_id()); // Email
        values.put(KEY_SUBJECT, project.getProjectSubject()); // Email
        values.put(KEY_TYPE, project.getProjectType()); // Email
        values.put(KEY_TITLE, project.getProjectTitle()); // Email
        values.put(KEY_WORTH, project.getProjectWorth()); // Email
        values.put(KEY_DUEDATE, date); // Email
        values.put(KEY_DETAILS, project.getProjectDetails()); // Email
        values.put(KEY_EMAIL, project.getProjectEmail()); // Email

        // Inserting Row
        mDb.insert(SQLITE_TABLE, null, values);
        mDb.close(); // Closing database connection


    }

    public int updateProject(Project project) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(project.getProjectDueDate());

        ContentValues values = new ContentValues();
        values.put(KEY_ID, project.get_id()); // Email
        values.put(KEY_SUBJECT, project.getProjectSubject()); // Email
        values.put(KEY_TYPE, project.getProjectType()); // Email
        values.put(KEY_TITLE, project.getProjectTitle()); // Email
        values.put(KEY_WORTH, project.getProjectWorth()); // Email
        values.put(KEY_DUEDATE, date); // Email
        values.put(KEY_DETAILS, project.getProjectDetails()); // Email
        values.put(KEY_EMAIL, project.getProjectEmail()); // Email

        // updating row
        return mDb.update(SQLITE_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(project.get_id())});

    }

    public boolean deleteAllProjects() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public Cursor fetchProjectsByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null || inputText.length() == 0) {
            mCursor = mDb.query(SQLITE_TABLE, new String[]{
                            KEY_ID, KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
                    null, null, null, null, null, null);

        } else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[]{
                            KEY_ID, KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
                    KEY_TITLE + " like '%" + inputText + "%'", null, null, null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchProjectsByEmail(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null || inputText.length() == 0) {
            mCursor = mDb.query(SQLITE_TABLE, new String[]{
                            KEY_ID, KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
                    null, null, null, null, null, null);

        } else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[]{
                            KEY_ID, KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
                    KEY_EMAIL + " = " + inputText + " ", null, null, null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean fetchProjectBySubjectDetails(String subject, String details) throws SQLException {


        String Query = "" +
                "Select * from " + DATABASE_NAME
                + " where " + KEY_SUBJECT + " = \"" + subject
                + "\" AND " + KEY_DETAILS + " = \"" + details + "\";";

        Cursor cursor = mDb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    public Cursor fetchAllProjects() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{
                        KEY_ID,
                        KEY_SUBJECT,
                        KEY_TYPE,
                        KEY_TITLE,
                        KEY_WORTH,
                        KEY_DUEDATE,
                        KEY_DETAILS,
                        KEY_EMAIL},
                null, null, null, null, KEY_DUEDATE + " ASC", null);

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
    public boolean deleteTitle(String id)
    {
        return mDb.delete(SQLITE_TABLE, KEY_ID + "=" + id, null) > 0;
    }

    // Getting single contact
    public Project getProject(int id) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{
                        KEY_ID,
                        KEY_SUBJECT,
                        KEY_TYPE,
                        KEY_TITLE,
                        KEY_WORTH,
                        KEY_DUEDATE,
                        KEY_DETAILS,
                        KEY_EMAIL
                },
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();


        String dateString = cursor.getString(5);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        Date date = new Date(cursor.getLong(5));
        // Date date = new Date();
        Log.d("Date : ", date.toString());

        Project project = new Project(

                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                convertedDate,
                cursor.getString(6),
                cursor.getString(7));

        // return contact
        return project;
    }

    public List<String> getSubjects() {

        Cursor crs = mDb.rawQuery("SELECT " + KEY_SUBJECT + " FROM " + SQLITE_TABLE + " ", null);


        List<String> array = new ArrayList<String>();
        while (crs.moveToNext()) {
            String uname = crs.getString(crs.getColumnIndex(KEY_SUBJECT));
            array.add(uname);
        }


        return array;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, CREATE_PROJECTS_TABLE);
            db.execSQL(CREATE_PROJECTS_TABLE);
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

