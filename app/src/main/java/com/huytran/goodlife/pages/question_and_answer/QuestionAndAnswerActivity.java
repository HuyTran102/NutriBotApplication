package com.huytran.goodlife.pages.question_and_answer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomeActivity;

public class QuestionAndAnswerActivity extends AppCompatActivity {
    private LinearLayout basicNutrition, nutritionSchoolHealth, examSeasonNutrition, nutritionPhysicalActivity, nutritionMentalHealth, nutritionPubertyPeriod, malnutrition, overWeight;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_and_answer);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        basicNutrition = findViewById(R.id.basic_nutrition);
        nutritionSchoolHealth = findViewById(R.id.nutrition_school_health);
        examSeasonNutrition = findViewById(R.id.exam_season_nutrition);
        nutritionPhysicalActivity = findViewById(R.id.nutrition_physical_activity);
        nutritionPubertyPeriod = findViewById(R.id.nutrition_puberty_period);
        nutritionMentalHealth = findViewById(R.id.nutrition_mental_health);
        malnutrition = findViewById(R.id.malnutrition);
        overWeight = findViewById(R.id.over_weigth);
        backButton = findViewById(R.id.back_button);

        basicNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, BasicNutritionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nutritionSchoolHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, NutritionSchoolHealthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        examSeasonNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, ExamSeasonNutritionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nutritionPhysicalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, NutritionPhysicalActivityActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nutritionMentalHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, NutritionMentalHealthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nutritionPubertyPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, NutritionPubertyPeriodActivity.class);
                startActivity(intent);
                finish();
            }
        });

        malnutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, MalnutritionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        overWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, OverWeightActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionAndAnswerActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}