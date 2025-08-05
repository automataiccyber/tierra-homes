package com.example.tierrahomes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;
import com.example.tierrahomes.NetworkUtils;

public class MainScreenActivity extends AppCompatActivity {

    private static final String TAG = "MainScreenActivity";
    private boolean isDarkMode = false;
    private Fragment currentFragment;
    private boolean isMenuVisible = false;
    private View currentIndicator = null;
    private NetworkReceiver networkReceiver;
    private DatabaseHelper dbHelper;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Always check system theme on app start
        boolean isSystemDark = (getResources().getConfiguration().uiMode & 
                              Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        isDarkMode = isSystemDark;
        
        // Apply the system theme before setting content view
        applyTheme(isDarkMode);
        
        setContentView(R.layout.main_screen);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up network monitoring
        setupNetworkMonitoring();

        // Initialize views
        ImageView themeToggle = findViewById(R.id.themeToggle);
        ImageView menuButton = findViewById(R.id.menuButton);
        LinearLayout dropdownMenu = findViewById(R.id.dropdownMenu);
        
        // Set smaller text sizes for dropdown menu items
        TextView menuBudget = findViewById(R.id.menuBudget);
        TextView menuLocation = findViewById(R.id.menuLocation);
        TextView menuLogout = findViewById(R.id.menuLogout);
        
        // Apply responsive text sizing to dropdown menu items
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            menuBudget,
            8,  // min size in sp
            12, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            menuLocation,
            8,  // min size in sp
            12, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            menuLogout,
            8,  // min size in sp
            12, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        // Restore menu visibility state if recreating
        if (savedInstanceState != null) {
            isMenuVisible = savedInstanceState.getBoolean("menuVisible", false);
            dropdownMenu.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
        }

        // Set initial toggle button state
        themeToggle.setImageResource(isDarkMode ? R.drawable.switchon : R.drawable.swithoff);

                themeToggle.setOnClickListener(v -> {
            isDarkMode = !isDarkMode;

            // Animate dropdown menu up before hiding it
            if (dropdownMenu.getVisibility() == View.VISIBLE) {
                Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                slideUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dropdownMenu.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                dropdownMenu.startAnimation(slideUp);
            }
            
            // Update toggle button image
            themeToggle.setImageResource(isDarkMode ? R.drawable.switchon : R.drawable.swithoff);
            
            // Store current fragment
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            
            // Apply theme change
            applyTheme(isDarkMode);
            
            // Recreate activity and restore the current fragment
            recreate();
        });

        menuButton.setOnClickListener(v -> {
            isMenuVisible = !isMenuVisible;
            dropdownMenu.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
        });

        // Check if we're being recreated and have a saved fragment
        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            if (currentFragment != null) {
                loadFragment(currentFragment);
            } else {
                loadFragment(new HomeFragment());
            }
        } else {
            loadFragment(new HomeFragment());
        }

        // Initialize Firebase house sales data
        initializeFirebaseHouseSales();
        
        // Force initialize Firebase data (for testing)
        // forceInitializeFirebaseData(); // This line is removed as per the edit hint.

        // Initialize indicators
        View assetIndicator = findViewById(R.id.assetIndicator);
        View homeIndicator = findViewById(R.id.homeIndicator);
        View infoIndicator = findViewById(R.id.infoIndicator);

        // Set initial indicator for home
        homeIndicator.setVisibility(View.VISIBLE);
        currentIndicator = homeIndicator;

        // Bottom navigation with indicators
        findViewById(R.id.keyButton).setOnClickListener(v -> {
            updateIndicator(assetIndicator);
            loadFragment(new AssetFragment(), true);
        });

        findViewById(R.id.homeButton).setOnClickListener(v -> {
            updateIndicator(homeIndicator);
            loadFragment(new HomeFragment(), true);
        });

        findViewById(R.id.infoButton).setOnClickListener(v -> {
            updateIndicator(infoIndicator);
            loadFragment(new AboutFragment(), true);
        });


        menuButton.setOnClickListener(v -> {
            if (dropdownMenu.getVisibility() == View.GONE) {
                // Show menu with slide down animation
                Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
                dropdownMenu.startAnimation(slideDown);
                dropdownMenu.setVisibility(View.VISIBLE);
            } else {
                // Hide menu with slide up animation
                Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                slideUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dropdownMenu.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                dropdownMenu.startAnimation(slideUp);
            }
        });


        // Dropdown Menu Actions
        findViewById(R.id.menuBudget).setOnClickListener(v -> {
            // Animate dropdown menu up
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    dropdownMenu.setVisibility(View.GONE);
                    // Load Budget Fragment with animation
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    );
                    transaction.replace(R.id.content_frame, new BudgetFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            dropdownMenu.startAnimation(slideUp);
        });

        findViewById(R.id.menuLocation).setOnClickListener(v -> {
            // Animate dropdown menu up
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    dropdownMenu.setVisibility(View.GONE);
                    // Load Location Fragment with animation
                    try {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                        );
                        transaction.replace(R.id.content_frame, new LocationFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                        Log.d(TAG, "LocationFragment loaded successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading LocationFragment: " + e.getMessage(), e);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            dropdownMenu.startAnimation(slideUp);
        });

        findViewById(R.id.menuLogout).setOnClickListener(v -> {
            dropdownMenu.setVisibility(View.GONE);
            // When logging out, keep the user email but set isLoggedIn to false
            // This way the email field can be pre-filled on the login screen
            SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String userEmail = preferences.getString("userEmail", "");

            preferences.edit()
                    .putBoolean("isLoggedIn", false)
                    // Keep the email but clear other sensitive info
                    .putString("userEmail", userEmail)
                    // Keep rememberMe setting as is
                    .apply();

            Log.d(TAG, "User logged out. Email saved for convenience: " + userEmail);

            // Go to login screen
            Intent intent = new Intent(MainScreenActivity.this, LoginScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears back stack
            startActivity(intent);

            // Add transition animation
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current fragment and menu state
        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
        }
        outState.putBoolean("menuVisible", isMenuVisible);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Update theme toggle button when system theme changes
        ImageView themeToggle = findViewById(R.id.themeToggle);
        if (themeToggle != null) {
            themeToggle.setImageResource(isDarkMode ? R.drawable.switchon : R.drawable.swithoff);
        }
    }

    private void loadFragment(Fragment fragment) {
        try {
            currentFragment = fragment; // Update current fragment reference
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Log.d(TAG, "Fragment loaded: " + fragment.getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading page. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyTheme(boolean darkMode) {
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void updateIndicator(View newIndicator) {
        if (currentIndicator != null) {
            currentIndicator.setVisibility(View.GONE);
        }
        newIndicator.setVisibility(View.VISIBLE);
        currentIndicator = newIndicator;
    }

    private void loadFragment(Fragment fragment, boolean animate) {
        try {
            currentFragment = fragment;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (animate) {
                transaction.setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );
            }

            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Log.d(TAG, "Fragment loaded: " + fragment.getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading page. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeFirebaseHouseSales() {
        FirebaseManager firebaseManager = new FirebaseManager();
        
        firebaseManager.getAllHouseSales(new FirebaseManager.HouseSalesMapCallback() {
            @Override
            public void onSalesMapReceived(Map<String, Integer> salesMap) {
                if (salesMap.isEmpty()) {
                    // If no data exists, initialize it
                    firebaseManager.initializeHouseSales();
                    Log.d(TAG, "Firebase house sales initialized");
                } else {
                    Log.d(TAG, "Firebase house sales data found: " + salesMap.size() + " house types");
                }
            }
        });
    }

    private void setupNetworkMonitoring() {
        networkReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    Log.d(TAG, "Network connection restored");
                    // Sync offline purchases when network becomes available
                    if (dbHelper != null) {
                        dbHelper.syncOfflinePurchases();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for offline purchases to sync when app resumes
        if (NetworkUtils.isNetworkAvailable(this) && dbHelper != null) {
            dbHelper.syncOfflinePurchases();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }
}