package com.huytran.goodlife.pages.water_demand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomePage;


public class WaterDemand extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String CHANNEL_ID = "example_channel";
    private TextView dataView1, dataView2, dataView3, dataView4, dataView5, sumDataView;
    private int addWater;
    private String name;
    private double recommendWeight, recommendWaterAmount;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_demand);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        dataView1 = findViewById(R.id.num1);
        dataView2 = findViewById(R.id.num2);
        dataView3 = findViewById(R.id.num3);
        dataView4 = findViewById(R.id.num4);
        dataView5 = findViewById(R.id.num5);
        sumDataView = findViewById(R.id.sum_num);
        backButton = findViewById(R.id.back_button);

        SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);

        name = sp.getString("Name", null);

        Task<QuerySnapshot> nutritionTask = firebaseFirestore.collection("GoodLife")
                .document(name).collection("Dinh dưỡng")
                .get();

        Tasks.whenAll(nutritionTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot nutritionResult = nutritionTask.getResult();
                if (nutritionResult != null) {
                    for (QueryDocumentSnapshot document : nutritionResult) {
                        if (document.getString("userRecommendWeight") != null) {
                            try {
                                recommendWeight = Double.parseDouble(document.getString("userRecommendWeight"));

                                double tmpRecommendWaterAmount = (40 * recommendWeight) / 1000;

                                recommendWaterAmount = Math.floor(tmpRecommendWaterAmount * 100.0) / 100.0;

                                recommendWaterAmount *= 1000;

                                sumDataView.setText(recommendWaterAmount + " ml");

                                addWater = (int) (recommendWaterAmount / 5);

                                dataView1.setText(addWater + " ml");
                                dataView2.setText(addWater + " ml");
                                dataView3.setText(addWater + " ml");
                                dataView4.setText(addWater + " ml");
                                dataView5.setText(addWater + " ml");

                                SharedPreferences sharedPreferences = getSharedPreferences("Water", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("Val", addWater);
                                editor.apply();

                            } catch (Exception e) {
                                recommendWeight = 0.0;
                            }
                        }
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaterDemand.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}