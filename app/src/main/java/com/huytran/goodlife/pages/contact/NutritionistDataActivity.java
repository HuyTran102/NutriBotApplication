package com.huytran.goodlife.pages.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomeActivity;

public class NutritionistDataActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL = 1;
    private ImageView phoneButton, videoCallButton, textButton, emailButton, backButton;

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

        phoneButton = findViewById(R.id.contact_phone);
        videoCallButton = findViewById(R.id.contact_video_call);
        textButton = findViewById(R.id.contact_text);
        emailButton = findViewById(R.id.contact_email);
        backButton = findViewById(R.id.back_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(NutritionistDataActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall();
                } else {
                    ActivityCompat.requestPermissions(NutritionistDataActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                }
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.fromParts("sms", "0989631715", null));
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
                        "&add=quynhchiytb@gmail.com";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"quynhchiytb@gmail.com"});
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
        intent.setData(Uri.parse("tel:" + "0989631715"));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
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
        }
    }

}