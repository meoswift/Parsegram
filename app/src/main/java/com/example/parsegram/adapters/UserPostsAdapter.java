package com.example.parsegram.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parsegram.fragments.PostDetailFragment;
import com.example.parsegram.models.Post;
import com.example.parsegram.R;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

// This class implements a View adapter. For each of current user's Post object, the adapter
// will create a ViewHolder and bind that ViewHolder to the RecyclerView.
public class UserPostsAdapter extends RecyclerView.Adapter<UserPostsAdapter.ViewHolder> {

    private List<Post> mUserPostsList;
    private Context context;

    public UserPostsAdapter(List<Post> postList, Context context) {
        this.mUserPostsList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    // Create new views by inflating a layout (invoked by the layout manager)
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // Replace the contents of a view with data at a specific Post object
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mUserPostsList.get(position);

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()). into(holder.mPhotoIv);
        }
    }

    // Return the size of our Post list
    @Override
    public int getItemCount() {
        return mUserPostsList.size();
    }

    // Find views from our post item layout
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mPhotoIv;

        public ViewHolder(@NonNull View item) {
            super(item);
            mPhotoIv = item.findViewById(R.id.userPostIv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Post post = mUserPostsList.get(pos);

            PostDetailFragment fragment = new PostDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("post", Parcels.wrap(post));

            ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.your_placeholder, fragment)
                    .addToBackStack(null)
                    .commit();

            fragment.setArguments(bundle);

        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mUserPostsList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mUserPostsList.addAll(list);
        notifyDataSetChanged();
    }
}