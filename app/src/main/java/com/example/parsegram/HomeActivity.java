package com.example.parsegram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

//        queryPosts();
    }

    // When user click on post, gets description and saves post to database
    public void onPostClicked(View view) {
        String description = mDescriptionEt.getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user and save new post
        ParseUser currentUser = ParseUser.getCurrentUser();
        savePost(description, currentUser);
    }

    // Function to save a new post to Parse database
    private void savePost(String description, ParseUser currentUser) {
        // create a new Post that's already a Parse object
        Post post = new Post();
        // set required properties of the post
        post.setUser(currentUser);
        post.setDescription(description);
        // save post to database
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("HomeActivity", "Error saving post to dtb" + e);
                }

                Toast.makeText(HomeActivity.this, "Post saved successfully!",
                        Toast.LENGTH_SHORT).show();
                mDescriptionEt.setText(null); // clear description box once saved
            }
        });
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