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

        if (message.isUser()) {

            holder.layoutMessage.setGravity(Gravity.END);

            holder.txtMessage.setBackgroundResource(
                    R.drawable.bg_user_message
            );

            holder.txtMessage.setTextColor(Color.WHITE);

            holder.txtMessage.setText(
                    message.getMessage()
            );

        } else {

            holder.layoutMessage.setGravity(Gravity.START);

            holder.txtMessage.setBackgroundResource(
                    R.drawable.bg_ai_message
            );

            holder.txtMessage.setTextColor(Color.BLACK);

            holder.txtMessage.setText(
                    formatAiMessage(message.getMessage())
            );
        }

        holder.txtMessage.setLineSpacing(0f, 1.15f);
    }
    private android.text.Spanned formatAiMessage(String text) {

        // Convert Markdown bold to HTML bold
        text = text.replaceAll(
                "\\*\\*(.*?)\\*\\*",
                "<b>$1</b>"
        );

        // Preserve paragraph/line breaks
        text = text.replace("\r\n", "\n");
        text = text.replace("\n\n", "<br><br>");
        text = text.replace("\n", "<br>");

        // Add spacing before numbered list items
        text = text.replaceAll(
                "(?<!^)(\\d+\\.\\s)",
                "<br><br>$1"
        );

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            return android.text.Html.fromHtml(
                    text,
                    android.text.Html.FROM_HTML_MODE_LEGACY
            );

        } else {

            return android.text.Html.fromHtml(text);
        }
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }
}