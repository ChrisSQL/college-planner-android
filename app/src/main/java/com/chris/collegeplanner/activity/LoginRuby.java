package com.chris.collegeplanner.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.chris.collegeplanner.R;

public class LoginRuby extends AppCompatActivity {

    private ProgressDialog progressBar;
    private static final String TAG = "Main";
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ruby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Register");

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        progressBar = ProgressDialog.show(LoginRuby.this, "Register", "Loading...");
        registerButton = (Button) findViewById(R.id.registerBackButton);


        WebView webview = (WebView) findViewById(R.id.registerWebview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);



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
                Toast.makeText(LoginRuby.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
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

        webview.loadUrl("https://radiant-sea-5676.herokuapp.com/mobile");

    }

    public void clickFunc(View view) {

        Intent intent = new Intent(LoginRuby.this, TimeTableWebView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(LoginRuby.this, SummaryActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }


}
