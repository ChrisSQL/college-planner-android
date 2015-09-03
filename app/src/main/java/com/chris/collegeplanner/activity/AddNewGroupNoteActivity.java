package com.chris.collegeplanner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.model.Subject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddNewGroupNoteActivity extends ActionBarActivity {


    private static final String subjectsURL = "http://chrismaher.info/AndroidProjects2/subjects.php";
    List<String> subjectsArray = new ArrayList<>();
    AutoCompleteTextView subjectGroupText;
    List<HashMap<String, String>> fillSubjectsArray;
    String subject, details, projectEmail, groupNoteCourse, date;
    private SessionManager session;
    private EditText detailsText;
    private Button saveButton2;
    private String urlUpload = "http://chrismaher.info/AndroidProjects2/group_note_upload.php";
    // Progress Dialog
    private ProgressDialog pDialog;
    // This is the date picker used to select the date for our notification
    private DatePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group_note);
        setTitle("Add New Group Note");

        // Instantiate
        //    details = (EditText)   findViewById(R.id.Details);

        subjectGroupText = (AutoCompleteTextView) findViewById(R.id.SubjectSpinnerGroup);
        subjectGroupText.setThreshold(1);
        session = new SessionManager(getApplicationContext());
        detailsText = (EditText) findViewById(R.id.NoteDetails);
        saveButton2 = (Button) findViewById(R.id.SaveButtonGroup);
        saveButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {

                    // Date Validation
                    if (detailsText.getText().toString().matches("")) {

                        Toast.makeText(getApplicationContext(), "No Note Entered.", Toast.LENGTH_LONG).show();


                    } else {

                     new CreateNewProject().execute();

                    }


                } else {

                    Toast.makeText(getApplicationContext(), "Internet Connection Required.", Toast.LENGTH_LONG).show();

                }

            }
        });

        if(isNetworkAvailable()){

            getWebData();

        }else{

            Toast.makeText(getApplicationContext(), "Internet Connection Required.", Toast.LENGTH_LONG).show();
        }

        subject = subjectGroupText.getText().toString();
        details = detailsText.getText().toString();

        projectEmail = session.getUserName();
        //   String groupNoteCourse = session.getUserCourse();
        groupNoteCourse = session.getUserCourse();
        date = new SimpleDateFormat("dd-mm-yyyy'T'HH:mm").format(new Date());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_project, menu);
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

    private void getWebData() {

        addSubjectsToListBackgroundTask task = new addSubjectsToListBackgroundTask();
        task.execute(subjectsURL);

    }

    class addSubjectsToListBackgroundTask extends AsyncTask<String, Void, String> {

        private String jsonResult;


        @Override
        protected String doInBackground(String... params) {

//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(params[0]);
//            try {
//                HttpResponse response = httpclient.execute(httppost);
//                jsonResult = inputStreamToString(
//                        response.getEntity().getContent()).toString();
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


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
            fillSubjectsArray = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"projectSubject"};
            int[] to = new int[]{R.id.subjectName};


            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("subjects");

                Subject subjects = new Subject();


                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    subjects.setSubjectName(jsonChildNode.optString("projectSubject"));



                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("projectSubject", subjects.getSubjectName());
                    subjectsArray.add(map.get("projectSubject"));


                    fillSubjectsArray.add(map);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
            }


            //   adapter = new SimpleAdapter(getApplicationContext(), fillCollegesArray, R.layout.layout_college_list, from, to);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddNewGroupNoteActivity.this, android.R.layout.simple_dropdown_item_1line, subjectsArray);

            subjectGroupText.setAdapter(adapter2);






        }


    }

    // Background task to Enter Details into Database
    class CreateNewProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(AddNewGroupNoteActivity.this);
//            pDialog.setMessage("Creating Project..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {


//
//            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("GroupNoteId", "0"));
//            params.add(new BasicNameValuePair("GroupNoteAuthor", projectEmail));
//            params.add(new BasicNameValuePair("GroupNoteDatePosted", date));
//            params.add(new BasicNameValuePair("GroupNoteText", details));
//            params.add(new BasicNameValuePair("GroupNoteCourse", session.getUserCourse()));
//            params.add(new BasicNameValuePair("GroupNoteSubject", subject));





            return null;


        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            //    pDialog.dismiss();
        }

    }


}
