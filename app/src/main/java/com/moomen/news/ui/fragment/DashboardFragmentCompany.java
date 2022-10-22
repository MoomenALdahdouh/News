package com.moomen.news.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.news.R;
import com.moomen.news.adapter.NewsAdapter;
import com.moomen.news.adapter.NewsAdapter;
import com.moomen.news.adapter.UsersAdapter;
import com.moomen.news.model.PostNews;
import com.moomen.news.model.User;
import com.moomen.news.ui.Activity.CreateNewsActivity;
import com.moomen.news.ui.Activity.MainActivityCompany;
import com.moomen.news.ui.Activity.MannageNewsCompany;
import com.moomen.news.ui.Activity.OpenUserProfile;
import com.moomen.news.ui.Activity.ViewNews;
import com.moomen.news.viewModel.DashboardViewModel;

public class DashboardFragmentCompany extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FirebaseUser firebaseUser;
    private NewsAdapter newsAdapter;
    private View view;
    public final static String USER_ID_COMPANY = "USER_ID_COMPANY";
    public final static String NEWS_ID_COMPANY = "NEWS_ID_COMPANY";

    private ConstraintLayout constraintLayoutBlocked;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        view = inflater.inflate(R.layout.fragment_dashboard_company, container, false);

        constraintLayoutBlocked = view.findViewById(R.id.constraint_blocked_account_id);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ImageButton createNewsButton = view.findViewById(R.id.button_create_new_news_id);
        createNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateNewsActivity.class));
            }
        });
        setUpRecyclerView();
        return view;
    }

    User user = new User();

    private void setUpRecyclerView() {
        //Check if user blocked or not
        FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                //if user is not blocked
                if (user.isStatus()) {
                    constraintLayoutBlocked.setVisibility(View.GONE);
                    Query query = FirebaseFirestore.getInstance().collection("News")
                            .orderBy("date", Query.Direction.DESCENDING)
                            .whereEqualTo("companyID", (firebaseUser.getUid()));
                    FirestoreRecyclerOptions<PostNews> options = new FirestoreRecyclerOptions.Builder<PostNews>()
                            .setQuery(query, PostNews.class)
                            .build();
                    newsAdapter = new NewsAdapter(options);
                    newsAdapter.setCompany(true);
                    newsAdapter.onItemSetOnClickListener(new NewsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DocumentSnapshot documentSnapshot, String userID, int position) {
                            String newsID = documentSnapshot.getId();
                            Intent intent = new Intent(getContext(), MannageNewsCompany.class);
                            intent.putExtra(NEWS_ID_COMPANY, newsID);
                            intent.putExtra(USER_ID_COMPANY, userID);
                            startActivity(intent);
                        }
                    });
                    newsAdapter.onUserNameSetOnClickListener(new NewsAdapter.OnUserNameClickListener() {
                        @Override
                        public void onUserNameClick(String userID, int position) {
                        }
                    });
                    newsAdapter.setContext(getContext());
                    RecyclerView recyclerView = view.findViewById(R.id.recycler_view_news_id);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(newsAdapter);
                    recyclerView.setHasFixedSize(true);
                    newsAdapter.startListening();
                } else {
                    constraintLayoutBlocked.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
       /* if (newsAdapter != null && user.isStatus())
            newsAdapter.startListening();
        else
            constraintLayoutBlocked.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onStop() {
        super.onStop();
       /* if (newsAdapter != null && user.isStatus())
            newsAdapter.stopListening();
        else
            constraintLayoutBlocked.setVisibility(View.VISIBLE);*/
    }
}