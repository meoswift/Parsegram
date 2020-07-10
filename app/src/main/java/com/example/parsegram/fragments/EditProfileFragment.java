package com.example.parsegram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parsegram.R;
import com.example.parsegram.models.User;
import com.parse.ParseUser;

// This Dialog fragment allows users to edit their display name and bio
public class EditProfileFragment extends DialogFragment {

    private EditText mNameEt;
    private EditText mBioEt;
    private String mDisplayName;
    private String mUserBio;
    private ImageView mSaveBtn;
    private ImageView mCancelBtn;

    private ParseUser user;

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
        void onFinishEditDialog(String displayName, String userBio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find views
        mNameEt = view.findViewById(R.id.nameEt);
        mBioEt = view.findViewById(R.id.bioEt);
        mSaveBtn = view.findViewById(R.id.saveBtn);
        mCancelBtn = view.findViewById(R.id.cancelBtn);

        // Get the display name and bio of current user
        getUserInfo();

        // Populate views
        mNameEt.setText(mDisplayName);
        mBioEt.setText(mUserBio);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
        user.saveInBackground();

        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        EditProfileListener listener = (EditProfileListener) getTargetFragment();
        listener.onFinishEditDialog(mDisplayName, mUserBio);
        dismiss();
    }

}