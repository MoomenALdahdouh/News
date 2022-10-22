package com.moomen.news.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.news.R;
import com.moomen.news.model.Comments;
import com.moomen.news.model.Likes;
import com.moomen.news.model.PostNews;
import com.moomen.news.ui.Activity.CreateNewsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapter extends FirestoreRecyclerAdapter<PostNews, NewsAdapter.ViewHolder> {
    private Context context;
    private View view;
    boolean clickLike = true;
    private String userID = "";
    int likes = 0;
    String userName = "";
    String userImage = "";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DocumentReference documentReference;
    private NewsAdapter.OnItemClickListener listener;
    private NewsAdapter.OnUserNameClickListener listenerUserName;
    private NewsAdapter.OnPaymentClickListener listenerPayment;

    private boolean isCompany = false;
    private boolean isAdmin = false;

    public void setCompany(boolean company) {
        isCompany = company;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NewsAdapter(@NonNull FirestoreRecyclerOptions<PostNews> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position, @NonNull PostNews model) {
        Picasso.get()
                .load(model.getCompanyImage())
                .into(holder.companyImageView);
        userName = model.getCompanyName();
        userID = model.getCompanyID();
        holder.companyNameTextView.setText(userName);
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
        if (!isAdmin || !isCompany) {
            if (model.isPaid()) {
                holder.paymentLayout.setVisibility(View.VISIBLE);
            } else
                holder.paymentLayout.setVisibility(View.GONE);
        }
        //Change image send comment
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
        String newsID = getSnapshots().getSnapshot(position).getId();
        String userID = firebaseUser.getUid();
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = holder.commentEditText.getText().toString();
                if (!comment.isEmpty()) {
                    //updateDocument(model);
                    documentReference = firebaseFirestore.collection("News").document(newsID);
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
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                } else
                    Toast.makeText(context, "Comment is Empty!, Please Enter Comment", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void postLikeOnFirebase(ViewHolder holder, PostNews postNews, String newsID, String userID) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("News").document(newsID);
        DocumentReference dfLikes = firebaseFirestore.collection("Likes").document(newsID);
        firebaseFirestore.collection("Likes").document("newsID");
        likes = postNews.getLike();
        dfLikes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                documentSnapshot.getDocumentReference("usersID").collection(userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (likes > 0) {
                            documentReference.update("like", likes - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.likeCountImageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    holder.likeCountTextView.setText(likes - 1 + "");
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.likeCountImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                documentReference.update("like", likes + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.likeCountImageView.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                                        holder.likeCountTextView.setText(likes + 1 + "");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ArrayList<String> userIDArr = new ArrayList();
                userIDArr.add(userID);
                Likes likesOp = new Likes(newsID, userIDArr);
                dfLikes.collection("News").add(likesOp).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
    }

    ViewGroup viewGroup;

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.news_item, parent, false);
        return new NewsAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView companyImageView;
        TextView companyNameTextView;
        TextView datePostTextView;
        ImageView postImageView;
        ImageView shareImageView;
        TextView showingCountTextView;
        TextView likeCountTextView;
        ImageView likeCountImageView;
        TextView titlePostTextView;
        TextView descriptionPostTextView;
        EditText commentEditText;
        TextView commentCount;
        ImageButton commentButton;
        ConstraintLayout commentsConstraintLayout;
        ImageView userCommentImageView;
        TextView userNameComment;
        TextView commentUser;
        LinearLayout seeMoreComment;
        ProgressBar progressBar;
        TextView locationTextView;
        ConstraintLayout constraintLayoutItem;

        LinearLayout settingEditLinearLayout;
        LinearLayout settingLinearLayout;
        LinearLayout editLinearLayout;

        ConstraintLayout paymentLayout;
        Button paymentButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyImageView = itemView.findViewById(R.id.image_view_company_id);
            companyNameTextView = itemView.findViewById(R.id.text_view_company_name_id);
            datePostTextView = itemView.findViewById(R.id.text_view_date_published_id);
            postImageView = itemView.findViewById(R.id.image_view_post_news_id);
            shareImageView = itemView.findViewById(R.id.image_view_share_post_id);
            showingCountTextView = itemView.findViewById(R.id.text_view_seeing_id);
            likeCountTextView = itemView.findViewById(R.id.text_view_like_id);
            likeCountImageView = itemView.findViewById(R.id.image_view_like_id);
            titlePostTextView = itemView.findViewById(R.id.text_view_title_post_id);
            descriptionPostTextView = itemView.findViewById(R.id.text_view_description_post_id);
            commentEditText = itemView.findViewById(R.id.edit_text_comment_id);
            commentCount = itemView.findViewById(R.id.textView_number_comment_id);
            commentButton = itemView.findViewById(R.id.image_view_comment_post_id);
            progressBar = itemView.findViewById(R.id.progressBar);
            locationTextView = itemView.findViewById(R.id.text_view_location_id);
            commentsConstraintLayout = itemView.findViewById(R.id.constraintLayout_first_comment_id);
            userCommentImageView = itemView.findViewById(R.id.image_view_user_image_comment_id);
            userNameComment = itemView.findViewById(R.id.text_view_user_name_comment_id);
            commentUser = itemView.findViewById(R.id.text_view_comment_id);
            seeMoreComment = itemView.findViewById(R.id.linearLayout_see_more_comment_id);
            constraintLayoutItem = itemView.findViewById(R.id.constraintLayout_item_id);

            settingEditLinearLayout = itemView.findViewById(R.id.linearLayout_sitting_edit_news_id);
            settingLinearLayout = itemView.findViewById(R.id.linear_setting_news_id);
            editLinearLayout = itemView.findViewById(R.id.linear_edit_news_id);

            paymentLayout = itemView.findViewById(R.id.constraintLayout_payment_id);
            paymentButton = itemView.findViewById(R.id.button_payment_id);

            if (isAdmin) {
                settingEditLinearLayout.setVisibility(View.VISIBLE);
                settingLinearLayout.setVisibility(View.VISIBLE);
                editLinearLayout.setVisibility(View.GONE);
            } else if (isCompany) {
                settingEditLinearLayout.setVisibility(View.VISIBLE);
                settingLinearLayout.setVisibility(View.VISIBLE);
                editLinearLayout.setVisibility(View.VISIBLE);
            } else {
                settingEditLinearLayout.setVisibility(View.GONE);
            }

            paymentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listenerPayment.onItemClick(documentSnapshot, userID, getAdapterPosition());
                    }
                }
            });

            postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, userID, getAdapterPosition());
                    }
                }
            });
            settingLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, userID, getAdapterPosition());
                    }
                }
            });
            companyNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        listenerUserName.onUserNameClick(userID, getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, String userID, int position);
    }

    public void onItemSetOnClickListener(NewsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void onUserNameSetOnClickListener(NewsAdapter.OnUserNameClickListener listenerUserName) {
        this.listenerUserName = listenerUserName;
    }

    public interface OnUserNameClickListener {
        void onUserNameClick(String userID, int position);
    }

    public interface OnPaymentClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, String userID, int position);
    }

    public void onPaymentSetOnClickListener(NewsAdapter.OnPaymentClickListener listenerPayment) {
        this.listenerPayment = listenerPayment;
    }
}
