package com.example.parsegram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    }
}