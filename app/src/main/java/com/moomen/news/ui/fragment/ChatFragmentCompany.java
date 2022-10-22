package com.moomen.news.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.news.R;
import com.moomen.news.adapter.ChatAdapter;
import com.moomen.news.adapter.MessagesAdapter;
import com.moomen.news.adapter.NewsAdapter;
import com.moomen.news.model.Chat;
import com.moomen.news.model.Message;
import com.moomen.news.model.PostNews;
import com.moomen.news.model.User;
import com.moomen.news.ui.Activity.CreateNewsActivity;
import com.moomen.news.ui.Activity.OpenUserProfile;
import com.moomen.news.ui.Activity.ViewNews;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatFragmentCompany extends Fragment {
    private View view;
    private ImageView sendMessageButton;
    private EditText messageEditText;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private User user;
    private String userName;
    private String userEmail;
    private String userImage;

    private boolean senderExist = false;
    private MessagesAdapter messagesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_company, container, false);
        sendMessageButton = view.findViewById(R.id.image_view_send_message_id);
        messageEditText = view.findViewById(R.id.edit_text_message_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        senderIsExist();

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
                if (!senderExist) {
                    createRomeChat();
                } else {
                    String messageText = messageEditText.getText().toString();
                    if (!TextUtils.isEmpty(messageText)) {
                        addNewMessage(messageText);
                    }
                }
            }
        });

        return view;
    }

    private void createRomeChat() {
        firebaseFirestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                userName = user.getFirstName() + " " + user.getLastName();
                userImage = user.getUserImage();
                userEmail = user.getEmail();
                String dateRomeChat = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                Chat chat = new Chat(userID, "", userName, userEmail, userImage, dateRomeChat, new ArrayList<Message>());
                firebaseFirestore.collection("Chat").add(chat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        docID = task.getResult().getId();
                        senderExist = true;
                        String messageText = messageEditText.getText().toString();
                        if (!TextUtils.isEmpty(messageText)) {
                            addNewMessage(messageText);
                        }
                    }
                });
            }
        });

    }

    String docID;

    private void senderIsExist() {
        firebaseFirestore.collection("Chat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        Chat chat = d.toObject(Chat.class);
                        if (chat.getSenderID().equals(userID)) {
                            senderExist = true;
                            docID = d.getId();
                            if (!chat.getMessageArrayList().isEmpty())
                                fillRecycleAdapter(chat.getMessageArrayList());
                            break;
                        }
                    }
                    if (!senderExist) {
                        createRomeChat();
                    }
                }
            }
        });
    }


    private void fillRecycleAdapter(ArrayList<Message> messageArrayList) {
        messagesAdapter = new MessagesAdapter(getContext(), userID, messageArrayList);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_message_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messagesAdapter);
        messagesAdapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
    }

    private void addNewMessage(String messageText) {
        DocumentReference documentReference = firebaseFirestore.collection("Chat").document(docID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Chat chat = documentSnapshot.toObject(Chat.class);
                assert chat != null;
                ArrayList<Message> messageArrayList = chat.getMessageArrayList();
                String date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                Message message = new Message(messageText, userID, date);
                messageArrayList.add(message);
                documentReference.update("messageArrayList", messageArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideKeyboard(getActivity());
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
}