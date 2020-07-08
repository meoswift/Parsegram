package com.example.parsegram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.parsegram.R;
import com.example.parsegram.activities.LoginActivity;
import com.example.parsegram.adapters.PostsAdapter;
import com.example.parsegram.adapters.UserPostsAdapter;
import com.example.parsegram.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.xml.sax.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView mUserPostsRv;
    private UserPostsAdapter mAdapter;
    private Button mLogOutBtn;
    private TextView mCurrentUserTv;

    private List<Post> mUserPostsList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find view
        mUserPostsRv = view.findViewById(R.id.userPostsRv);
        mLogOutBtn = view.findViewById(R.id.logoutBtn);
        mCurrentUserTv = view.findViewById(R.id.currentUserTv);

        // Set display username of current user
        mCurrentUserTv.setText(ParseUser.getCurrentUser().getUsername());

        // Initialize posts list to empty
        mUserPostsList = new ArrayList<Post>();

        // Create a new adapter instance
        mAdapter = new UserPostsAdapter(mUserPostsList, getContext());
        // Set the adapter to posts RecyclerView
        mUserPostsRv.setAdapter(mAdapter);
        // Set layout as linear
        mUserPostsRv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        queryPosts();

    }

    // Finding all objects of class Post in Parse database
    protected void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include the user object related to the posts
        query.include(Post.KEY_USER);
        // Only get posts from current users
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order the posts from newest to oldest
        query.orderByDescending(Post.KEY_CREATED_AT);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.d("PostsFragment", "Issue with querying posts" + e);
                    return;
                }

                // add the returned posts from API call to our local post list
                mAdapter.addAll(posts);
                // notify the adapter than new items have been added
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}