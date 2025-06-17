package com.huytran.goodlife.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huytran.goodlife.R;
import com.huytran.goodlife.model.NutritionistDataView;
import com.huytran.goodlife.pages.contact.NutritionistDataActivity;

import java.util.List;

public class NutritionistAdapter extends RecyclerView.Adapter<NutritionistAdapter.ViewHolder> {

    List<NutritionistDataView> nutritionists;
    Context context;

    public NutritionistAdapter(List<NutritionistDataView> nutritionists, Context context) {
        this.nutritionists = nutritionists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutritionist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutritionistDataView nutritionist = nutritionists.get(position);
        holder.nameTextView.setText(nutritionist.getName());
        holder.basicInfoTextView.setText(nutritionist.getBasicInfo());
        holder.profileImageView.setImageResource(nutritionist.getImage());
        holder.ratingBar.setRating(nutritionist.getRating());

        // Set other views as needed

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click, e.g., open a detailed view or start a chat
                // This can be implemented based on your requirements
                Intent intent = new Intent(context, NutritionistDataActivity.class);
                intent.putExtra("nutritionistName", nutritionist.getName());
                intent.putExtra("nutritionistDescription1", nutritionist.getDescription1());
                intent.putExtra("nutritionistDescription2", nutritionist.getDescription2());
                intent.putExtra("nutritionistImage", nutritionist.getImage());
                intent.putExtra("nutritionistEmail", nutritionist.getEmail());
                intent.putExtra("nutritionistPhone", nutritionist.getPhone());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nutritionists.size();
    }

    public void updateList(List<NutritionistDataView> newList) {
        nutritionists = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView, basicInfoTextView;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.imageview);
            nameTextView = itemView.findViewById(R.id.nutritionist_name);
            basicInfoTextView = itemView.findViewById(R.id.nutritionist_basic_description);
            ratingBar = itemView.findViewById(R.id.app_rating);
        }
    }
}
