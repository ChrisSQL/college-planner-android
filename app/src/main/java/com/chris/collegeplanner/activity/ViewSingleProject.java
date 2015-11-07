package com.chris.collegeplanner.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.ProjectsAdapter;
import com.chris.collegeplanner.model.Project;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewSingleProject extends AppCompatActivity {

//    private static final String TAG = ViewSingleProject.class.getSimpleName();
//    private List<HashMap<String, String>> fillMaps;
//    private SimpleCursorAdapter dataAdapter;

    private int id;
    private TextView subjectSpinner;
    private TextView typeSpinner;
    private TextView titleText;
    private TextView worthText;
    private TextView detailsText;
    private TextView dueDateText, dueDateText2;
    private ProjectsAdapter dbHelper;
    private Project project;
    private Button saveButton;
    private ParseUser currentUser;
    private String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_project);

        setTitle("Project Details");
        // Offline Projects
        dbHelper = new ProjectsAdapter(this);
        dbHelper.open();

        getExtras(savedInstanceState);
        project = new Project();

        subjectSpinner = (TextView) findViewById(R.id.subjectTextSingleProject);
        typeSpinner = (TextView) findViewById(R.id.typeTextSingleProject);
        worthText = (TextView) findViewById(R.id.worthTextSingleProject);
        titleText = (TextView) findViewById(R.id.titleTextSingleProject);
        dueDateText = (TextView) findViewById(R.id.dueDateTextSingleProject);
        dueDateText2 = (TextView) findViewById(R.id.DueDateTextSingleProject2);
        detailsText = (TextView) findViewById(R.id.detailsTextSingleProject);
        detailsText.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                updateProject();
            }
        });

        saveButton = (Button) findViewById(R.id.sendToClassmate);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                updateProject();
            }
        });
        getOfflineProjectDetails();


    }

    private void getOfflineProjectDetails() {


        project = dbHelper.getProject(id);

        if(project != null){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(project.getProjectDueDate());
            Date today = new Date();
            String days = (Days.daysBetween(new DateTime(today), new DateTime(date)).getDays()) + "";
            if (Integer.valueOf(days) < 0) {
                days = "-";

            }

//            DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT);
            String formattedDate = sdf.format(project.getProjectDueDate());


            subjectSpinner.setText(project.getProjectSubject());
            typeSpinner.setText(project.getProjectType());
            worthText.setText(project.getProjectWorth());
            detailsText.setText(project.getProjectDetails());
            titleText.setText(project.getProjectTitle());
            dueDateText.setText(days);
            dueDateText2.setText(formattedDate);


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_single_project, menu);
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

    private void getExtras(Bundle savedInstanceState) {


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = 0;
            } else {
                id = extras.getInt("id");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("id");
        }
    }

    public void deleteProject(MenuItem item) {

        final int idIn = id;

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dbHelper.deleteTitle(idIn + "");

                        // Launching the login activity
                        Intent intent = new Intent(ViewSingleProject.this, SummaryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

        // Delete from Parse Server.
        // get project from id

        Project project = dbHelper.getProject(id);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project");
        query.whereEqualTo("projectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
//                    Toast.makeText(getApplicationContext(), "Delete Failed " + id, Toast.LENGTH_LONG).show();
                } else {
                    object.deleteInBackground();
                }
            }
        });
    }


    public void updateProject() {

        Intent intent = new Intent(ViewSingleProject.this, UpdateProjectActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();

    }

    public void updateProject(MenuItem item) {

        Intent intent = new Intent(ViewSingleProject.this, UpdateProjectActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewSingleProject.this, SummaryActivity.class);
        startActivity(intent);
    }

    public void deleteAllOnline(final String email){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project");
        query.whereEqualTo("email",email);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {

                // TODO Auto-generated method stub
                if (list.size() != 0) {

                    for (int i = 0; i < list.size(); i++) {

                        list.get(i).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {

                                if (e == null) {
//                            Toast.makeText(getBaseContext(), "Deleted Successfully!", Toast.LENGTH_LONG).show();


                                } else {
                                    Toast.makeText(getBaseContext(), "Cant Delete!" + e.toString(), Toast.LENGTH_LONG).show();
                                }

                            }

                        });


                    }


                }


            }

        });




    }

}

