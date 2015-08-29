package com.chris.collegeplanner.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.chris.collegeplanner.R;

public class TimeTableWebView extends ActionBarActivity {

    WebView webview;
    String imageURL = "";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_web_view);
        setTitle("TimeTable");

        webview = (WebView) findViewById(R.id.webView);
        webview.setBackgroundColor(0);

        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);

        // Set Webview Image to a SharedPreference Option.

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageURL = preferences.getString("imageURL","");

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
        builder.setTitle("Add New Timetable");
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
                    Toast.makeText(getApplicationContext(), "Screen Resolution - " + getScreenResolution(getApplicationContext()), Toast.LENGTH_LONG).show();
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



                Toast.makeText(getApplicationContext(), "Gallery Image Selected", Toast.LENGTH_LONG).show();

                // Set SharedPreference ImageURL to Image URL

                String selectedImageURL = "";


                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageURL",selectedImageURL);
                editor.apply();

            }
        }
    }

    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "" + width + " x " + height + "";
    }
}
