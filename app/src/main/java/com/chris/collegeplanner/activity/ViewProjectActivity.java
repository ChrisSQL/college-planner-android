// Chris Maher 20059304
package com.chris.collegeplanner.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.TabsPagerAdapter;
//import com.chris.collegeplanner.helper.JSONParser;
import com.chris.collegeplanner.helper.SQLiteHandler;
import com.chris.collegeplanner.helper.SessionManager;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewProjectActivity extends ActionBarActivity implements ActionBar.TabListener {


    private static final String urlSingleProject = "http://chrismaher.info/AndroidProjects2/project_details_single_row.php";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    String pid;
    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    private ViewPager viewPager;
    private EditText detailsText;
    private EditText dueDateText;
    private EditText titleText;
    private EditText subjectSpinner;
    private EditText typeSpinner;
    private EditText worthSpinner;
    private ProgressDialog pDialog;
    private String[] tabs = {"Project Details"};
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
        setContentView(R.layout.activity_view_project);
        setTitle("View Project");

        list = (ListView) findViewById(R.id.groupNotesListView);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        viewPager = (ViewPager) findViewById(R.id.pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        //   mViewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setAdapter(mAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        subjectSpinner = (EditText) findViewById(R.id.SubjectText);
        typeSpinner = (EditText) findViewById(R.id.TypeText);
        worthSpinner = (EditText) findViewById(R.id.WorthText);
        detailsText = (EditText) findViewById(R.id.DetailsTextFragment);
        titleText = (EditText) findViewById(R.id.TitleTextFragment);
        dueDateText = (EditText) findViewById(R.id.DueDateTextFragment);

        // Session manager
        session = new SessionManager(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pid = extras.getString("ProjectID");
            //   Toast.makeText(getApplicationContext(), "ID :  " + extras.getString("ProjectID") + "", Toast.LENGTH_LONG).show();
            getWebData();

        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_project, menu);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private void getWebData() {

        GetProductDetails task = new GetProductDetails();
        // passes values for the urls string array
        task.execute(new String[]{urlSingleProject});

//        String urlurl = "http://chrismaher.info/AndroidProjects2/project_group_notes_details.php?email="+session.getUserEmail()+"";
//    //    url = "http://chrismaher.info/AndroidProjects2/project_group_notes_details.php?email=chrismaher.wit@gmail.com";
//        url = urlurl;
//
//        ReadAllProjectGroupNotesBackgroundTask task2 = new ReadAllProjectGroupNotesBackgroundTask();
//        // passes values for the urls string array
//        task2.execute(new String[]{url});


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_view_project, container, false);
            return rootView;
        }
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
//                case 1:
//                    return getString(R.string.title_section2).toUpperCase(l);

            }
            return null;
        }
    }

    class GetProductDetails extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(ViewProjectActivity.this);
            pDialog.setMessage("Loading project details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected String doInBackground(String... params) {

//            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    // Check for success tag
//                    int success;
//                    try {
//                        // Building Parameters
//                        List<NameValuePair> params = new ArrayList<NameValuePair>();
//                        //   Toast.makeText(getApplicationContext(), "PID " + pid, Toast.LENGTH_LONG).show();
//                        params.add(new BasicNameValuePair("ProjectID", pid));
//
//                        // getting product details by making HTTP request
//                        // Note that product details url will use GET request
////                        JSONParser jsonParser = new JSONParser();
////                        JSONObject json = jsonParser.makeHttpRequest(urlSingleProject, "GET", params);
//
//
//                        // check your log for json response
////                        Log.d("Single Product Details", json.toString());
//
//                        // json success tag
//                        success = json.getInt("success");
//
//                        if (success == 1) {
//
//                            // successfully received product details
//                            JSONArray productObj = json.getJSONArray("project"); // JSON Array
//
//
//                            // get first product object from JSON Array
//                            JSONObject project = productObj.getJSONObject(0);
//                            //        Toast.makeText(ViewProjectActivity.this, project.toString(), Toast.LENGTH_LONG).show();
//                            //        Toast.makeText(ViewProjectActivity.this, project.getString("ProjectTitle"), Toast.LENGTH_LONG).show();
//
//                            subjectSpinner = (EditText) findViewById(R.id.SubjectText);
//                            typeSpinner = (EditText) findViewById(R.id.TypeText);
//                            worthSpinner = (EditText) findViewById(R.id.WorthText);
//                            detailsText = (EditText) findViewById(R.id.DetailsTextFragment);
//                            titleText = (EditText) findViewById(R.id.TitleTextFragment);
//                            dueDateText = (EditText) findViewById(R.id.DueDateTextFragment);
//
//                            titleText.setText(project.getString("ProjectTitle"));
//                            detailsText.setText(project.getString("ProjectDetails"));
//                            dueDateText.setText(project.getString("ProjectDueDate"));
//                            worthSpinner.setText(project.getString("ProjectWorth"));
//                            titleText.setText(project.getString("ProjectTitle"));
//                            dueDateText.setText(project.getString("ProjectDueDate"));
//                            subjectSpinner.setText(project.getString("ProjectSubject"));
//                            typeSpinner.setText(project.getString("ProjectType"));
//
//
//                        } else {
//                            // product with pid not found
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

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

    public void addGroupNote(View view){

        Toast.makeText(getApplicationContext(), "Clicked!!!", Toast.LENGTH_LONG).show();


    }






}
