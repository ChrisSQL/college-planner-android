package com.chris.collegeplanner.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.ProjectsAdapter;
import com.chris.collegeplanner.model.Project;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewSingleProject extends AppCompatActivity {

    private static final String TAG = ViewSingleProject.class.getSimpleName();
    List<HashMap<String, String>> fillMaps;
    private int id;
    private TextView subjectSpinner;
    private TextView typeSpinner;
    private TextView titleText;
    private TextView worthText;
    private TextView detailsText;
    private TextView dueDateText, dueDateText2;
    private ProjectsAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private Project project;

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

        getOfflineProjectDetails();


    }

    private void getOfflineProjectDetails() {


        project = dbHelper.getProject(id);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(project.getProjectDueDate());
        Date today = new Date();
        String days = (Days.daysBetween(new DateTime(today), new DateTime(date)).getDays()) + "";
        if (Integer.valueOf(days) < 0) {
            days = "-";

        }

        DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        String formattedDate = f.format(project.getProjectDueDate());


        subjectSpinner.setText(project.getProjectSubject());
        typeSpinner.setText(project.getProjectType());
        worthText.setText(project.getProjectWorth());
        detailsText.setText(project.getProjectDetails());
        titleText.setText(project.getProjectTitle());
        dueDateText.setText(days);
        dueDateText2.setText(formattedDate);

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


}

