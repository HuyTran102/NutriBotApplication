package com.huytran.goodlife.pages.scanner.helper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Pair;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageClassifier {
    private List<String> labels;
    private Interpreter interpreter;

    public ImageClassifier(Context context) throws IOException {
        interpreter = new Interpreter(loadModelFile(context, "model_unquant.tflite"));
        labels = loadLabels(context, "labels.txt");
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(Context context, String labelPath) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(labelPath)));
        List<String> result = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        reader.close();
        return result;
    }

    public String classify(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        TensorImage tensorImage = new TensorImage(DataType.UINT8);
        tensorImage.load(bitmap);

        TensorBuffer output = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, DataType.UINT8);
        interpreter.run(tensorImage.getBuffer(), output.getBuffer().rewind());

        byte[] outputArray = output.getBuffer().array();
        int maxIndex = 0;
        int maxConfidence = 0;
        for (int i = 0; i < outputArray.length; i++) {
            int value = outputArray[i] & 0xFF;
            if (value > maxConfidence) {
                maxConfidence = value;
                maxIndex = i;
            }
        }

        return labels.get(maxIndex) + " (" + maxConfidence + ")";
    }

    public String classifyFLOAT32(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

        // Tạo TensorImage để nạp ảnh
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(bitmap);

        // Tạo đầu ra TensorBuffer
        TensorBuffer output = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, DataType.FLOAT32);

        // Chạy mô hình
        interpreter.run(tensorImage.getBuffer(), output.getBuffer().rewind());

        // Lấy mảng float kết quả
        float[] outputArray = output.getFloatArray();

        // Tìm nhãn có độ tin cậy cao nhất
        int maxIndex = 0;
        float maxConfidence = 0;

        // Dừng sớm nếu confidence vượt ngưỡng
        float threshold = 0.95f;

        for (int i = 0; i < outputArray.length; i++) {
            if (outputArray[i] > maxConfidence) {
                maxConfidence = outputArray[i];
                maxIndex = i;

                // Dừng nếu vượt ngưỡng
                if (maxConfidence >= threshold) {
                    break;
                }
            }
        }

        return labels.get(maxIndex) + " (" + String.format("%.2f", maxConfidence * 100) + "%)";
    }

    public List<Pair<String, Float>> classifyTopK(Bitmap bitmap, int topK) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(bitmap);

        TensorBuffer output = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, DataType.FLOAT32);
        interpreter.run(tensorImage.getBuffer(), output.getBuffer().rewind());

        float[] outputArray = output.getFloatArray();

        // Tạo list cặp (label, confidence)
        List<Pair<String, Float>> labeledConfidences = new ArrayList<>();
        for (int i = 0; i < outputArray.length; i++) {
            labeledConfidences.add(new Pair<>(labels.get(i), outputArray[i]));
        }

        // Sắp xếp giảm dần theo confidence
        Collections.sort(labeledConfidences, (a, b) -> Float.compare(b.second, a.second));

        // Lấy Top-K
        if (topK > labeledConfidences.size()) {
            topK = labeledConfidences.size();
        }
        return labeledConfidences.subList(0, topK);
    }
}
