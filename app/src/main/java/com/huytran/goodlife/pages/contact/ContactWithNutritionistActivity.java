package com.huytran.goodlife.pages.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomeActivity;

import java.util.Random;

public class ContactWithNutritionistActivity extends AppCompatActivity {
    private static final int REQUEST_PHONE_CALL = 1;
    private ImageView phoneButton, videoCallButton, textButton, emailButton, backButton;

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
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "0989631715"));
                startActivity(intent);
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

//                String url = "https://calendar.google.com/calendar/u/0/r/eventedit" +
//                        "?text=Cuộc+họp+tư+vấn+dinh+dưỡng" +
//                        "&details=Tham+gia+tại+link+sau" +
//                        "&location=https://meet.google.com/" +
//                        "&add=quynhchiytb@gmail.com";
//
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);

                String roomName = "Cuoc_hop_tu_van_dinh_duong";

                String jitsiUrl = "https://meet.jit.si/" + roomName;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822"); // MIME type dành riêng cho email
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"quynhchiytb@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Tham gia cuộc họp tư vấn dinh dưỡng");
                intent.putExtra(Intent.EXTRA_TEXT, "Xin mời tham gia cuộc họp tại đường link sau: " + jitsiUrl);

                try {
                    startActivity(Intent.createChooser(intent, "Chọn ứng dụng email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ContactWithNutritionistActivity.this, "Không tìm thấy ứng dụng email nào!", Toast.LENGTH_SHORT).show();
                }

                // Đợi vài giây rồi mở Jitsi Meet (nếu muốn tách riêng)
                new Handler().postDelayed(() -> {
                    Intent meetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jitsiUrl));
                    startActivity(meetIntent);
                }, 20000); // 20 giây sau mở link

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
                Intent intent = new Intent(ContactWithNutritionistActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    public String generateRandomRoomName(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder roomName = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            roomName.append(characters.charAt(index));
        }

        return roomName.toString();
    }

}