package com.huytran.goodlife.pages.tracking_diagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huytran.goodlife.pages.home.HomeActivity;
import com.huytran.goodlife.R;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TrackingDiagramActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ArrayList<Entry> entries = new ArrayList<>();
    private LocalDate currentDate;
    private LineChart lineChart;
    private PieChart pieChart, innerChart;
    private WeekFields weekFields;
    private LocalDate startOfWeek;
    private LocalDate endOfWeek;
    private String name;
    private ImageButton backButton;
    private LineDataSet dataSet1, dataSet2;

    private double actualWeight, actualHeight, usedEnergy, addEnergy, actualEnergy, recommendWeight, recommendHeight, recommendEnergy, statusWeight, statusHeight, statusEnergy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_diagram);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        innerChart = findViewById(R.id.innerChart);
        backButton = findViewById(R.id.back_button);

        SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);

        name = sp.getString("Name",null);

        currentDate = LocalDate.now();

        weekFields = WeekFields.of(Locale.getDefault());

        startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

//        Toast.makeText(this, "" + startOfWeek + " " + endOfWeek, Toast.LENGTH_SHORT).show();

        LoadLineChartData();

        LoadLineChartRecommendData();

        setupInnerChart();

        LoadPieChartData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrackingDiagramActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupInnerChart() {
        // Task 1: Lấy dữ liệu từ Firestore cho "Dinh dưỡng"
        Task<QuerySnapshot> nutritionTask = firebaseFirestore.collection("GoodLife")
                .document(name).collection("Dinh dưỡng")
                .get();

        // Task 2: Lấy dữ liệu từ Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query userDatabase = reference.orderByChild("name").equalTo(name);
        TaskCompletionSource<Void> realtimeDatabaseTask = new TaskCompletionSource<>();

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String gender = snapshot.child(name).child("gender").getValue(String.class);
                String date = snapshot.child(name).child("date_of_birth").getValue(String.class);

                String[] birth = date.split("/");
                String year_of_birth = birth[2];

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);

                int age = year - Integer.parseInt(year_of_birth);

                if (gender.equals("Nam")) {
                    if (10 <= age && age <= 11) {
                        recommendEnergy = 1900;
                    } else if (12 <= age && age <= 14) {
                        recommendEnergy = 2200;
                    } else {
                        recommendEnergy = 2500;
                    }
                } else {
                    if (10 <= age && age <= 11) {
                        recommendEnergy = 1750;
                    } else if (12 <= age && age <= 14) {
                        recommendEnergy = 2050;
                    } else {
                        recommendEnergy = 2100;
                    }
                }

                // Hoàn thành task khi dữ liệu từ Realtime Database được lấy
                realtimeDatabaseTask.setResult(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                realtimeDatabaseTask.setException(error.toException());
            }
        });

        // Get the current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // tháng 1 bắt đầu từ 0 trong Calendar
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Task 3: Lấy dữ liệu từ Firestore cho "Hoạt động thể lực"
        Task<QuerySnapshot> activityTask = firebaseFirestore.collection("GoodLife")
                .document(name).collection("Hoạt động thể lực")
                .whereEqualTo("year", String.valueOf(year))
                .whereEqualTo("month", String.valueOf(month))
                .whereEqualTo("day", String.valueOf(day))
                .get();

        // Task 4: Lấy dữ liệu từ Firestore cho "Nhật kí"
        Task<QuerySnapshot> diaryTask = firebaseFirestore.collection("GoodLife")
                .document(name)
                .collection("Nhật kí")
                .whereEqualTo("year", String.valueOf(year))
                .whereEqualTo("month", String.valueOf(month))
                .whereEqualTo("day", String.valueOf(day))
                .get();

        // Chờ cho tất cả các Task hoàn thành
        Tasks.whenAll(nutritionTask, realtimeDatabaseTask.getTask(), activityTask, diaryTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Task 1: Xử lý kết quả của Nutrition task
                        QuerySnapshot nutritionResult = nutritionTask.getResult();
                        if (nutritionResult != null) {
                            for (QueryDocumentSnapshot document : nutritionResult) {
                                if (document.getString("userHeight") != null
                                        && document.getString("userWeight") != null
                                        && document.getString("userRecommendHeight") != null
                                        && document.getString("userRecommendWeight") != null) {
                                    try {
                                        actualHeight = Double.parseDouble(document.getString("userHeight"));
                                    } catch (Exception e) {
                                        actualHeight = 0.0;
                                    }

                                    try {
                                        actualWeight = Double.parseDouble(document.getString("userWeight"));
                                    } catch (Exception e) {
                                        actualWeight = 0.0;
                                    }

                                    try {
                                        recommendHeight = Double.parseDouble(document.getString("userRecommendHeight"));
                                    } catch (Exception e) {
                                        recommendHeight = 0.0;
                                    }

                                    try {
                                        recommendWeight = Double.parseDouble(document.getString("userRecommendWeight"));
                                    } catch (Exception e) {
                                        recommendWeight = 0.0;
                                    }
                                }
                            }
                        }

                        recommendEnergy = recommendWeight * 24 * 1.5;

                        // Task 3: Xử lý kết quả của Activity task
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
                            usedEnergy = total_sum;

                            recommendEnergy = recommendWeight * 24 * 1.5 + usedEnergy;
                        }

                        double protein = (recommendEnergy * 0.2) / 4;
                        double lipid = (recommendEnergy * 0.2) / 9;
                        double glucid = (recommendEnergy * 0.6) / 4;

                        ArrayList<PieEntry> entries = new ArrayList<>();
                        entries.add(new PieEntry((float) glucid, "Bột"));
                        entries.add(new PieEntry((float) protein, "Đạm"));
                        entries.add(new PieEntry((float) lipid, "Béo"));

                        PieDataSet dataSet = new PieDataSet(entries, "");
                        dataSet.setColors(
                                Color.parseColor("#EBD405"),  // Bright Yellow
                                Color.parseColor("#E8A527"),  // Light Amber
                                Color.parseColor("#82BF3B")   // Avocado Green
                        );
                        dataSet.setValueTextSize(10f);
                        dataSet.setValueTextColor(getResources().getColor(R.color.text));
                        dataSet.setDrawValues(true);

                        PieData data = new PieData(dataSet);
                        innerChart.setData(data);
                        innerChart.setHoleRadius(0f);
                        innerChart.setTransparentCircleRadius(0f);
                        innerChart.getLegend().setEnabled(false);
                        innerChart.getDescription().setEnabled(false);
                        innerChart.setBackgroundColor(Color.TRANSPARENT);
                        innerChart.setExtraBottomOffset(18f);
                        innerChart.invalidate();

                        Log.d("Firestore", "All tasks completed successfully");
                    } else {
                        Log.w("Firestore", "Error completing tasks", task.getException());
                    }
                });
    }

    public void LoadPieChartData() {
        firebaseFirestore.collection("GoodLife")
                .document(name)
                .collection("Nhật kí")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Loop through all documents
                            if (!task.getResult().isEmpty()) {

                                double sum_protein = 0, sum_lipid = 0, sum_glucid = 0;
                                int sumKcal = 0;

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    int day = Integer.parseInt(document.getString("day")), month = Integer.parseInt(document.getString("month")), year = Integer.parseInt(document.getString("year"));
                                    if (LocalDate.now().getDayOfMonth() == day && LocalDate.now().getYear() == year
                                            && LocalDate.now().getMonthValue() == month) {
                                        sumKcal += Integer.parseInt(document.getString("kcal"));
                                        sum_protein += Double.parseDouble(document.getString("protein"));
                                        sum_lipid += Double.parseDouble(document.getString("lipid"));
                                        sum_glucid += Double.parseDouble(document.getString("glucid"));
                                    }
                                }


                                double protein_per = sum_protein / sumKcal * 100;
                                double lipid_per = sum_lipid / sumKcal * 100;
                                double glucid_per = sum_glucid / sumKcal * 100;

                                ArrayList<PieEntry> entries = new ArrayList<>();
                                entries.add(new PieEntry((float) glucid_per, "Bột"));
                                entries.add(new PieEntry((float) protein_per, "Đạm"));
                                entries.add(new PieEntry((float) lipid_per, "Béo"));

                                PieDataSet dataSet = new PieDataSet(entries, "");
                                dataSet.setColors(
                                        Color.parseColor("#2E7D32"),  // Leaf Green
                                        Color.parseColor("#CDDC39"),  // Lime Green
                                        Color.parseColor("#81C784"),  // Fresh Green
                                        Color.parseColor("#FFEB3B"),  // Bright Yellow
                                        Color.parseColor("#FFD54F"),  // Light Amber
                                        Color.parseColor("#9CCC65")   // Avocado Green
                                );
                                dataSet.setValueTextSize(12f);
                                dataSet.setValueTextColor(getResources().getColor(R.color.text));
                                dataSet.setDrawValues(true);

                                PieData pieData = new PieData(dataSet);
                                pieChart.setData(pieData);
                                pieChart.setUsePercentValues(true);
                                pieChart.getDescription().setEnabled(false);
                                Legend legend = pieChart.getLegend();
                                legend.setEnabled(true);
                                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // căn dưới
                                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // căn giữa
                                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                                legend.setDrawInside(false);
                                pieChart.animateY(1000);
                                pieChart.setHoleRadius(40f);
                                pieChart.setTransparentCircleRadius(53f);
                                pieChart.invalidate();
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents", task.getException());
                        }
                    }
                });
    }

    public void LoadLineChartData() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        firebaseFirestore.collection("GoodLife")
                .document(name)
                .collection("Nhật kí")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Loop through all documents
                            if (!task.getResult().isEmpty()) {

                                int dateIndex = 0;

                                for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {

                                    int sumKcal = 0;

                                    dateIndex++;

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String day = document.getString("day"), month = document.getString("month"), year = document.getString("year");

                                        if(Integer.parseInt(day) < 10) {
                                            day = "0" + day;
                                        }

                                        if(Integer.parseInt(month) < 10) {
                                            month = "0" + month;
                                        }

                                        String docDateStr = year + "-" + month + "-" + day;

                                        LocalDate docDate = LocalDate.parse(docDateStr, formatter);

                                        if (docDate.equals(date)) {
                                            sumKcal += Integer.parseInt(document.getString("kcal"));
                                        }
                                    }

                                    entries.add(new Entry(dateIndex, sumKcal));

                                }
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents", task.getException());
                        }

                        dataSet1 = new LineDataSet(entries, "Năng lượng (Kcal)");
                        dataSet1.setColor(getResources().getColor(R.color.purple_color));
                        dataSet1.setHighLightColor(getResources().getColor(R.color.highlight));
                        dataSet1.setCircleColor(getResources().getColor(R.color.dot));
                        dataSet1.setCircleHoleColor(getResources().getColor(R.color.purple_color));
                        dataSet1.setLineWidth(3f);
                        dataSet1.setValueTextColor(getResources().getColor(R.color.darkblue));
                        dataSet1.setValueTextSize(15f);
                    }
                });
    }

    public void LoadLineChartRecommendData() {
        ArrayList<Entry> entries = new ArrayList<>();

        // Task 1: Lấy dữ liệu từ Firestore cho "Dinh dưỡng"
        Task<QuerySnapshot> nutritionTask = firebaseFirestore.collection("GoodLife")
                .document(name).collection("Dinh dưỡng")
                .get();

        // Task 2: Lấy dữ liệu từ Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query userDatabase = reference.orderByChild("name").equalTo(name);
        TaskCompletionSource<Void> realtimeDatabaseTask = new TaskCompletionSource<>();

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String gender = snapshot.child(name).child("gender").getValue(String.class);
                String date = snapshot.child(name).child("date_of_birth").getValue(String.class);

                String[] birth = date.split("/");
                String year_of_birth = birth[2];

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);

                int age = year - Integer.parseInt(year_of_birth);

                if (gender.equals("Nam")) {
                    if (10 <= age && age <= 11) {
                        recommendEnergy = 1900;
                    } else if (12 <= age && age <= 14) {
                        recommendEnergy = 2200;
                    } else {
                        recommendEnergy = 2500;
                    }
                } else {
                    if (10 <= age && age <= 11) {
                        recommendEnergy = 1750;
                    } else if (12 <= age && age <= 14) {
                        recommendEnergy = 2050;
                    } else {
                        recommendEnergy = 2100;
                    }
                }

                // Hoàn thành task khi dữ liệu từ Realtime Database được lấy
                realtimeDatabaseTask.setResult(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                realtimeDatabaseTask.setException(error.toException());
            }
        });

        // Get the current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // tháng 1 bắt đầu từ 0 trong Calendar
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Task 3: Lấy dữ liệu từ Firestore cho "Hoạt động thể lực"
        Task<QuerySnapshot> activityTask = firebaseFirestore.collection("GoodLife")
                .document(name).collection("Hoạt động thể lực")
                .whereEqualTo("year", String.valueOf(year))
                .whereEqualTo("month", String.valueOf(month))
                .whereEqualTo("day", String.valueOf(day))
                .get();

        // Task 4: Lấy dữ liệu từ Firestore cho "Nhật kí"
        Task<QuerySnapshot> diaryTask = firebaseFirestore.collection("GoodLife")
                .document(name)
                .collection("Nhật kí")
                .whereEqualTo("year", String.valueOf(year))
                .whereEqualTo("month", String.valueOf(month))
                .whereEqualTo("day", String.valueOf(day))
                .get();

        // Chờ cho tất cả các Task hoàn thành
        Tasks.whenAll(nutritionTask, realtimeDatabaseTask.getTask(), activityTask, diaryTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Task 1: Xử lý kết quả của Nutrition task
                        QuerySnapshot nutritionResult = nutritionTask.getResult();
                        if (nutritionResult != null) {
                            for (QueryDocumentSnapshot document : nutritionResult) {
                                if (document.getString("userHeight") != null
                                        && document.getString("userWeight") != null
                                        && document.getString("userRecommendHeight") != null
                                        && document.getString("userRecommendWeight") != null) {
                                    try {
                                        actualHeight = Double.parseDouble(document.getString("userHeight"));
                                    } catch (Exception e) {
                                        actualHeight = 0.0;
                                    }

                                    try {
                                        actualWeight = Double.parseDouble(document.getString("userWeight"));
                                    } catch (Exception e) {
                                        actualWeight = 0.0;
                                    }

                                    try {
                                        recommendHeight = Double.parseDouble(document.getString("userRecommendHeight"));
                                    } catch (Exception e) {
                                        recommendHeight = 0.0;
                                    }

                                    try {
                                        recommendWeight = Double.parseDouble(document.getString("userRecommendWeight"));
                                    } catch (Exception e) {
                                        recommendWeight = 0.0;
                                    }
                                }
                            }
                        }

                        recommendEnergy = recommendWeight * 24 * 1.5;

                        // Toast.makeText(this, " " + recommendWeight + " ", Toast.LENGTH_SHORT).show();

                        // Task 3: Xử lý kết quả của Activity task
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
                            usedEnergy = total_sum;

                            recommendEnergy = recommendWeight * 24 * 1.5 + usedEnergy;
                        }

                        // Task 4: Xử lý kết quả của Diary task
                        QuerySnapshot diaryResult = diaryTask.getResult();
                        if (diaryResult != null) {
                            double total_sum = 0;
                            for (QueryDocumentSnapshot document : diaryResult) {
                                String amount = document.getString("kcal");
                                if (amount != null && !amount.isEmpty()) {
                                    try {
                                        total_sum += Double.parseDouble(amount);
                                    } catch (NumberFormatException e) {
                                        Log.w("Firestore", "Error parsing diary energy", e);
                                    }
                                }
                            }
                            addEnergy = total_sum;
                        }

                        int dateIndex = 0;
                        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
                            dateIndex++;
                            entries.add(new Entry(dateIndex, (float) recommendEnergy));

                        }

                        Log.d("Firestore", "All tasks completed successfully");
                    } else {
                        Log.w("Firestore", "Error completing tasks", task.getException());
                    }

                    dataSet2 = new LineDataSet(entries, "Năng lượng khuyến nghị (Kcal)");
                    dataSet2.setColor(getResources().getColor(R.color.red_color));
                    dataSet2.setHighLightColor(getResources().getColor(R.color.highlight));
                    dataSet2.setCircleColor(getResources().getColor(R.color.dot2));
                    dataSet2.setCircleHoleColor(getResources().getColor(R.color.red_color));
                    dataSet2.setLineWidth(3f);
                    dataSet2.setValueTextColor(getResources().getColor(R.color.dot2));
                    dataSet2.setValueTextSize(15f);
                    dataSet2.setDrawValues(false);

                    LineData lineData = new LineData(dataSet1, dataSet2);
                    Legend legend = lineChart.getLegend();
                    legend.setEnabled(true);
                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // căn dưới
                    legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // căn giữa
                    legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    legend.setDrawInside(false);
                    lineChart.setData(lineData);
                    lineChart.getDescription().setEnabled(false);
                    lineChart.getDescription().setTypeface(Typeface.DEFAULT_BOLD);
                    lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();

                });
    }

}