package com.moomen.news.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.R;
import com.moomen.news.utils.PreferenceUtils;

public class SplashActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        View decorViewFull = getWindow().getDecorView();
        decorViewFull.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceUtils.getEmail(SplashActivity.this) != null && !PreferenceUtils.getEmail(SplashActivity.this).equals("")) {
                    //Check type user
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    checkUserTypeToSignIn(userID);
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginRegisterActivity.class));
                    finish();
                }
            }
        }, 3000);
    }

    private void checkUserTypeToSignIn(String userID) {
        DocumentReference df = firebaseFirestore.collection("Users").document(userID);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userType = documentSnapshot.getString("userType");
                if (userType != null) {
                    switch (userType) {
                        case "user":
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            break;
                        case "company":
                            startActivity(new Intent(SplashActivity.this, MainActivityCompany.class));
                            break;
                        case "admin":
                            startActivity(new Intent(SplashActivity.this, MainActivityAdmin.class));
                            break;
                    }
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
