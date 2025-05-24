package com.huytran.goodlife.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huytran.goodlife.model.ChatMessage;
import com.huytran.goodlife.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<ChatMessage> messages;
    private Context context;

    public ChatAdapter(List<ChatMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userText, botText;
        LinearLayout userLayout, botLayout;
        ImageButton btnEditUser, btnCopyUser, btnCopyBot;
        LinearLayout userActions, botActions;


        public ViewHolder(View view) {
            super(view);
            userText = view.findViewById(R.id.userText);
            botText = view.findViewById(R.id.botText);
            userLayout = view.findViewById(R.id.userLayout);
            botLayout = view.findViewById(R.id.botLayout);
            btnEditUser = view.findViewById(R.id.btnEditUser);
            btnCopyUser = view.findViewById(R.id.btnCopyUser);
            btnCopyBot = view.findViewById(R.id.btnCopyBot);
            userActions = view.findViewById(R.id.userActions);
            botActions = view.findViewById(R.id.botActions);

        }
    }


    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser ? 0 : 1;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    public interface OnMessageEditListener {
        void onMessageEdit(int position, ChatMessage message);
    }

    private OnMessageEditListener editListener;

    public void setOnMessageEditListener(OnMessageEditListener listener) {
        this.editListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (message.isUser) {
            holder.userLayout.setVisibility(View.VISIBLE);
            holder.userText.setText(message.message);

            holder.botLayout.setVisibility(View.GONE);

            holder.userText.setOnLongClickListener(v -> {
                if (editListener != null) {
                    editListener.onMessageEdit(position, message);
                }
                return true;
            });

        } else {
            holder.botLayout.setVisibility(View.VISIBLE);
            holder.botText.setText(message.message);

            holder.userLayout.setVisibility(View.GONE);

            holder.botText.setOnLongClickListener(null);

        }

        if (message.isUser) {
            holder.userLayout.setVisibility(View.VISIBLE);
            holder.botLayout.setVisibility(View.GONE);
            holder.userText.setText(message.message);
            holder.userActions.setVisibility(View.VISIBLE);
            holder.botActions.setVisibility(View.GONE);

            holder.btnEditUser.setOnClickListener(v -> {
                // Gửi tin nhắn cũ lên EditText
                if (editListener != null) {
                    editListener.onMessageEdit(position, message);
                }
            });

            holder.btnCopyUser.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", message.message);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Đã sao chép!", Toast.LENGTH_SHORT).show();
            });

        } else {
            holder.userLayout.setVisibility(View.GONE);
            holder.botLayout.setVisibility(View.VISIBLE);
            holder.botText.setText(message.message);
            holder.userActions.setVisibility(View.GONE);
            holder.botActions.setVisibility(View.VISIBLE);

            holder.btnCopyBot.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", message.message);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Đã sao chép!", Toast.LENGTH_SHORT).show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}