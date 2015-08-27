package com.chris.collegeplanner.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.helper.SQLiteHandler;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.model.GroupNote;
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

public class GroupNotesActivity extends ActionBarActivity {

    private static String url = "";
    List<HashMap<String, String>> fillMaps;
    private int id = 0;
    SimpleAdapter adapter;
    AlphaInAnimationAdapter animationAdapter;
    ListView list;
    private SQLiteHandler db;
    private SessionManager session;
    public static final String MyPREFERENCES = "MySettings" ;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notes);

        // Session manager
        session = new SessionManager(getApplicationContext());
        setTitle(session.getUserCourse()+" Group Notes");
        String email = session.getUserEmail();
        list = (ListView) findViewById(R.id.groupNotesListView1);

     //   Toast.makeText(getApplicationContext(), session.getUserCourse(), Toast.LENGTH_LONG).show();


        try {
            getWebData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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

        String course = URLEncoder.encode(session.getUserCourse(), "utf-8");
        url = String.format("http://chrismaher.info/AndroidProjects2/project_group_notes_details.php?course=%s", course);




        ReadAllProjectsBackgroundTask task = new ReadAllProjectsBackgroundTask();
        // passes values for the urls string array
        Log.d("Log...", url);
        task.execute(new String[]{url});


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
            pDialog.setMessage(session.getUserCourse()+"");
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


            adapter = new SimpleAdapter(getApplicationContext(), fillMaps, R.layout.group_notes_list_item, from, to);

            animationAdapter = new AlphaInAnimationAdapter(adapter);
            animationAdapter.setAbsListView(list);
            list.setAdapter(animationAdapter);




            //  list.setAdapter(adapter);



        }

        ;


    }


}
