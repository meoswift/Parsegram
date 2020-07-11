package com.example.parsegram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.parsegram.models.Post;
import com.example.parsegram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

// This Fragment allows user to take a picture, add a description, and post to timeline
public class ComposeFragment extends Fragment {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 123;
    private static final String APP_TAG = "Parsegram";

    private EditText mDescriptionEt;
    private Button mCaptureBtn;
    private Button mPostBtn;
    private ImageView mCapturedIv;
    private ProgressBar mProgressBar;

    private File photoFile;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Find views
        mDescriptionEt = view.findViewById(R.id.etDescription);
        mCaptureBtn = view.findViewById(R.id.btnCaptureImage);
        mPostBtn = view.findViewById(R.id.btnPost);
        mCapturedIv = view.findViewById(R.id.ivCaptured);
        mProgressBar = view.findViewById(R.id.pbLoading);

        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCaptureClicked();
            }
        });

        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPostClicked();
            }
        });

    }

    // When user click on Capture button, starts an intent to open the camera
    public void onCaptureClicked() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        String photoFileName = "photo.png";
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.parsegram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP
                // Load the taken image into a preview
                mCapturedIv.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // When user click on post, gets description and saves post to database
    public void onPostClicked() {
        String description = mDescriptionEt.getText().toString();
        // Description is required
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Description cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // An image file is required
        if (photoFile == null || mCapturedIv.getDrawable() == null) {
            Toast.makeText(getContext(), "Must include a photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user and save new post
        ParseUser currentUser = ParseUser.getCurrentUser();
        mProgressBar.setVisibility(View.VISIBLE);
        savePost(description, currentUser, photoFile);
    }

    // Function to save a new post to Parse database
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        // create a new Post that's already a Parse object
        Post post = new Post();
        // set required properties of the post: description, image, and user
        post.setUser(currentUser);
        post.setImage(new ParseFile(photoFile));
        post.setDescription(description);
        // save post to database
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("HomeActivity", "Error saving post to dtb" + e);
                }

                Toast.makeText(getContext(), "Posted to timeline!",
                        Toast.LENGTH_SHORT).show();
                mDescriptionEt.setText(null); // clear description box once saved
                mCapturedIv.setImageDrawable(null); // clear image once saved
                mProgressBar.setVisibility(View.GONE); // progressBar disappear
            }
        });
    }

}