package com.moomen.news.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.news.R;
import com.moomen.news.adapter.UsersAdapter;
import com.moomen.news.model.User;
import com.moomen.news.ui.Activity.ViewCompany;

public class AdminFragmentAdmin extends Fragment {

    private View view;
    public final static String USER_ID = "USER_ID";
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_admin, container, false);
        getCompanies();
        return view;
    }
    private void getCompanies() {
        Query query = FirebaseFirestore.getInstance().collection("Users")
                .orderBy("dateCreate", Query.Direction.DESCENDING)
                .whereEqualTo("userType", ("admin"));
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        UsersAdapter usersAdapter = new UsersAdapter(options);
        usersAdapter.onItemSetOnClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String userID = documentSnapshot.getId();
                //User user = documentSnapshot.toObject(User.class);
                Intent intent = new Intent(getContext(), ViewCompany.class);
                intent.putExtra(USER_ID, userID);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_company_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(usersAdapter);
        recyclerView.setHasFixedSize(true);
        usersAdapter.startListening();
    }
}