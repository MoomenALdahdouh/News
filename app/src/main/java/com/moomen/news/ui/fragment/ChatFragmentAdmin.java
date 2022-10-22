package com.moomen.news.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.news.R;
import com.moomen.news.adapter.ChatAdapter;
import com.moomen.news.adapter.UsersAdapter;
import com.moomen.news.model.Chat;
import com.moomen.news.model.User;
import com.moomen.news.ui.Activity.ViewChat;
import com.moomen.news.ui.Activity.ViewCompany;

public class ChatFragmentAdmin extends Fragment {

    public static final String CHAT_ID = "CHAT_ID";

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat_admin, container, false);
        getAllChats();
        return view;
    }

    private void getAllChats() {
        Query query = FirebaseFirestore.getInstance().collection("Chat")
                .orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();
        ChatAdapter chatAdapter = new ChatAdapter(options);
        chatAdapter.onItemSetOnClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String chatID = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), ViewChat.class);
                intent.putExtra(CHAT_ID, chatID);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_chat_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setHasFixedSize(true);
        chatAdapter.startListening();
    }
}