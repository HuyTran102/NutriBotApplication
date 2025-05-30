package com.huytran.goodlife.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huytran.goodlife.R;

public class SplitViewHolder extends RecyclerView.ViewHolder {
    private final int viewType;
    public TextView date;

    public SplitViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;

        date = itemView.findViewById(R.id.date);
    }

    public int getViewType() {
        return viewType;
    }
}
