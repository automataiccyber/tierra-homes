package com.example.tierrahomes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

// Add these imports at the top of the file
import android.graphics.Color;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // Add these field declarations at class level
    private PieChart pieChart;
    private TextView noDataText;
    private com.airbnb.lottie.LottieAnimationView loadingAnimation;
    private DatabaseHelper dbHelper;
    private String currentLevel = "island";
    private String currentParent = null;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300; // milliseconds
    private TextView tvUserInside;
    private DatabaseReference usersRef;
    private boolean isNetworkAvailable = true;
    private android.os.Handler networkHandler;
    private Runnable networkRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Initialize UI elements
        ImageButton btnBuy = view.findViewById(R.id.btnBuy);
        ImageButton btnBuild = view.findViewById(R.id.btnBuild);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        tvUserInside = view.findViewById(R.id.tvUserInside);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users_locations");

        // Set OFFLINE immediately if offline
        isNetworkAvailable = NetworkUtils.isNetworkAvailable(requireContext());
        Log.d(TAG, "Initial network state: " + isNetworkAvailable);
        if (!isNetworkAvailable) {
            tvUserInside.setText("OFFLINE");
        }

        // Set up button click listeners
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
                transaction.replace(R.id.content_frame, new BuyFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btnBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current location data
                SharedPreferences preferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                String userEmail = preferences.getString("userEmail", "");
                DatabaseHelper.UserPreferences userPrefs = new DatabaseHelper(requireContext()).getUserPreferences(userEmail);

                // Create fragment with arguments
                BuildFragment buildFragment = new BuildFragment();
                if (userPrefs != null) {
                    Bundle args = new Bundle();
                    args.putString("region", userPrefs.getRegion());
                    args.putString("municipality", userPrefs.getMunicipality());
                    buildFragment.setArguments(args);
                }

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
                transaction.replace(R.id.content_frame, new BuildFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Load and display user data from SharedPreferences
        loadUserData(tvPrice, tvLocation);

        // Set up PieChart
        setupPieChart(view);

        setupUserCountListener();
        
        // Set up network state monitoring
        setupNetworkStateMonitoring();
        
        // Immediately check network state and update UI
        if (!isNetworkAvailable) {
            Log.d(TAG, "Initial network check: offline, showing no chart data");
            showNoChartDataAvailable();
        }

        return view;
    }

    /**
     * Load user data from SharedPreferences and display it
     * @param tvPrice TextView to display budget
     * @param tvLocation TextView to display location
     */
    private void loadUserData(TextView tvPrice, TextView tvLocation) {
        // Get the current user's email from SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");

        if (userEmail.isEmpty()) {
            Log.e(TAG, "User email is empty, cannot load user data");
            return;
        }

        // Get database helper
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // Load user preferences from the database
        DatabaseHelper.UserPreferences userPrefs = dbHelper.getUserPreferences(userEmail);

        if (userPrefs != null) {
            // Format and display budget - Using approach from BudgetFragment
            try {
                String budget = userPrefs.getBudget();
                Log.d(TAG, "Raw budget from DB: " + budget);

                // Parse as double to handle decimal places
                double budgetValue = Double.parseDouble(budget);

                // Format with consistent decimal places and thousands separators
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                String formattedBudget = formatter.format(budgetValue);

                tvPrice.setText(formattedBudget);
                Log.d(TAG, "Set budget to: " + formattedBudget);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing budget: " + e.getMessage());
                tvPrice.setText("0.00");
            }

            // Build location string from database values
            StringBuilder locationBuilder = new StringBuilder();

            String island = userPrefs.getIsland();
            String region = userPrefs.getRegion();
            String province = userPrefs.getProvince();
            String municipality = userPrefs.getMunicipality();

            // Check if we have NCR (National Capital Region)
            boolean isNCR = "NCR".equals(userPrefs.getRegionCode());

            // Build location string
            if (island != null && !island.isEmpty()) locationBuilder.append(island);

            if (region != null && !region.isEmpty()) {
                if (locationBuilder.length() > 0) locationBuilder.append(", ");
                locationBuilder.append(region);
            }

            // For NCR, skip province display since it's redundant
            if (!isNCR && province != null && !province.isEmpty() && !province.equals("NCR")) {
                if (locationBuilder.length() > 0) locationBuilder.append(", ");
                locationBuilder.append(province);
            }

            if (municipality != null && !municipality.isEmpty()) {
                if (locationBuilder.length() > 0) locationBuilder.append(", ");
                locationBuilder.append(municipality);
            }

            // Set the location text
            String locationText = locationBuilder.toString();
            if (locationText.isEmpty()) {
                tvLocation.setText("Location not set");
            } else {
                tvLocation.setText(locationText);
            }

            Log.d(TAG, "Set location to: " + locationText);
        } else {
            Log.e(TAG, "User preferences not found");
            tvPrice.setText("0.00");
            tvLocation.setText("Location not set");
        }
    }

    /**
     * Set up the PieChart with data
     * @param view The main view
     */
    private void setupPieChart(View view) {
        pieChart = view.findViewById(R.id.pieChart);
        noDataText = view.findViewById(R.id.noDataText);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        FirebaseDatabase.getInstance().getReference("users_locations")
                .addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Check if network is available before processing data
                        if (!isNetworkAvailable) {
                            Log.d(TAG, "Network unavailable, skipping chart update in setupPieChart");
                            showNoChartDataAvailable();
                            return;
                        }
                        
                        Map<String, Integer> locationCounts = new HashMap<>();

                        // Count locations based on current level
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            FirebaseManager.UserLocation location = userSnapshot.getValue(FirebaseManager.UserLocation.class);
                            if (location != null) {
                                String key = "";
                                switch (currentLevel) {
                                    case "island":
                                        key = location.island;
                                        break;
                                    case "region":
                                        key = location.region;
                                        break;
                                    case "province":
                                        key = location.province;
                                        break;
                                    case "municipality":
                                        key = location.municipality;
                                        break;
                                }
                                if (key != null && !key.isEmpty()) {
                                    locationCounts.put(key, locationCounts.getOrDefault(key, 0) + 1);
                                }
                            }
                        }

                        updatePieChartData(locationCounts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Firebase Database error: " + error.getMessage());
                    }
                });


        dbHelper = new DatabaseHelper(requireContext());

        // Configure chart appearance and interactions
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.TRANSPARENT); // Hide inner labels
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setRotationEnabled(false);

        // Add padding to the chart (left, top, right, bottom)
        pieChart.setExtraOffsets(10f, 10f, 0f, 10f);

        // Configure legend with two columns
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(8f);
        legend.setTextColor(Color.BLACK);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXOffset(10f);

        // Add click listener for drilling down
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    String selectedLocation = ((PieEntry) e).getLabel().split(" \\(")[0];
                    drillDown(selectedLocation);
                }
            }

            @Override
            public void onNothingSelected() {
                // Optional: Implement behavior when nothing is selected
            }
        });

        // Add long press listener for drilling up
        pieChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return drillUp();
            }
        });

        // Initialize with island-level data
        currentLevel = "island";
        currentParent = null;
        updateChartData();
    }

    private void drillDown(String selectedLocation) {
        switch(currentLevel) {
            case "island":
                currentLevel = "region";
                currentParent = selectedLocation;
                break;
            case "region":
                currentLevel = "province";
                currentParent = selectedLocation;
                break;
            case "province":
                currentLevel = "municipality";
                currentParent = selectedLocation;
                break;
            default:
                return; // Already at the lowest level
        }
        updateChartData();
    }

    private boolean drillUp() {
        switch(currentLevel) {
            case "municipality":
                String parentProvince = dbHelper.getParentProvince(currentParent);
                if (parentProvince != null) {
                    currentLevel = "province";
                    currentParent = parentProvince;
                }
                break;
            case "province":
                String parentRegion = dbHelper.getParentRegion(currentParent);
                if (parentRegion != null) {
                    currentLevel = "region";
                    currentParent = parentRegion;
                }
                break;
            case "region":
                currentLevel = "island";
                currentParent = null;
                break;
            default:
                return false; // Already at the highest level
        }
        updateChartData();
        return true;
    }

    private void updateChartData() {
        Log.d(TAG, "Updating chart data for level: " + currentLevel + ", parent: " + currentParent);

        // Check if network is available before fetching data
        if (!isNetworkAvailable) {
            showNoChartDataAvailable();
            return;
        }

        // Show loading animation and hide "no data" text when starting to load data
        showLoadingAnimation();
        if (pieChart != null) {
            // Clear any existing data to prevent old text from showing
            pieChart.clear();
        }

        FirebaseDatabase.getInstance().getReference("users_locations")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Integer> locationCounts = new HashMap<>();

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            FirebaseManager.UserLocation location = userSnapshot.getValue(FirebaseManager.UserLocation.class);
                            if (location != null) {
                                String key = "";
                                boolean shouldCount = false;

                                switch (currentLevel) {
                                    case "island":
                                        key = location.island;
                                        shouldCount = true;
                                        break;
                                    case "region":
                                        if (currentParent != null && currentParent.equals(location.island)) {
                                            key = location.region;
                                            shouldCount = true;
                                        }
                                        break;
                                    case "province":
                                        if (currentParent != null && currentParent.equals(location.region)) {
                                            key = location.province;
                                            shouldCount = true;
                                        }
                                        break;
                                    case "municipality":
                                        if (currentParent != null && currentParent.equals(location.province)) {
                                            key = location.municipality;
                                            shouldCount = true;
                                        }
                                        break;
                                }

                                if (shouldCount && key != null && !key.isEmpty()) {
                                    locationCounts.put(key, locationCounts.getOrDefault(key, 0) + 1);
                                }
                            }
                        }

                        // If no data found for current level/parent, drill up
                        if (locationCounts.isEmpty() && currentLevel != "island") {
                            drillUp();
                            return;
                        }

                        updatePieChartData(locationCounts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Firebase Database error: " + error.getMessage());
                    }
                });
    }

    private void updatePieChartData(Map<String, Integer> locationCounts) {
        // Check if network is available before showing chart data
        if (!isNetworkAvailable) {
            Log.d(TAG, "Network unavailable, showing no chart data instead of pie chart");
            showNoChartDataAvailable();
            return;
        }
        
        ArrayList<PieEntry> entries = new ArrayList<>();
        float total = 0;

        // Calculate total for percentages
        for (Map.Entry<String, Integer> entry : locationCounts.entrySet()) {
            total += entry.getValue();
        }

        // Create entries with both label and value
        for (Map.Entry<String, Integer> entry : locationCounts.entrySet()) {
            float percentage = (entry.getValue() / total) * 100;
            String label = String.format("%s (%d)", entry.getKey(), entry.getValue());
            entries.add(new PieEntry(entry.getValue(), label));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        // Use the same colors as in updateChartData
        int[] chartColors = new int[]{
                Color.rgb(62, 34, 20),    // Dark Brown
                Color.rgb(253, 247, 239),  // Beige
                Color.rgb(120, 75, 43),    // Medium Brown
                Color.rgb(44, 29, 16),     // Deep Brown
                Color.rgb(208, 199, 185),  // Warm Gray
                Color.rgb(92, 60, 34),     // Walnut Brown
                Color.rgb(147, 110, 84),   // Taupe
                Color.rgb(181, 158, 131),  // Sandstone
                Color.rgb(235, 224, 211),  // Light Beige
                Color.rgb(70, 45, 28)      // Chestnut Brown
        };
        dataSet.setColors(chartColors);

        // Configure value text
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        // Position values outside the slices
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.7f);
        dataSet.setValueLineColor(Color.BLACK);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        
        // Hide "no data" text and loading animation, show chart
        if (noDataText != null) {
            noDataText.setVisibility(View.GONE);
        }
        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(View.GONE);
        }
        pieChart.setVisibility(View.VISIBLE);
        
        // Re-enable interactions when data is available
        pieChart.setTouchEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
    }
    private void setupUserCountListener() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isNetworkAvailable) {
                    tvUserInside.setText("OFFLINE");
                } else {
                    long userCount = dataSnapshot.getChildrenCount();
                    String userCountText = "USERS: " + userCount;
                    tvUserInside.setText(userCountText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user count: " + databaseError.getMessage());
            }
        });
    }
    
    private void setupNetworkStateMonitoring() {
        // Clean up any existing monitoring first
        if (networkHandler != null && networkRunnable != null) {
            networkHandler.removeCallbacks(networkRunnable);
        }
        
        // Initialize handler and runnable
        networkHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        networkRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if fragment is still attached before proceeding
                if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                
                try {
                    boolean currentNetworkState = NetworkUtils.isNetworkAvailable(requireContext());
                    if (currentNetworkState != isNetworkAvailable) {
                        Log.d(TAG, "Network state changed: " + isNetworkAvailable + " -> " + currentNetworkState);
                        isNetworkAvailable = currentNetworkState;
                        updateChartForNetworkState();
                        updateUserCountForNetworkState();
                    }
                    
                    // Continue monitoring only if fragment is still attached
                    if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                        networkHandler.postDelayed(this, 2000); // Check every 2 seconds
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in network monitoring: " + e.getMessage());
                }
            }
        };
        
        // Start monitoring
        networkHandler.postDelayed(networkRunnable, 2000);
    }
    
    private void updateChartForNetworkState() {
        // Check if fragment is still attached
        if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
            Log.d(TAG, "updateChartForNetworkState: Fragment not attached, skipping");
            return;
        }
        
        Log.d(TAG, "updateChartForNetworkState: isNetworkAvailable = " + isNetworkAvailable);
        if (!isNetworkAvailable) {
            // Show "no chart data available" when offline
            Log.d(TAG, "Network unavailable, showing no chart data");
            showNoChartDataAvailable();
        } else {
            // Show loading animation when transitioning from offline to online
            Log.d(TAG, "Network available, showing loading animation and refreshing chart data");
            showLoadingAnimation();
            updateChartData();
        }
    }
    
    private void updateUserCountForNetworkState() {
        // Check if fragment is still attached
        if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        
        if (!isNetworkAvailable) {
            tvUserInside.setText("OFFLINE");
        } else {
            // Force refresh the user count when network is restored
            if (usersRef != null) {
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check if fragment is still attached before updating UI
                        if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        long userCount = dataSnapshot.getChildrenCount();
                        String userCountText = "USERS: " + userCount;
                        tvUserInside.setText(userCountText);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error fetching user count: " + databaseError.getMessage());
                    }
                });
            }
        }
    }
    
    private void showNoChartDataAvailable() {
        Log.d(TAG, "showNoChartDataAvailable called - pieChart: " + (pieChart != null) + ", noDataText: " + (noDataText != null));
        if (pieChart != null && noDataText != null && loadingAnimation != null) {
            // Hide the chart and loading animation, show "no data" text
            pieChart.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
            
            // Ensure text color is correct (not yellow)
            noDataText.setTextColor(requireContext().getResources().getColor(R.color.colorCard));
            
            // Disable chart interactions when offline
            pieChart.setTouchEnabled(false);
            pieChart.setHighlightPerTapEnabled(false);
            Log.d(TAG, "No chart data available - chart hidden, text shown");
        }
    }
    
    private void showLoadingAnimation() {
        Log.d(TAG, "showLoadingAnimation called");
        if (pieChart != null && noDataText != null && loadingAnimation != null) {
            // Hide chart and "no data" text, show loading animation
            pieChart.setVisibility(View.GONE);
            noDataText.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.VISIBLE);
            
            // Disable chart interactions during loading
            pieChart.setTouchEnabled(false);
            pieChart.setHighlightPerTapEnabled(false);
            Log.d(TAG, "Loading animation shown");
        }
    }
    

    
    @Override
    public void onResume() {
        super.onResume();
        // Check network state when fragment resumes
        try {
            if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                boolean currentNetworkState = NetworkUtils.isNetworkAvailable(requireContext());
                isNetworkAvailable = currentNetworkState;
                updateChartForNetworkState();
                updateUserCountForNetworkState();
                
                // Restart network monitoring
                setupNetworkStateMonitoring();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage());
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Stop network monitoring when fragment is paused
        if (networkHandler != null && networkRunnable != null) {
            networkHandler.removeCallbacks(networkRunnable);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler to prevent memory leaks
        if (networkHandler != null && networkRunnable != null) {
            networkHandler.removeCallbacks(networkRunnable);
        }
    }
}

    