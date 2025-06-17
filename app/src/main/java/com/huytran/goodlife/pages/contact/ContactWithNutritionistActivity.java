package com.huytran.goodlife.pages.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huytran.goodlife.R;
import com.huytran.goodlife.adapter.NutritionistAdapter;
import com.huytran.goodlife.model.NutritionistDataView;
import com.huytran.goodlife.pages.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactWithNutritionistActivity extends AppCompatActivity {
    private final List<NutritionistDataView> nutritionistDataViewList = new ArrayList<>();
    private ImageButton backButton;
    private NutritionistAdapter adapter;
    private RecyclerView recyclerView;
    private androidx.appcompat.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_with_nutritionist);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        recyclerView = findViewById(R.id.recycleView);
        searchView = findViewById(R.id.search_bar);
        backButton = findViewById(R.id.back_button);

        nutritionistDataViewList.add(new NutritionistDataView(
                R.drawable.nutritionist,
                4,
                "Vũ Thị Quỳnh Chi",
                "Hơn 15 năm nghiên cứu chuyên sâu về Dnh dưỡng",
                "Tham gia triển khai các trương trình dinh dưỡng học đường",
                "15 kinh nghiệm,\nDinh dưỡng lâm sàng, học đường",
                "quynhchiytb@gmail.com",
                "0989631715"
        ));

        nutritionistDataViewList.add(new NutritionistDataView(
                R.drawable.nutritionist2,
                4,
                "Lê Thị Hồng Vân",
                "Expert in Nutrition and Dietetics",
                "Specializes in weight management and sports nutrition.",
                "8 năm kinh nghiệm,\nDinh dưỡng lâm sàng, thể thao",
                "hongvanle@gmail.com",
                "0987654321"
        ));

        nutritionistDataViewList.add(new NutritionistDataView(
                R.drawable.nutritionist3,
                4,
                "Phạm Thị Vy",
                "Expert in Nutrition and Dietetics",
                "Specializes in weight management and sports nutrition.",
                "10 năm kinh nghiệm,\nDinh dưỡng lâm sàng, thể thao",
                "vypham@gmail.com",
                "0123456789"
        ));

        nutritionistDataViewList.add(new NutritionistDataView(
                R.drawable.nutritionist4,
                4,
                "Phan Đức Huy",
                "Expert in Nutrition and Dietetics",
                "Specializes in weight management and sports nutrition.",
                "10 năm kinh nghiệm,\nDinh dưỡng lâm sàng, nội khoa",
                "huyhuyphan@gmail.com",
                "0981234567"
        ));

        nutritionistDataViewList.add(new NutritionistDataView(
                R.drawable.nutritionist5,
                4,
                "Trần Lê Minh Thảo",
                "Expert in Nutrition and Dietetics",
                "Specializes in weight management and sports nutrition.",
                "10 năm kinh nghiệm,\nDinh dưỡng lâm sàng",
                "thaothao@gmailcom",
                "0987654321"
        ));

        adapter = new NutritionistAdapter(nutritionistDataViewList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                findData(newText);
                return true;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactWithNutritionistActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void findData(String name) {
        List<NutritionistDataView> list = new ArrayList<>();
        for (NutritionistDataView data : nutritionistDataViewList) {
            if (data.getName().toLowerCase().contains(name.toLowerCase())) {
                list.add(data);
            }
        }
        adapter.updateList(list);
    }

}