package com.huytran.goodlife.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huytran.goodlife.pages.dietary.DiaryItemDataActivity;
import com.huytran.goodlife.pages.dietary.DietaryActivity;
import com.huytran.goodlife.R;
import com.huytran.goodlife.model.DiaryItem;

import java.text.DecimalFormat;
import java.util.List;

public class DiaryViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<DiaryItem> items;
    private static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;

    public DiaryViewAdapter(Context context, List<DiaryItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return LAYOUT_ONE;
        } else {
            return LAYOUT_TWO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        if(viewType == LAYOUT_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_bar, parent, false);
            viewHolder = new SplitViewHolder(view, viewType);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_recycler_item, parent, false);
            viewHolder = new DiaryViewHolder(view, viewType);
        }

        return viewHolder;
    }

    // Convert and format from date to String
    private String makeDateString(int day, int month, int year) {
        return " " + day + "/" + month + "/" + year + " ";
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DiaryItem itemAtPosition = items.get(position);

        if(holder.getItemViewType() == LAYOUT_ONE) {
            SplitViewHolder splitHolder = (SplitViewHolder) holder;

            splitHolder.date.setText(makeDateString(itemAtPosition.getAdding_day(), itemAtPosition.getAdding_month(), itemAtPosition.getAdding_year()));
        } else {
            DiaryViewHolder itemHolder = (DiaryViewHolder) holder;
            
            SharedPreferences sp = context.getSharedPreferences("Data", Context.MODE_PRIVATE);

            String user_name = sp.getString("Name",null);

            DecimalFormat decimalFormat = new DecimalFormat("0.0");

            itemHolder.name.setText(itemAtPosition.getName());
            itemHolder.unit_type.setText(itemAtPosition.getUnit_type());
            itemHolder.unit_name.setText(itemAtPosition.getUnit_name());
            itemHolder.amount.setText(decimalFormat.format(itemAtPosition.getAmount()));
            itemHolder.kcal.setText(decimalFormat.format(itemAtPosition.getKcal()));
            itemHolder.protein.setText(decimalFormat.format(itemAtPosition.getProtein()));
            itemHolder.lipid.setText(decimalFormat.format(itemAtPosition.getLipid()));
            itemHolder.glucid.setText(decimalFormat.format(itemAtPosition.getGlucid()));

            // set when click on delete button to delete item from database
            itemHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                    // get the document to delete
                    DocumentReference documentReference = firebaseFirestore.collection("GoodLife").document(user_name).collection("Nhật kí").document(itemAtPosition.getName());

                    // delete document from database
                    firebaseFirestore.collection("GoodLife")
                            .document(user_name)
                            .collection("Nhật kí")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        // Loop through all documents
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String docId = null;
                                            if(document.getString("name").equals(itemAtPosition.getName())) {
                                                docId = document.getId();

                                                firebaseFirestore.collection("GoodLife")
                                                        .document(user_name)
                                                        .collection("Nhật kí")
                                                        .document(docId)
                                                        .delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.w("Firestore", "Getting documents successfuly");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("Firestore", "Error getting documents", task.getException());
                                                            }
                                                        });
                                            }
                                        }
                                    } else {
                                        Log.w("Firestore", "Error getting documents", task.getException());
                                    }
                                }
                            });

                    Intent intent = new Intent(context, DietaryActivity.class);
                    context.startActivity(intent);
                }
            });

            itemHolder.infomation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DiaryItemDataActivity.class);
                    intent.putExtra("Name", itemAtPosition.name);
                    intent.putExtra("Amount", itemAtPosition.amount);
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class SplitViewHolder extends RecyclerView.ViewHolder{
        public TextView date;
        private final int viewType;

        public SplitViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            date = itemView.findViewById(R.id.date);
        }

        public int getViewType() {
            return viewType;
        }
    }

    public class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView name, unit_type, unit_name, kcal, amount, protein, lipid, glucid;
        Button delete, infomation;

        private final int viewType;

        public DiaryViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            this.viewType = viewType;

            name = itemView.findViewById(R.id.item_view);
            unit_type = itemView.findViewById(R.id.unit_type);
            unit_name = itemView.findViewById(R.id.unit_name);
            kcal = itemView.findViewById(R.id.item_kcalo);
            amount = itemView.findViewById(R.id.item_amount);
            protein = itemView.findViewById(R.id.item_protein);
            lipid = itemView.findViewById(R.id.item_lipid);
            glucid = itemView.findViewById(R.id.item_glucid);

            delete = itemView.findViewById(R.id.delete_item_button);
            infomation = itemView.findViewById(R.id.item_info_button);

            delete.setGravity(Gravity.CENTER);
            infomation.setGravity(Gravity.CENTER);
        }

        public int getViewType() {
            return viewType;
        }
    }
}