package com.moomen.news.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.news.R;
import com.moomen.news.adapter.MessagesAdapter;
import com.moomen.news.model.Chat;
import com.moomen.news.model.Message;
import com.moomen.news.ui.fragment.ChatFragmentAdmin;
import com.moomen.news.ui.fragment.UserFragmentAdmin;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewChat extends AppCompatActivity {

    private String chatID;
    private MessagesAdapter messagesAdapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userID;

    private ImageView sendMessageButton;
    private EditText messageEditText;
    private ImageView senderImage;
    private TextView senderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);
        sendMessageButton = findViewById(R.id.image_view_send_message_id);
        messageEditText = findViewById(R.id.edit_text_message_id);
        senderImage = findViewById(R.id.imageView_user_id);
        senderName = findViewById(R.id.text_view_user_name_id);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ChatFragmentAdmin.CHAT_ID)) {
            chatID = intent.getStringExtra(ChatFragmentAdmin.CHAT_ID);
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();


        DocumentReference documentReference = firebaseFirestore.collection("Chat").document(chatID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Chat chat = documentSnapshot.toObject(Chat.class);
                assert chat != null;
                senderName.setText(chat.getSenderName());
                Picasso.get()
                        .load(chat.getSenderImage())
                        .into(senderImage);
                fillRecycleAdapter(chat.getMessageArrayList());
            }
        });
        //Change image send message
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!messageEditText.getText().toString().isEmpty())
                    sendMessageButton.setImageResource(R.drawable.ic_baseline_send_blue_24);
                else
                    sendMessageButton.setImageResource(R.drawable.ic_baseline_send_24);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageEditText.getText().toString();
                if (!TextUtils.isEmpty(messageText)) {
                    String date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                    Message message = new Message(messageText, userID, date);
                    sendNewMessage(message);
                }
            }
        });
    }

    private void sendNewMessage(Message message) {
        DocumentReference documentReference = firebaseFirestore.collection("Chat").document(chatID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Chat chat = documentSnapshot.toObject(Chat.class);
                ArrayList<Message> messageArrayList = chat.getMessageArrayList();
                messageArrayList.add(message);
                documentReference.update("messageArrayList", messageArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideKeyboard(ViewChat.this);
                        messageEditText.setText("");
                        fillRecycleAdapter(messageArrayList);
                    }
                });
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void fillRecycleAdapter(ArrayList<Message> messageArrayList) {
        messagesAdapter = new MessagesAdapter(ViewChat.this, userID, messageArrayList);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_message_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewChat.this));
        recyclerView.setAdapter(messagesAdapter);
        recyclerView.setHasFixedSize(true);
    }
}