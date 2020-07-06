package com.example.parsegram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

// Activity that allows user to create a post with description and image, then post it
public class HomeActivity extends AppCompatActivity {

    EditText mDescriptionEt;
    Button mCaptureBtn;
    Button mPostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find views
        mDescriptionEt = findViewById(R.id.etDescription);
        mCaptureBtn = findViewById(R.id.btnCaptureImage);
        mPostBtn = findViewById(R.id.btnPost);

        queryPosts();
    }

    // Finding all objects of class Post in Parse database
    private void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER); // include the user object related to the posts
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.d("HomeActivity", "Issue with querying posts" + e);
                    return;
                }

                for (Post post : posts) {
                    Log.d("HomeActivity", "Post: " + post.getDescription());
                    Log.d("HomeActivity", "User: " + post.getUser().getUsername());
                }
            }
        });
    }
}