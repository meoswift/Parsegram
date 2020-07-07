package com.example.parsegram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

// Activity that allows user to create a post with description and image, then post it
public class HomeActivity extends AppCompatActivity {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 123;
    private static final String APP_TAG = "Parsegram";

    EditText mDescriptionEt;
    Button mCaptureBtn;
    Button mPostBtn;
    ImageView mCapturedIv;

    File photoFile;
    String photoFileName = "photo.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find views
        mDescriptionEt = findViewById(R.id.etDescription);
        mCaptureBtn = findViewById(R.id.btnCaptureImage);
        mPostBtn = findViewById(R.id.btnPost);
        mCapturedIv = findViewById(R.id.ivCaptured);

        // queryPosts();
    }

    // When user click on Capture button, starts an intent to open the camera
    public void onCaptureClicked(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider.parsegram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP
                // Load the taken image into a preview
                ImageView image = findViewById(R.id.ivCaptured);
                image.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // When user click on post, gets description and saves post to database
    public void onPostClicked(View view) {
        String description = mDescriptionEt.getText().toString();
        // Description is required
        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // An image file is required
        if (photoFile == null || mCapturedIv.getDrawable() == null) {
            Toast.makeText(this, "Must include a photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user and save new post
        ParseUser currentUser = ParseUser.getCurrentUser();
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

                Toast.makeText(HomeActivity.this, "Post saved successfully!",
                        Toast.LENGTH_SHORT).show();
                mDescriptionEt.setText(null); // clear description box once saved
                mCapturedIv.setImageDrawable(null); // clear image once saved
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