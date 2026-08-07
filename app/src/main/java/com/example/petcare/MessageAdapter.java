package com.example.petcare;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMessage;
        LinearLayout layoutMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMessage = itemView.findViewById(R.id.txtMessage);
            layoutMessage = itemView.findViewById(R.id.layoutMessage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Message message = messageList.get(position);

        holder.txtMessage.setText(message.getMessage());

        if (message.isUser()) {

            holder.layoutMessage.setGravity(Gravity.END);

            holder.txtMessage.setBackgroundColor(Color.parseColor("#F7943D"));
            holder.txtMessage.setTextColor(Color.WHITE);

        } else {

            holder.layoutMessage.setGravity(Gravity.START);

            holder.txtMessage.setBackgroundColor(Color.WHITE);
            holder.txtMessage.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}