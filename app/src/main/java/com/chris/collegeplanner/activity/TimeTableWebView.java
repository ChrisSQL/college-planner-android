package com.chris.collegeplanner.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.TimeTableAdapter;
import com.chris.collegeplanner.model.TimeTable;

public class TimeTableWebView extends ActionBarActivity {

    WebView webview;
    String imageURL = "";
    SharedPreferences preferences;
    private String selectedImagePath;
    private String htmlImagePath;

    private TimeTableAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private ProgressDialog progressBar;
    private static final String TAG = "Timetable";

    private TimeTable timetable;

    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "" + width + " x " + height + "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_web_view);
        setTitle("TimeTable");

        timetable = new TimeTable();

        dbHelper = new TimeTableAdapter(this);
        dbHelper.open();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        progressBar = ProgressDialog.show(TimeTableWebView.this, "Timetable", "Loading...");

        webview = (WebView) findViewById(R.id.webView);
        webview.setBackgroundColor(0);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setDisplayZoomControls(true);
        webview.setInitialScale(2);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
                Toast.makeText(TimeTableWebView.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        //    Toast.makeText(getApplicationContext(), "URL - " + timetable.getTimetableURL(), Toast.LENGTH_LONG).show();


        if (dbHelper.exists(1)) {

            timetable = dbHelper.getTimeTable(1);
            webview.loadDataWithBaseURL("", timetable.getTimetableURL(), "text/html", "utf-8", "");

        } else {

            timetable.set_id(1);
            timetable.setTimetableURL("Add an image of your timetable from the gallery.");

            webview.loadUrl("file:///android_asset/webview.html");

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_table_web_view, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_timetable, menu);
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

    public void addNewTimetable(MenuItem item) {

        //    final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(TimeTableWebView.this);
        builder.setTitle("Add New TimeTable");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                // Camera Option Removed but might be added later. Better off to select image from Gallery thats been cropped etc..

//                if (options[item].equals("Take Photo")) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                    startActivityForResult(intent, 1);
//                } else

                if (options[item].equals("Choose from Gallery")) {
                    Toast.makeText(getApplicationContext(), "For best results Crop image on Computer.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Then transfer image to Phone.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Your Screen Resolution is - " + getScreenResolution(getApplicationContext()), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Toast.makeText(getApplicationContext(), "Camera Image Selected", Toast.LENGTH_LONG).show();

            } else if (requestCode == 2) {

                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);

                Toast.makeText(getApplicationContext(), "Gallery Image Selected", Toast.LENGTH_LONG).show();

                //////////////////////////////////////////////
                //   String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
                String imagePath = "file://" + selectedImagePath;
                htmlImagePath = "<html><head></head><body><img src=\"" + imagePath + "\"></body></html>";
                webview.loadDataWithBaseURL("", htmlImagePath, "text/html", "utf-8", "");
                //////////////////////////////////////////////

                saveImagePathToDatabase(htmlImagePath);

            }
        }
    }

    private void saveImagePathToDatabase(String htmlImagePath) {

        timetable.set_id(1);
        timetable.setTimetableURL(htmlImagePath);

        if (dbHelper.exists(timetable.get_id())) {
            dbHelper.updateTimeTable(timetable);
        } else {
            dbHelper.createTimeTable(1, timetable.getTimetableURL());
        }

    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }
}
