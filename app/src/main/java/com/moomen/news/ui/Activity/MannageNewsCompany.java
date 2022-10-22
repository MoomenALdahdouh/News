package com.moomen.news.ui.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.R;
import com.moomen.news.adapter.CommentsAdapter;
import com.moomen.news.model.Comments;
import com.moomen.news.model.PostNews;
import com.moomen.news.ui.fragment.DashboardFragmentCompany;
import com.moomen.news.ui.fragment.HomeFragment;
import com.moomen.news.ui.fragment.HomeFragmentAdmin;
import com.moomen.news.ui.fragment.HomeFragmentCompany;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MannageNewsCompany extends AppCompatActivity {

    private String newsID = "";
    private String userID = "";

    private ImageView companyImageView;
    private TextView nameCompanyTextView;
    private TextView locationNewsTextView;
    private TextView titleNewsTextView;
    private TextView descriptionNewsTextView;
    private ImageView newsImageView;
    private ImageView imageViewFull;
    private TextView statusTextView;
    private ImageView imageViewStatus;
    private ImageView imageViewRemove;
    private LinearLayout linearLayoutStatus;
    private LinearLayout linearLayoutRemove;

    private String companyImage;
    private String companyName;
    private String locationNews;
    private String titleNews;
    private String descriptionNews;
    private String imageNews;

    private FirebaseFirestore firebaseFirestore;

    private String isAdmin = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mannage_news_company);

        companyImageView = findViewById(R.id.imageView_company_id);
        nameCompanyTextView = findViewById(R.id.text_view_company_name_id);
        locationNewsTextView = findViewById(R.id.text_view_address_news_id);
        titleNewsTextView = findViewById(R.id.text_view_title_news_id);
        descriptionNewsTextView = findViewById(R.id.text_view_description_news_id);
        newsImageView = findViewById(R.id.image_view_post_news_id);
        linearLayoutRemove = findViewById(R.id.linear_layout_remove_news_id);
        linearLayoutStatus = findViewById(R.id.linear_layout_status_news_id);
        imageViewStatus = findViewById(R.id.image_view_status_post_id);
        statusTextView = findViewById(R.id.text_view_status_post_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
             if (intent.hasExtra(DashboardFragmentCompany.NEWS_ID_COMPANY) && intent.hasExtra(DashboardFragmentCompany.USER_ID_COMPANY) ) {
                newsID = intent.getStringExtra(DashboardFragmentCompany.NEWS_ID_COMPANY);
                userID = intent.getStringExtra(DashboardFragmentCompany.USER_ID_COMPANY);
            } else if (intent.hasExtra(ViewCompany.NEWS_ID) && intent.hasExtra(ViewCompany.USER_ID)) {
                newsID = intent.getStringExtra(ViewCompany.NEWS_ID);
                userID = intent.getStringExtra(ViewCompany.USER_ID);
                isAdmin = "true";
            } else if (intent.hasExtra(HomeFragmentAdmin.NEWS_ID) && intent.hasExtra(HomeFragmentAdmin.USER_ID)) {
                newsID = intent.getStringExtra(HomeFragmentAdmin.NEWS_ID);
                userID = intent.getStringExtra(HomeFragmentAdmin.USER_ID);
                 isAdmin = "true";
            }
        }
        getCompanyInfo();
        getNewsInfo();
    }


    //get company info name and image
    private void getCompanyInfo() {
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                companyName = task.getResult().getString("firstName") + " " + task.getResult().getString("lastName");
                companyImage = task.getResult().getString("userImage");
                nameCompanyTextView.setText(companyName);
                Picasso.get()
                        .load(companyImage)
                        .into(companyImageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private PostNews postNews;

    //get News info
    private void getNewsInfo() {
        firebaseFirestore.collection("News").document(newsID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                postNews = documentSnapshot.toObject(PostNews.class);
                locationNews = postNews.getCountry();
                titleNews = postNews.getTitle();
                descriptionNews = postNews.getDescription();
                imageNews = postNews.getImage();
                locationNewsTextView.setText(locationNews);
                titleNewsTextView.setText(titleNews);
                descriptionNewsTextView.setText(descriptionNews);
                Picasso.get()
                        .load(imageNews)
                        .into(newsImageView);

                if (isAdmin.equals("true"))
                    adminRemoveAndChangeStatusPostNews(postNews);
                else
                    companyRemoveAndChangeStatusPostNews(postNews);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void backOnClick(View view) {
        finish();
    }

    // Show image on full screen
    private void showImageFillToZoom() {
        //Deffin dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_view_image, null);
        builder.setView(dialogView);
        // Show image on full screen
        AlertDialog alertDialog = builder.create();
        ProgressBar progressBar = dialogView.findViewById(R.id.progress_bar_image_dialog);
        ConstraintLayout constraintLayout = dialogView.findViewById(R.id.constraintLayout_full_image_id);
        imageViewFull = dialogView.findViewById(R.id.imageView_full_id);
        Picasso.get()
                .load(imageNews)
                .into(imageViewFull, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("e", e.getMessage());
                    }
                });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void openImageToZoom(View view) {
        showImageFillToZoom();
    }

    //Remove news post
    DocumentReference documentReference;

    private void adminRemoveAndChangeStatusPostNews(PostNews model) {
        documentReference = firebaseFirestore.collection("News").document(newsID);
        //Get status
        if (model.isNewsStatus()) {
            //Initial state
            statusTextView.setText("Enable");
            imageViewStatus.setImageResource(R.drawable.ic_baseline_visibility_24);
        } else {
            statusTextView.setText("Block");
            imageViewStatus.setImageResource(R.drawable.ic_baseline_block_24);
        }//Remove news
        linearLayoutRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Removed", Toast.LENGTH_SHORT).show();
                documentReference.delete();
                finish();
            }
        });
        //Change status
        linearLayoutStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call the news again when click because changed value when clicked
                firebaseFirestore.collection("News").document(newsID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        postNews = documentSnapshot.toObject(PostNews.class);
                        documentReference = firebaseFirestore.collection("News").document(newsID);
                        //1- If user not blocked and news not blocked from admin and news is visible
                        documentReference.update("newsStatus", !postNews.isNewsStatus()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (postNews.isNewsStatus()) {
                                    Toast.makeText(getApplicationContext(), "Block", Toast.LENGTH_SHORT).show();
                                    statusTextView.setText("Block");
                                    imageViewStatus.setImageResource(R.drawable.ic_baseline_block_24);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Enable", Toast.LENGTH_SHORT).show();
                                    statusTextView.setText("Enable");
                                    imageViewStatus.setImageResource(R.drawable.ic_baseline_visibility_24);
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    public void companyRemoveAndChangeStatusPostNews(PostNews model) {
        documentReference = firebaseFirestore.collection("News").document(newsID);
        if (model.isCompanyStatus()) {
            //If user not blocked from admin
            linearLayoutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Removed", Toast.LENGTH_SHORT).show();
                    documentReference.delete();
                    finish();
                }
            });
            if (model.isNewsStatus()) {
                //Get status
                if (model.isVisibility()) {
                    //Initial state
                    statusTextView.setText("Enable");
                    imageViewStatus.setImageResource(R.drawable.ic_baseline_visibility_24);
                } else {
                    statusTextView.setText("Disable");
                    imageViewStatus.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }
                //Change status
                linearLayoutStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Call the news again when click because changed value when clicked
                        firebaseFirestore.collection("News").document(newsID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                postNews = documentSnapshot.toObject(PostNews.class);
                                documentReference = firebaseFirestore.collection("News").document(newsID);
                                //1- If user not blocked and news not blocked from admin and news is visible
                                documentReference.update("visibility", !postNews.isVisibility()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (postNews.isVisibility()) {
                                            Toast.makeText(getApplicationContext(), "Disable", Toast.LENGTH_SHORT).show();
                                            statusTextView.setText("Disable");
                                            imageViewStatus.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Enable", Toast.LENGTH_SHORT).show();
                                            statusTextView.setText("Enable");
                                            imageViewStatus.setImageResource(R.drawable.ic_baseline_visibility_24);
                                        }
                                    }
                                });
                            }
                        });

                    }
                });
            } else {
                statusTextView.setText("Blocked");
                imageViewStatus.setImageResource(R.drawable.ic_baseline_block_24);
                linearLayoutStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "This news is blocked from admin!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            //Todo: Show Layout Blocked User You can not arrived to any things contact with support team
            Toast.makeText(getApplicationContext(), "You are Blocked!", Toast.LENGTH_SHORT).show();
        }
    }
}