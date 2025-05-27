package com.huytran.goodlife.pages.scanner;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.huytran.goodlife.R;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

public class ScannerActivity extends AppCompatActivity {

    private CameraView cameraView;
    private boolean isProcessing = false;

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

        cameraView = findViewById(R.id.cameraView);

        cameraView.setLifecycleOwner(this);

        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(Frame frame) {
                if (isProcessing) return;

                isProcessing = true;
                byte[] data = frame.getData();
                int rotation = FirebaseVisionImageMetadata.ROTATION_0;
                switch (frame.getRotation()) {
                    case 90: rotation = FirebaseVisionImageMetadata.ROTATION_90; break;
                    case 180: rotation = FirebaseVisionImageMetadata.ROTATION_180; break;
                    case 270: rotation = FirebaseVisionImageMetadata.ROTATION_270; break;
                }

                FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                        .setWidth(frame.getSize().getWidth())
                        .setHeight(frame.getSize().getHeight())
                        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                        .setRotation(rotation)
                        .build();

                try {
                    FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(data, metadata);

                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

                    labeler.processImage(image)
                            .addOnSuccessListener(labels -> {
                                for (FirebaseVisionImageLabel label : labels) {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    System.out.println("Label: " + text + " (confidence: " + confidence + ")");
                                }
                                isProcessing = false;
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                isProcessing = false;
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                    isProcessing = false;
                }
            }
        });

    }


}