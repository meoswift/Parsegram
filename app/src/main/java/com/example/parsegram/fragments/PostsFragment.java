package com.example.parsegram.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parsegram.models.Post;
import com.example.parsegram.R;
import com.example.parsegram.adapters.PostsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    protected RecyclerView mPostsRv;
    protected SwipeRefreshLayout swipeContainer;

    protected PostsAdapter mAdapter;
    protected List<Post> mPostList;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Find view
        mPostsRv = view.findViewById(R.id.rvPosts);

        // Initialize posts list to empty
        mPostList = new ArrayList<Post>();

        // Create a new adapter instance
        mAdapter = new PostsAdapter(mPostList, getContext());
        // Set the adapter to posts RecyclerView
        mPostsRv.setAdapter(mAdapter);
        // Set layout as linear
        mPostsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryPosts();
    }

    // Finding all objects of class Post in Parse database
    protected void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include the user object related to the posts
        query.include(Post.KEY_USER);
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

                // clear out old items before fetching new ones on refresh
                mAdapter.clear();
                // add the returned posts from API call to our local post list
                mAdapter.addAll(posts);
                // notify the adapter than new items have been added
                mAdapter.notifyDataSetChanged();
                // call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
        });
    }
}