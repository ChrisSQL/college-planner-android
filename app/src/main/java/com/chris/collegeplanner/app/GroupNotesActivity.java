package com.chris.collegeplanner.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.helper.SQLiteHandler;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.objects.GroupNote;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notes);

        setTitle("Group Notes");


        // Session manager
        session = new SessionManager(getApplicationContext());
        list = (ListView) findViewById(R.id.groupNotesListView1);


        getWebData();
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
    private void getWebData() {

        String urlurl = "http://chrismaher.info/AndroidProjects2/project_group_notes_details.php?email="+session.getUserDetails()+"";
        url = "http://chrismaher.info/AndroidProjects2/project_group_notes_details.php?email=chrismaher.wit@gmail.com";
        url = urlurl;

        ReadAllProjectGroupNotesBackgroundTask task = new ReadAllProjectGroupNotesBackgroundTask();
        // passes values for the urls string array
        task.execute(new String[]{url});


    }

    public void addNewGroupNote(MenuItem item) {

        Intent intent = new Intent(GroupNotesActivity.this, AddNewGroupNoteActivity.class);
        startActivity(intent);

    }



    class ReadAllProjectGroupNotesBackgroundTask extends AsyncTask<String, Void, String> {

        // Send session.getUserDetails() to filter List by LoggedIn user

        private String jsonResult;



        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getApplicationContext(), "Syncing with Server...", Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onPostExecute(String result) {
            ListMaker();
        }

        public void ListMaker() {

            List<Map<String, String>> projectList = new ArrayList<Map<String, String>>();
            fillMaps = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"GroupNoteAuthor", "GroupNoteDatePosted", "GroupNoteText"};
            int[] to = new int[]{R.id.Author, R.id.GroupNotesDate, R.id.GroupNotesText};
            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("GroupNotes");

                GroupNote groupNote = new GroupNote();


                for (int i = 0; i < jsonMainNode.length(); i++) {



                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    groupNote.setGroupNoteId(jsonChildNode.optString("GroupNoteId"));
                    groupNote.setGroupNoteAuthor(jsonChildNode.optString("GroupNoteAuthor"));
                    groupNote.setGroupNoteDatePosted(jsonChildNode.optString("GroupNoteDatePosted"));
                    groupNote.setGroupNoteText(jsonChildNode.optString("GroupNoteText"));


                    Toast.makeText(getApplicationContext(), "Notes visible to your class only.", Toast.LENGTH_SHORT).show();



                    //        ------------------------------------------------------------------------------------------


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("GroupNoteId", groupNote.getGroupNoteId());
                    map.put("GroupNoteAuthor", groupNote.getGroupNoteAuthor());
                    map.put("GroupNoteDatePosted", groupNote.getGroupNoteDatePosted());
                    map.put("GroupNoteText", "" + groupNote.getGroupNoteText());

                    fillMaps.add(map);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
            }


            adapter = new SimpleAdapter(getApplicationContext(), fillMaps, R.layout.group_notes_list_item, from, to);


            list.setAdapter(adapter);



        }

        ;


    }


}
