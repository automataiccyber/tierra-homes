package com.example.tierrahomes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

public class LoadingScreenActivity extends AppCompatActivity {

    private static final String TAG = "LoadingScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Initialize Firebase
        try {
            FirebaseDatabase.getInstance();
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage());
        }

        // Critical: Log at the beginning to verify this activity starts
        Log.e(TAG, "LoadingScreenActivity onCreate STARTED");

        try {
            setContentView(R.layout.loading_screen);
            Log.e(TAG, "Layout loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting content view: " + e.getMessage());
            e.printStackTrace();
        }

        // Start animations
        startLoadingAnimations();

        // Use a simple Handler with explicit Looper
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.e(TAG, "LoadingScreenActivity delay completed, preparing navigation");

            try {
                // Initialize database helper
                DatabaseHelper dbHelper = new DatabaseHelper(LoadingScreenActivity.this);

                // Check login status and remember me preference
                SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
                boolean rememberMe = preferences.getBoolean("rememberMe", false);
                String userEmail = preferences.getString("userEmail", "");

                Log.e(TAG, "Login state: isLoggedIn=" + isLoggedIn + ", rememberMe=" + rememberMe);

                Intent intent;

                // Only auto-login if both isLoggedIn is true AND rememberMe was checked
                if (isLoggedIn && rememberMe && !userEmail.isEmpty()) {
                    boolean isSetupComplete = dbHelper.isUserSetupComplete(userEmail);

                    // Update SharedPreferences with the latest database value
                    preferences.edit().putBoolean("isSetupComplete", isSetupComplete).apply();

                    // Navigate based on setup status
                    if (isSetupComplete) {
                        Log.e(TAG, "Navigation: Going to MainScreenActivity");
                        intent = new Intent(LoadingScreenActivity.this, MainScreenActivity.class);
                    } else {
                        Log.e(TAG, "Navigation: Going to BudgetAndLocationActivity");
                        intent = new Intent(LoadingScreenActivity.this, BudgetAndLocationActivity.class);
                    }
                } else {
                    // Not logged in or Remember Me not checked, go to login screen
                    Log.e(TAG, "Navigation: Going to LoginScreenActivity");
                    intent = new Intent(LoadingScreenActivity.this, LoginScreenActivity.class);
                }

                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Error during navigation: " + e.getMessage());
                e.printStackTrace();

                // Emergency fallback to login screen
                Intent fallbackIntent = new Intent(LoadingScreenActivity.this, LoginScreenActivity.class);
                startActivity(fallbackIntent);
                finish();
            }
        }, 3500); // Reduced to 3.5 seconds since we removed the loading animation
    }

    private void startLoadingAnimations() {
        ImageView logoImage = findViewById(R.id.logoImage);
        TextView appNameText = findViewById(R.id.appNameText);
        TextView developerText = findViewById(R.id.developerText);

        // Animate logo first
        logoImage.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1000)
                .setStartDelay(200)
                .withEndAction(() -> {
                    // After logo animation, animate app name
                    appNameText.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(800)
                            .setStartDelay(200)
                            .withEndAction(() -> {
                                // After app name, animate developer text
                                developerText.animate()
                                        .alpha(1f)
                                        .translationY(0f)
                                        .setDuration(600)
                                        .setStartDelay(100);
                            });
                });
    }
}