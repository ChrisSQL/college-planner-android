
package com.chris.collegeplanner.activity;

// If Logged in and theres Internet connection download online projects and merge with offline projects then save all online again.

// If logged out just get projects from SQLIte

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.ProjectsAdapter;
import com.chris.collegeplanner.controller.AppConfig;
import com.chris.collegeplanner.controller.AppController;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.model.Project;
import com.chris.collegeplanner.model.User;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //  private static final String urlDelete = "http://chrismaher.info/AndroidProjects2/project_delete.php";
    private static final int SELECT_PHOTO = 100;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static String url = "";
    ListView list;
    Context context;
    SimpleAdapter adapter;
    List<HashMap<String, String>> fillMaps;
    HashMap map;
    String pid;
    int id;
    RelativeLayout relLayout;
    ImageView img;
    AlphaInAnimationAdapter animationAdapter;
    String extraEmail;
    private ProjectsAdapter db;
    private SessionManager session;
    private User user;
    private ProjectsAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private TextView dueDateText;

    // Method to convert Strings to Title Case for use in ListView
    public static String ConvertStringToTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        ShortcutIcon();

        // Setup User
        user = new User();

        // Get Intent extra sent through
        getExtras(savedInstanceState);

        user.setEmail(extraEmail);

        //       Toast.makeText(getApplicationContext(), "User logged in : " + extraEmail, Toast.LENGTH_LONG).show();

        // Offline Projects
        dbHelper = new ProjectsAdapter(this);
        dbHelper.open();

        // Start Session manager
        session = new SessionManager(getApplicationContext());

        // Context
        context = getApplicationContext();
        // session manager
        session = new SessionManager(getApplicationContext());

        // Initialise Variables
        list = (ListView) findViewById(R.id.SummaryListView);
        list.setOnItemClickListener(this);


        img = (ImageView) findViewById(R.id.fullscreen_content);
        relLayout = (RelativeLayout) findViewById(R.id.RelBackGround);

        // Check if Internet is connected and email is available before syncing
        if (isNetworkAvailable() && user.getEmail() != null) {

            getOfflineProjects();

//            getUserData(user.getEmail());
//         // TODO merge with offline projects - Upload SQLite to Cloud.
//             mergeSQLiteProjectsToCloud();
//            Toast.makeText(getApplicationContext(), "Syncing with Cloud.", Toast.LENGTH_LONG).show();

        } else {


            // Get projects from SQLite
            getOfflineProjects();


        }


    }

    private void getExtras(Bundle savedInstanceState) {


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                extraEmail = null;
            } else {
                extraEmail = extras.getString("email");
            }
        } else {
            extraEmail = (String) savedInstanceState.getSerializable("email");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        dataAdapter.getItem(position);
        Intent intent = new Intent(this, ViewSingleProject.class);
        intent.putExtra("id", (int) id);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_schedule, menu);
        MenuItem login = menu.findItem(R.id.login);
        MenuItem logout = menu.findItem(R.id.logout);
        if (session.isLoggedIn()) {
            login.setVisible(false);
            logout.setVisible(true);

        } else {
            login.setVisible(true);
            logout.setVisible(false);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to check if device has internet connection.
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Method to start the Delete of an Individual listItem
    private void deleteProject() {


//        DeleteProjectBackgroundTask task = new DeleteProjectBackgroundTask();
//        // passes values for the urls string array
//        task.execute(new String[]{url});

    }

    // Launches The ADD NEW Screen
    public void addNewEntryIntentClick(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, AddNewProjectActivity.class);
        intent.putExtra("email", extraEmail);
        startActivity(intent);
        finish();

    }

    // Launches The ADD NEW Screen
    public void addNewEntryIntentClick(View view) {

        Intent intent = new Intent(SummaryActivity.this, AddNewProjectActivity.class);
        startActivity(intent);

    }

    // Launches The TimeTable Screen
    public void launchSchedule(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, TimeTableActivity.class);
        startActivity(intent);

    }

    // Launches The Group Notes Screen
    public void launchGroupNotesScreen(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, GroupNotesActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("course", user.getCourse());
        intent.putExtra("email", user.getEmail());
        startActivity(intent);

    }

    // Launches The Settings Screen
    public void launchSettings(MenuItem item) {
//
//        Intent intent = new Intent(SummaryActivity.this, SettingsActivity.class);
//        startActivity(intent);

    }

    // handles the Pictures selected from Gallery or Camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {

                        imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 50, bs);


                        Intent intent = new Intent(SummaryActivity.this, TimeTableActivity.class);

                        intent.putExtra("byteArray", bs.toByteArray());

                        startActivity(intent);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
        }


    }

    public void logoutUserProxy(MenuItem item) {

        logoutUser();
    }

    public void loginUserProxy(MenuItem item) {

        loginUser();
    }

    public void addNewProject(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, AddNewProjectActivity.class);
        startActivity(intent);


    }

    private void getOnlineProjects(final String email) {

        List<Map<String, String>> projectList = new ArrayList<Map<String, String>>();
        fillMaps = new ArrayList<HashMap<String, String>>();
        final String[] from = new String[]{"SubjectTextList", "DueDateTextList", "IdText", "ProjectTitleTextList", "WorthText", "DetailsTextList"};
        final int[] to = new int[]{R.id.SubjectTextList, R.id.DueDateTextList, R.id.IdText, R.id.ProjectTitleTextList, R.id.WorthText, R.id.DetailsTextList};

        // Tag used to cancel the request
        String tag_string_req = "req_projects";

        String projectUrl = "http://chrismaher.info/college_planner/project_details.php?email=" + email + "";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                projectUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jsonMainNode = jObj.optJSONArray("projects");

                    //        boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (jsonMainNode.length() > 0) {

                        Project project = new Project();

                        try {
                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                project.set_id(jsonChildNode.optInt("ProjectID"));
                                id = jsonChildNode.optInt("ProjectID");

                                project.setProjectSubject(jsonChildNode.optString("ProjectSubject"));
                                project.setProjectDueDate(jsonChildNode.optString("ProjectDueDate"));
                                project.setProjectTitle(jsonChildNode.optString("ProjectTitle"));
                                project.setProjectWorth(jsonChildNode.optString("ProjectWorth"));
                                project.setProjectDetails(jsonChildNode.optString("ProjectDetails"));
                                project.setProjectType(jsonChildNode.optString("ProjectType"));
                                String s = project.getProjectDetails().substring(0, Math.min(project.getProjectDetails().length(), 28)) + "...";


                                //        ------------------------------------------------------------------------------------------

                                // Get difference between today and dueDate in Days
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = project.getProjectDueDate();
                                Date today = new Date();
                                String days = (Days.daysBetween(new DateTime(today), new DateTime(date)).getDays()) + "";
                                if (Integer.valueOf(days) < 0) {
                                    days = "-";

                                }

                                //        ------------------------------------------------------------------------------------------


                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("SubjectTextList", project.getProjectSubject());
                                map.put("DueDateTextList", "" + days);
                                map.put("IdText", "" + project.get_id());
                                map.put("ProjectTitleTextList", "" + ConvertStringToTitleCase(project.getProjectTitle()));
                                map.put("WorthText", "" + "(" + project.getProjectWorth() + "%)");
                                map.put("DetailsTextList", "" + s);

                                fillMaps.add(map);

                                String subject = project.getProjectSubject();
                                Date dateDue = project.getProjectDueDate();
                                String details = s;
                                String type = project.getProjectType();

//                                if(dbHelper.fetchProjectBySubjectDetails(subject, details) == false){
//
//                                    dbHelper.createProject(
//                                            0,
//                                            subject,
//                                            type,
//                                            ConvertStringToTitleCase(project.getProjectTitle()),
//                                            "" + "(" + project.getProjectWorth() + "%)",
//                                            days,
//                                            s,
//                                            "");
//
//                                }


                            }


                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

//                adapter = new SimpleAdapter(getApplicationContext(), fillMaps, R.layout.summary_list_item, from, to);
//
//                animationAdapter = new AlphaInAnimationAdapter(adapter);
//                animationAdapter.setAbsListView(list);
//                list.setAdapter(animationAdapter);

                Cursor cursor = dbHelper.fetchAllProjects();
                dataAdapter = new SimpleCursorAdapter(SummaryActivity.this, R.layout.summary_list_item, cursor, from, to, 0);

                list.setAdapter(dataAdapter);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        session.createLoginSession(email);
        session.setLoginCourse("Software Systems Development");
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

//        String urlUser = "http://chrismaher.info/AndroidProjects2/user_details.php?email=" + session.getUserEmail() + "";
//        Log.d("URLUSER : ", urlUser);
        //    setSessionDetails task = new setSessionDetails();
        // passes values for the urls string array
        //    task.execute(new String[]{urlUser});


    }

    private void getOfflineProjects() {

////        //Clean all data
//        dbHelper.deleteAllProjects();
////        //Add some data
//        dbHelper.insertSomeProjects();

//      final String[] from = new String[]{"SubjectTextList", "DueDateTextList", "IdText", "ProjectTitleTextList", "WorthText", "DetailsTextList"};
//      final int[] to = new int[]{R.id.SubjectTextList, R.id.DueDateTextList, R.id.IdText, R.id.ProjectTitleTextList, R.id.WorthText, R.id.DetailsTextList};
//


        final String[] from = new String[]{"_id", "ProjectSubject", "ProjectTitle", "ProjectWorth", "ProjectDueDate", "ProjectDetails"};
        final int[] to = new int[]{R.id.IdText, R.id.SubjectTextList, R.id.ProjectTitleTextList, R.id.WorthText, R.id.DueDateTextList, R.id.DetailsTextList};

        Cursor cursor = dbHelper.fetchAllProjects();
        dataAdapter = new SimpleCursorAdapter(context, R.layout.summary_list_item, cursor, from, to, 0);

        dueDateText = (TextView) findViewById(R.id.DueDateTextList);

        dataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 5) {


                    // Chosen Date as Date
                    Date chosenDate = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String dtStart = cursor.getString(columnIndex);
                    try {
                        chosenDate = format.parse(dtStart);
                        System.out.println(chosenDate);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //                    Date today = new Date();
                    String days = (Days.daysBetween(new DateTime(new Date()), new DateTime(chosenDate)).getDays()) + "";
                    if (Integer.valueOf(days) < 0) {
                        days = "-";

                    }

                    ((TextView) view).setText(days);


                    return true;
                } else if (columnIndex == 4) {

                    String worthFormat = "(" + cursor.getString(columnIndex) + "%)";
                    ((TextView) view).setText(worthFormat);

                    return true;

                } else {
                    return false;
                }
            }
        });


        list.setAdapter(dataAdapter);


    }

    private void logoutUser() {

        session.setLogin(false);
        user.setEmail("");

        //    db.deleteAllProjects();
        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();

        // Launching the login activity
        Intent intent = new Intent(SummaryActivity.this, SummaryActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        session.setLogin(true);

        // Launching the login activity
        Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getUserData(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_USERDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        user.setName(jObj.getString("name"));
                        user.setEmail(jObj.getString("email"));
                        user.setCourse(jObj.getString("course"));

                        Toast.makeText(getApplicationContext(), user.getCourse(), Toast.LENGTH_LONG).show();


                        getOnlineProjects(user.getEmail());


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "No projects saved online.";
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "get_user_by_email");
                params.put("email", email);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void viewProject(int id) {

        Intent intent = new Intent(SummaryActivity.this, ViewProjectActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

    }

    public void shareList(MenuItem item) {

        if (extraEmail == null || extraEmail.equals("")) {

            Toast.makeText(getApplicationContext(), "Login to send list.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
            startActivity(intent);

        } else {

//            Intent intent = new Intent(SummaryActivity.this, ShareActivity.class);
//            startActivity(intent);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("Classmates Email?");

            final EditText email = new EditText(this);
            email.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            email.setHint("Email...");

            alert.setView(email);

            alert.setPositiveButton("Ok", null);

            alert.setNegativeButton("Cancel", null);

            alert.show();


        }

    }

    private void ShortcutIcon() {

        Intent shortcutIntent = new Intent(getApplicationContext(), SummaryActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "College Planner");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }


}// Main Program Ends..









