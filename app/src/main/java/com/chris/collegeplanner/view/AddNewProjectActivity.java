package com.chris.collegeplanner.view;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.model.Subject;
import com.chris.collegeplanner.reminders.AlarmReceiver;

//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddNewProjectActivity extends ActionBarActivity {




    // Date For DueDate
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        }

    };
    private SessionManager session;
    private EditText detailsText;
    private EditText dueDateText;
    private TextView titleText;
    private AutoCompleteTextView subjectSpinner;

    private Spinner typeSpinner;
    private Spinner worthSpinner;
    private Button saveButton2;
    private Button selectDateButton;
    List<String> subjectsArray = new ArrayList<>();
  //  AutoCompleteTextView subject;
    private String urlUpload = "http://chrismaher.info/AndroidProjects2/project_upload.php";
    private static final String subjectsURL = "http://chrismaher.info/AndroidProjects2/subjects.php";
    // Progress Dialog
    private ProgressDialog pDialog;
    List<HashMap<String, String>> fillSubjectsArray;
    AutoCompleteTextView subject;
    String type;
    String title;
    String worth;
    String details;
    String dueDate;
    String projectEmail;


    // This is the date picker used to select the date for our notification
    private DatePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);
        setTitle("Add New Project");

        // Instantiate
        //    details = (EditText)   findViewById(R.id.Details);

        subject = (AutoCompleteTextView) findViewById(R.id.SubjectSpinner);
        subject.setThreshold(1);
        session = new SessionManager(getApplicationContext());
        subjectSpinner = (AutoCompleteTextView) findViewById(R.id.SubjectSpinner);
        typeSpinner = (Spinner) findViewById(R.id.TypeSpinner);
        worthSpinner = (Spinner) findViewById(R.id.WorthSpinner);
        detailsText = (EditText) findViewById(R.id.DetailsText);
        titleText = (EditText) findViewById(R.id.TitleText);
        dueDateText = (EditText) findViewById(R.id.DueDateText);
        saveButton2 = (Button) findViewById(R.id.SaveButton);
        saveButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {

                    // Date Validation
                    if (dueDateText.getText().toString().matches("")) {

                        Toast.makeText(getApplicationContext(), "No Date Selected.", Toast.LENGTH_LONG).show();
                        selectDateButton.performClick();

                    } else if (titleText.getText().toString().matches("")) {

                        Toast.makeText(getApplicationContext(), "Enter a Title.", Toast.LENGTH_LONG).show();
                        titleText.requestFocus();

                    } else {

                        try {
                            scheduleAlarm();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        new CreateNewProject().execute();

                    }


                } else {

                    Toast.makeText(getApplicationContext(), "Internet Connection Required.", Toast.LENGTH_LONG).show();

                }

              //  subject = subjectSpinner.getText().toString();
                type = typeSpinner.getSelectedItem().toString();
                title = titleText.getText().toString();
                worth = worthSpinner.getSelectedItem().toString();
                details = detailsText.getText().toString();
                dueDate = dueDateText.getText().toString();
                projectEmail = session.getUserEmail();

            }
        });
        selectDateButton = (Button) findViewById(R.id.DueDateButton);
        selectDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddNewProjectActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        getWebData();


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
        task.execute(new String[]{subjectsURL});

    }

    class addSubjectsToListBackgroundTask extends AsyncTask<String, Void, String> {

        private String jsonResult;


        @Override
        protected String doInBackground(String... params) {

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
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddNewProjectActivity.this, android.R.layout.simple_dropdown_item_1line, subjectsArray);

            subject.setAdapter(adapter2);






        }

        ;


    }

    // Updates the DueDate TextField
    private void updateLabel() {

        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        dueDateText.setText(sdf.format(myCalendar.getTime()));
    }
    // Schedules the Alarm after Project is entered
    public void scheduleAlarm() throws ParseException {

        // Get date in milliseconds

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateInString = dueDateText.getText().toString();
        Date date = sdf.parse(dateInString);
        //  Toast.makeText(this, dateInString, Toast.LENGTH_LONG).show();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Send Message 48 hours before Due Date
        Long time = (calendar.getTimeInMillis()) - (86400000 + 86400000);

        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        intentAlarm.putExtra("Subject", subjectSpinner.getText().toString());
        intentAlarm.putExtra("Title", titleText.getText().toString());
        intentAlarm.putExtra("PhoneNumber", session.getUserPhone());

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Project Reminder Added", Toast.LENGTH_SHORT).show();

    }
    // Background task to Enter Details into Database
    class CreateNewProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(AddNewProjectActivity.this);
//            pDialog.setMessage("Creating Project..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {



            if (title.equalsIgnoreCase("")) {
                title = "NA";
            }
            if (details.equalsIgnoreCase("")) {
                details = "NA";
            }


            Log.e("Parameters", subject + " " + type + " " + title + " " + worth + " " + details + " " + dueDate);

            // Building Parameters
//            List<BasicNameValuePair> params = new ArrayList<>();
//            params.add(new BasicNameValuePair("ProjectID", 0 + ""));
//        //    params.add(new BasicNameValuePair("ProjectSubject", subject));
//            params.add(new BasicNameValuePair("ProjectType", type));
//            params.add(new BasicNameValuePair("ProjectTitle", title));
//            params.add(new BasicNameValuePair("ProjectWorth", worth));
//            params.add(new BasicNameValuePair("ProjectDueDate", dueDate));
//            params.add(new BasicNameValuePair("ProjectDetails", details));
//            params.add(new BasicNameValuePair("ProjectEmail", projectEmail));

//            // getting JSON Object
//            // Note that create product url accepts POST method
//            Log.d("PARAMS HERE ", params.toString());
//            JSONObject json = jsonParser.makeHttpRequest(urlUpload,
//                    "POST", params);
//
//            // check log cat fro response
//            Log.d("Create Response", json.toString());
//
//            // check for success tag
//            try {
//                int success = json.getInt("success");
//
//                if (success == 1) {
//                    // successfully created product
//                    Intent i = new Intent(getApplicationContext(), SummaryActivity.class);
//                    startActivity(i);
//
//                    // closing this screen
//                    finish();
//                } else {
//                    // failed to create product
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


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
