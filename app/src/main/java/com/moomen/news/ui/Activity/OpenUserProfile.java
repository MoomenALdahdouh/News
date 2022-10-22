package com.moomen.news.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.news.R;
import com.moomen.news.adapter.NewsAdapter;
import com.moomen.news.adapter.NewsAdapter;
import com.moomen.news.model.PostNews;
import com.moomen.news.ui.fragment.HomeFragmentCompany;
import com.squareup.picasso.Picasso;


public class OpenUserProfile extends AppCompatActivity {
    private ImageView userImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView dateCreateTextView;
    private TextView aboutTextView;
    private TextView genderTextView;
    private TextView addressTextView;

    private RecyclerView recyclerViewActivity;

    private FirebaseFirestore firebaseFirestore;
    private String userName;
    private String userEmail;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String gender;
    private String userType;
    private  String dateCreate;
    private  String userImage;
    private boolean status;
    private String aboutUser;
    private String userID = "";
    public final static String NEWS_ID = "NEWS_ID";
    public final static String USER_ID = "USER_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_user_profile);

        userImageView = findViewById(R.id.imageView_user_id);
        nameTextView = findViewById(R.id.text_view_user_name_id);
        emailTextView = findViewById(R.id.text_view_user_email_id);
        phoneTextView = findViewById(R.id.textView_user_phone_id);
        genderTextView = findViewById(R.id.textView_user_gender_id);
        dateCreateTextView = findViewById(R.id.text_view_date_user_create_id);
        addressTextView = findViewById(R.id.text_view_user_location_id);
        aboutTextView = findViewById(R.id.textView_about_user_id);
        recyclerViewUserActivity = findViewById(R.id.recycler_view_activity_user_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(HomeFragmentCompany.USER_ID)) {
            userID = intent.getStringExtra(HomeFragmentCompany.USER_ID);
        }

        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userName = task.getResult().getString("firstName") + " " + task.getResult().getString("lastName");
                userImage = task.getResult().getString("userImage");
                userEmail = task.getResult().getString("email");
                dateOfBirth = task.getResult().getString("dateOfBirth");
                address = task.getResult().getString("address");
                phone = task.getResult().getString("phone");
                gender = task.getResult().getString("gender");
                userType = task.getResult().getString("userType");
                dateCreate = task.getResult().getString("dateCreate");
                aboutUser = task.getResult().getString("aboutUser");
                //status = task.getResult().getBoolean("status");

                nameTextView.setText(userName);
                Picasso.get()
                        .load(userImage)
                        .into(userImageView);
                emailTextView.setText(userEmail);
                dateCreateTextView.setText(dateCreate);
                addressTextView.setText(address);
                phoneTextView.setText(phone);
                genderTextView.setText(gender);
                dateCreateTextView.setText(dateCreate);
                aboutTextView.setText(aboutUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        getUserActivity();
    }
    private RecyclerView recyclerViewUserActivity;
    private NewsAdapter newsAdapter;
    private void getUserActivity() {
        Query query = FirebaseFirestore.getInstance().collection("News")
                .whereEqualTo("visibility",true)
                .whereEqualTo("newsStatus",true)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("companyID", (userID));
        FirestoreRecyclerOptions<PostNews> options = new FirestoreRecyclerOptions.Builder<PostNews>()
                .setQuery(query, PostNews.class)
                .build();
        newsAdapter = new NewsAdapter(options);
        newsAdapter.setContext(this);
        recyclerViewUserActivity.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserActivity.setAdapter(newsAdapter);
        newsAdapter.startListening();
    }
}