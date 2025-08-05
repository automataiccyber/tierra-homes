package com.example.tierrahomes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;  // Add this import for ImageView
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import androidx.cardview.widget.CardView;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BudgetFragment extends Fragment {
    private static final String TAG = "BudgetFragment";

    // UI Components
    private TextView tvPrice; // This is for displaying the total budget
    private EditText moneyEditText; // For input amount
    private Button addButton;
    private ScrollView budgetHistoryScrollView;
    private LinearLayout budgetHistoryContainer; // Container for history items

    private DatabaseHelper dbHelper;
    private String userEmail;
    private String currentBudget = "0";

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.budget_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Initialize UI components
            tvPrice = view.findViewById(R.id.tvPrice);
            moneyEditText = view.findViewById(R.id.moneyy);
            addButton = view.findViewById(R.id.add);

            // Remove the custom hint - will use default hint from XML layout

            // Handle the ScrollView for budget history
            budgetHistoryScrollView = view.findViewById(R.id.budget_history_scroll);

            // Setup budget history container
            if (budgetHistoryScrollView.getChildCount() > 0 &&
                    budgetHistoryScrollView.getChildAt(0) instanceof LinearLayout) {
                budgetHistoryContainer = (LinearLayout) budgetHistoryScrollView.getChildAt(0);
                // Clear any example entries
                budgetHistoryContainer.removeAllViews();
                
                // Add padding to the container
                budgetHistoryContainer.setPadding(
                    dpToPx(16), // left
                    dpToPx(24), // top
                    dpToPx(16), // right
                    dpToPx(24)  // bottom
                );
            } else {
                // Create a LinearLayout for history items
                budgetHistoryContainer = new LinearLayout(requireContext());
                budgetHistoryContainer.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                budgetHistoryContainer.setOrientation(LinearLayout.VERTICAL);
                
                // Add padding to the container
                budgetHistoryContainer.setPadding(
                    dpToPx(16), // left
                    dpToPx(24), // top
                    dpToPx(16), // right
                    dpToPx(24)  // bottom
                );

                budgetHistoryScrollView.removeAllViews();
                budgetHistoryScrollView.addView(budgetHistoryContainer);
            }

            // Initialize database helper
            dbHelper = new DatabaseHelper(requireContext());

            // Get user email from shared preferences
            SharedPreferences preferences = requireActivity().getSharedPreferences("loginPrefs", requireActivity().MODE_PRIVATE);
            userEmail = preferences.getString("userEmail", "");

            if (userEmail.isEmpty()) {
                Log.e(TAG, "User email not found in preferences");
                Toast.makeText(requireContext(), "User session error. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load user's current budget
            loadCurrentBudget();

            // Make EditText responsive to smaller devices
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                moneyEditText,
                1,  // min size in sp
                40, // max size in sp
                1,  // step size in sp
                TypedValue.COMPLEX_UNIT_SP
            );

            // Format input for money EditText to show commas and two decimal places
            moneyEditText.addTextChangedListener(new TextWatcher() {
                private String current = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals(current)) {
                        moneyEditText.removeTextChangedListener(this);

                        // Remove all non-digit characters
                        String cleanString = s.toString().replaceAll("[^\\d]", "");
                        if (cleanString.isEmpty()) cleanString = "0";

                        // Parse the clean string as a number and divide by 100 to handle decimals
                        double parsed = Double.parseDouble(cleanString) / 100;

                        // Create a number format with two decimal places
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                        formatter.setMinimumFractionDigits(2);
                        formatter.setMaximumFractionDigits(2);
                        String formatted = formatter.format(parsed);

                        current = formatted;
                        moneyEditText.setText(formatted);
                        moneyEditText.setSelection(formatted.length());

                        moneyEditText.addTextChangedListener(this);
                    }
                }
            });

            // Setup add button
            setupAddButton();

        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error initializing budget view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentBudget() {
        DatabaseHelper.UserPreferences preferences = dbHelper.getUserPreferences(userEmail);

        if (preferences != null && preferences.getBudget() != null && !preferences.getBudget().isEmpty()) {
            currentBudget = preferences.getBudget();

            // Format and display the current budget in tvPrice with two decimal places
            try {
                double budgetValue = Double.parseDouble(currentBudget);
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                String formattedBudget = formatter.format(budgetValue);
                tvPrice.setText(formattedBudget); // Display budget without peso symbol
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error formatting budget: " + e.getMessage());
                tvPrice.setText("0.00");
            }
        } else {
            tvPrice.setText("0.00");
        }

        // Load budget history from database
        loadBudgetHistory();
    }

    private void loadBudgetHistory() {
        // Clear existing views
        if (budgetHistoryContainer != null) {
            budgetHistoryContainer.removeAllViews();

            // Here you would load the history from your database
            // This is a placeholder for database implementation
            // For now, we'll load from SharedPreferences as a simple example

            SharedPreferences historyPrefs = requireActivity().getSharedPreferences(
                    "budgetHistory_" + userEmail, requireActivity().MODE_PRIVATE);

            // Get the number of history entries
            int historySize = historyPrefs.getInt("historySize", 0);

            // If no history but we have a current budget, add first budget entry without "Initial:" label
            if (historySize == 0 && !currentBudget.equals("0")) {
                try {
                    double initialBudget = Double.parseDouble(currentBudget);
                    if (initialBudget > 0) {
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                        formatter.setMinimumFractionDigits(2);
                        formatter.setMaximumFractionDigits(2);
                        String initialEntry = "₱" + formatter.format(initialBudget);
                        addHistoryEntryToUI(initialEntry);
                        saveHistoryEntry(initialEntry);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing initial budget: " + e.getMessage());
                }
            } else {
                // Load each entry in order (from first added to last)
                for (int i = 0; i < historySize; i++) {
                    String entry = historyPrefs.getString("entry_" + i, null);
                    if (entry != null) {
                        addHistoryEntryToUI(entry);
                    }
                }
            }
        }
    }

    private void setupAddButton() {
        addButton.setOnClickListener(v -> {
            // Get the text from the EditText and process it
            String newBudgetText = moneyEditText.getText().toString().replaceAll("[,]", "").trim();

            if (newBudgetText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a budget amount", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Parse as double to handle decimal places
                double newBudget = Double.parseDouble(newBudgetText);

                // Get user's current preferences
                DatabaseHelper.UserPreferences preferences = dbHelper.getUserPreferences(userEmail);

                if (preferences != null) {
                    // Calculate the new TOTAL budget by adding to current budget
                    double oldBudget = Double.parseDouble(currentBudget);
                    double totalBudget = oldBudget + newBudget;
                    String totalBudgetText = String.valueOf(totalBudget);

                    // Update budget in database
                    boolean success = dbHelper.saveUserPreferences(
                            userEmail,
                            totalBudgetText,
                            preferences.getIsland(),
                            preferences.getRegion(),
                            preferences.getRegionCode(),
                            preferences.getProvince(),
                            preferences.getMunicipality()
                    );

                    if (success) {
                        // Update UI - set the new total budget in tvPrice with two decimal places
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                        formatter.setMinimumFractionDigits(2);
                        formatter.setMaximumFractionDigits(2);
                        String formattedBudget = formatter.format(totalBudget);
                        tvPrice.setText(formattedBudget);

                        // Add the new budget amount as a history entry (always positive)
                        String historyEntry = "+₱" + formatter.format(newBudget);

                        // Update budget history UI
                        addHistoryEntryToUI(historyEntry);

                        // Save history entry to SharedPreferences
                        saveHistoryEntry(historyEntry);

                        // Update current budget for next comparison
                        currentBudget = totalBudgetText;

                        // Clear input field for next entry
                        moneyEditText.setText("");

                        Toast.makeText(requireContext(), "Budget updated successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to HomeFragment after successful budget update
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, new HomeFragment())
                                .commit();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update budget", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "User preferences not found", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid budget amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveHistoryEntry(String historyEntry) {
        SharedPreferences historyPrefs = requireActivity().getSharedPreferences(
                "budgetHistory_" + userEmail, requireActivity().MODE_PRIVATE);

        // Get current history size
        int historySize = historyPrefs.getInt("historySize", 0);

        // Save new entry
        SharedPreferences.Editor editor = historyPrefs.edit();
        editor.putString("entry_" + historySize, historyEntry);
        editor.putInt("historySize", historySize + 1);
        editor.apply();
    }

    private void addHistoryEntryToUI(String historyEntry) {
        if (budgetHistoryContainer != null) {
            // Inflate CardView layout
            CardView cardView = new CardView(requireContext());
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 4, 0, 4);
            cardView.setLayoutParams(cardParams);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCard1));
            cardView.setRadius(getResources().getDimensionPixelSize(R.dimen.cardview_radius));
            cardView.setCardElevation(getResources().getDimensionPixelSize(R.dimen.cardview_elevation));

            // Create horizontal LinearLayout
            LinearLayout layout = new LinearLayout(requireContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layout.setLayoutParams(layoutParams);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.setPadding(
                    dpToPx(16),
                    dpToPx(12),
                    dpToPx(16),
                    dpToPx(12)
            );

            // Add Peso Icon
            ImageView pesoIcon = new ImageView(requireContext());
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    dpToPx(24),
                    dpToPx(24)
            );
            iconParams.setMarginEnd(dpToPx(12));
            iconParams.gravity = Gravity.CENTER_VERTICAL;
            pesoIcon.setLayoutParams(iconParams);
            pesoIcon.setImageResource(R.drawable.peso);
            pesoIcon.setColorFilter(getResources().getColor(R.color.colorAccent));
            pesoIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Add Amount TextView
            TextView amountText = new TextView(requireContext());
            LinearLayout.LayoutParams amountParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            amountParams.gravity = Gravity.CENTER_VERTICAL;
            amountText.setLayoutParams(amountParams);
            amountText.setText(historyEntry);
            amountText.setTextColor(getResources().getColor(R.color.colorPrimaryText1));
            amountText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            // Responsive auto-size for amount text
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    amountText,
                    1,  // min size in sp
                    18,  // max size in sp
                    1,   // step size in sp
                    TypedValue.COMPLEX_UNIT_SP
            );
            amountText.setTypeface(null, Typeface.BOLD);
            amountText.setMaxLines(1);
            amountText.setEllipsize(android.text.TextUtils.TruncateAt.END);

            // Add Date TextView
            TextView dateText = new TextView(requireContext());
            LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dateParams.gravity = Gravity.CENTER_VERTICAL;
            dateText.setLayoutParams(dateParams);
            dateText.setText(getCurrentDate());
            dateText.setTextColor(getResources().getColor(R.color.colorPrimaryText1));
            dateText.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            // Responsive auto-size for date text with smaller range for small screens
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    dateText,
                    1,   // min size in sp (reduced from 10)
                    10,  // max size in sp (reduced from 12)
                    1,   // step size in sp
                    TypedValue.COMPLEX_UNIT_SP
            );
            dateText.setMaxLines(1);

            // Add views to layout
            layout.addView(pesoIcon);
            layout.addView(amountText);
            layout.addView(dateText);

            // Add layout to CardView
            cardView.addView(layout);

            // Add CardView to container at the top (most recent first)
            budgetHistoryContainer.addView(cardView, 0);
        }
    }

    // Helper method to get current date in required format
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the budget data when fragment becomes visible
        loadCurrentBudget();
    }
}