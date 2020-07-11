package com.example.parsegram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parsegram.R;
import com.example.parsegram.models.Comment;
import com.example.parsegram.models.Post;
import com.parse.ParseException;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

// This class implements an adapter. For each Comment object, the adapter will create a
// ViewHolder and bind that ViewHolder to the RecyclerView.
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<Comment> mComments;
    Context mContext;

    public CommentsAdapter(ArrayList<Comment> comments, Context context) {
        this.mComments = comments;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = mComments.get(position);

        holder.mUsernameTv.setText(comment.getUser().getUsername());
        holder.mCommentTv.setText(comment.getBody());
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUsernameTv;
        TextView mCommentTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views
            mUsernameTv = itemView.findViewById(R.id.usernameTv);
            mCommentTv = itemView.findViewById(R.id.commentTv);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mComments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Comment> comments) {
        mComments.addAll(comments);
        notifyDataSetChanged();
    }

}
