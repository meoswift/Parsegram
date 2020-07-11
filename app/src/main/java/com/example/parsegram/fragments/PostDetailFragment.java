package com.example.parsegram.fragments;

import android.content.Intent;
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
import com.example.parsegram.activities.CommentsActivity;
import com.example.parsegram.models.Post;
import com.example.parsegram.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

// This Fragment display a post in detail view when user click on the post in their profile
public class PostDetailFragment extends Fragment implements View.OnClickListener {

    TextView mUsernameTopTv;
    TextView mUsernameBottomTv;
    ImageView mPhotoIv;
    TextView mDescriptionTv;
    TextView mTimeStampTv;
    ImageView mProfileIv;
    TextView mViewCommentsTv;
    ImageView mCommentBtnIv;
    ImageView mLikeBtnIv;
    TextView mLikeCountTv;

    private Post post;
    private ParseUser mCurrentUser;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.post_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        post = Parcels.unwrap(bundle.getParcelable("post"));

        mUsernameTopTv = view.findViewById(R.id.tvUsernameTop);
        mUsernameBottomTv = view.findViewById(R.id.tvUsernameBottom);
        mPhotoIv = view.findViewById(R.id.ivPhoto);
        mDescriptionTv = view.findViewById(R.id.tvDescription);
        mTimeStampTv = view.findViewById(R.id.tvTimeStamp);
        mProfileIv = view.findViewById(R.id.profilePic);
        mViewCommentsTv = view.findViewById(R.id.viewCommentsTv);
        mCommentBtnIv = view.findViewById(R.id.commentBtnIv);
        mLikeBtnIv = view.findViewById(R.id.likeBtn);
        mLikeCountTv = view.findViewById(R.id.likeCountTv);

        displayPostDetails(post);

        mCommentBtnIv.setOnClickListener(this);
        mViewCommentsTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CommentsActivity.class);
        intent.putExtra("post", Parcels.wrap(post));
        getContext().startActivity(intent);
    }

    public void displayPostDetails(Post post) {
        mUsernameTopTv.setText(post.getUser().getUsername());
        mUsernameBottomTv.setText(post.getUser().getUsername());
        mDescriptionTv.setText(post.getDescription());
        mTimeStampTv.setText(post.getFormatedTime());
        setProfilePic(post);

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(getContext()).load(image.getUrl()). into(mPhotoIv);
        }
    }

    // Set profile pic with either file from database or default image
    private void setProfilePic(Post post) {
        ParseFile image = (ParseFile) post.getUser().get(User.KEY_PROFILE_PIC);

        if (image != null)
            Glide.with(getContext()).load(image.getUrl()).circleCrop().into(mProfileIv);
        else
            Glide.with(getContext()).load(R.drawable.ic_launcher_background).circleCrop().into(mProfileIv);
    }
}