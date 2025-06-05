package com.huytran.goodlife.splash;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomeActivity;
import com.huytran.goodlife.pages.intro.IntroActivity;
import com.huytran.goodlife.pages.login.LoginScreenActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    File myInternalFile;
    String username_tmp, password_tmp, islogin = "false";
    private final String filename = "Storage.txt";
    private final String filepath = "Super_mystery_folder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // check login
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory, filename);
        if (check_is_login()) {
            checkUserData(username_tmp, password_tmp);
        } else {
            Log.d("wtf", "wtf");
            Intent intent = new Intent(SplashActivity.this, LoginScreenActivity.class);
            startActivity(intent);
            finish();
        }

    }

    // Login function
    public void checkUserData(String name, String password) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase = reference.orderByChild("name").equalTo(name);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child(name).child("password").getValue(String.class);

                    if (Objects.equals(passwordFromDB, password)) {
                        try {
                            String data = name + "\n" + password + "\n" + "true";
                            FileOutputStream fos = new FileOutputStream(myInternalFile);
                            fos.write(data.getBytes());
                            fos.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                            if (e instanceof IOException) {

                            }
                        }

                        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        boolean isIntroShown = prefs.getBoolean("isIntroShown", false);

                        if (isIntroShown) {
                            // Nếu đã xem intro, vào thẳng Home
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Nếu chưa xem intro, vào Intro
                            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                            startActivity(intent);
                            finish();
                        }

//                        Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
//                        startActivity(intent);
//                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public boolean check_is_login() {
        try {
            FileInputStream fis = new FileInputStream(myInternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i = 1;
            while ((strLine = br.readLine()) != null) {
                if (i == 1) username_tmp = strLine;
                if (i == 2) password_tmp = strLine;
                if (i == 3) islogin = strLine;
                i++;
            }
            in.close();
            return islogin.equals("true");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}