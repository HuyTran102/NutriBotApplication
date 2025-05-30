package com.huytran.goodlife.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huytran.goodlife.R;
import com.huytran.goodlife.model.Item;
import com.huytran.goodlife.pages.dietary.ItemDataActivity;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    Context context;

    List<Item> items;

    public ViewAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(items.get(position).getImage());
        holder.text.setText(items.get(position).getName());
        Item itemAtPosition = items.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemDataActivity.class);
                intent.putExtra("Name", itemAtPosition.name);
                intent.putExtra("Kcal", itemAtPosition.kcal);
                intent.putExtra("Protein", itemAtPosition.protein);
                intent.putExtra("Lipid", itemAtPosition.lipid);
                intent.putExtra("Glucid", itemAtPosition.glucid);
                intent.putExtra("Image", itemAtPosition.getImage());
                intent.putExtra("UnitType", itemAtPosition.unit_type);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<Item> newList) {
        items = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageview);
            text = itemView.findViewById(R.id.name);
        }

    }
}
