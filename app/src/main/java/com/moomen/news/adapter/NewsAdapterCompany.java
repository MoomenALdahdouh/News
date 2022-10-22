package com.moomen.news.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.R;
import com.moomen.news.model.Comments;
import com.moomen.news.model.PostNews;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapterCompany extends FirestoreRecyclerAdapter<PostNews, NewsAdapterCompany.ViewHolder> {

    int likes = 0;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String userName = "";
    String userImage = "";
    View view;


    String newsID;
    DocumentReference documentReference;
    String userID;
    private Context context;
    private NewsAdapterCompany.OnItemClickListener listener;

    public void setContext(Context context) {
        this.context = context;
    }

    public NewsAdapterCompany(@NonNull FirestoreRecyclerOptions<PostNews> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsAdapterCompany.ViewHolder holder, int position, @NonNull PostNews model) {
        //PostNews model = postNewsArrayList.get(position);
        newsID = getSnapshots().getSnapshot(position).getId();
        userID = firebaseUser.getUid();
        documentReference = firebaseFirestore.collection("News").document(newsID);

        Picasso.get()
                .load(model.getCompanyImage())
                .into(holder.companyImageView);
        holder.companyNameTextView.setText(model.getCompanyName());
        holder.datePostTextView.setText(model.getDate());
        Picasso.get()
                .load(model.getImage())
                .into(holder.postImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        holder.showingCountTextView.setText(model.getShowing() + "");
        holder.likeCountTextView.setText(model.getLike() + "");
        holder.titlePostTextView.setText(model.getTitle());
        holder.descriptionPostTextView.setText(model.getDescription());
        holder.locationTextView.setText(model.getCountry());
        //Check the news status
        if (model.isCompanyStatus()) {
            if (model.isNewsStatus()) {
                if (model.isVisibility()) {
                    holder.activeNewsSwitch.setChecked(true);
                    holder.statusNewsTextView.setText("Active");
                    holder.statusNewsImageView.setImageResource(R.drawable.ic_baseline_visibility_blue_24);
                    holder.activeNewsSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Todo: Edit the value on firebase store
                            documentReference.update("visibility", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Invisible", Toast.LENGTH_SHORT).show();
                                    holder.statusNewsTextView.setText("Invisible");
                                    holder.statusNewsImageView.setImageResource(R.drawable.ic_baseline_visibility_24);
                                    //notifyDataSetChanged();
                                }
                            });

                        }
                    });
                } else if (!model.isVisibility()) {
                    holder.activeNewsSwitch.setChecked(false);
                    holder.statusNewsTextView.setText("Invisible");
                    holder.statusNewsImageView.setImageResource(R.drawable.ic_baseline_visibility_24);
                    holder.activeNewsSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Todo: Edit the value on firebase store
                            documentReference.update("visibility", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Visible", Toast.LENGTH_SHORT).show();
                                    holder.statusNewsTextView.setText("Visible");
                                    holder.statusNewsImageView.setImageResource(R.drawable.ic_baseline_visibility_blue_24);
                                    //notifyDataSetChanged();
                                }
                            });

                        }
                    });
                }
            } else {
                holder.statusNewsTextView.setText("Blocked");
                holder.statusNewsImageView.setImageResource(R.drawable.ic_baseline_block_24);
                holder.activeNewsSwitch.setChecked(false);
            }
        } else {
            holder.statusNewsTextView.setText("Blocked");
            holder.statusNewsImageView.setImageResource(R.drawable.ic_baseline_block_24);
            holder.activeNewsSwitch.setChecked(false);
        }
        //Change icon send comment
        holder.commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!holder.commentEditText.getText().toString().isEmpty())
                    holder.commentButton.setImageResource(R.drawable.ic_baseline_send_blue_24);
                else
                    holder.commentButton.setImageResource(R.drawable.ic_baseline_send_24);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //Add new Comment
        postComment(model, position, holder);
        //Get First Comment
        getFirstComment(model, position, holder);
        //Get number comments
        holder.commentCount.setText(model.getCommentsArrayList().size() + "");
        //Insert new Like Update Like and Showing count

        //postLikeOnFirebase(holder, model, newsID, userID);

    }

    private void getFirstComment(PostNews model, int position, ViewHolder holder) {
        if (!model.getCommentsArrayList().isEmpty()) {
            holder.commentsConstraintLayout.setVisibility(View.VISIBLE);
            Comments comments = model.getCommentsArrayList().get(0);
            Picasso.get()
                    .load(comments.getUserImage())
                    .into(holder.userCommentImageView);
            holder.userNameComment.setText(comments.getUserName());
            holder.commentUser.setText(comments.getComment());
            //Show more Comment on pup sheet
            if (model.getCommentsArrayList().size() > 1) {
                holder.seeMoreComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                        View viewPupSheet = LayoutInflater.from(context).inflate(R.layout.comments_pup_sheet, view.findViewById(R.id.comment_pup_sheet_id));
                        LinearLayout linearLayoutBack = viewPupSheet.findViewById(R.id.linearLayout_comment_pup_sheet_id);
                        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialog.dismiss();
                            }
                        });
                        RecyclerView recyclerView = viewPupSheet.findViewById(R.id.recycler_view_comments_id);
                        CommentsAdapter commentsAdapter = new CommentsAdapter(context, model.getCommentsArrayList());
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(commentsAdapter);
                        bottomSheetDialog.setContentView(viewPupSheet);
                        bottomSheetDialog.show();
                    }
                });
            }
        } else
            holder.commentsConstraintLayout.setVisibility(View.GONE);
    }

    private void postComment(PostNews model, int position, ViewHolder holder) {
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = holder.commentEditText.getText().toString();
                if (!comment.isEmpty()) {
                    //updateDocument(model);
                    String dateComment = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                    //Get user details
                    DocumentReference df = firebaseFirestore.collection("Users").document(userID);
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot != null) {
                                String firstName = documentSnapshot.getString("firstName");
                                String lastName = documentSnapshot.getString("lastName");
                                userName = firstName + " " + lastName;
                                userImage = documentSnapshot.getString("userImage");
                                //Add comment
                                Comments newComment = new Comments(userName, userID, dateComment, newsID, comment, userImage);
                                ArrayList<Comments> commentsArrayList = model.getCommentsArrayList();
                                commentsArrayList.add(newComment);
                                //model.setCommentsArrayList(commentsArrayList);
                                documentReference.update("commentsArrayList", commentsArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
                                        holder.commentButton.setImageResource(R.drawable.ic_baseline_send_24);
                                        holder.commentEditText.setText("");
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                } else
                    Toast.makeText(context, "Comment is Empty!, Please Enter Comment", Toast.LENGTH_SHORT).show();
            }
        });
        //
        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //


    }

    private void checkNewsStatus() {

    }

    @NonNull
    @Override
    public NewsAdapterCompany.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.news_item_company, parent, false);
        return new NewsAdapterCompany.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView companyImageView;
        TextView companyNameTextView;
        TextView datePostTextView;
        ImageView postImageView;
        ImageView likeImageView;
        ImageView shareImageView;
        TextView showingCountTextView;
        TextView likeCountTextView;
        TextView titlePostTextView;
        TextView descriptionPostTextView;
        EditText commentEditText;
        TextView commentCount;
        LinearLayout showAllCommentLinearLayout;
        ImageButton commentButton;
        ConstraintLayout commentsConstraintLayout;
        ImageView userCommentImageView;
        TextView userNameComment;
        TextView commentUser;
        TextView seeMoreComment;
        ProgressBar progressBar;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch activeNewsSwitch;
        TextView statusNewsTextView;
        ImageView statusNewsImageView;
        TextView locationTextView;
        ImageView locationImageView;
        ConstraintLayout constraintLayoutItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyImageView = itemView.findViewById(R.id.image_view_company_id);
            companyNameTextView = itemView.findViewById(R.id.text_view_company_name_id);
            datePostTextView = itemView.findViewById(R.id.text_view_date_published_id);
            postImageView = itemView.findViewById(R.id.image_view_post_news_id);
            shareImageView = itemView.findViewById(R.id.image_view_share_post_id);
            showingCountTextView = itemView.findViewById(R.id.text_view_seeing_id);
            likeCountTextView = itemView.findViewById(R.id.text_view_like_id);
            likeImageView = itemView.findViewById(R.id.image_view_like_id);
            titlePostTextView = itemView.findViewById(R.id.text_view_title_post_id);
            descriptionPostTextView = itemView.findViewById(R.id.text_view_description_post_id);
            commentEditText = itemView.findViewById(R.id.edit_text_comment_id);
            commentCount = itemView.findViewById(R.id.textView_number_comment_id);
            showAllCommentLinearLayout = itemView.findViewById(R.id.linear_layout_show_all_comments_id);
            commentButton = itemView.findViewById(R.id.image_view_comment_post_id);
            progressBar = itemView.findViewById(R.id.progressBar);
            activeNewsSwitch = itemView.findViewById(R.id.switch_active_news_id);
            statusNewsTextView = itemView.findViewById(R.id.text_view_publish_news_id);
            statusNewsImageView = itemView.findViewById(R.id.image_view_publish_news_id);
            locationTextView = itemView.findViewById(R.id.text_view_location_id);
            locationImageView = itemView.findViewById(R.id.imageView8);
            commentsConstraintLayout = itemView.findViewById(R.id.constraintLayout_first_comment_id);
            userCommentImageView = itemView.findViewById(R.id.image_view_user_image_comment_id);
            userNameComment = itemView.findViewById(R.id.text_view_user_name_comment_id);
            commentUser = itemView.findViewById(R.id.text_view_comment_id);
            seeMoreComment = itemView.findViewById(R.id.text_view_more_comment_id);
            constraintLayoutItem = itemView.findViewById(R.id.constraintLayout_item_id);

            postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, userID, getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, String userID, int position);
    }

    public void onItemSetOnClickListener(NewsAdapterCompany.OnItemClickListener listener) {
        this.listener = listener;
    }
}
