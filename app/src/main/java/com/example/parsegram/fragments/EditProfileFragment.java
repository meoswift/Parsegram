package com.example.parsegram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parsegram.R;
import com.example.parsegram.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

import static android.app.Activity.RESULT_OK;

// This Dialog fragment allows users to edit their display name and bio
public class EditProfileFragment extends DialogFragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String APP_TAG = "EditProfileFragment";
    private EditText mNameEt;
    private EditText mBioEt;
    private String mDisplayName;
    private String mUserBio;
    private ImageView mSaveBtn;
    private ImageView mCancelBtn;
    private TextView mChangePicBtn;
    private ImageView mProfilePic;

    private ParseUser user;
    private File photoFile;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public int getTheme() {
        return R.style.DialogTheme;
    }

    // Defines the listener interface
    public interface EditProfileListener {
        void onFinishEditDialog(String displayName, String userBio, File photoFile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find views
        mNameEt = view.findViewById(R.id.nameEt);
        mBioEt = view.findViewById(R.id.bioEt);
        mSaveBtn = view.findViewById(R.id.saveBtn);
        mCancelBtn = view.findViewById(R.id.cancelBtn);
        mChangePicBtn = view.findViewById(R.id.changePicBtn);
        mProfilePic = view.findViewById(R.id.profilePicIv);

        // Get the display name and bio of current user
        getUserInfo();

        // Populate views
        mNameEt.setText(mDisplayName);
        mBioEt.setText(mUserBio);

        // By default
        Glide.with(getContext()).load(R.drawable.ic_launcher_background).circleCrop().into(mProfilePic);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mChangePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangePicClicked();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult();
            }
        });
    }

    private void getUserInfo() {
        // Get current user
        user = ParseUser.getCurrentUser();
        // Get user information
        mDisplayName = user.getString(User.KEY_NAME);
        mUserBio = user.getString(User.KEY_BIO);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        mDisplayName = mNameEt.getText().toString();
        mUserBio = mBioEt.getText().toString();

        // Update the database with edited information
        user.put(User.KEY_NAME, mDisplayName);
        user.put(User.KEY_BIO, mUserBio);
        user.put(User.KEY_PROFILE_PIC, new ParseFile(photoFile));
        user.saveInBackground();

        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        EditProfileListener listener = (EditProfileListener) getTargetFragment();
        listener.onFinishEditDialog(mDisplayName, mUserBio, photoFile);
        dismiss();
    }

    // When user click on Capture button, starts an intent to open the camera
    public void onChangePicClicked() {
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
                // Load the taken image into a preview
//                mProfilePic.setImageBitmap(takenImage);
                Glide.with(getContext()).load(takenImage).circleCrop().into(mProfilePic);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}