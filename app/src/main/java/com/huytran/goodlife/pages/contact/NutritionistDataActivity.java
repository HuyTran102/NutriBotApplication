package com.huytran.goodlife.pages.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huytran.goodlife.R;

public class NutritionistDataActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL = 1;
    private TextView nutritionistName, nutritionistDescription1, nutritionistDescription2;
    private ImageView nutritionistImage, phoneButton, videoCallButton, textButton, emailButton, backButton;
    private String email, phone;
    private boolean isCallRequested = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutritionist_data);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        nutritionistImage = findViewById(R.id.nutritionist_image);
        nutritionistName = findViewById(R.id.nutritionist_name);
        nutritionistDescription1 = findViewById(R.id.description_1);
        nutritionistDescription2 = findViewById(R.id.description_2);
        phoneButton = findViewById(R.id.contact_phone);
        videoCallButton = findViewById(R.id.contact_video_call);
        textButton = findViewById(R.id.contact_text);
        emailButton = findViewById(R.id.contact_email);
        backButton = findViewById(R.id.back_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }

        Intent intent = getIntent();
        String name = intent.getStringExtra("nutritionistName");
        String description1 = intent.getStringExtra("nutritionistDescription1");
        String description2 = intent.getStringExtra("nutritionistDescription2");
        int imageResource = intent.getIntExtra("nutritionistImage", R.drawable.nutritionist);
        email = intent.getStringExtra("nutritionistEmail");
        phone = intent.getStringExtra("nutritionistPhone");

        nutritionistName.setText(name);
        nutritionistDescription1.setText(description1);
        nutritionistDescription2.setText(description2);
        nutritionistImage.setImageResource(imageResource);

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(NutritionistDataActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall();
                } else {
                    isCallRequested = true;  // <-- Đánh dấu người dùng yêu cầu gọi
                    ActivityCompat.requestPermissions(NutritionistDataActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                }
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.fromParts("sms", phone, null));
                startActivity(intent);
            }
        });

        videoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://calendar.google.com/calendar/u/0/r/eventedit" +
                        "?text=Cuộc+họp+tư+vấn+dinh+dưỡng" +
                        "&details=Tham+gia+tại+link+sau" +
                        "&location=https://meet.google.com/" +
                        "&add=" + email;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NutritionistDataActivity.this, ContactWithNutritionistActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCallRequested) {
                    makePhoneCall();  // <-- Chỉ gọi nếu người dùng chủ động yêu cầu
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Cấp quyền gọi điện")
                            .setMessage("Ứng dụng cần quyền để gọi điện thoại. Bạn có muốn cấp quyền không?")
                            .setPositiveButton("Cho phép", (dialog, which) -> {
                                ActivityCompat.requestPermissions(NutritionistDataActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                            })
                            .setNegativeButton("Từ chối", null)
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Quyền bị từ chối")
                            .setMessage("Quyền gọi điện đã tắt vĩnh viễn. Vui lòng bật lại trong Cài đặt.")
                            .setPositiveButton("Mở Cài đặt", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            }
            isCallRequested = false;
        }
    }

}