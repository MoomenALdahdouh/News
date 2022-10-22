package com.moomen.news.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.R;
import com.moomen.news.model.User;
import com.moomen.news.ui.Activity.MainActivity;
import com.moomen.news.ui.Activity.MainActivityAdmin;
import com.moomen.news.ui.Activity.MainActivityCompany;
import com.moomen.news.ui.Activity.ResetPasswordActivity;
import com.moomen.news.ui.Activity.UserBlocked;
import com.moomen.news.utils.PreferenceUtils;

public class LoginFragment extends Fragment {

    private View view;
    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordTextView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        emailEditText = view.findViewById(R.id.edit_text_email_id);
        passwordEditText = view.findViewById(R.id.edit_text_password_id);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        loginButton = view.findViewById(R.id.button_login_id);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        forgotPasswordTextView = view.findViewById(R.id.text_view_forgot_password_id);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ResetPasswordActivity.class));
            }
        });
        return view;
    }

    private void userLogin() {
        String email, password;
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        emptyEditTextError(email, emailEditText, "Email");
        emptyEditTextError(password, passwordEditText, "Password");
        if (!email.isEmpty() && !password.isEmpty())
            signInWithEmailAndPassword(email, password);
    }

    private void emptyEditTextError(String stringValue, EditText editTextName, String tagName) {
        if (stringValue.isEmpty()) {
            editTextName.setError(tagName + " is required!");
            editTextName.requestFocus();
            return;
        }
        if (tagName.equals("Email")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(stringValue).matches()) {
                editTextName.setError(tagName + "Please provide valid email!");
                editTextName.requestFocus();
                return;
            }
        }
        if (tagName.equals("Password")) {
            if (stringValue.length() < 8) {
                editTextName.setError(tagName + "Min password length should be 6 characters!");
                editTextName.requestFocus();
                return;
            }
        }
    }


    private void signInWithEmailAndPassword(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //check email is verified and save User Login and loin
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (firebaseUser.isEmailVerified()) {
                                //redirect to user profile
                                PreferenceUtils.saveEmail(email, getContext());
                                PreferenceUtils.savePassword(password, getContext());
                                //Check type user
                                checkUserTypeToSignin(authResult.getUser().getUid());
                            } else {
                                progressBar.setVisibility(View.GONE);
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(getContext(), "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            startActivity(new Intent(getContext(), UserBlocked.class));
                            //getActivity().finish();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startActivity(new Intent(getContext(), UserBlocked.class));
                        //getActivity().finish();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "Failed to login! please check your password or email", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkUserTypeToSignin(String uid) {
        DocumentReference df = firebaseFirestore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userType = documentSnapshot.getString("userType");
                if (userType != null) {
                    if (userType.equals("user"))
                        startActivity(new Intent(getContext(), MainActivity.class));
                    else if (userType.equals("company"))
                        startActivity(new Intent(getContext(), MainActivityCompany.class));
                    else if (userType.equals("admin"))
                        startActivity(new Intent(getContext(), MainActivityAdmin.class));
                    getActivity().finish();
                }
            }
        });
    }
}