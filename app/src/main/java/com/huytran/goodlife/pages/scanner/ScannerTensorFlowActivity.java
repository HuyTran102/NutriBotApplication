package com.huytran.goodlife.pages.scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomeActivity;
import com.huytran.goodlife.pages.scanner.helper.ImageClassifier;
import com.otaliastudios.cameraview.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScannerTensorFlowActivity extends AppCompatActivity {

    private final int REQUIRED_STABLE_COUNT = 2; // Giả sử 1 frame mỗi ~300ms => 5 lần là khoảng 1.5s
    private final List<Pair<String, Float>> tempResults = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CameraView cameraView;
    private boolean isProcessing;
    private TextView objectName, objectKcalo, objectProtein, objectLipid, objectGlucid;
    private ImageClassifier classifier;
    private ImageButton backButton;
    private Bitmap tempBitmap = null;
    private final Runnable resultRunnable = () -> {
        if (tempResults.isEmpty()) return;

        // Sắp xếp để lấy kết quả có confidence cao nhất
        Collections.sort(tempResults, (a, b) -> Float.compare(b.second, a.second));
        Pair<String, Float> bestResult = tempResults.get(0);

        // Hiển thị kết quả lên UI
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ScannerTensorFlowActivity.this);
            builder.setTitle("Kết quả nhận diện");

            builder.setPositiveButton("OK", null);
            builder.show();
        });

        // Xóa dữ liệu cũ, đợi đếm tiếp 5 giây mới lại lấy dữ liệu
        tempResults.clear();
        tempBitmap = null;

//            // Bắt đầu lại đếm 5 giây
//            handler.postDelayed((Runnable) ScannerTensorFlowActivity.this, 5000);
    };
    private String lastDetectedLabel = "";
    private int detectionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scanner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        cameraView = findViewById(R.id.cameraView);
        objectName = findViewById(R.id.item_name);
        objectKcalo = findViewById(R.id.item_kcalo);
        objectProtein = findViewById(R.id.item_protein);
        objectLipid = findViewById(R.id.item_lipid);
        objectGlucid = findViewById(R.id.item_glucid);
        backButton = findViewById(R.id.back_button);

        cameraView.setLifecycleOwner(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            setupCamera();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScannerTensorFlowActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setupCamera() {
        try {
            classifier = new ImageClassifier(this);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        cameraView.setLifecycleOwner(this);

        cameraView.addFrameProcessor(frame -> {
            if (isProcessing) return;
            isProcessing = true;

            byte[] data = frame.getData();
            int width = frame.getSize().getWidth();
            int height = frame.getSize().getHeight();

            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, width, height), 90, out);
            byte[] jpegData = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);

            int rotation = frame.getRotationToUser();
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            // Gọi classify trả về label chính xác
            String result = classifier.classifyFLOAT32(rotatedBitmap);
            String[] words = result.split(",");

            if (words.length < 5) {
                isProcessing = false;
                return;
            }

            String ob_name = words[0];
            String[] name = ob_name.split(" ");
            if (name.length < 2) {
                isProcessing = false;
                return;
            }
            String label = name[1];
            String kcalo_val = words[1];
            String protein_val = words[2];
            String lipid_val = words[3];
            String glucid_val = words[4];

            // Nếu cùng label thì tăng đếm, nếu khác thì reset
            if (label.equals(lastDetectedLabel)) {
                detectionCount++;
            } else {
                lastDetectedLabel = label;
                detectionCount = 1;
            }

            // Chỉ khi nhận diện liên tục trên N lần mới hiển thị
            if (detectionCount >= REQUIRED_STABLE_COUNT) {
                runOnUiThread(() -> {
                    objectName.setText(label);
                    objectKcalo.setText(kcalo_val);
                    objectProtein.setText(protein_val);
                    objectLipid.setText(lipid_val);
                    objectGlucid.setText(glucid_val);
                });
            }

            // Gọi classify trả về List<Pair<String, Float>>
            List<Pair<String, Float>> results = classifier.classifyTopK(rotatedBitmap, 3);

            // Lưu kết quả tốt nhất của frame này vào danh sách tạm
            if (!results.isEmpty()) {
                Pair<String, Float> best = results.get(0);

                // Cập nhật list kết quả chung (lưu lại kết quả tốt nhất trong 5 giây)
                synchronized (tempResults) {
                    tempResults.add(best);
                    // Cập nhật bitmap mới nhất
                    tempBitmap = rotatedBitmap;
                }
            }

            isProcessing = false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.close();
    }

}