package com.moomen.news.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.news.R;
import com.moomen.news.model.Comments;
import com.moomen.news.model.PostNews;
import com.moomen.news.model.User;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateNewAdmin extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private EditText nameEditText, emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private Button createAdminButton;
    private String name, email, password, dateOfCreate;
    boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_admin);

        nameEditText = findViewById(R.id.edit_text_admin_name_id);
        emailEditText = findViewById(R.id.edit_text_admin_email_create_id);
        passwordEditText = findViewById(R.id.edit_text_admin_password_create_id);
        createAdminButton = findViewById(R.id.button_create_admin_id);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        createAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        emptyEditTextError(name, nameEditText, "Name");
        emptyEditTextError(email, emailEditText, "Email");
        emptyEditTextError(password, passwordEditText, "Password");
        dateOfCreate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        status = true;
        if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty())
            createUserWithEmailAndPassword();
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

    private void createUserWithEmailAndPassword() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addNewUserOnDbFirebase();
                }
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void addNewUserOnDbFirebase() {
        User user = new User(name, "", email, "", "", "", "",
                "admin", dateOfCreate, "https://i.ibb.co/W0hVGcJ/accont.png", status, "");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // save on cluode
        DocumentReference documentReference = firebaseFirestore.collection("Users")
                .document(firebaseUser.getUid());
        documentReference.set(user);
        verifyEmail();
        //save on reayltime database
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    verifyEmail();
                } else {
                    Toast.makeText(CreateNewAdmin.this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void verifyEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.sendEmailVerification();
        Toast.makeText(CreateNewAdmin.this, "Registered Successfully!\nCheck your email to verify your account!", Toast.LENGTH_LONG).show();
    }
}