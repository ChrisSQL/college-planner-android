/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.chris.collegeplanner.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.UserAdapter;
import com.chris.collegeplanner.controller.AppConfig;
import com.chris.collegeplanner.controller.AppController;
import com.chris.collegeplanner.helper.SQLiteHandler;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.model.College;
import com.chris.collegeplanner.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.http.HttpResponse;

public class RegisterActivity extends Activity {
    public static final String MyPREFERENCES = "MySettings";
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String collegesURL = "http://chrismaher.info/AndroidProjects2/colleges.php";
    private static final String coursesURL = "http://chrismaher.info/AndroidProjects2/courses.php";
    AutoCompleteTextView college, course;
    List<String> collegesArray = new ArrayList<>();
    List<String> courseArray = new ArrayList<>();
    List<HashMap<String, String>> fillCollegesArray, fillCoursesArray;
    SharedPreferences sharedpreferences;
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputEmail, inputDisplayName, inputPhoneNumber;
    private EditText inputPassword, inputConfirmPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private UserAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private User user;
    private String email, password, confirmPassword, displayName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Offline Projects
        dbHelper = new UserAdapter(this);
        dbHelper.open();
        user = new User();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        inputDisplayName = (EditText) findViewById(R.id.displayName);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);


//        inputPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
//
//        college = (AutoCompleteTextView) findViewById(R.id.college);
//        college.setThreshold(1);
//        course = (AutoCompleteTextView) findViewById(R.id.course);
//        course.setThreshold(1);

        //       getWebData();


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity

            Toast.makeText(getApplicationContext(), session.getUserEmail(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this, SummaryActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                confirmPassword = inputConfirmPassword.getText().toString();
//                String collegeText = college.getText().toString();
//                String courseText = course.getText().toString();
                displayName = inputDisplayName.getText().toString();
//                String phoneNumber = inputPhoneNumber.getText().toString();

                // Check if college exists already

//                if(!collegeText.isEmpty()){
//
//
//
//                }


                if (confirmPassword.equals(password)) {

                    // Register User

                    if (!confirmPassword.isEmpty() && !email.isEmpty() && !password.isEmpty() && !displayName.isEmpty()) {
//                        registerCourse(courseText);
//                        registerCollege(collegeText);

                        user.setEmail(email);
                        user.setPassword(password);
                        user.setName(displayName);
                        user.setRegistrationDate(new Date());


//                            createUser(email, password, confirmPassword, displayName);
//                            registerUser(email, password, confirmPassword, displayName);

                        if (isNetworkAvailable()) {

                            Log.d("DETAILS : ", email + password + displayName);

                            registerUser(email, password, displayName);

                        }

                        dbHelper.createUser(user);
                        session.createLoginSession(email);
                        Intent intent = new Intent(RegisterActivity.this, SummaryActivity.class);
                        startActivity(intent);
                        finish();


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter your details!", Toast.LENGTH_LONG)
                                .show();
                    }


                } else {

                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();


                }

            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void createUser(String email, String password, String confirmPassword, String displayName) {


    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String email, final String password, final String nameIn) {


        // Tag used to cancel the request
        String tag_string_req = "register";

        pDialog.setMessage("Registering ...");
        showDialog();


        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER_2, new Response.Listener<String>() {

//

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                // hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");

                        String email = user.getString("email");
                        //    String confirmPassword = user.getString("confirmPassword");

                        // Inserting row in users table
//                                db.addUser(email, uid, nameIn);


                        // Launch login activity


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("email", email);
                params.put("password", password);
                params.put("name", nameIn);


                return params;
            }

        };

        // Adding request to request queue
        // session.createLoginSession(email);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void registerCollege(final String collegeIn) {


        // Tag used to cancel the request
        String tag_string_req = "req_college";

        pDialog.setMessage("Registering ...");
        showDialog();


        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

//

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "College Response: " + response.toString());
                // hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
//                        // User successfully stored in MySQL
//                        // Now store the user in sqlite
//                        String uid = jObj.getString("uid");
//                        JSONObject user = jObj.getJSONObject("user");
//                        String email = user.getString("email");
//                        // Inserting row in users table
//                        db.addUser(email, uid);
//                        // Launch login activity
//                        Intent intent = new Intent( RegisterActivity.this, SummaryActivity.class);
//                        startActivity(intent);
//                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
//                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "college");
                params.put("college", collegeIn);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void registerCourse(final String courseIn) {


        // Tag used to cancel the request
        String tag_string_req = "req_course";

        pDialog.setMessage("Registering ...");
        showDialog();


        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

//

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Course Response: " + response.toString());
                //               hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
//                        // User successfully stored in MySQL
//                        // Now store the user in sqlite
//                        String uid = jObj.getString("uid");
//                        JSONObject user = jObj.getJSONObject("user");
//                        String email = user.getString("email");
//                        // Inserting row in users table
//                        db.addUser(email, uid);
//                        // Launch login activity
//                        Intent intent = new Intent( RegisterActivity.this, SummaryActivity.class);
//                        startActivity(intent);
//                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
//                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "course");
                params.put("course", courseIn);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void getWebData() {

//        addCollegesToListBackgroundTask task = new addCollegesToListBackgroundTask();
//        task.execute(new String[]{collegesURL});

//        addCoursesToListBackgroundTask task2 = new addCoursesToListBackgroundTask();
//        task2.execute(new String[]{coursesURL});


    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    // Method to check if device has internet connection.
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class addCollegesToListBackgroundTask extends AsyncTask<String, Void, String> {

        private String jsonResult;


        @Override
        protected String doInBackground(String... params) {

            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListMaker();
        }

        public void ListMaker() {

            List<Map<String, String>> projectList = new ArrayList<Map<String, String>>();
            fillCollegesArray = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"collegeName"};
            int[] to = new int[]{R.id.collegeName};


        }

    }

    class addCoursesToListBackgroundTask extends AsyncTask<String, Void, String> {

        private String jsonResult;


        @Override
        protected String doInBackground(String... params) {

            return new String("");

        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListMaker();
        }

        public void ListMaker() {

            List<Map<String, String>> projectList = new ArrayList<Map<String, String>>();
            fillCoursesArray = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"courseName"};
            int[] to = new int[]{R.id.courseName};


            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("courses");

                College colleges = new College();


                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    colleges.setCollegeName(jsonChildNode.optString("courseName"));


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("courseName", colleges.getCollegeName());
                    courseArray.add(map.get("courseName"));


                    fillCoursesArray.add(map);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
            }


            //   adapter = new SimpleAdapter(getApplicationContext(), fillCoursesArray, R.layout.layout_college_list, from, to);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_dropdown_item_1line, courseArray);

            course.setAdapter(adapter2);


        }


    }
}
