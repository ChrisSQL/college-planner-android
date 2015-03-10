// Chris Maher 20059304
// Multiple Screens - 5% Yes
// Validation - 5% Yes - On ServerSide and in Java
// App Design - 10%
// UI Design/Usability - 25%
// Functionality - 30%
// Extras - 25% - WebServer - SMS - Push Notification


package com.chris.collegeplanner.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.helper.JSONParser;
import com.chris.collegeplanner.helper.SQLiteHandler;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.objects.Project;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryActivity extends ActionBarActivity {

    private static String url = "";
    private static final String urlDelete = "http://chrismaher.info/AndroidProjects2/project_delete.php";
    private static final int SELECT_PHOTO = 100;
    ListView list;
    Context context;
    SimpleAdapter adapter;
    List<HashMap<String, String>> fillMaps;
    HashMap map;
    String[] projectArray;
    String pid;
    JSONParser jsonParser = new JSONParser();
    int id;
    RelativeLayout relLayout;
    ImageView img;
    private ProgressDialog pDialog;
    AlphaInAnimationAdapter animationAdapter;
    private SQLiteHandler db;
    private SessionManager session;
    public static final String MyPREFERENCES = "MySettings" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Session manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()){


//            Toast.makeText(getApplicationContext(), "User Login Status: " + session.getUserDetails(), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Syncing!", Toast.LENGTH_SHORT).show();
            getWebData();

        }


        // Context
        context = getApplicationContext();
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());

      //  url = "http://chrismaher.info/AndroidProjects2/project_details.php?email=" + session.getUserEmail();

      //  url = "http://chrismaher.info/AndroidProjects2/project_details.php";

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        projectArray = new String[7];

        // Initialise Variables
        list = (ListView) findViewById(R.id.SummaryListView);
        registerForContextMenu(list);
        img = (ImageView) findViewById(R.id.fullscreen_content);


        relLayout = (RelativeLayout) findViewById(R.id.RelBackGround);

        // Check if Internet is connected before syncing
        if (isNetworkAvailable()) {

        //    Toast.makeText(getApplicationContext(), "Online and Syncing!", Toast.LENGTH_SHORT).show();



        } else {

            Toast.makeText(getApplicationContext(), "Internet Connection Required!", Toast.LENGTH_LONG).show();
        }

        list.setTextFilterEnabled(true);
        registerForContextMenu(list);
        list.setLongClickable(true);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {


                // We know the View is a TextView so we can cast it
                TextView clickedView = (TextView) view;

                Toast.makeText(SummaryActivity.this, "Item with id ["+id+"] - Position ["+position+"] - Planet ["+clickedView.getText()+"]", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_schedule, menu);
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

    // Context Menu for Long Click on ListItems
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // We know that each row in the adapter is a Map
        map = (HashMap) adapter.getItem(aInfo.position);

        menu.setHeaderTitle(map.get("SubjectTextList") + "");

        menu.add(1, 1, 1, "Delete");
        menu.add(1, 2, 2, "Update");
        menu.add(1, 3, 3, "View");

    }

    // ActionListeners for the ContextItems
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Update") {

            if (isNetworkAvailable()) {
                pid = map.get("IdText") + "";
            } else {
                Toast.makeText(getApplicationContext(), "Internet Connection Required!", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(SummaryActivity.this, UpdateProjectActivity.class);
            intent.putExtra("ProjectID", map.get("IdText") + "");

            startActivity(intent);
        } else if (item.getTitle() == "Delete") {

            if (isNetworkAvailable()) {

                pid = map.get("IdText") + "";
                deleteProject();

                Intent intent = new Intent(SummaryActivity.this, SummaryActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), "Internet Connection Required!", Toast.LENGTH_LONG).show();
            }


        } else if (item.getTitle() == "View") {

            if (isNetworkAvailable()) {

                pid = map.get("IdText") + "";
                Intent intent = new Intent(SummaryActivity.this, ViewProjectActivity.class);
                //    Toast.makeText(getApplicationContext(), pid, Toast.LENGTH_LONG).show();
                intent.putExtra("ProjectID", pid);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), "Internet Connection Required!", Toast.LENGTH_LONG).show();
            }


        } else {
            return false;
        }
        return true;
    }

    // Method to check if device has internet connection.
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Method to start the List Fill Process
    private void getWebData() {

        String urlurl = "http://chrismaher.info/AndroidProjects2/project_details.php?email="+session.getUserDetails()+"";
        url = "http://chrismaher.info/AndroidProjects2/project_details.php?email=chrismaher.wit@gmail.com";
        url = urlurl;

        ReadAllProjectsBackgroundTask task = new ReadAllProjectsBackgroundTask();
        // passes values for the urls string array
        task.execute(new String[]{url});


    }

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

    // Method to start the Delete of an Individual listItem
    private void deleteProject() {


        DeleteProjectBackgroundTask task = new DeleteProjectBackgroundTask();
        // passes values for the urls string array
        task.execute(new String[]{url});

    }

    // Launches The ADD NEW Screen
    public void addNewEntryIntentClick(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, AddNewProjectActivity.class);
        startActivity(intent);

    }

    // Launches The TimeTable Screen
    public void launchSchedule(MenuItem item) {

        Intent intent = new Intent(SummaryActivity.this, TimeTableWebView.class);
        startActivity(intent);

    }

//    // Launches The Settings Screen
//    public void launchSettings(MenuItem item) {
//
//        Intent intent = new Intent(SummaryActivity.this, SettingsActivity.class);
//        startActivity(intent);
//
//    }

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

//    public void launchLoginScreen(MenuItem item) {
//
//        Intent intent = new Intent(SummaryActivity.this, LoginActivity2.class);
//        startActivity(intent);
//
//
//    }

    // Background Async Task to Delete Product
    class DeleteProjectBackgroundTask extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SummaryActivity.this);
            pDialog.setMessage("Deleting Project...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("ProjectID", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        urlDelete, "POST", params);

                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt("success");
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }

    // Background Async Task to Fill List with WebServer Data
    class ReadAllProjectsBackgroundTask extends AsyncTask<String, Void, String> {

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
        protected void onPostExecute(String result) {
            ListMaker();
        }

        public void ListMaker() {

            List<Map<String, String>> projectList = new ArrayList<Map<String, String>>();
            fillMaps = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"SubjectTextList", "DueDateTextList", "IdText", "ProjectTitleTextList", "WorthText", "DetailsTextList"};
            int[] to = new int[]{R.id.SubjectTextList, R.id.DueDateTextList, R.id.IdText, R.id.ProjectTitleTextList, R.id.WorthText, R.id.DetailsTextList};
            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("projects");

                Project project = new Project();


                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    project.setProjectID(jsonChildNode.optInt("ProjectID"));
                    id = jsonChildNode.optInt("ProjectID");

                    project.setProjectSubject(jsonChildNode.optString("ProjectSubject"));
                    project.setProjectDueDate(jsonChildNode.optString("ProjectDueDate"));
                    project.setProjectTitle(jsonChildNode.optString("ProjectTitle"));
                    project.setProjectWorth(jsonChildNode.optString("ProjectWorth"));
                    project.setProjectDetails(jsonChildNode.optString("ProjectDetails"));
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
                    map.put("IdText", "" + project.getProjectID());
                    map.put("ProjectTitleTextList", "" + ConvertStringToTitleCase(project.getProjectTitle()));
                    map.put("WorthText", "" + "(" + project.getProjectWorth() + "%)");
                    map.put("DetailsTextList", "" + s);

                    fillMaps.add(map);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            adapter = new SimpleAdapter(getApplicationContext(), fillMaps, R.layout.summary_list_item, from, to);

            animationAdapter = new AlphaInAnimationAdapter(adapter);
            animationAdapter.setAbsListView(list);
            list.setAdapter(animationAdapter);




          //  list.setAdapter(adapter);



        }

        ;


    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}





