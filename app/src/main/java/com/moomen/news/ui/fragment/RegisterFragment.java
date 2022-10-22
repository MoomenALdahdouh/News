package com.moomen.news.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.R;
import com.moomen.news.model.User;
import com.moomen.news.ui.Activity.MainActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class RegisterFragment extends Fragment {

    private View view;
    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, dateOfBirthEditText, addressEditText, phoneEditText;
    private String gender = "male";
    private String userType = "user";
    private Spinner spinnerGender;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private CheckBox userTypeBox;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String firstName, lastName, email, password, dateOfBirth, address, phone,dateOfCreate,image,aboutCompany;
    boolean status;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firstNameEditText = view.findViewById(R.id.edit_text_first_name_id);
        lastNameEditText = view.findViewById(R.id.edit_text_second_name_id);
        emailEditText = view.findViewById(R.id.edit_text_email_register_id);
        passwordEditText = view.findViewById(R.id.edit_text_password_register_id);
        spinnerGender = view.findViewById(R.id.spinner_gender_id);
        dateOfBirthEditText = view.findViewById(R.id.edit_text_date_id);
        addressEditText = view.findViewById(R.id.edit_text_address_id);
        phoneEditText = view.findViewById(R.id.edit_text_phone_id);
        buttonRegister = view.findViewById(R.id.button_register_id);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
        userTypeBox = view.findViewById(R.id.checkBox_user_type_id);
        spinnerCreate();
        //userTypeCheckBox();
        userTypeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userTypeBox.isChecked()) {
                    userType = "company";
                }
                else {
                    userType = "user";
                }
                //Toast.makeText(getContext(), userType, Toast.LENGTH_SHORT).show();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        return view;
    }

    private void spinnerCreate() {
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(), R.array.genderArray, R.layout.support_simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapterSpinner);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                switch (position) {
                    case 0:
                        gender = "male";
                        break;
                    case 1:
                        gender = "female";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = "male";
            }
        });
    }

    private void userTypeCheckBox() {
        userTypeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userTypeBox.isChecked())
                    userType = "company";
                else
                    userType = "user";
            }
        });
    }

    private void registerUser() {
        firstName = firstNameEditText.getText().toString().trim();
        lastName = lastNameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        dateOfBirth = dateOfBirthEditText.getText().toString().trim();
        address = addressEditText.getText().toString().trim();
        phone = phoneEditText.getText().toString().trim();
        emptyEditTextError(firstName, firstNameEditText, "First name");
        emptyEditTextError(lastName, lastNameEditText, "Last Name");
        emptyEditTextError(email, emailEditText, "Email");
        emptyEditTextError(password, passwordEditText, "Password");
        emptyEditTextError(dateOfBirth, dateOfBirthEditText, "Date of birth");
        emptyEditTextError(address, addressEditText, "Address");
        emptyEditTextError(phone, phoneEditText, "Phone");
        dateOfCreate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        image = "https://i.ibb.co/W0hVGcJ/accont.png";
        status = true;
        aboutCompany = "";
        if (!email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()
                && !dateOfBirth.isEmpty() && !address.isEmpty() && !phone.isEmpty() && !gender.isEmpty())
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
                /* else {
                    Toast.makeText(getContext(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                }*/
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewUserOnDbFirebase() {
        User user = new User(firstName, lastName, email, dateOfBirth, address, phone, gender, userType,dateOfCreate,image,status,aboutCompany);
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
                    //Toast.makeText(getContext(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void verifyEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.sendEmailVerification();
        Toast.makeText(getContext(), "Registered Successfully!\nCheck your email to verify your account!", Toast.LENGTH_LONG).show();
    }

}