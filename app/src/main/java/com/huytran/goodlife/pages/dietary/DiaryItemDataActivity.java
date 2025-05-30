package com.huytran.goodlife.pages.dietary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huytran.goodlife.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiaryItemDataActivity extends AppCompatActivity {
    private TextView viewItemName, itemKcalo, itemProtein, itemLipid, itemGlucid, itemUnitType, itemUnitName;
    private TextInputEditText itemAmount;
    private ImageButton backButton;
    private Button saveButton;
    private ImageView itemImage;
    private String glucidValue, lipidValue, proteinValue, kcalValue, name;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_item_data);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        backButton = findViewById(R.id.back_button);
        itemAmount = findViewById(R.id.item_amount);
        saveButton = findViewById(R.id.save_button);
        viewItemName = findViewById(R.id.item_name);
        itemUnitType = findViewById(R.id.unit_type);
        itemUnitName = findViewById(R.id.unit_name);
        itemKcalo = findViewById(R.id.item_kcalo);
        itemProtein = findViewById(R.id.item_protein);
        itemLipid = findViewById(R.id.item_lipid);
        itemGlucid = findViewById(R.id.item_glucid);
        itemAmount = findViewById(R.id.item_amount);
        itemImage = findViewById(R.id.item_image);

        firebaseFirestore = FirebaseFirestore.getInstance();

        SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);

        name = sp.getString("Name", null);

        // get item value fromm intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String itemName = "", unitType = "", unitName;
        final int[] kcal = {0}, view_kcal = {0};
        int imageId = 0;
        final double[] protein = {0}, view_protein = {0}, lipid = {0}, view_lipid = {0}, glucid = {0}, view_glucid = {0}, view_amount = new double[1];

        if (bundle != null) {
            itemName = intent.getStringExtra("Name");
            view_amount[0] = intent.getDoubleExtra("Amount", 0);
            view_kcal[0] = intent.getIntExtra("Kcal", 0);
            view_protein[0] = intent.getDoubleExtra("Protein", 0);
            view_lipid[0] = intent.getDoubleExtra("Lipid", 0);
            view_glucid[0] = intent.getDoubleExtra("Glucid", 0);
            imageId = intent.getIntExtra("Image", 0);
            unitType = intent.getStringExtra("UnitType");
        }

        // Transfer the unit type to the uint name
        if (Objects.equals(unitType, "(g)")) {
            unitName = "Khối lượng";
        } else {
            unitName = "Thể tích";
        }

        // set item name, image, unit name and unit type
        viewItemName.setText(itemName);

        itemImage.setImageResource(imageId);

        itemAmount.setText(String.valueOf(view_amount[0]));

        itemUnitType.setText(unitType);
        itemUnitName.setText(unitName);

        // set item kcalo, protein, lipid, glucid value
        itemKcalo.setText(String.valueOf(view_kcal[0]));
        itemProtein.setText(String.valueOf(view_protein[0]));
        itemLipid.setText(String.valueOf(view_lipid[0]));
        itemGlucid.setText(String.valueOf(view_glucid[0]));

        // change the item value to the default value
        kcal[0] = (int) ((view_kcal[0] * 100) / view_amount[0]);
        protein[0] = (view_protein[0] * 100) / view_amount[0];
        lipid[0] = (view_lipid[0] * 100) / view_amount[0];
        glucid[0] = (view_glucid[0] * 100) / view_amount[0];

        // Make other value change along with the item amount
        final double[] amount = new double[1];
        itemAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (itemAmount.getText().toString().equals("") || Double.parseDouble(itemAmount.getText().toString()) < 0.0) {
                    itemKcalo.setText("0");
                    itemGlucid.setText("0");
                    itemProtein.setText("0");
                    itemLipid.setText("0");
                    return;
                }
                amount[0] = Double.parseDouble(itemAmount.getText().toString());

                // Calculate all value with the user item amount
                DecimalFormat decimalFormat = new DecimalFormat("0.0");

                kcalValue = String.valueOf((int) ((kcal[0] * amount[0]) / 100));
                proteinValue = decimalFormat.format((protein[0] * amount[0]) / 100);
                lipidValue = decimalFormat.format((lipid[0] * amount[0]) / 100);
                glucidValue = decimalFormat.format((glucid[0] * amount[0]) / 100);

                // set item new kcalo, protein, lipid, glucid value
                itemKcalo.setText(kcalValue);
                itemProtein.setText(proteinValue);
                itemLipid.setText(lipidValue);
                itemGlucid.setText(glucidValue);
            }
        });

        String finalItemName = itemName;
        String finalUnitType = unitType;
        String finalUnitName = unitName;

        // save item to database when click on the saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change the "," in number to "."
                proteinValue = proteinValue.replace(",", ".");
                lipidValue = lipidValue.replace(",", ".");
                glucidValue = glucidValue.replace(",", ".");

                // Get the current date
                Calendar cal = Calendar.getInstance();

                // write the data to the database
                WriteDataFireBase(finalItemName, amount[0], kcalValue, proteinValue, lipidValue, glucidValue, finalUnitType, finalUnitName);

                Intent intent = new Intent(DiaryItemDataActivity.this, DietaryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // set when click on the back button to went back to Dietary.Java
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryItemDataActivity.this, DietaryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    // Write Data to Cloud Firestone
    public void WriteDataFireBase(String itemName, double itemAmount, String itemKcalValue, String itemProteinValue, String itemLipidValue, String itemGlucidValue, String itemUnitType, String itemUnitName) {
        // Create a new item with all of the data like name, amount, ...
        Map<String, Object> item = new HashMap<>();
        item.put("amount", String.valueOf(itemAmount));
        item.put("kcal", itemKcalValue);
        item.put("protein", itemProteinValue);
        item.put("lipid", itemLipidValue);
        item.put("glucid", itemGlucidValue);
        item.put("unit_name", itemUnitName);
        item.put("unit_type", itemUnitType);
        firebaseFirestore.collection("GoodLife").document(name).collection("Nhật kí").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Loop through all documents
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("name").equals(itemName)) {
                            firebaseFirestore.collection("GoodLife").document(name).collection("Nhật kí").document(document.getId()) // Dùng ID của tài liệu cần ghi đè
                                    .update(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Firestore", "Data successfully overwritten!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error writing document", e);
                                        }
                                    });
                        }
                    }
                } else {
                    Log.w("Firestore", "Error getting documents", task.getException());
                }
            }
        });
    }
}