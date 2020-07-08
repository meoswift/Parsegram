package com.example.parsegram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parsegram.R;
import com.example.parsegram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class PostDetailFragment extends Fragment {

    TextView mUsernameTopTv;
    TextView mUsernameBottomTv;
    ImageView mPhotoIv;
    TextView mDescriptionTv;
    TextView mTimeStampTv;

    private Post post;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        post = Parcels.unwrap(bundle.getParcelable("post"));

        mUsernameTopTv = view.findViewById(R.id.usernameTopTv);
        mUsernameBottomTv = view.findViewById(R.id.usernameBottomTv);
        mPhotoIv = view.findViewById(R.id.photoIv);
        mDescriptionTv = view.findViewById(R.id.descriptionTv);
        mTimeStampTv = view.findViewById(R.id.tvTimeStamp);

        displayPostDetails(post);
    }

    public void displayPostDetails(Post post) {
        mUsernameTopTv.setText(post.getUser().getUsername());
        mUsernameBottomTv.setText(post.getUser().getUsername());
        mDescriptionTv.setText(post.getDescription());
        mTimeStampTv.setText(post.getFormatedTime());

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(getContext()).load(image.getUrl()). into(mPhotoIv);
        }
    }
}