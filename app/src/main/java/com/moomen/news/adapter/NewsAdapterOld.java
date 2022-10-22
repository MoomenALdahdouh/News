package com.moomen.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moomen.news.R;
import com.moomen.news.model.PostNews;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapterOld extends RecyclerView.Adapter<NewsAdapterOld.ViewHolder> {
    public static final String USER_ID = "USER_ID";
    private Context context;
    private ArrayList<PostNews> postNewsArrayList;
    private View view;

    public NewsAdapterOld(Context context, ArrayList<PostNews> postNewsArrayList) {
        this.context = context;
        this.postNewsArrayList = postNewsArrayList;
    }

    @NonNull
    @Override
    public NewsAdapterOld.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.news_item, parent, false);
        return new NewsAdapterOld.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapterOld.ViewHolder holder, int position) {
        PostNews currentNews = postNewsArrayList.get(position);
        Picasso.get()
                .load(currentNews.getCompanyImage())
                .into(holder.companyImageView);
        holder.companyNameTextView.setText(currentNews.getCompanyName());
        holder.datePostTextView.setText(currentNews.getDate());
        Picasso.get()
                .load(currentNews.getImage())
                .into(holder.postImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        holder.showingCountTextView.setText(currentNews.getShowing() + "");
        holder.likeCountTextView.setText(currentNews.getLike() + "");
        holder.titlePostTextView.setText(currentNews.getTitle());
        holder.descriptionPostTextView.setText(currentNews.getDescription());
    }

    @Override
    public int getItemCount() {
        return postNewsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView companyImageView;
        TextView companyNameTextView;
        TextView datePostTextView;
        ImageView postImageView;
        ImageView shareImageView;
        TextView showingCountTextView;
        TextView likeCountTextView;
        TextView titlePostTextView;
        TextView descriptionPostTextView;
        EditText commentEditText;
        LinearLayout showAllCommentLinearLayout;
        ImageButton commentButton;
        RecyclerView allCommentRecyclerView;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyImageView = itemView.findViewById(R.id.image_view_company_id);
            companyNameTextView = itemView.findViewById(R.id.text_view_company_name_id);
            datePostTextView = itemView.findViewById(R.id.text_view_date_published_id);
            postImageView = itemView.findViewById(R.id.image_view_post_news_id);
            shareImageView = itemView.findViewById(R.id.image_view_share_post_id);
            showingCountTextView = itemView.findViewById(R.id.text_view_seeing_id);
            likeCountTextView = itemView.findViewById(R.id.text_view_like_id);
            titlePostTextView = itemView.findViewById(R.id.text_view_title_post_id);
            descriptionPostTextView = itemView.findViewById(R.id.text_view_description_post_id);
            commentEditText = itemView.findViewById(R.id.edit_text_comment_id);
            showAllCommentLinearLayout = itemView.findViewById(R.id.linear_layout_show_all_comments_id);
            commentButton = itemView.findViewById(R.id.image_view_comment_post_id);
            allCommentRecyclerView = itemView.findViewById(R.id.recycler_view_comments_id);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
