package com.example.parsegram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parsegram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/* A login screen that lets user log into their account with valid username and password */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    EditText mUsernameEt;
    EditText mPasswordEt;


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
    }

    // On click listener for login button
    public void onLoginClicked(View view) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        loginUser(username, password);
    }

    public void onSignUpClicked(View view) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        createAccount(username, password);
    }

    // Function that authenticates a user based on input username and password
    private void loginUser(String username, String password) {
        // user get logged in with the provided username and password
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user == null) {
                    // Log in failed. Check logcat for error and send a Toast to let user know
                    Log.d(TAG, e.toString());
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                // User is logged in successfully, navigate to Home/Feed.
                startHomeActivity();
                Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    // Log in failed. Check logcat for error and send a Toast to let user know
                    Log.d(TAG, e.toString());
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                // User is logged in successfully, navigate to Home/Feed.
                startHomeActivity();
                Toast.makeText(LoginActivity.this, "Welcome to Instagram!", Toast.LENGTH_SHORT).show();
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