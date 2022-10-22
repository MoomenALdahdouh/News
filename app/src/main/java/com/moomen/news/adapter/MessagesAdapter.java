package com.moomen.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moomen.news.R;
import com.moomen.news.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> {
    static int MY_MESSAGE = 1;
    static int OTHER_MESSAGES = 2;

    String currentUserId;

    ArrayList<Message> messagesArrayList;
    Context context;
    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a, dd-MM-yy");

    public MessagesAdapter( Context context,String currentUserId, ArrayList<Message> messagesArrayList) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatBubble;

        if (viewType == MY_MESSAGE) {
            chatBubble = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);
        } else {
            chatBubble = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item_sender, parent, false);
        }

        return new MessagesHolder(chatBubble);
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesArrayList.get(position).getSenderID().equals(currentUserId)) {
            return MY_MESSAGE;
        } else {
            return OTHER_MESSAGES;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        holder.bindView(messagesArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    class MessagesHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView dateTextView;
        public MessagesHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textView_message_id);
            dateTextView = itemView.findViewById(R.id.textView_date_message_id);
        }

        public void bindView(Message message) {
            messageTextView.setText(message.getMessage());
            dateTextView.setText(message.getDate());
            /*if (message.getDate() != null) {
                dateTextView.setText(format.format(message.getDate()));
            } else {
                dateTextView.setText(format.format(new Date()));
            }*/
        }
    }
}
