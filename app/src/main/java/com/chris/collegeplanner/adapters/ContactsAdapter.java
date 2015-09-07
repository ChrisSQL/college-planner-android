//package com.chris.collegeplanner.adapters;
//
///**
// * Created by Chris on 06/09/2015.
// */
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import com.chris.collegeplanner.model.Contact;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class ContactsAdapter {
//
//    // Login Table Columns names
//
//
//    private static final String KEY_ID = " _id";
//    private static final String KEY_NAME = "ContactSubject";
//    private static final String KEY_DATA1 = "ContactType";
//
//    private static final String TAG = "ContactsDbAdapter";
//    private static final String DATABASE_NAME = "chrinrim_eee";
//    private static final String SQLITE_TABLE = "Contacts";
//    private static final int DATABASE_VERSION = 4;
//    private static final String CREATE_PROJECTS_TABLE =
//            "CREATE TABLE "
//                    + SQLITE_TABLE + "(" +
//                    " _id    INTEGER PRIMARY KEY,  "
//                    + KEY_NAME + " TEXT,"
//                    + KEY_DATA1 + " TEXT"
//                    + ")";
//    private final Context mCtx;
//    private DatabaseHelper mDbHelper;
//    private SQLiteDatabase mDb;
//
//    public ContactsAdapter(Context ctx) {
//        this.mCtx = ctx;
//    }
//
//    public ContactsAdapter open() throws SQLException {
//        mDbHelper = new DatabaseHelper(mCtx);
//        mDb = mDbHelper.getWritableDatabase();
//        return this;
//    }
//
//    public void close() {
//        if (mDbHelper != null) {
//            mDbHelper.close();
//        }
//    }
//
//    public void createContact(Contact project) {
//
//
//        ContentValues values = new ContentValues();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String date = sdf.format(project.getContactDueDate());
//
//        // values.put(KEY_ID, project.get_id()); // Email
//        values.put(KEY_SUBJECT, project.getContactSubject()); // Email
//        values.put(KEY_TYPE, project.getContactType()); // Email
//        values.put(KEY_TITLE, project.getContactTitle()); // Email
//        values.put(KEY_WORTH, project.getContactWorth()); // Email
//        values.put(KEY_DUEDATE, date); // Email
//        values.put(KEY_DETAILS, project.getContactDetails()); // Email
//        values.put(KEY_EMAIL, project.getContactEmail()); // Email
//
//        // Inserting Row
//        mDb.insert(SQLITE_TABLE, null, values);
//        mDb.close(); // Closing database connection
//
//
//    }
//
//    public int updateContact(Contact project) {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String date = sdf.format(project.getContactDueDate());
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_ID, project.get_id()); // Email
//        values.put(KEY_SUBJECT, project.getContactSubject()); // Email
//        values.put(KEY_TYPE, project.getContactType()); // Email
//        values.put(KEY_TITLE, project.getContactTitle()); // Email
//        values.put(KEY_WORTH, project.getContactWorth()); // Email
//        values.put(KEY_DUEDATE, date); // Email
//        values.put(KEY_DETAILS, project.getContactDetails()); // Email
//        values.put(KEY_EMAIL, project.getContactEmail()); // Email
//
//        // updating row
//        return mDb.update(SQLITE_TABLE, values, KEY_ID + " = ?",
//                new String[]{String.valueOf(project.get_id())});
//
//    }
//
//    public boolean deleteAllContacts() {
//
//        int doneDelete = 0;
//        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
//        Log.w(TAG, Integer.toString(doneDelete));
//        return doneDelete > 0;
//
//    }
//
//    public Cursor fetchContactsByName(String inputText) throws SQLException {
//        Log.w(TAG, inputText);
//        Cursor mCursor = null;
//        if (inputText == null || inputText.length() == 0) {
//            mCursor = mDb.query(SQLITE_TABLE, new String[]{
//                            KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
//                    null, null, null, null, null, null);
//
//        } else {
//            mCursor = mDb.query(true, SQLITE_TABLE, new String[]{
//                            KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
//                    KEY_TITLE + " like '%" + inputText + "%'", null, null, null, null, null, null);
//        }
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }
//
//    public Cursor fetchContactsByEmail(String inputText) throws SQLException {
//        Log.w(TAG, inputText);
//        Cursor mCursor = null;
//        if (inputText == null || inputText.length() == 0) {
//            mCursor = mDb.query(SQLITE_TABLE, new String[]{
//                            KEY_ID, KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
//                    null, null, null, null, null, null);
//
//        } else {
//            mCursor = mDb.query(true, SQLITE_TABLE, new String[]{
//                            KEY_ID, KEY_SUBJECT, KEY_TYPE, KEY_TITLE, KEY_WORTH, KEY_DUEDATE, KEY_DETAILS, KEY_EMAIL},
//                    KEY_EMAIL + " = " + inputText + " ", null, null, null, null, null, null);
//        }
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }
//
//    public boolean fetchContactBySubjectDetails(String subject, String details) throws SQLException {
//
//
//        String Query = "" +
//                "Select * from " + DATABASE_NAME
//                + " where " + KEY_SUBJECT + " = \"" + subject
//                + "\" AND " + KEY_DETAILS + " = \"" + details + "\";";
//
//        Cursor cursor = mDb.rawQuery(Query, null);
//        if (cursor.getCount() <= 0) {
//            cursor.close();
//            return false;
//        }
//        cursor.close();
//        return true;
//
//    }
//
//    public Cursor fetchAllContacts() {
//
//        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{
//                        KEY_ID,
//                        KEY_SUBJECT,
//                        KEY_TYPE,
//                        KEY_TITLE,
//                        KEY_WORTH,
//                        KEY_DUEDATE,
//                        KEY_DETAILS,
//                        KEY_EMAIL},
//                null, null, null, null, KEY_DUEDATE + " ASC", null);
//
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//
//
//    }
//
//    public void insertSomeContacts() {
//
////        createContact(0, "Android", "Contact", "Group Contact", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
////        createContact(0, "Java", "Contact", "Group Contact", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
////        createContact(0, "Software Enterprise", "Contact", "Group Contact", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
////        createContact(0, "Android", "Contact", "Group Contact", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
////        createContact(0, "Java", "Contact", "Group Contact", "40", "0", "Android group project due.", "chrismaher.wit@gmail.com");
//
//
//    }
//
//    //---deletes a particular title---
//    public boolean deleteTitle(String id)
//    {
//        return mDb.delete(SQLITE_TABLE, KEY_ID + "=" + id, null) > 0;
//    }
//
//    // Getting single contact
//    public Contact getContact(int id) {
//
//        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{
//                        KEY_ID,
//                        KEY_SUBJECT,
//                        KEY_TYPE,
//                        KEY_TITLE,
//                        KEY_WORTH,
//                        KEY_DUEDATE,
//                        KEY_DETAILS,
//                        KEY_EMAIL
//                },
//                KEY_ID + "=?",
//                new String[]{String.valueOf(id)}, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//
//        String dateString = cursor.getString(5);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date convertedDate = new Date();
//        try {
//            convertedDate = dateFormat.parse(dateString);
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//
//
//        Date date = new Date(cursor.getLong(5));
//        // Date date = new Date();
//        Log.d("Date : ", date.toString());
//
//        Contact project = new Contact(
//
//                Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1),
//                cursor.getString(2),
//                cursor.getString(3),
//                cursor.getString(4),
//                convertedDate,
//                cursor.getString(6),
//                cursor.getString(7));
//
//        // return contact
//        return project;
//    }
//
//    public List<String> getSubjects() {
//
//        Cursor crs = mDb.rawQuery("SELECT " + KEY_SUBJECT + " FROM " + SQLITE_TABLE + " ", null);
//
//
//        List<String> array = new ArrayList<String>();
//        while (crs.moveToNext()) {
//            String uname = crs.getString(crs.getColumnIndex(KEY_SUBJECT));
//            array.add(uname);
//        }
//
//
//        return array;
//    }
//
//    private static class DatabaseHelper extends SQLiteOpenHelper {
//
//        DatabaseHelper(Context context) {
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        }
//
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            Log.w(TAG, CREATE_PROJECTS_TABLE);
//            db.execSQL(CREATE_PROJECTS_TABLE);
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
//            onCreate(db);
//        }
//    }
//}
//
//
