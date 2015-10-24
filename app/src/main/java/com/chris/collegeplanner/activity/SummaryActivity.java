
package com.chris.collegeplanner.activity;

// Add email to deeplink

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.chris.collegeplanner.model.Project;
import com.chris.collegeplanner.model.User;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

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

public class SummaryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

//    final static public String PREFS_NAME = "PREFS_NAME";
//    private static String url = "";
//    HashMap map;
//    String pid;
//    ListView welcomeList;
//    SimpleAdapter adapter;
//    AlphaInAnimationAdapter animationAdapter;

//    private Boolean loggedIn;
//    String intentEmail;
//    private ProjectsAdapter db;
//    private Tracker mTracker;
//    private static final String urlDelete = "http://chrismaher.info/AndroidProjects2/project_delete.php";

    private static final int SELECT_PHOTO = 100;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 0;
    private static final int REQUEST_INVITE = 0;
    final static private String PREF_KEY_SHORTCUT_ADDED = "PREF_KEY_SHORTCUT_ADDED";
    public GoogleApiClient mGoogleApiClient;
    private TextView dueDateText;
    private int id;
    private RelativeLayout relLayout;
    private ImageView img;
    private ListView list;
    private Context context;
    private List<HashMap<String, String>> fillMaps;
    private BroadcastReceiver mDeepLinkReceiver;
    private String googleEmail;
    private User user;
    private ProjectsAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private MenuItem logout, share;
    private Button addButton, timetableButton;
    private LinearLayout welcomePanellayout;
    private int projectCount = 0;
    private EditText editText;
    private boolean searchVisible;
    private String extraEmail;

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


        // Check Deep Links
        if (savedInstanceState == null) {
            // No savedInstanceState, so it is the first launch of this activity
            Intent intent = getIntent();
            if (AppInviteReferral.hasReferral(intent)) {
                // In this case the referral data is in the intent launching the MainActivity,
                // which means this user already had the app installed. We do not have to
                // register the Broadcast Receiver to listen for Play Store Install information
                // launchDeepLinkActivity(intent);
                launchDeepLinkActivity(intent);
                // Set Referral email to sync projects

            }
        }

//        ShortcutIcon();
//        onCoachMark();

        // Setup User
        user = new User();

        // Search Text


        // Offline Projects
        dbHelper = new ProjectsAdapter(this);
        dbHelper.open();

        // Context
        context = getApplicationContext();

        // Initialise Variables
        list = (ListView) findViewById(R.id.SummaryListView);
        list.setOnItemClickListener(this);
        list.setTextFilterEnabled(true);
        searchVisible = false;


        addButton = (Button) findViewById(R.id.welcomeAddButton);
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, TimeTableWebView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(new Intent(SummaryActivity.this, TimeTableWebView.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            }
        });
        timetableButton = (Button) findViewById(R.id.welcomeTimetableButton);
        timetableButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, AddNewProjectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(new Intent(SummaryActivity.this, AddNewProjectActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            }
        });

        welcomePanellayout = (LinearLayout) this.findViewById(R.id.welcomePanel);
        if (projectCount == 0) {
            welcomePanellayout.setVisibility(LinearLayout.VISIBLE);
        } else {
            welcomePanellayout.setVisibility(LinearLayout.INVISIBLE);
        }



        img = (ImageView) findViewById(R.id.fullscreen_content);
        relLayout = (RelativeLayout) findViewById(R.id.RelBackGround);

        // Get projects from SQLite
        getOfflineProjects();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, ViewSingleProject.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_schedule, menu);
        logout = menu.findItem(R.id.logout);
        share = menu.findItem(R.id.action_group);

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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, AddNewProjectActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        finish();

    }

    // Launches The ADD NEW Screen
    public void addNewEntryIntentClick(View view) {

        Intent intent = new Intent(SummaryActivity.this, AddNewProjectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, AddNewProjectActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        finish();

    }

    // Launches The TimeTable Screen
    public void launchSchedule(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, TimeTableWebView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, TimeTableWebView.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

    }

    // Launches The Group Notes Screen
    public void launchGroupNotesScreen(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, GroupNotesActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("course", user.getCourse());
        intent.putExtra("email", user.getEmail());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, GroupNotesActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


    }

    // Launches The Settings Screen
    public void launchSettings(MenuItem item) {
//
//        Intent intent = new Intent(SummaryActivity.this, SettingsActivity.class);
//        startActivity(intent);

    }

    // handles the Pictures selected from Gallery or Camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                showMessage(getString(R.string.send_failed));
            }
        }

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
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

        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

//        if (requestCode == REQUEST_INVITE) {
//            if (resultCode == RESULT_OK) {
//                // Check how many invitations were sent and log a message
//                // The ids array contains the unique invitation ids for each invitation sent
//                // (one for each contact select by the user). You can use these for analytics
//                // as the ID will be consistent on the sending and receiving devices.
//                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
//                Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
//            } else {
//                // Sending failed or it was canceled, show failure message to the user
//                showMessage(getString(R.string.send_failed));
//            }
//        }

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }


    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), "Message : " + msg, Toast.LENGTH_LONG).show();
    }

    public void logoutUserProxy(MenuItem item) {

        logoutUser();
    }

    public void loginUserProxy(MenuItem item) {

        // loginUser();
        signInWithGplus();
    }

    public void addNewProject(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, AddNewProjectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, AddNewProjectActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

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
        projectCount = cursor.getCount();

        // Show Welcome Panel if there are no projects in list //

        if (projectCount == 0) {
            welcomePanellayout.setVisibility(LinearLayout.VISIBLE);
        } else {
            welcomePanellayout.setVisibility(LinearLayout.INVISIBLE);
        }

        /////////////////////////////////////////////////////////

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

                    // Calculate days left till project due
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

        dataAdapter.setStringConversionColumn(cursor.getColumnIndex("ProjectSubject"));
        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {
                    return dbHelper.fetchAllProjects();
                } else {
                    return dbHelper.fetchProjectsByName("" + constraint);

                }
            }
        });

        editText = (EditText) findViewById(R.id.search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setVisibility(View.GONE);

    }

    private void logoutUser() {

    }

    private void loginUser() {


        // Launching the login activity
        Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(SummaryActivity.this, ViewProjectActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

    }

    private void ShortcutIcon() {

        // Checking if ShortCut was already added
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean shortCutWasAlreadyAdded = sharedPreferences.getBoolean(PREF_KEY_SHORTCUT_ADDED, false);
        if (shortCutWasAlreadyAdded) return;

        Intent shortcutIntent = new Intent(getApplicationContext(), SummaryActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "College Planner");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);

        // Remembering that ShortCut was already added
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_KEY_SHORTCUT_ADDED, true);
        editor.commit();


    }

    public void onCoachMark() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.coach_mark);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        View masterView = dialog.findViewById(R.id.coach_mark_master_view);
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mSignInClicked = false;
        // Get user's information
        getProfileInformation();
        // Update the UI after signin
        updateUI(true);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        updateUI(false);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    /**
     * Sign-in into google
     */
    public void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();

        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     */
    private void updateUI(boolean isSignedIn) {

        logout.setVisible(false);
        share.setVisible(true);

    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                googleEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString("personName", personName);
                editor.putString("email", googleEmail);
                editor.apply();


                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + googleEmail
                        + ", Image: " + personPhotoUrl);


//                SuperToast superToast = new SuperToast(getActivity());
//                superToast.setDuration(SuperToast.DURATION_LONG);
//                superToast.setText("Hello world!");
//                superToast.setIconResource(R.drawable.image, SuperToast.IconPosition.LEFT);
//                superToast.show();


//                txtName.setText(personName);
//                txtEmail.setText(email);


                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X

//                personPhotoUrl = personPhotoUrl.substring(0,
//                        personPhotoUrl.length() - 2)
//                        + PROFILE_PIC_SIZE;
//
//                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

//                Intent intent = new Intent(SignIn.this, SummaryActivity.class);
//                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        registerDeepLinkReceiver();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        unregisterDeepLinkReceiver();
    }

    public void launchShareProjects(MenuItem item) {

        if (googleEmail == null) {
            googleEmail = "na";
        }

        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse("email-" + googleEmail))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);

    }

    private void registerDeepLinkReceiver() {
        // Create local Broadcast receiver that starts DeepLinkActivity when a deep link
        // is found
        mDeepLinkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AppInviteReferral.hasReferral(intent)) {
                    launchDeepLinkActivity(intent);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(getString(R.string.action_deep_link));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDeepLinkReceiver, intentFilter);
    }

    private void unregisterDeepLinkReceiver() {
        if (mDeepLinkReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeepLinkReceiver);
        }
    }

    private void updateInvitationStatus(Intent intent) {
        String invitationId = AppInviteReferral.getInvitationId(intent);

        // Note: these  calls return PendingResult(s), so one could also wait to see
        // if this succeeds instead of using fire-and-forget, as is shown here
        if (AppInviteReferral.isOpenedFromPlayStore(intent)) {
            AppInvite.AppInviteApi.updateInvitationOnInstall(mGoogleApiClient, invitationId);
        }

        // If your invitation contains deep link information such as a coupon code, you may
        // want to wait to call `convertInvitation` until the time when the user actually
        // uses the deep link data, rather than immediately upon receipt
        AppInvite.AppInviteApi.convertInvitation(mGoogleApiClient, invitationId);
    }

    private void launchDeepLinkActivity(Intent intent) {

        Log.d(TAG, "launchDeepLinkActivity:" + intent);
        Intent newIntent = new Intent(intent).setClass(this, SummaryActivity.class);
        startActivity(newIntent);

    }

    public void setSearchVisibility(MenuItem item) {

        if (searchVisible == false) {

            editText.setVisibility(View.VISIBLE);
            searchVisible = true;
        } else {
            editText.setVisibility(View.GONE);
            searchVisible = false;
        }

    }
}// Main Program Ends..









