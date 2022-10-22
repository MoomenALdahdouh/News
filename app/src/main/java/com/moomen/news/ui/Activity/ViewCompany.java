package com.moomen.news.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.news.R;
import com.moomen.news.adapter.NewsAdapter;
import com.moomen.news.model.PostNews;
import com.moomen.news.model.User;
import com.moomen.news.ui.fragment.UserFragmentAdmin;
import com.squareup.picasso.Picasso;

public class ViewCompany extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    public final static String USER_ID = "USER_ID";
    public final static String NEWS_ID = "NEWS_ID";
    public final static String IS_ADMIN = "IS_ADMIN";

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView dateOfBirthTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView genderTextView;
    private TextView userTypeTextView;
    private TextView dateCreateTextView;
    private ImageView userImageView;
    private TextView aboutUserTextView;
    private ImageView statusImageView;
    private Switch statusSwitch;
    private RecyclerView recyclerViewUserActivity;
    private NewsAdapter newsAdapter;

    private LinearLayout linearLayoutStatus;
    private LinearLayout linearLayoutRemove;
    private ImageView imageViewStatus;
    private TextView statusTextView;

    private String userName;
    private String userEmail;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String gender;
    private String userType;
    private String dateCreate;
    private String userImage;
    private boolean status;
    private String aboutUser;
    private String userID = "";

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_company);

        userNameTextView = findViewById(R.id.text_view_user_name_id);
        userEmailTextView = findViewById(R.id.text_view_user_email_id);
        addressTextView = findViewById(R.id.text_view_user_location_id);
        phoneTextView = findViewById(R.id.textView_user_phone_id);
        genderTextView = findViewById(R.id.textView_user_gender_id);
        userTypeTextView = findViewById(R.id.textView_user_type_id);
        dateCreateTextView = findViewById(R.id.text_view_date_user_create_id);
        userImageView = findViewById(R.id.image_view_account_user_id);
        aboutUserTextView = findViewById(R.id.textView_about_user_id);
        statusImageView = findViewById(R.id.image_view_user_status_id);
        recyclerViewUserActivity = findViewById(R.id.recycler_view_activity_user_id);

        linearLayoutRemove = findViewById(R.id.linear_layout_remove_company_id);
        linearLayoutStatus = findViewById(R.id.linear_layout_status_company_id);
        imageViewStatus = findViewById(R.id.image_view_status_company_id);
        statusTextView = findViewById(R.id.text_view_status_company_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(UserFragmentAdmin.USER_ID)) {
            userID = intent.getStringExtra(UserFragmentAdmin.USER_ID);
        }
        user = new User();
        firebaseFirestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                userName = user.getFirstName() + " " + user.getLastName();
                userImage = user.getUserImage();
                userEmail = user.getEmail();
                dateOfBirth = user.getDateOfBirth();
                address = user.getAddress();
                phone = user.getPhone();
                gender = user.getGender();
                userType = user.getUserType();
                dateCreate = user.getDateCreate();
                aboutUser = user.getAboutCompany();
                status = user.isStatus();

                userNameTextView.setText(userName);
                Picasso.get()
                        .load(userImage)
                        .into(userImageView);
                userEmailTextView.setText(userEmail);
                dateCreateTextView.setText(dateCreate);
                addressTextView.setText(address);
                phoneTextView.setText(phone);
                genderTextView.setText(gender);
                userTypeTextView.setText(userType);
                dateCreateTextView.setText(dateCreate);
                aboutUserTextView.setText(aboutUser);

                adminRemoveAndChangeStatusUsers(user);
            }
        });
        getUserActivity();
    }

    private void getUserActivity() {
        Query query = FirebaseFirestore.getInstance().collection("News")
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("companyID", (userID));
        FirestoreRecyclerOptions<PostNews> options = new FirestoreRecyclerOptions.Builder<PostNews>()
                .setQuery(query, PostNews.class)
                .build();
        newsAdapter = new NewsAdapter(options);
        newsAdapter.setAdmin(true);
        newsAdapter.onItemSetOnClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot,String userID, int position) {
                String newsID = documentSnapshot.getId();
                Intent intent = new Intent(getApplicationContext(), MannageNewsCompany.class);
                intent.putExtra(NEWS_ID, newsID);
                intent.putExtra(USER_ID, userID);
                intent.putExtra(IS_ADMIN, "true");
                startActivity(intent);
            }
        });
        newsAdapter.onPaymentSetOnClickListener(new NewsAdapter.OnPaymentClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, String userID, int position) {
                startActivity(new Intent(getApplicationContext(),PaymentActivity.class));
            }
        });
        newsAdapter.setContext(this);
        recyclerViewUserActivity.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserActivity.setAdapter(newsAdapter);
        newsAdapter.startListening();
    }
    DocumentReference documentReference;
    private void adminRemoveAndChangeStatusUsers(User user) {
        documentReference = firebaseFirestore.collection("Users").document(userID);
        //Get status
        if (user.isStatus()) {
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
                //Delete from fireStore
                documentReference.delete();
                finish();
            }
        });
        //Change status
        linearLayoutStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call the news again when click because changed value when clicked
                firebaseFirestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        documentReference = firebaseFirestore.collection("Users").document(userID);
                        //1- If user not blocked and news not blocked from admin and news is visible
                        assert user != null;
                        documentReference.update("status", !user.isStatus()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (user.isStatus()) {
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
}