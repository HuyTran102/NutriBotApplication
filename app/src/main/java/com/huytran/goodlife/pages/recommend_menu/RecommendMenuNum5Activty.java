package com.huytran.goodlife.pages.recommend_menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huytran.goodlife.R;

import java.util.Calendar;

public class RecommendMenuNum5Activty extends AppCompatActivity {
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView banhMi, pate, trung, rauSong, suaChua, cam, gaoTe1, ga, thitBaChi, biXanh, rauThom, dauAn1, duaLe, gaoTe2, chaThit, dauPhu, biDo, dauAn2, duDu, tongSang, tongTrua, tongToi;
    private String name;
    private double recommendWeight, recommendEnergy;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_menu_num5);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        banhMi = findViewById(R.id.banh_mi_g);
        pate = findViewById(R.id.pate_g);
        trung = findViewById(R.id.trung_g);
        rauSong = findViewById(R.id.rau_song_g);
        suaChua = findViewById(R.id.sua_chua_g);
        cam = findViewById(R.id.cam_g);
        gaoTe1 = findViewById(R.id.gao_te_1_g);
        ga = findViewById(R.id.ga_g);
        thitBaChi = findViewById(R.id.thit_ba_chi_g);
        biXanh = findViewById(R.id.bi_xanh_g);
        rauThom = findViewById(R.id.rau_thom_g);
        dauAn1 = findViewById(R.id.dau_an_1_g);
        duaLe = findViewById(R.id.dua_le_g);
        gaoTe2 = findViewById(R.id.gao_te_2_g);
        chaThit = findViewById(R.id.cha_thit_g);
        dauPhu = findViewById(R.id.dau_phu_g);
        biDo = findViewById(R.id.bi_do_g);
        dauAn2 = findViewById(R.id.dau_an_2_g);
        duDu = findViewById(R.id.du_du_g);
        tongSang = findViewById(R.id.tong_sang);
        tongTrua = findViewById(R.id.tong_trua);
        tongToi = findViewById(R.id.tong_toi);
        backButton = findViewById(R.id.back_button);

        SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);

        name = sp.getString("Name", null);

        LoadData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendMenuNum5Activty.this, RecommendMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void LoadData() {
        // Get the current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // tháng 1 bắt đầu từ 0 trong Calendar
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Task<QuerySnapshot> nutritionTask = firebaseFirestore.collection("GoodLife").document(name).collection("Dinh dưỡng").get();

        Task<QuerySnapshot> activityTask = firebaseFirestore.collection("GoodLife").document(name).collection("Hoạt động thể lực").whereEqualTo("year", String.valueOf(year)).whereEqualTo("month", String.valueOf(month)).whereEqualTo("day", String.valueOf(day)).get();

        Tasks.whenAll(nutritionTask, activityTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot nutritionResult = nutritionTask.getResult();
                if (nutritionResult != null) {
                    for (QueryDocumentSnapshot document : nutritionResult) {
                        if (document.getString("userHeight") != null && document.getString("userWeight") != null && document.getString("userRecommendHeight") != null && document.getString("userRecommendWeight") != null) {
                            try {
                                recommendWeight = Double.parseDouble(document.getString("userRecommendWeight"));
                            } catch (Exception e) {
                                recommendWeight = 0.0;
                            }
                        }
                    }
                }

                recommendEnergy = recommendWeight * 24 * 1.5;

                QuerySnapshot activityResult = activityTask.getResult();
                if (activityResult != null) {
                    double total_sum = 0;
                    for (QueryDocumentSnapshot document : activityResult) {
                        String amount = document.getString("userUsedEnergy");
                        if (amount != null && !amount.isEmpty()) {
                            try {
                                total_sum += Double.parseDouble(amount);
                            } catch (NumberFormatException e) {
                                Log.w("Firestore", "Error parsing used energy", e);
                            }
                        }
                    }

                    recommendEnergy = recommendWeight * 24 * 1.5 + total_sum;

                    double sang = recommendEnergy * 30 / 100, trua = recommendEnergy * 40 / 100, toi = recommendEnergy * 30 / 100;

                    banhMi.setText(String.format("%.0f", (sang * 40 / 100) * 100 / 110));
                    pate.setText(String.format("%.0f", (sang * 10 / 100) * 100 / 118));
                    trung.setText(String.format("%.0f", (sang * 23 / 100) * 100 / 230));
                    rauSong.setText(String.format("%.0f", (sang * 1 / 100) * 100 / 18));
                    suaChua.setText(String.format("%.0f", (sang * 20 / 100) * 100 / 61));
                    cam.setText(String.format("%.0f", (sang * 6 / 100) * 100 / 43));

                    gaoTe1.setText(String.format("%.0f", (trua * 48 / 100) * 100 / 347));
                    ga.setText(String.format("%.0f", (trua * 22 / 100) * 100 / 199));
                    thitBaChi.setText(String.format("%.0f", (trua * 10 / 100) * 100 / 73));
                    biXanh.setText(String.format("%.0f", (trua * 6 / 100) * 100 / 109));
                    rauThom.setText(String.format("%.0f", (trua * 1 / 100) * 100 / 187));
                    dauAn1.setText(String.format("%.0f", (trua * 3 / 100) * 100 / 900));
                    duaLe.setText(String.format("%.0f", (trua * 10 / 100) * 100 / 16));

                    gaoTe2.setText(String.format("%.0f", (toi * 48 / 100) * 100 / 347));
                    chaThit.setText(String.format("%.0f", (toi * 26 / 100) * 100 / 136));
                    dauPhu.setText(String.format("%.0f", (toi * 15 / 100) * 100 / 118));
                    biDo.setText(String.format("%.0f", (toi * 3 / 100) * 100 / 14));
                    dauAn2.setText(String.format("%.0f", (toi * 3 / 100) * 100 / 756));
                    duDu.setText(String.format("%.0f", (toi * 5 / 100) * 100 / 50));

                    tongSang.setText(String.format("%.0f", sang));

                    tongTrua.setText(String.format("%.0f", trua));

                    tongToi.setText(String.format("%.0f", toi));

                }

                Log.d("Firestore", "All tasks completed successfully");
            } else {
                Log.w("Firestore", "Error completing tasks", task.getException());
            }
        });
    }
}