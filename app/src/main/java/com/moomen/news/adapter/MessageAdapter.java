package com.moomen.news.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.moomen.news.R;
import com.moomen.news.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder> {

    static int MY_MESSAGE = 1;
    static int OTHER_MESSAGES = 2;
    private Message message;
    private String currentUserId;

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    private SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a, dd-MM-yy");

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position, @NonNull Message model) {
        message = model;
        holder.message.setText(model.getMessage());
        if (model.getDate() != null) {
            holder.date.setText(format.format(model.getDate()));
        } else {
            holder.date.setText(format.format(new Date()));
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.message_item, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.textView_message_id);
            date = itemView.findViewById(R.id.textView_date_message_id);

        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(message.getSenderID().equals(currentUserId)){
            return MY_MESSAGE;
        }else{
            return OTHER_MESSAGES;
        }
    }
}
