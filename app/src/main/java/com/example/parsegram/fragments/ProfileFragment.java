package com.example.parsegram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parsegram.R;
import com.example.parsegram.activities.LoginActivity;
import com.example.parsegram.adapters.UserPostsAdapter;
import com.example.parsegram.models.Post;
import com.example.parsegram.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements EditProfileFragment.EditProfileListener {

    private RecyclerView mUserPostsRv;
    private UserPostsAdapter mAdapter;
    private Button mLogOutBtn;
    private Button mEditProfileBtn;
    private TextView mDisplayNameTv;
    private TextView mUserBioTv;
    private ImageView mProfilePicIv;

    private List<Post> mUserPostsList;
    private String mUsername;
    private ParseUser mUser;

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
        mEditProfileBtn = view.findViewById(R.id.editBtn);
        mDisplayNameTv = view.findViewById(R.id.displayNameTv);
        mUserBioTv = view.findViewById(R.id.userBioTv);
        mProfilePicIv = view.findViewById(R.id.profilePic);

        Bundle bundle = this.getArguments();
        mUsername = bundle.getString(Post.KEY_USERNAME);

        // Get the user whose profile will be displayed
        queryUser();

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

        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showEditDialog();
           }
        });
    }

    private void queryUser() {
        // Specify which class to query
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        // Only get posts from current users
        query.whereEqualTo(Post.KEY_USERNAME, mUsername);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.d("PostsFragment", "Issue with querying posts" + e);
                    return;
                }

                mUser = users.get(0);
                populateUserInfo();
                queryPosts();
            }
        });
    }

    // Finding all objects of class Post in Parse database
    private void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include the user object related to the posts
        query.include(Post.KEY_USER);
        // Only get posts from current users
        query.whereEqualTo(Post.KEY_USER, mUser);
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


    // Call this method to launch the edit dialog
    private void showEditDialog() {
        FragmentManager fm = getFragmentManager();
        EditProfileFragment editProfileDialog = new EditProfileFragment();
        // SETS the target fragment for use later when sending results
        editProfileDialog.setTargetFragment(ProfileFragment.this, 300);
        editProfileDialog.show(fm, "fragment_edit_profile");
    }

    @Override
    public void onFinishEditDialog(String displayName, String userBio, File photoFile) {
        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        mDisplayNameTv.setText(displayName);
        mUserBioTv.setText(userBio);
        Glide.with(getContext()).load(takenImage).circleCrop().into(mProfilePicIv);
    }

    private void populateUserInfo() {
        mDisplayNameTv.setText(mUser.getString(User.KEY_NAME));
        mUserBioTv.setText(mUser.getString(User.KEY_BIO));

        setProfilePic();

        if (!mUsername.equals(ParseUser.getCurrentUser().getUsername())) {
            mEditProfileBtn.setVisibility(View.GONE);
            mLogOutBtn.setVisibility(View.GONE);
        }
    }

    // Set profile pic with either file from database or default image
    private void setProfilePic() {
        ParseFile image = (ParseFile) mUser.get(User.KEY_PROFILE_PIC);

        if (image != null)
            Glide.with(getContext()).load(image.getUrl()).circleCrop().into(mProfilePicIv);
        else
            Glide.with(getContext()).load(R.drawable.ic_launcher_background)
                    .circleCrop().into(mProfilePicIv);
    }
}