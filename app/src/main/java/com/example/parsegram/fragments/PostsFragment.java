package com.example.parsegram.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    RecyclerView mPostsRv;
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

        queryPosts();
    }

    // Finding all objects of class Post in Parse database
    private void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include the user object related to the posts
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.d("PostsFragment", "Issue with querying posts" + e);
                    return;
                }

                // add the returned posts from API call to our local post list
                mPostList.addAll(posts);
                // notify the adapter than new items have been added
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}