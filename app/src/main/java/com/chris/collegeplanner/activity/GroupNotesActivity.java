package com.chris.collegeplanner.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chris.collegeplanner.R;
import com.chris.collegeplanner.controller.AppController;
import com.chris.collegeplanner.helper.SQLiteHandler;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.model.GroupNote;
import com.chris.collegeplanner.model.User;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

//import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupNotesActivity extends AppCompatActivity {

    private static String url = "";
    List<HashMap<String, String>> fillMaps;
    private int id = 0;
    SimpleAdapter adapter;
    AlphaInAnimationAdapter animationAdapter;
    ListView list;
    private SQLiteHandler db;
    private SessionManager session;
    public static final String MyPREFERENCES = "MySettings";
    private ProgressDialog pDialog;
    private User user;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notes);

        user = new User();

        // Session manager
        session = new SessionManager(getApplicationContext());
        setTitle(session.getUserCourse() + " Group Notes");
        String email = session.getUserEmail();
        list = (ListView) findViewById(R.id.groupNotesListView1);

        //   Toast.makeText(getApplicationContext(), session.getUserCourse(), Toast.LENGTH_LONG).show();

        // Get Intent extra sent through
        getExtras(savedInstanceState);


        try {


            getGroupNotes();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getExtras(Bundle savedInstanceState) {


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {

                Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_SHORT).show();
            } else {
                user.setCourse(extras.getString("course"));
                user.setName(extras.getString("name"));
                user.setEmail(extras.getString("email"));
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Results", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_notes, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_group, menu);
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

    // Method to start the List Fill Process
    private void getWebData() throws UnsupportedEncodingException {

        // Send in an email and get a course

        String course = URLEncoder.encode(user.getCourse(), "utf-8");
        Toast.makeText(getApplicationContext(), "Course : " + course, Toast.LENGTH_SHORT).show();
        url = String.format("http://chrismaher.info/college_planner/project_group_notes_details.php?course=%s", course);

//        ReadAllProjectsBackgroundTask task = new ReadAllProjectsBackgroundTask();
//        // passes values for the urls string array
//        Log.d("Log...", url);
//        task.execute(new String[]{url});


    }

    private void getGroupNotes() throws UnsupportedEncodingException {

        List<Map<String, String>> projectList = new ArrayList<Map<String, String>>();
        fillMaps = new ArrayList<HashMap<String, String>>();
        final String[] from = new String[]{"GroupNoteAuthor", "GroupNoteDatePosted", "GroupNoteText", "GroupNoteSubject"};
        final int[] to = new int[]{R.id.Author, R.id.GroupNotesDate, R.id.GroupNotesText, R.id.SubjectText};

        // Tag used to cancel the request
        String tag_string_req = "req_projects";

        String course = URLEncoder.encode("Software Systems Development", "utf-8");
        url = String.format("http://chrismaher.info/college_planner/project_group_notes_details.php?course=%s", course);


        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jsonMainNode = jObj.optJSONArray("GroupNotes");

                    //        boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (jsonMainNode.length() > 0) {

                        GroupNote groupNote = new GroupNote();

                        try {
                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);


                                groupNote.setGroupNoteAuthor(jsonChildNode.optString("GroupNoteAuthor"));
                                groupNote.setGroupNoteDatePosted(jsonChildNode.optString("GroupNoteDatePosted"));
                                groupNote.setGroupNoteText(jsonChildNode.optString("GroupNoteText"));
                                groupNote.setGroupNoteSubject(jsonChildNode.optString("GroupNoteSubject"));


                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("GroupNoteAuthor", groupNote.getGroupNoteAuthor());
                                map.put("GroupNoteDatePosted", groupNote.getGroupNoteDatePosted());
                                map.put("GroupNoteText", "" + groupNote.getGroupNoteText());
                                map.put("GroupNoteSubject", groupNote.getGroupNoteSubject());

                                fillMaps.add(map);

                            }


                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
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

                adapter = new SimpleAdapter(getApplicationContext(), fillMaps, R.layout.group_notes_list_item, from, to);

                animationAdapter = new AlphaInAnimationAdapter(adapter);
                animationAdapter.setAbsListView(list);
                list.setAdapter(animationAdapter);

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

                return params;
            }

        };

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

//        String urlUser = "http://chrismaher.info/AndroidProjects2/user_details.php?email=" + session.getUserEmail() + "";
//        Log.d("URLUSER : ", urlUser);
        //    setSessionDetails task = new setSessionDetails();
        // passes values for the urls string array
        //    task.execute(new String[]{urlUser});


    }

    public void addNewGroupNote(MenuItem item) {

        Intent intent = new Intent(GroupNotesActivity.this, AddNewGroupNoteActivity.class);
        startActivity(intent);

    }


    // Background Async Task to Fill List with WebServer Data
    class ReadAllProjectsBackgroundTask extends AsyncTask<String, Void, String> {

        // Send session.getUserEmail() to filter List by LoggedIn user


        private String jsonResult;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            super.onPreExecute();
            pDialog = new ProgressDialog(GroupNotesActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

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
            pDialog.dismiss();


        }

        public void ListMaker() {


            List<Map<String, String>> projectList = new ArrayList<Map<String, String>>();
            fillMaps = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"GroupNoteAuthor", "GroupNoteDatePosted", "GroupNoteText", "GroupNoteSubject"};
            int[] to = new int[]{R.id.Author, R.id.GroupNotesDate, R.id.GroupNotesText, R.id.SubjectText};


            if (jsonResult != null) {

                try {

                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("GroupNotes");

                    GroupNote project = new GroupNote();


                    for (int i = 0; i < jsonMainNode.length(); i++) {
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        project.setGroupNoteAuthor(jsonChildNode.optString("GroupNoteAuthor"));
                        project.setGroupNoteDatePosted(jsonChildNode.optString("GroupNoteDatePosted"));
                        project.setGroupNoteText(jsonChildNode.optString("GroupNoteText"));
                        project.setGroupNoteSubject(jsonChildNode.optString("GroupNoteSubject"));


                        //        ------------------------------------------------------------------------------------------


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("GroupNoteAuthor", project.getGroupNoteAuthor());
                        map.put("GroupNoteDatePosted", project.getGroupNoteDatePosted());
                        map.put("GroupNoteText", "" + project.getGroupNoteText());
                        map.put("GroupNoteSubject", "" + project.getGroupNoteSubject());

                        fillMaps.add(map);

                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
                }

            } else {

                Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();

            }


            adapter = new SimpleAdapter(getApplicationContext(), fillMaps, R.layout.group_notes_list_item, from, to);

            animationAdapter = new AlphaInAnimationAdapter(adapter);
            animationAdapter.setAbsListView(list);
            list.setAdapter(animationAdapter);


            //  list.setAdapter(adapter);


        }

        ;


    }


}
