package com.chris.collegeplanner.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.ProjectsAdapter;
import com.chris.collegeplanner.helper.SessionManager;
import com.chris.collegeplanner.model.Project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

//import org.apache.http.message.BasicNameValuePair;


public class AddNewProjectActivity extends ActionBarActivity {


    private static final String subjectsURL = "http://chrismaher.info/AndroidProjects2/subjects.php";
    // Date For DueDate
    Calendar myCalendar = Calendar.getInstance();
    List<String> subjectsArray = new ArrayList<>();
    List<HashMap<String, String>> fillSubjectsArray;
    AutoCompleteTextView subject;
    String type;
    String title;
    String worth;
    String details;
    String dueDate;
    String extraEmail;
    private SessionManager session;
    private EditText detailsText;
    private EditText dueDateText;
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
    private TextView titleText;
    private AutoCompleteTextView subjectSpinner;
    private Spinner typeSpinner;
    private Spinner worthSpinner;
    private Button saveButton2, backButton, selectDateButton;
  //  AutoCompleteTextView subject;
    private String urlUpload = "http://chrismaher.info/AndroidProjects2/project_upload.php";
    // Progress Dialog
    private ProgressDialog pDialog;
    private ProjectsAdapter projectsAdapter;
    private Project project;
    private ProjectsAdapter dbHelper;


    // This is the date picker used to select the date for our notification
    private DatePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);
        setTitle("Add New Project");

        dbHelper = new ProjectsAdapter(this);
        dbHelper.open();

        getExtras(savedInstanceState);
        projectsAdapter = new ProjectsAdapter(getApplicationContext());
        project = new Project();
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
        selectDateButton = (Button) findViewById(R.id.DueDateButton);
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AddNewProjectActivity.this, SummaryActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

                        type = typeSpinner.getSelectedItem().toString();
                        title = titleText.getText().toString();
                        worth = worthSpinner.getSelectedItem().toString();
                        details = detailsText.getText().toString();
                        dueDate = dueDateText.getText().toString();

                        project.set_id(0);
                        project.setProjectSubject(subjectSpinner.getText().toString());
                        project.setProjectType(typeSpinner.getSelectedItem().toString());
                        project.setProjectTitle(titleText.getText().toString());
                        project.setProjectWorth(worthSpinner.getSelectedItem().toString());
                        project.setProjectDetails(detailsText.getText().toString());

                        try {
                            project.setProjectDueDate(dueDateText.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        project.setProjectEmail(extraEmail);

                        dbHelper.createProject(project);
                        dbHelper.close();

                        Intent intent = new Intent(AddNewProjectActivity.this, SummaryActivity.class);
                        startActivity(intent);
                        finish();
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

            }
        });

        selectDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddNewProjectActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    //    getWebData();


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

    // Updates the DueDate TextField
    private void updateLabel() {

        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        dueDateText.setText(sdf.format(myCalendar.getTime()));

    }
    // Schedules the Alarm after Project is entered
    public void scheduleAlarm() throws ParseException {

//        // Get date in milliseconds
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String dateInString = dueDateText.getText().toString();
//        Date date = sdf.parse(dateInString);
//        //  Toast.makeText(this, dateInString, Toast.LENGTH_LONG).show();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//
//        // Send Message 48 hours before Due Date
//        Long time = (calendar.getTimeInMillis()) - (86400000 + 86400000);
//
//        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
//        intentAlarm.putExtra("Subject", subjectSpinner.getText().toString());
//        intentAlarm.putExtra("Title", titleText.getText().toString());
//        intentAlarm.putExtra("PhoneNumber", session.getUserPhone());
//
//        // create the object
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        //set the alarm for particular time
//        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
//        Toast.makeText(this, "Project Reminder Added", Toast.LENGTH_SHORT).show();

    }
    // Background task to Enter Details into Database



}
