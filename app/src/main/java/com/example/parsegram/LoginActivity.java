package com.example.parsegram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/* A login screen that lets user log into their account with valid username and password */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    EditText mUsernameEt;
    EditText mPasswordEt;
    Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If a current is already logged in, we will not ask them to login again
        // Instead, direct them to HomeActivity and skip Login screen.
        if (ParseUser.getCurrentUser() != null) {
            startHomeActivity();
        }

        // Find views
        mUsernameEt = findViewById(R.id.etUsername);
        mPasswordEt = findViewById(R.id.etPassword);
        mLoginBtn = findViewById(R.id.btnLogin);
    }

    // On click listener for login button
    public void onLoginClicked(View view) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        loginUser(username, password);
    }

    // Function that authenticates a user based on input username and password
    private void loginUser(String username, String password) {
        // user get logged in with the provided username and password
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user == null) {
                    // Log in failed. Check logcat for error and send a Toast to let user know
                    Log.d(TAG, e.toString());
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // User is logged in successfully, navigate to Home/Feed.
                startHomeActivity();
                Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function that creates an intent to navigate user to their Home page.
    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish(); // prevents user from going back to login screen
    }
}