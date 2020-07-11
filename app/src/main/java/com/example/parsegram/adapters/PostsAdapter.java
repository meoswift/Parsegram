package com.example.parsegram.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parsegram.activities.CommentsActivity;
import com.example.parsegram.fragments.ProfileFragment;
import com.example.parsegram.models.Comment;
import com.example.parsegram.models.Post;
import com.example.parsegram.R;
import com.example.parsegram.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

// This class implements a View adapter. For each Post object, the adapter will create a
// ViewHolder and bind that ViewHolder to the RecyclerView.
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    List<Post> postList;
    Context context;
    AppCompatActivity app;

    public PostsAdapter(List<Post> postList, Context context, AppCompatActivity app) {
        this.postList = postList;
        this.context = context;
        this.app = app;
    }

    @NonNull
    @Override
    // Create new views by inflating a layout (invoked by the layout manager)
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // Replace the contents of a view with data at a specific Post object
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.mUsernameTopTv.setText(post.getUser().getUsername());
        holder.mUsernameBottomTv.setText(post.getUser().getUsername());
        holder.mDescriptionTv.setText(post.getDescription());
        holder.mTimeStampTv.setText(post.getFormatedTime());

        setProfilePic(post, holder);

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()). into(holder.mPhotoIv);
        }
    }

    // Return the size of our Post list
    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Set profile pic with either file from database or default image
    private void setProfilePic(Post post, @NonNull ViewHolder holder) {
        ParseFile image = (ParseFile) post.getUser().get(User.KEY_PROFILE_PIC);

        if (image != null)
            Glide.with(context).load(image.getUrl()).circleCrop().into(holder.mProfileIv);
        else
            Glide.with(context).load(R.drawable.ic_launcher_background)
                               .circleCrop().into(holder.mProfileIv);
    }

    // Find views from our post item layout
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        public ViewHolder(@NonNull View item) {
            super(item);

            // Find views for each UI element
            findViews(item);

            // Show comments when user click on View comments or the Comment icon
            mViewCommentsTv.setOnClickListener(this);
            mCommentBtnIv.setOnClickListener(this);

            // Shows user profile when user click on the top username in a post
            mUsernameTopTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchUserProfile();
                }
            });

            // Shows user profile when user click on the bottom username in a post
            mUsernameBottomTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchUserProfile();
                }
            });

            // Shows user profile when user click on the profile pic in a post
            mProfileIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchUserProfile();
                }
            });

            // Like a post when user click on heart icon
            mLikeBtnIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = postList.get(getAdapterPosition());
                    ParseRelation<ParseUser> likedUsers = post.getLikes();
                    queryLikes(likedUsers);
                }
            });
        }

        // Function to direct user to CommentsActivity
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Post post = postList.get(pos);

            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("post", Parcels.wrap(post));
            context.startActivity(intent);
        }

        // Function to display the user profile based on username and profile pic
        private void fetchUserProfile() {
            final FragmentManager fragmentManager = app.getSupportFragmentManager();
            Fragment fragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Post.KEY_USERNAME, mUsernameTopTv.getText().toString());
            fragment.setArguments(bundle);
            // Replace the contents of the container with the new fragment and update in view
            fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment).commit();
        }

        private void findViews(View item) {
            mUsernameTopTv = item.findViewById(R.id.tvUsernameTop);
            mUsernameBottomTv = item.findViewById(R.id.tvUsernameBottom);
            mPhotoIv = item.findViewById(R.id.ivPhoto);
            mDescriptionTv = item.findViewById(R.id.tvDescription);
            mTimeStampTv = item.findViewById(R.id.tvTimeStamp);
            mProfileIv = item.findViewById(R.id.profilePic);
            mViewCommentsTv = item.findViewById(R.id.viewCommentsTv);
            mCommentBtnIv = item.findViewById(R.id.commentBtnIv);
            mLikeBtnIv = item.findViewById(R.id.likeBtn);
            mLikeCountTv = item.findViewById(R.id.likeCountTv);
        }

        // Get all Comment objects in a post and add to local comments list
        private void queryLikes(final ParseRelation<ParseUser> mLikesRelation) {
            // Get current user
            final ParseUser user = ParseUser.getCurrentUser();
            // Specify which class to query
            ParseQuery<ParseUser> query = mLikesRelation.getQuery();
            // start an asynchronous call for posts
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> users, ParseException e) {
                    if (e != null) {
                        Log.d("PostAdapter", e.toString());
                        return;
                    }

                    // If the current has already liked the post, remove that like. Else, add.
                    if (users.size() != 0) {
                        mLikesRelation.remove(user);
                        post.saveInBackground();
                        // Change icon to liked 
                        mLikeBtnIv.setImageDrawable(context.getDrawable(R.drawable.ufi_heart_active));
                   } else {
                        mLikesRelation.add(user);
                        post.saveInBackground();
                        // Change icon to unliked
                        mLikeBtnIv.setImageDrawable(context.getDrawable(R.drawable.ufi_heart_icon));
                    }
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        postList.addAll(list);
        notifyDataSetChanged();
    }
}
