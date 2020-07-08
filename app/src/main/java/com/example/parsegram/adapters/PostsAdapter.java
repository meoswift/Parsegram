package com.example.parsegram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parsegram.models.Post;
import com.example.parsegram.R;
import com.parse.ParseFile;

import java.util.List;

// This class implements a View adapter. For each Post object, the adapter will create a
// ViewHolder and bind that ViewHolder to the RecyclerView.
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    List<Post> postList;
    Context context;

    public PostsAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
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

        ParseFile image = post.getImage();
        if (image != null) {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(image.getUrl()). into(holder.mPhotoIv);
            holder.mProgressBar.setVisibility(View.GONE);
        }
    }

    // Return the size of our Post list
    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Find views from our post item layout
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mUsernameTopTv;
        TextView mUsernameBottomTv;
        ImageView mPhotoIv;
        TextView mDescriptionTv;
        ProgressBar mProgressBar;
        TextView mTimeStampTv;

        public ViewHolder(@NonNull View item) {
            super(item);

            mUsernameTopTv = item.findViewById(R.id.tvUsernameTop);
            mUsernameBottomTv = item.findViewById(R.id.tvUsernameBottom);
            mPhotoIv = item.findViewById(R.id.ivPhoto);
            mDescriptionTv = item.findViewById(R.id.tvDescription);
            mProgressBar = item.findViewById(R.id.pbLoading);
            mTimeStampTv = item.findViewById(R.id.tvTimeStamp);
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
