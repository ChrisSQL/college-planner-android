package com.chris.collegeplanner.activity;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.chris.collegeplanner.R;
        import com.parse.LogInCallback;
        import com.parse.ParseException;
        import com.parse.ParseUser;
        import com.parse.SignUpCallback;

public class ParseLogin extends Activity {
    // Declare Variables
    Button loginbutton;
    Button signup;
    Button skipSignIn;
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from main.xml
        setContentView(R.layout.activity_login);
        // Locate EditTexts in main.xml
        username = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        // Locate Buttons in main.xml
        loginbutton = (Button) findViewById(R.id.btnLogin);
        signup = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        skipSignIn = (Button) findViewById(R.id.btnSkipSignin);

        // Login Button Click Listener
        loginbutton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                // Send data to Parse.com for verification
                ParseUser.logInInBackground(usernametxt, passwordtxt,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    // If user exist and authenticated, send user to Welcome.class
                                    Intent intent = new Intent(
                                            ParseLogin.this,
                                            SummaryActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            "Successfully Logged in",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    signup.performClick();

//                                    Toast.makeText(
//                                            getApplicationContext(),
//                                            "Account Registered.",
//                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Login Button Click Listener
        skipSignIn.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {

                Intent intent = new Intent(
                        ParseLogin.this,
                        SummaryActivity.class);
                startActivity(intent);

            }
        });

        // Sign up Button Click Listener
        signup.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                // Force user to fill up the form
                if (usernametxt.equals("") && passwordtxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please fill in your details then press sign up again.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    // Save new user data into Parse.com Data Storage
                    ParseUser user = new ParseUser();
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(
                                        ParseLogin.this,
                                        SummaryActivity.class);
                                startActivity(intent);
                                // Show a simple Toast message upon successful registration
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }

            }
        });

    }
}