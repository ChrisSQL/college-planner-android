
package com.chris.collegeplanner.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chris.collegeplanner.R;
import com.chris.collegeplanner.adapters.UserAdapter;
import com.chris.collegeplanner.controller.AppConfig;
import com.chris.collegeplanner.controller.AppController;
import com.chris.collegeplanner.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;


public class LoginActivity extends Activity {
    // LogCat tag
    private static final String TAG = RegisterActivity.class.getSimpleName();
    String email = "";
    private Button btnLogin, btnLinkToRegister, btnSkipLogin;
    private EditText inputEmail, inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private String url = "";
    private UserAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new UserAdapter(this);
        dbHelper.open();

        session = new SessionManager(this);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnSkipLogin = (Button) findViewById(R.id.btnSkipSignin);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        url = "http://chrismaher.info/AndroidProjects2/get_login_course.php?email=" + email;

        initialiseOnClickListeners();


    }

    private void initialiseOnClickListeners() {


        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    if (dbHelper.checkLoginOffline(email, password)) {

                        session.setLogin(true);


                        Intent i = new Intent(getApplicationContext(), SummaryActivity.class);
                        i.putExtra("email", email);
                        startActivity(i);
                        finish();

                    } else {

                        Toast.makeText(getApplicationContext(),
                                "Wrong details.", Toast.LENGTH_LONG)
                                .show();

                    }
                    // checkLoginOnline(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {

                session.setLogin(false);

                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);

            }
        });

        // Link to Register Screen
        btnSkipLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                session.setLogin(false);

                Intent i = new Intent(getApplicationContext(),
                        SummaryActivity.class);
                startActivity(i);

            }
        });
    }

    // Checks the login through SQL Database.
    private void checkLoginOnline(final String email, final String password) {


        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this, SummaryActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        //    session.createLoginSession(email);
        //    session.setLoginCourse("Software Systems Development");
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

//        String urlUser = "http://chrismaher.info/AndroidProjects2/user_details.php?email=" + session.getUserEmail() + "";
//        Log.d("URLUSER : ", urlUser);
        //    setSessionDetails task = new setSessionDetails();
        // passes values for the urls string array
        //    task.execute(new String[]{urlUser});


    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}