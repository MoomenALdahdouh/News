package com.moomen.news.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moomen.news.R;
import com.moomen.news.model.Comments;
import com.moomen.news.model.PostNews;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import id.zelory.compressor.Compressor;


public class CreateNewsActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 100;
    private Uri newsImageUri = null;

    private Button publishButton;
    private ImageView newsImage;
    private EditText newsTitleEditText;
    private EditText newsDescriptionEditText;
    private EditText newsCountryEditText;
    private ProgressBar progressBarImage;
    private CheckBox checkBoxPaid;

    private Button allTagButton;
    private Button breakingTagButton;
    private Button lifestyleTagButton;
    private Button musicTagButton;
    private Button entertainmentTagButton;
    private Button technologyTagButton;
    private Button politicsTagButton;
    private Button localTagButton;
    private ArrayList<String> tagsArrayList;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private Bitmap bitmap = null;
    private Bitmap compressor;
    private String title;
    private String description;
    private String country;
    private String companyID;
    private String companyName;
    private String companyImage;
    private String imageName;
    private String date;
    private boolean companyStatus;
    private boolean newsStatus;
    private boolean paid;
    private String downloadUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_news);

        allTagButton = findViewById(R.id.button10);
        breakingTagButton = findViewById(R.id.button11);
        lifestyleTagButton = findViewById(R.id.button12);
        musicTagButton = findViewById(R.id.button13);
        entertainmentTagButton = findViewById(R.id.button14);
        technologyTagButton = findViewById(R.id.button15);
        politicsTagButton = findViewById(R.id.button16);
        localTagButton = findViewById(R.id.button17);

        buttonSetOnClickListener(allTagButton);
        buttonSetOnClickListener(breakingTagButton);
        buttonSetOnClickListener(lifestyleTagButton);
        buttonSetOnClickListener(musicTagButton);
        buttonSetOnClickListener(entertainmentTagButton);
        buttonSetOnClickListener(technologyTagButton);
        buttonSetOnClickListener(politicsTagButton);
        buttonSetOnClickListener(localTagButton);

        tagsArrayList = new ArrayList<>();
        tagsArrayList.add("All");

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        publishButton = findViewById(R.id.button_publish_news_id);
        newsImage = findViewById(R.id.image_view_upload_image_id);
        newsTitleEditText = findViewById(R.id.edit_text_title_news_id);
        newsDescriptionEditText = findViewById(R.id.edit_text_description_news_id);
        newsCountryEditText = findViewById(R.id.edit_text_country_id);
        progressBarImage = findViewById(R.id.progressBar);
        checkBoxPaid = findViewById(R.id.check_box_pid_id);
        checkBoxPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxPaid.isChecked())
                    paid = false;
                else
                    paid = true;
            }
        });
        newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(CreateNewsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateNewsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                //.setMinCropResultSize(512,512)
                                .setAspectRatio(4, 3)
                                .start(CreateNewsActivity.this);

                    }
                }
            }
        });
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarImage.setVisibility(View.VISIBLE);
                title = newsTitleEditText.getText().toString().trim();
                description = newsDescriptionEditText.getText().toString().trim();
                country = newsCountryEditText.getText().toString().trim();
                emptyEditTextError(title, newsTitleEditText, "Title");
                emptyEditTextError(country, newsCountryEditText, "Country");
                if (!title.isEmpty() && !country.isEmpty() && newsImageUri != null) {
                    companyID = firebaseAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("Users").document(companyID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            companyName = task.getResult().getString("firstName") + " " + task.getResult().getString("lastName");
                            companyImage = task.getResult().getString("userImage");
                            companyStatus = task.getResult().getBoolean("status");
                            newsStatus = true;
                            imageName = random() + ".jpg";
                            date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                            storageImageAndPostNews();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

                }
            }
        });

    }

    private void buttonSetOnClickListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!tagsArrayList.contains(button.getText().toString()) && !button.getText().toString().equals("All")) {
                    tagsArrayList.add(button.getText().toString());
                    button.setBackgroundResource(R.drawable.bg_button_dark);
                    button.setTextColor(R.color.white);
                } else if (tagsArrayList.contains(button.getText().toString()) && !button.getText().toString().equals("All")) {
                    tagsArrayList.remove(button.getText().toString());
                    button.setBackgroundResource(R.drawable.bg_button_light);
                    button.setTextColor(R.color.purple_500);
                }

            }
        });
    }

    private void postNewsOnFirebase(PostNews postNews) {
        firebaseFirestore.collection("News").add(postNews).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateNewsActivity.this, "Published", Toast.LENGTH_SHORT).show();
                    progressBarImage.setVisibility(View.GONE);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateNewsActivity.this, "Failed to publish, try again!", Toast.LENGTH_SHORT).show();
                progressBarImage.setVisibility(View.GONE);
            }
        });
    }

    private void storageImageAndPostNews() {
        File imageFile = new File(newsImageUri.getPath());
        try {
            compressor = new Compressor(CreateNewsActivity.this)
                    .setMaxHeight(240)
                    .setMaxWidth(360)
                    .setQuality(5)
                    .compressToBitmap(imageFile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        compressor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayInputStream);
        byte[] thumpData = byteArrayInputStream.toByteArray();
        StorageReference filePath = storageReference.child("news_image").child(imageName);
        UploadTask uploadTask = filePath.putBytes(thumpData);
        //UploadTask uploadTask = filePath.putFile(newsImageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUri = task.getResult().toString();
                            PostNews postNews = new PostNews(companyID, companyName, companyImage
                                    , downloadUri, title, description, date + "", 0, 0, new ArrayList<Comments>()
                                    , paid, tagsArrayList, true, companyStatus, newsStatus, country);
                            postNewsOnFirebase(postNews);
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void emptyEditTextError(String stringValue, EditText editTextName, String tagName) {
        if (stringValue.isEmpty()) {
            editTextName.setError(tagName + " is required!");
            editTextName.requestFocus();
            progressBarImage.setVisibility(View.GONE);
        }
    }

    //Crop image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                newsImageUri = result.getUri();
                newsImage.setImageURI(newsImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}