package com.moomen.news.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.news.adapter.NewsAdapter;
import com.moomen.news.adapter.NewsAdapterOld;
import com.moomen.news.adapter.NewsAdapterCompany;
import com.moomen.news.adapter.UsersAdapter;
import com.moomen.news.model.Comments;
import com.moomen.news.model.PostNews;
import com.moomen.news.ui.Activity.OpenUserProfile;
import com.moomen.news.ui.Activity.PaymentActivity;
import com.moomen.news.ui.Activity.ViewNews;
import com.moomen.news.viewModel.HomeViewModel;
import com.moomen.news.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static final String NEWS_ID = "NEWS_ID";
    public static final String USER_ID = "USER_ID";

    private View view;
    private HomeViewModel homeViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userID;

    private RecyclerView recyclerView;
    private NewsAdapterOld newsAdapterOld;
    private ArrayList<PostNews> postNewsArrayList;
    private ArrayList<Comments> commentsArrayList;
    private ArrayList<String> tagsArrayList;

    private Button allTagButton;
    private Button breakingTagButton;
    private Button lifestyleTagButton;
    private Button musicTagButton;
    private Button entertainmentTagButton;
    private Button technologyTagButton;
    private Button politicsTagButton;
    private Button localTagButton;

    Query query = FirebaseFirestore.getInstance().collection("News");
    FirestoreRecyclerOptions<PostNews> options;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        allTagButton = view.findViewById(R.id.button10);
        breakingTagButton = view.findViewById(R.id.button11);
        lifestyleTagButton = view.findViewById(R.id.button12);
        musicTagButton = view.findViewById(R.id.button13);
        entertainmentTagButton = view.findViewById(R.id.button14);
        technologyTagButton = view.findViewById(R.id.button15);
        politicsTagButton = view.findViewById(R.id.button16);
        localTagButton = view.findViewById(R.id.button17);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseUser.getUid();

        buttonSetOnClickListener(allTagButton);
        buttonSetOnClickListener(breakingTagButton);
        buttonSetOnClickListener(lifestyleTagButton);
        buttonSetOnClickListener(musicTagButton);
        buttonSetOnClickListener(entertainmentTagButton);
        buttonSetOnClickListener(technologyTagButton);
        buttonSetOnClickListener(politicsTagButton);
        buttonSetOnClickListener(localTagButton);
        getNews();
        return view;
    }


    //Get Info from realTime Database
        /*databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null){
                    String fullName, email;
                    fullName = userProfile.getFirstName() + " " +userProfile.getLastName();
                    email = userProfile.getEmail();
                    nameTextView.setText(fullName);
                    emailTextView.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });*/


    //Old code
    private void getNew() {
        firebaseFirestore.collection("News").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            PostNews postNews = doc.getDocument().toObject(PostNews.class);
                            postNewsArrayList.add(postNews);
                        }
                    }
                    newsAdapterOld.notifyDataSetChanged();
                }
            }
        });
    }

    private void getNews() {
        Query query = FirebaseFirestore.getInstance().collection("News");
        query.whereEqualTo("visibility", true)
                .whereEqualTo("newsStatus", true)
                .orderBy("date", Query.Direction.DESCENDING);
        options = new FirestoreRecyclerOptions.Builder<PostNews>()
                .setQuery(query, PostNews.class)
                .build();
        fillRecycleAdapter(options);
    }

    private void buttonSetOnClickListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (button.getText().equals("Local")) {
                    DocumentReference df = firebaseFirestore.collection("Users").document(userID);
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot != null) {
                                String address = documentSnapshot.getString("address");
                                query = FirebaseFirestore.getInstance().collection("News")
                                        .whereEqualTo("visibility", true)
                                        .whereEqualTo("newsStatus", true)
                                        .whereEqualTo("country", address + "")
                                        .orderBy("date", Query.Direction.DESCENDING);
                                options = new FirestoreRecyclerOptions.Builder<PostNews>()
                                        .setQuery(query, PostNews.class)
                                        .build();
                                fillRecycleAdapter(options);
                            }
                        }
                    });
                } else {
                    query = FirebaseFirestore.getInstance().collection("News")
                            .whereEqualTo("visibility", true)
                            .whereEqualTo("newsStatus", true)
                            .whereArrayContains("tagArrayList", button.getText() + "")
                            .orderBy("date", Query.Direction.DESCENDING);
                    options = new FirestoreRecyclerOptions.Builder<PostNews>()
                            .setQuery(query, PostNews.class)
                            .build();
                    fillRecycleAdapter(options);
                }
                /*options = new FirestoreRecyclerOptions.Builder<PostNews>()
                        .setQuery(query, PostNews.class)
                        .build();
                fillRecycleAdapter(options);*/
            }
        });
    }

    private void fillRecycleAdapter(FirestoreRecyclerOptions<PostNews> options) {
        NewsAdapter newsAdapter = new NewsAdapter(options);
        newsAdapter.onItemSetOnClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, String userID, int position) {
                String newsID = documentSnapshot.getId();
                //User user = documentSnapshot.toObject(User.class);
                Intent intent = new Intent(getContext(), ViewNews.class);
                intent.putExtra(NEWS_ID, newsID);
                intent.putExtra(USER_ID, userID);
                startActivity(intent);
            }
        });
        newsAdapter.onUserNameSetOnClickListener(new NewsAdapter.OnUserNameClickListener() {
            @Override
            public void onUserNameClick(String userID, int position) {
                Intent intent = new Intent(getContext(), OpenUserProfile.class);
                intent.putExtra(USER_ID, userID);
                startActivity(intent);
            }
        });

        newsAdapter.onPaymentSetOnClickListener(new NewsAdapter.OnPaymentClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, String userID, int position) {
                startActivity(new Intent(getContext(), PaymentActivity.class));
            }
        });
        newsAdapter.setContext(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_news_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setHasFixedSize(true);
        newsAdapter.startListening();
    }
}