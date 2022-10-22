package com.moomen.news.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.moomen.news.R;
import com.moomen.news.adapter.NewsAdapterOld;
import com.moomen.news.model.Comments;
import com.moomen.news.model.PostNews;
import com.moomen.news.viewModel.HomeViewModel;

import java.util.ArrayList;

public class MainActivityCompany extends AppCompatActivity {
    private HomeViewModel homeViewModel;
    private FirebaseAuth firebaseAuth;
    private ImageButton createNewsButton;

    private RecyclerView recyclerView;
    private NewsAdapterOld newsAdapterOld;
    private ArrayList<PostNews> postNewsArrayList;
    private ArrayList<Comments> commentsArrayList;
    private ArrayList<String> tagsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        /*createNewsButton = findViewById(R.id.button_create_new_news_id);
        createNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityCompany.this, CreateNewsActivity.class));
            }
        });*/
    }
}