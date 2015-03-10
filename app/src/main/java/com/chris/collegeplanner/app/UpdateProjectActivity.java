// Chris Maher 20059304
package com.chris.collegeplanner.app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.helper.JSONParser;
import com.chris.collegeplanner.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class UpdateProjectActivity extends ActionBarActivity {

    // single product url
    private static final String urlSingleProject = "http://chrismaher.info/AndroidProjects2/project_details_single_row.php";
    JSONParser jsonParser = new JSONParser();
    String pid;
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
    String[] projectArray;

    private SessionManager session;
    private EditText detailsText;
    private EditText dueDateText;
    private TextView titleText;
    private Spinner subjectSpinner;
    private Spinner typeSpinner;
    private Spinner worthSpinner;
    private Button updateButton;
    private Button selectDateButton;
    private String urlUpdate = "http://chrismaher.info/AndroidProjects2/project_update.php";
    // Progress Dialog
    private ProgressDialog pDialog;

    public static String toTitleCase(String givenString) {
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
        setContentView(R.layout.activity_update_project);
        setTitle("Update Project");

        session = new SessionManager(getApplicationContext());
        subjectSpinner = (Spinner) findViewById(R.id.SubjectSpinner);
        typeSpinner = (Spinner) findViewById(R.id.TypeSpinner);
        worthSpinner = (Spinner) findViewById(R.id.WorthSpinner);
        detailsText = (EditText) findViewById(R.id.DetailsText);
        titleText = (EditText) findViewById(R.id.TitleText);
        dueDateText = (EditText) findViewById(R.id.DueDateText);
        updateButton = (Button) findViewById(R.id.SaveButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UpdateProject().execute();

            }
        });
        selectDateButton = (Button) findViewById(R.id.DueDateButton);
        selectDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UpdateProjectActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        projectArray = new String[7];

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        // getting product details from intent
        Intent i = getIntent();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pid = extras.getString("ProjectID");
            getWebData();
           // Toast.makeText(getApplicationContext(), "ID :  " + pid, Toast.LENGTH_LONG).show();
        }

    }

    private void getWebData() {

        GetProductDetails task = new GetProductDetails();
        // passes values for the urls string array
        task.execute(new String[]{urlSingleProject});


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_project, menu);
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

    private void updateLabel() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        dueDateText.setText(sdf.format(myCalendar.getTime()));
    }

    /**
     * Background Async Task to Get complete product details
     */
    class GetProductDetails extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateProjectActivity.this);
            pDialog.setMessage("Loading project details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("ProjectID", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(urlSingleProject, "GET", params);


                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt("success");

                        if (success == 1) {

                            // successfully received product details
                            JSONArray productObj = json.getJSONArray("project"); // JSON Array


                            // get first product object from JSON Array
                            JSONObject project = productObj.getJSONObject(0);
                            //    Toast.makeText(UpdateProjectActivity.this, project.getString("ProjectID"), Toast.LENGTH_SHORT).show();
                            titleText.setText(project.getString("ProjectTitle"));
                            detailsText.setText(project.getString("ProjectDetails"));
                            dueDateText.setText(project.getString("ProjectDueDate"));

                            // Set subjectSpinner Values
                            String compareValue = project.getString("ProjectSubject");
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.subject_array, R.layout.spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            subjectSpinner.setAdapter(adapter);
                            if (!compareValue.equals(null)) {
                                int spinnerPostion = adapter.getPosition(compareValue);
                                subjectSpinner.setSelection(spinnerPostion);
                                spinnerPostion = 0;
                            }

                            // Set typeSpinner Values
                            compareValue = project.getString("ProjectTitle");
                            adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.project_type_array, R.layout.spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            typeSpinner.setAdapter(adapter);
                            if (!compareValue.equals(null)) {
                                int spinnerPostion = adapter.getPosition(compareValue);
                                typeSpinner.setSelection(spinnerPostion);

                                spinnerPostion = 0;
                            }
                            // Set worthSpinner Values
                            compareValue = project.getString("ProjectWorth");
                            adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.percentage_array, R.layout.spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            worthSpinner.setAdapter(adapter);
                            if (!compareValue.equals(null)) {
                                int spinnerPostion = adapter.getPosition(compareValue);
                                worthSpinner.setSelection(spinnerPostion);
                                spinnerPostion = 0;
                            }


                        } else {
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

    class UpdateProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateProjectActivity.this);
            pDialog.setMessage("Updating Project..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            String subject = subjectSpinner.getSelectedItem().toString();
            String type = typeSpinner.getSelectedItem().toString();
            String title = titleText.getText().toString();
            String worth = worthSpinner.getSelectedItem().toString();
            String details = detailsText.getText().toString();
            String dueDate = dueDateText.getText().toString();
            String projectEmail = session.getUserDetails();

            if (title.equalsIgnoreCase("")) {
                title = "NA";
            }
            if (details.equalsIgnoreCase("")) {
                title = "NA";
            }

            Log.e("Parameters", subject + " " + type + " " + title + " " + worth + " " + details + " " + dueDate + " " + projectEmail  + " " + pid);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("ProjectID", pid));
            params.add(new BasicNameValuePair("ProjectSubject", subject));
            params.add(new BasicNameValuePair("ProjectType", type));
            params.add(new BasicNameValuePair("ProjectTitle", toTitleCase(title)));
            params.add(new BasicNameValuePair("ProjectWorth", worth));
            params.add(new BasicNameValuePair("ProjectDueDate", dueDate));
            params.add(new BasicNameValuePair("ProjectDetails", details));
            params.add(new BasicNameValuePair("ProjectEmail", projectEmail));

            // getting JSON Object
            // Note that create product url accepts POST method
            Log.d("PARAMS HERE ", params.toString());
            JSONObject json = jsonParser.makeHttpRequest(urlUpdate,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), SummaryActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}
