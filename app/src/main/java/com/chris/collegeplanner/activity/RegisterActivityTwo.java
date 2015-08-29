package com.chris.collegeplanner.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.model.College;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

//import org.apache.http.HttpResponse;

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

public class RegisterActivityTwo extends ActionBarActivity {

    AutoCompleteTextView college, course;
    InputStream is=null;
    String result=null;
    String line=null;
    Spinner spinner;
    List<HashMap<String, String>> fillCollegesArray, fillCoursesArray;
    int id;
    SimpleAdapter adapter;
    private static final String collegesURL = "http://chrismaher.info/AndroidProjects2/colleges.php";
    private static final String coursesURL = "http://chrismaher.info/AndroidProjects2/courses.php";
    AlphaInAnimationAdapter animationAdapter;
    String[] collegeNames;
    List<String> collegesArray = new ArrayList<>();
    List<String> courseArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity_two);

        college = (AutoCompleteTextView) findViewById(R.id.college);
        college.setThreshold(1);
        course = (AutoCompleteTextView) findViewById(R.id.course);
        course.setThreshold(1);

        /////////////////////////////////////////////////////////////

        fillNoOfSubjectsSpinner();
        getWebData();

    //    fillCourseAutoComplete();

        /////////////////////////////////////////////////////////////

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_activity_two, menu);
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

    private void fillNoOfSubjectsSpinner(){

        // Array of choices
        String colors[] = {"No. Of Subjects", "1","2","3","4","5","6","7","8","9","10"};
        // Selection of the spinner
        spinner = (Spinner) findViewById(R.id.noOfSubjects);
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, colors);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

    }

    private void getWebData() {

        addCollegesToListBackgroundTask task = new addCollegesToListBackgroundTask();
        task.execute(new String[]{collegesURL});

        addCoursesToListBackgroundTask task2 = new addCoursesToListBackgroundTask();
        task2.execute(new String[]{coursesURL});


    }

    class addCollegesToListBackgroundTask extends AsyncTask<String, Void, String> {

        private String jsonResult;


        @Override
        protected String doInBackground(String... params) {

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
            fillCollegesArray = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"collegeName"};
            int[] to = new int[]{R.id.collegeName};


            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("colleges");

               College colleges = new College();


                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    colleges.setCollegeName(jsonChildNode.optString("collegeName"));
                    Toast.makeText(getApplicationContext(), jsonChildNode.optString("collegeName"), Toast.LENGTH_SHORT).show();



                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("collegeName", colleges.getCollegeName());
                    collegesArray.add(map.get("collegeName"));


                    fillCollegesArray.add(map);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
            }


         //   adapter = new SimpleAdapter(getApplicationContext(), fillCollegesArray, R.layout.layout_college_list, from, to);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, collegesArray);

            college.setAdapter(adapter2);






        }

        ;


    }

    class addCoursesToListBackgroundTask extends AsyncTask<String, Void, String> {

        private String jsonResult;


        @Override
        protected String doInBackground(String... params) {

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
            fillCoursesArray = new ArrayList<HashMap<String, String>>();
            String[] from = new String[]{"courseName"};
            int[] to = new int[]{R.id.courseName};


            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("courses");

                College colleges = new College();


                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    colleges.setCollegeName(jsonChildNode.optString("courseName"));
                    Toast.makeText(getApplicationContext(), jsonChildNode.optString("courseName"), Toast.LENGTH_SHORT).show();



                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("courseName", colleges.getCollegeName());
                    courseArray.add(map.get("courseName"));


                    fillCoursesArray.add(map);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Nothing Added Yet.", Toast.LENGTH_SHORT).show();
            }


            //   adapter = new SimpleAdapter(getApplicationContext(), fillCoursesArray, R.layout.layout_college_list, from, to);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, courseArray);

            course.setAdapter(adapter2);






        }

        ;


    }


    }




