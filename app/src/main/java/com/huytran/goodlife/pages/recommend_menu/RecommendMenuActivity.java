package com.huytran.goodlife.pages.recommend_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.huytran.goodlife.pages.home.HomeActivity;
import com.huytran.goodlife.R;

public class RecommendMenuActivity extends AppCompatActivity {
    private LinearLayout menu1, menu2, menu3, menu4, menu5;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_menu);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        menu1 = findViewById(R.id.menu_1);
        menu2 = findViewById(R.id.menu_2);
        menu3 = findViewById(R.id.menu_3);
        menu4 = findViewById(R.id.menu_4);
        menu5 = findViewById(R.id.menu_5);
        backButton = findViewById(R.id.back_button);

        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendMenuActivity.this, RecommendMenuNum1Activity.class);
                startActivity(intent);
                finish();
            }
        });

        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendMenuActivity.this, RecommendMenuNum2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendMenuActivity.this, RecommendMenuNum3Activity.class);
                startActivity(intent);
                finish();
            }
        });

        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendMenuActivity.this, RecommendMenuNum4Activity.class);
                startActivity(intent);
                finish();
            }
        });

        menu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendMenuActivity.this, RecommendMenuNum5Activty.class);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendMenuActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}