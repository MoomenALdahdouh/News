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
import com.google.firebase.firestore.DocumentSnapshot;
import com.moomen.news.R;
import com.moomen.news.model.Chat;
import com.squareup.picasso.Picasso;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder> {

    private OnItemClickListener listener;


    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position, @NonNull Chat model) {
        Picasso.get()
                .load(model.getSenderImage())
                .into(holder.senderImage);
        holder.senderName.setText(model.getSenderName());
        holder.senderEmail.setText(model.getSenderEmail());
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chat_item, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView senderImage;
        TextView senderName;
        TextView senderEmail;
        LinearLayout chatItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderImage = itemView.findViewById(R.id.image_view_user_image_sender_id);
            senderName = itemView.findViewById(R.id.text_view_sender_name_id);
            senderEmail = itemView.findViewById(R.id.text_view_sender_email_id);
            chatItem = itemView.findViewById(R.id.sender_item_layout_id);
            chatItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, getAdapterPosition());
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onItemSetOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
