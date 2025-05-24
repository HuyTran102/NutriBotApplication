package com.huytran.goodlife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huytran.goodlife.pages.notification.NotificationData;
import com.huytran.goodlife.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    Context context;
    List<NotificationData> items;

    public NotificationAdapter(Context context, List<NotificationData> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationHolder(LayoutInflater.from(context).inflate(R.layout.notifiation_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        NotificationData itemAtPosition = items.get(position);
        holder.name.setText(itemAtPosition.getName());
        holder.information.setText(itemAtPosition.getInformation());
        holder.time.setText(itemAtPosition.getTime());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {
        public TextView name, information, time;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            information = itemView.findViewById(R.id.information);
            time = itemView.findViewById(R.id.time);
        }
    }
}
