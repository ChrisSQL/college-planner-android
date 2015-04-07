package com.chris.collegeplanner.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();
    List<HashMap<String, String>> fillMaps;

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name

	private static final String PREF_NAME = "MySettings";
	private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_COURSE = "course";
    public static final String KEY_COLLEGE = "college";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setLogin(boolean isLoggedIn) {

		editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}
	
	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGEDIN, false);
	}

    public void createLoginSession(String email, String name, String phone) {
        // Storing login value as TRUE
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PHONE, phone);

        // commit changes
        editor.commit();
    }

    public void createLoginSession(String email) {

        // Storing login value as TRUE
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // get details from Database

        //   String url = "http://chrismaher.info/AndroidProjects2/project_details.php?email="+session.getUserEmail()+"";


        // commit changes
        editor.commit();
    }



    public void setLoginCourse(String course){

        // Storing email in pref
        editor.putString(KEY_COURSE, course);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public String getUserEmail(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return pref.getString(KEY_EMAIL, null);
    }

    /**
     * Get stored session data
     * */
    public String getUserCourse(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_COURSE, pref.getString(KEY_COURSE, null));

        // return user
        return pref.getString(KEY_COURSE, null);
    }

    /**
     * Get stored session data
     */
    public String getUserName() {
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // return user
        return pref.getString(KEY_NAME, null);
    }

    /**
     * Get stored session data
     */
    public String getUserPhone() {
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

        // return user
        return pref.getString(KEY_PHONE, null);
    }
}
