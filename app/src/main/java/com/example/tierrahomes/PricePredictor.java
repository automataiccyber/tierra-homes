package com.example.tierrahomes;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class PricePredictor {
    private Interpreter tflite;
    private final Context context;
    private boolean isInitialized = false;
    private String errorMessage = "";

    public PricePredictor(Context context) {
        this.context = context;
        try {
            initializeInterpreter();
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
    }

    private void initializeInterpreter() {
        try {
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(loadModelFile(), options);
            isInitialized = true;
        } catch (Exception e) {
            errorMessage = "Error initializing TensorFlow Lite: " + e.getMessage();
        }
    }

    private MappedByteBuffer loadModelFile() throws Exception {
        String modelPath = "price_prediction.tflite";
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public Map<String, Object> analyzePrices(float[] prices) {
        Map<String, Object> analysis = new HashMap<>();
        
        if (!isInitialized) {
            analysis.put("error", errorMessage);
            return analysis;
        }

        try {
            // Prepare input data
            float[][] inputArray = new float[1][prices.length];
            inputArray[0] = prices;

            // Prepare output buffer
            float[][] outputArray = new float[1][3]; // [predicted_price, trend_confidence, volatility]

            // Run inference
            tflite.run(inputArray, outputArray);

            // Extract predictions
            analysis.put("predictedPrice", outputArray[0][0]);
            analysis.put("trendConfidence", outputArray[0][1]);
            analysis.put("volatility", outputArray[0][2]);
            analysis.put("success", true);

        } catch (Exception e) {
            analysis.put("error", "Error running TensorFlow analysis: " + e.getMessage());
        }

        return analysis;
    }
}