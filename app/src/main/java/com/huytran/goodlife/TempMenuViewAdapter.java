package com.huytran.goodlife;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TempMenuViewAdapter extends RecyclerView.Adapter<ViewHolder>{
    Context context;

    List<TempMenuItem> items;

    public TempMenuViewAdapter(Context context, List<TempMenuItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.temp_menu_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TempMenuItem itemAtPosition = items.get(position);

        holder.image.setImageResource(itemAtPosition.getIcon_image());
        holder.text.setText(itemAtPosition.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TempMenuData.class);
                intent.putExtra("Image", itemAtPosition.getData_image());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void updateList(List<TempMenuItem> newList) {
        items = newList;
        notifyDataSetChanged();;
    }
}
