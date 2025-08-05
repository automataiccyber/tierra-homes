package com.example.tierrahomes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    
    public static class OfflineQueue {
        private static final String PREF_NAME = "offline_queue";
        private static final String KEY_QUEUE_SIZE = "queue_size";
        private static final String KEY_QUEUE_ITEM = "queue_item_";
        
        public static void addToQueue(Context context, String houseType) {
            android.content.SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            int queueSize = prefs.getInt(KEY_QUEUE_SIZE, 0);
            
            // Add to queue
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_QUEUE_ITEM + queueSize, houseType);
            editor.putInt(KEY_QUEUE_SIZE, queueSize + 1);
            editor.apply();
            
            Log.d(TAG, "Added " + houseType + " to offline queue. Queue size: " + (queueSize + 1));
        }
        
        public static List<String> getQueue(Context context) {
            android.content.SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            int queueSize = prefs.getInt(KEY_QUEUE_SIZE, 0);
            List<String> queue = new ArrayList<>();
            
            for (int i = 0; i < queueSize; i++) {
                String houseType = prefs.getString(KEY_QUEUE_ITEM + i, null);
                if (houseType != null) {
                    queue.add(houseType);
                }
            }
            
            return queue;
        }
        
        public static void clearQueue(Context context) {
            android.content.SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Log.d(TAG, "Cleared offline queue");
        }
        
        public static boolean hasPendingItems(Context context) {
            android.content.SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            int queueSize = prefs.getInt(KEY_QUEUE_SIZE, 0);
            return queueSize > 0;
        }
        
        public static int getQueueSize(Context context) {
            android.content.SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return prefs.getInt(KEY_QUEUE_SIZE, 0);
        }
    }
} 