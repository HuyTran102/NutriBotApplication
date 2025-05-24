package com.huytran.goodlife.pages.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huytran.goodlife.pages.home.HomePageActivity;
import com.huytran.goodlife.R;
import com.huytran.goodlife.adapter.NotificationAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private List<NotificationDataActivity> list_items = new ArrayList<>();
    private String name;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        recyclerView = findViewById(R.id.recycleView);
        backButton = findViewById(R.id.back_button);

        firebaseFirestore = FirebaseFirestore.getInstance();

        SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);

        name = sp.getString("Name",null);

        loadData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void loadData() {
        // Get the current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        firebaseFirestore.collection("GoodLife")
                .document(name)
                .collection("Thông Báo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            // Loop through all documents
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                NotificationDataActivity new_item = new NotificationDataActivity(document.getString("name")
                                        , document.getString("information"), document.getString("time"), document.getString("date"));
                                String curr_date = month + " " + day + " " + year;
                                if(document.getString("date").equals(curr_date)) {
                                    list_items.add(new_item);
                                }

                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                Collections.sort(list_items, new Comparator<NotificationDataActivity>() {
                                    @Override
                                    public int compare(NotificationDataActivity e1, NotificationDataActivity e2) {
                                        try {
                                            Date time1 = timeFormat.parse(e1.getTime());
                                            Date time2 = timeFormat.parse(e2.getTime());
                                            return time1.compareTo(time2);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        return 0;
                                    }
                                });
//                                Toast.makeText(Notification.this, "" + new_item.name + " " + new_item.information + " " + new_item.time + "", Toast.LENGTH_SHORT).show();
                            }
                            NotificationAdapter viewAdapter = new NotificationAdapter(NotificationActivity.this, list_items);
                            recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                            recyclerView.setAdapter(viewAdapter);
                        } else {
                            Log.w("Firestore", "Error getting documents", task.getException());
                        }
                    }
                });
    }
}