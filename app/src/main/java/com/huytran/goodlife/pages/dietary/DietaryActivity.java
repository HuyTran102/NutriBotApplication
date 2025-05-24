package com.huytran.goodlife.pages.dietary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.huytran.goodlife.pages.home.HomePageActivity;
import com.huytran.goodlife.loading_dialog.LoadingDialog;
import com.huytran.goodlife.R;
import com.huytran.goodlife.adapter.VPAdapter;
import com.huytran.goodlife.fragment.FragmentDiary;
import com.huytran.goodlife.fragment.FragmentDrinks;
import com.huytran.goodlife.fragment.FragmentFood;
import com.huytran.goodlife.fragment.FragmentGroceries;

public class DietaryActivity extends AppCompatActivity {
    private Button backButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietary);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        backButton = findViewById(R.id.back_button);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.pageViewer);

        loadingDialog = new LoadingDialog(this);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new FragmentFood(), "Món ăn");
        vpAdapter.addFragment(new FragmentGroceries(), "Thực phẩm");
        vpAdapter.addFragment(new FragmentDrinks(), "Sữa / Đồ  uống");
        vpAdapter.addFragment(new FragmentDiary(), "Nhật ký");

        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Không cần xử lý
            }

            @Override
            public void onPageSelected(int position) {
                // Lấy Fragment hiện tại
                if (position == 0) { // Tab "Món ăn"
                    FragmentFood fragmentFood = (FragmentFood) vpAdapter.getItem(position);
                    fragmentFood.loadData();
                } else if (position == 1) { // Tab "Thực phẩm"
                    FragmentGroceries fragmentGroceries = (FragmentGroceries) vpAdapter.getItem(position);
                    fragmentGroceries.loadData();
                } else if (position == 2) { // Tab "Sữa / Đồ uống"
                    FragmentDrinks fragmentDrinks = (FragmentDrinks) vpAdapter.getItem(position);
                    fragmentDrinks.loadData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Không cần xử lý
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietaryActivity.this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }

    public void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.show("Đang chạy...");
        }
    }

    public void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.hide();
        }
    }
}