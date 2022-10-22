package com.moomen.news.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.model.User;
import com.moomen.news.ui.Activity.LoginRegisterActivity;
import com.moomen.news.utils.PreferenceUtils;
import com.moomen.news.viewModel.ProfileViewModel;
import com.moomen.news.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private View view;
    private TextView nameTextView,emailTextView;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private LinearLayout logoutLinear;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        nameTextView = view.findViewById(R.id.tex_view_name_id);
        emailTextView = view.findViewById(R.id.text_view_email_id);
        logoutLinear = view.findViewById(R.id.linear_logout_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        getUserInformation(userID);
        logout();
        return view;
    }

    private void getUserInformation(String userID) {
        DocumentReference df = firebaseFirestore.collection("Users").document(userID);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String email = documentSnapshot.getString("email");
                    String fullName = firstName + " " + lastName;
                    nameTextView.setText(fullName);
                    emailTextView.setText(email);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void logout(){
        logoutLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                PreferenceUtils.saveEmail("",getContext());
                PreferenceUtils.savePassword("",getContext());
                //redirect to login activity
                startActivity(new Intent(getContext(), LoginRegisterActivity.class));
                getActivity().finish();
            }
        });
    }
}