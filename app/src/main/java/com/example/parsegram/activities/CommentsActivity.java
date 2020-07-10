package com.example.parsegram.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parsegram.R;
import com.example.parsegram.adapters.CommentsAdapter;
import com.example.parsegram.models.Comment;
import com.example.parsegram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

// This activity displays a list of comments on a post and allows user to add a new comment
public class CommentsActivity extends AppCompatActivity {

    TextView mPostBtn;
    EditText mAddCommentEt;
    RecyclerView mCommentsRv;
    CommentsAdapter mAdapter;

    Post post;
    ArrayList<Comment> mCommentsList;
    ParseRelation<ParseObject> mCommentsRelation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Find views
        mPostBtn = findViewById(R.id.postBtn);
        mAddCommentEt = findViewById(R.id.addCommentEt);
        mCommentsRv = findViewById(R.id.commentsRv);

        // Retrieve the post object that comment will be added to
        Intent intent = getIntent();
        post = Parcels.unwrap(intent.getParcelableExtra("post"));

        // Retrieve the relations that hold pointers to comment in current post
        mCommentsRelation = post.getComments();
        mCommentsList = new ArrayList<>(); // initialize the local comments list

        mAdapter = new CommentsAdapter(mCommentsList, this);
        mCommentsRv.setAdapter(mAdapter);
        mCommentsRv.setLayoutManager(new LinearLayoutManager(this));

        // Find all comments in the post and display to view
        queryComments();

        // When user click Post, add comment to database and RecyclerView
        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createComment(mAddCommentEt.getText().toString());
            }
        });
    }

    private void queryComments() {
        // Specify which class to query
        ParseQuery query = mCommentsRelation.getQuery();
        // include the user object related to the posts
        query.include(Comment.KEY_AUTHOR);
        // order the posts from newest to oldest
        query.orderByDescending(Comment.KEY_CREATED_AT);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.d("CommentsActivity", "Issue with querying posts" + e);
                    return;
                }

                Log.d("HUH", comments.toString());

                // clear out old items before fetching new ones on refresh
                mAdapter.clear();
                // add the returned posts from API call to our local post list
                mAdapter.addAll(comments);
                // notify the adapter than new items have been added
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    // This function create a comment and save to database.
    private void createComment(String body) {
        // Create new instance of comment
        final Comment comment = new Comment();

        // Set all properties of a comment
        comment.setBody(body);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setPost(post);

        // Save the comment to database and update list of comments
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CommentsActivity.this,
                            "Issue publishing comment!", Toast.LENGTH_SHORT).show();
                    Log.d("CommentsActivity", e.toString());
                }

                mCommentsRelation.add(comment);
                post.saveInBackground();

                mCommentsList.add(0, comment);
                mAdapter.notifyDataSetChanged();

            }
        });

        mAddCommentEt.setText("");
    }

}