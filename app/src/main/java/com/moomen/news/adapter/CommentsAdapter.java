package com.moomen.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moomen.news.R;
import com.moomen.news.model.Comments;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    public static final String CREDIT_ID = "CREDIT_ID";
    private Context context;
    private ArrayList<Comments> commentsModelArrayList;

    public CommentsAdapter(Context context, ArrayList<Comments> commentsModelArrayList) {
        this.context = context;
        this.commentsModelArrayList = commentsModelArrayList;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.comment_item, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comments currentComments = commentsModelArrayList.get(position);
        Picasso.get()
                .load(currentComments.getUserImage())
                .into(holder.userImage);
        holder.userName.setText(currentComments.getUserName());
        holder.date.setText(currentComments.getDate());
        holder.comment.setText(currentComments.getComment());
    }

    @Override
    public int getItemCount() {
        return commentsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        TextView date;
        TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.imageView_user_id);
            userName = itemView.findViewById(R.id.textView_name_user_comment_id);
            date = itemView.findViewById(R.id.textView_date_comment_id);
            comment = itemView.findViewById(R.id.textView_user_comment_id);
        }
    }
}