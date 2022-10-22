package com.moomen.news.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.R;
import com.moomen.news.adapter.CommentsAdapter;
import com.moomen.news.model.Comments;
import com.moomen.news.model.News;
import com.moomen.news.model.PostNews;
import com.moomen.news.ui.fragment.DashboardFragmentCompany;
import com.moomen.news.ui.fragment.HomeFragment;
import com.moomen.news.ui.fragment.HomeFragmentAdmin;
import com.moomen.news.ui.fragment.HomeFragmentCompany;
import com.moomen.news.ui.fragment.UserFragmentAdmin;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ViewNews extends AppCompatActivity {

    private String newsID = "";
    private String userID = "";

    private ImageView companyImageView;
    private TextView nameCompanyTextView;
    private TextView locationNewsTextView;
    private TextView titleNewsTextView;
    private TextView descriptionNewsTextView;
    private ImageView newsImageView;
    private ImageView imageViewFull;

    private String companyImage;
    private String companyName;
    private String locationNews;
    private String titleNews;
    private String descriptionNews;
    private String imageNews;
    private ArrayList<Comments> commentsAdapterArrayList = new ArrayList<>();

    private RecyclerView commentNewsRecyclerView;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        companyImageView = findViewById(R.id.imageView_company_id);
        nameCompanyTextView = findViewById(R.id.text_view_company_name_id);
        locationNewsTextView = findViewById(R.id.text_view_address_news_id);
        titleNewsTextView = findViewById(R.id.text_view_title_news_id);
        descriptionNewsTextView = findViewById(R.id.text_view_description_news_id);
        newsImageView = findViewById(R.id.image_view_post_news_id);


        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(HomeFragmentCompany.NEWS_ID) && intent.hasExtra(HomeFragmentCompany.USER_ID)) {
                newsID = intent.getStringExtra(HomeFragmentCompany.NEWS_ID);
                userID = intent.getStringExtra(HomeFragmentCompany.USER_ID);
            }
            if (intent.hasExtra(HomeFragmentAdmin.NEWS_ID) && intent.hasExtra(HomeFragmentAdmin.USER_ID)) {
                newsID = intent.getStringExtra(HomeFragmentAdmin.NEWS_ID);
                userID = intent.getStringExtra(HomeFragmentAdmin.USER_ID);
            }
            if (intent.hasExtra(HomeFragment.NEWS_ID) && intent.hasExtra(HomeFragment.USER_ID)) {
                newsID = intent.getStringExtra(HomeFragment.NEWS_ID);
                userID = intent.getStringExtra(HomeFragment.USER_ID);
            }
            if (intent.hasExtra(DashboardFragmentCompany.NEWS_ID_COMPANY) && intent.hasExtra(DashboardFragmentCompany.USER_ID_COMPANY)) {
                newsID = intent.getStringExtra(DashboardFragmentCompany.NEWS_ID_COMPANY);
                userID = intent.getStringExtra(DashboardFragmentCompany.USER_ID_COMPANY);
            }
            if (intent.hasExtra(ViewCompany.NEWS_ID) && intent.hasExtra(ViewCompany.USER_ID)) {
                newsID = intent.getStringExtra(ViewCompany.NEWS_ID);
                userID = intent.getStringExtra(ViewCompany.USER_ID);
            }
            getCompanyInfo();
        }
        getNewsInfo();
    }


    //get company info
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

    //get News info
    private void getNewsInfo() {
        firebaseFirestore.collection("News").document(newsID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PostNews postNews = documentSnapshot.toObject(PostNews.class);
                locationNews = postNews.getCountry();
                titleNews = postNews.getTitle();
                descriptionNews = postNews.getDescription();
                imageNews = postNews.getImage();
                commentsAdapterArrayList = postNews.getCommentsArrayList();
                getAllCommentNews();
                locationNewsTextView.setText(locationNews);
                titleNewsTextView.setText(titleNews);
                descriptionNewsTextView.setText(descriptionNews);
                Picasso.get()
                        .load(imageNews)
                        .into(newsImageView);

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

    //get all comments
    private void getAllCommentNews() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_comments_id);
        CommentsAdapter commentsAdapter = new CommentsAdapter(this, commentsAdapterArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentsAdapter);
    }
}