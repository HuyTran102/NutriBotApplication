package com.huytran.goodlife.pages.recommend_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Calendar;

public class RecommendMenuNum4 extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView gaoNep, thitBaChi, trungCut, lapXuong, suaDauNanh, nho, gaoTe1, caHo
            , chaCom, cuaDong, rauDay, dauAn1, chuoi, gaoTe2, thitBo, nam, rauMuong, dauAn2, man
            , tongSang, tongTrua, tongToi;
    private String name;
    private double recommendWeight, recommendEnergy;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_menu_num4);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        gaoNep = findViewById(R.id.gao_nep_g);
        thitBaChi = findViewById(R.id.thit_ba_chi_g);
        trungCut = findViewById(R.id.trung_cut_g);
        lapXuong = findViewById(R.id.lap_xuong_g);
        suaDauNanh = findViewById(R.id.sua_dau_nanh_g);
        nho = findViewById(R.id.nho_g);
        gaoTe1 = findViewById(R.id.gao_te_1_g);
        caHo = findViewById(R.id.ca_ho_g);
        chaCom = findViewById(R.id.cha_com_g);
        cuaDong = findViewById(R.id.cua_dong_g);
        rauDay = findViewById(R.id.rau_day_g);
        dauAn1 = findViewById(R.id.dau_an_1_g);
        chuoi = findViewById(R.id.chuoi_g);
        gaoTe2 = findViewById(R.id.gao_te_2_g);
        thitBo = findViewById(R.id.thit_bo_g);
        nam = findViewById(R.id.nam_g);
        rauMuong = findViewById(R.id.rau_muong_g);
        dauAn2 = findViewById(R.id.dau_an_2_g);
        man = findViewById(R.id.man_g);
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
                Intent intent = new Intent(RecommendMenuNum4.this, RecommendMenu.class);
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

        Task<QuerySnapshot> nutritionTask = firebaseFirestore.collection("GoodLife")
                .document(name).collection("Dinh dưỡng")
                .get();

        Task<QuerySnapshot> activityTask = firebaseFirestore.collection("GoodLife")
                .document(name).collection("Hoạt động thể lực")
                .whereEqualTo("year", String.valueOf(year))
                .whereEqualTo("month", String.valueOf(month))
                .whereEqualTo("day", String.valueOf(day))
                .get();

        Tasks.whenAll(nutritionTask, activityTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot nutritionResult = nutritionTask.getResult();
                        if (nutritionResult != null) {
                            for (QueryDocumentSnapshot document : nutritionResult) {
                                if (document.getString("userHeight") != null
                                        && document.getString("userWeight") != null
                                        && document.getString("userRecommendHeight") != null
                                        && document.getString("userRecommendWeight") != null) {
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

                            double sang = recommendEnergy * 30 / 100, trua = recommendEnergy * 40 /100, toi = recommendEnergy * 30 / 100;

                            gaoNep.setText(String.format("%.0f", (sang * 20 / 100) * 100 / 332));
                            thitBaChi.setText(String.format("%.0f", (sang * 25 / 100) * 100 / 136));
                            trungCut.setText(String.format("%.0f", (sang * 15 / 100) * 100 / 103));
                            lapXuong.setText(String.format("%.0f", (sang * 5 / 100) * 100 / 18));
                            suaDauNanh.setText(String.format("%.0f", (sang * 25 / 100) * 100 / 109));
                            nho.setText(String.format("%.0f", (sang * 10 / 100) * 100 / 45));

                            gaoTe1.setText(String.format("%.0f", (trua * 48 / 100) * 100 / 347));
                            caHo.setText(String.format("%.0f", (trua * 20 / 100) * 100 / 187));
                            chaCom.setText(String.format("%.0f", (trua * 10 / 100) * 100 / 54));
                            cuaDong.setText(String.format("%.0f", (trua * 6 / 100) * 100 / 166));
                            rauDay.setText(String.format("%.0f", (trua * 3 / 100) * 100 / 26));
                            dauAn1.setText(String.format("%.0f", (trua * 3 / 100) * 100 / 900));
                            chuoi.setText(String.format("%.0f", (trua * 10 / 100) * 100 / 53));

                            gaoTe2.setText(String.format("%.0f", (toi * 48 / 100) * 100 / 347));
                            thitBo.setText(String.format("%.0f", (toi * 30 / 100) * 100 / 406));
                            nam.setText(String.format("%.0f", (toi * 10 / 100) * 100 / 139));
                            rauMuong.setText(String.format("%.0f", (toi * 4 / 100) * 100 / 29));
                            dauAn2.setText(String.format("%.0f", (toi * 3 / 100) * 100 / 900));
                            man.setText(String.format("%.0f", (toi * 5 / 100) * 100 / 36));

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