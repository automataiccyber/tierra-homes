package com.example.tierrahomes;

import android.content.Context;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.nio.MappedByteBuffer;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

public class PricePredictionModel {
    private Interpreter tflite;
    private final Context context;

    public PricePredictionModel(Context context) {
        this.context = context;
        loadModel();
    }

    private void loadModel() {
        try {
            String modelPath = "price_prediction_model.tflite";
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, modelPath);
            tflite = new Interpreter(tfliteModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float predictPrice(float[] historicalPrices) {
        // Prepare input data
        float[][] inputArray = new float[1][historicalPrices.length];
        inputArray[0] = historicalPrices;

        // Prepare output buffer
        float[][] outputArray = new float[1][1];

        // Run inference
        tflite.run(inputArray, outputArray);

        return outputArray[0][0];
    }
}