package com.huytran.goodlife.pages.scanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.huytran.goodlife.R;

import com.huytran.goodlife.pages.home.HomeActivity;
import com.huytran.goodlife.pages.physical.PhysicalActivity;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScannerActivity extends AppCompatActivity {

    private CameraView cameraView;
    private boolean isProcessing;
    private TextView txtLabel;
    private ImageButton backButton;
    private final ImageLabeler labeler = ImageLabeling.getClient(
            new ImageLabelerOptions.Builder()
                    .setConfidenceThreshold(0.7f)
                    .build());
    private Map<String, String> labelMap = new HashMap<>();

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
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        cameraView = findViewById(R.id.cameraView);
        backButton = findViewById(R.id.back_button);
        txtLabel = findViewById(R.id.txtLabel);

        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(this::processFrame);

    }

    @SuppressLint("DefaultLocale")
    private void processFrame(Frame frame) {
        if (isProcessing) return;
        isProcessing = true;

        byte[] data = frame.getData(); // NV21 format
        int rotation = frame.getRotationToUser();

        InputImage image = InputImage.fromByteArray(
                data,
                frame.getSize().getWidth(),
                frame.getSize().getHeight(),
                rotation,
                InputImage.IMAGE_FORMAT_NV21
        );


        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    if (labels.isEmpty()) {
                        runOnUiThread(() -> txtLabel.setText("Không nhận diện được."));
                    } else {
                        StringBuilder result = new StringBuilder();
                        for (ImageLabel label : labels) {
                            String vietnamese = labelMap.getOrDefault(label.getText(), label.getText());

                            result.append(String.format("Đối tượng: %s (%.1f%%)\n",
                                    vietnamese,
                                    label.getConfidence() * 100));
                        }
                        runOnUiThread(() -> txtLabel.setText(result.toString()));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MLKit", "Lỗi nhận diện: ", e);
                    runOnUiThread(() -> txtLabel.setText("Lỗi khi xử lý ảnh."));
                })
                .addOnCompleteListener(task -> isProcessing = false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScannerActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}