package com.example.tierrahomes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;
import java.text.NumberFormat;
import java.util.Locale;
import android.widget.ImageView;

public class AssetFragment extends Fragment {
    private NumberFormat formatter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize formatter only
        formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        View view = inflater.inflate(R.layout.asset_fragment, container, false);

        // Get current user's email
        SharedPreferences loginPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String userEmail = loginPrefs.getString("userEmail", "");

        // Get the container for house cards
        LinearLayout houseContainer = view.findViewById(R.id.houseContainer);
        houseContainer.removeAllViews(); // Clear existing views

        // Get user-specific asset preferences
        SharedPreferences assetPrefs = requireActivity().getSharedPreferences(
                "asset_preferences_" + userEmail, Context.MODE_PRIVATE);
        int houseCount = assetPrefs.getInt("house_count", 0);

        if (houseCount == 0) {
            // Show empty state message
            TextView emptyStateText = new TextView(requireContext());
            emptyStateText.setText("No property owned yet");
            emptyStateText.setTextSize(14);
            emptyStateText.setGravity(Gravity.CENTER);
            emptyStateText.setTextColor(getResources().getColor(R.color.colorSecondaryText));
            emptyStateText.setPadding(0, 50, 0, 0);
            houseContainer.addView(emptyStateText);
        } else {
            // Add house cards dynamically
            int builtHouseCount = 0; // Counter for built houses
            int totalBuiltHouses = 0; // First count total built houses

            // First pass: count built houses
            for (int i = 1; i <= houseCount; i++) {
                boolean isBuilt = assetPrefs.getBoolean("house_" + i + "_isBuilt", false);
                if (isBuilt) {
                    totalBuiltHouses++;
                }
            }

            // Second pass: display houses
            for (int i = 1; i <= houseCount; i++) {
                String price = assetPrefs.getString("house_" + i + "_price", "0");
                String location = assetPrefs.getString("house_" + i + "_location", "");
                String propertyType = assetPrefs.getString("house_" + i + "_type", "");
                int imageResourceId = assetPrefs.getInt("house_" + i + "_image", 0);
                boolean isBuilt = assetPrefs.getBoolean("house_" + i + "_isBuilt", false);

                View houseCard = inflater.inflate(R.layout.house_card, houseContainer, false);
                TextView titleText = houseCard.findViewById(R.id.propertyTitle);
                Button sellButton = houseCard.findViewById(R.id.sellButton);
                ImageView propertyImage = houseCard.findViewById(R.id.propertyImage);

                // Set smaller text size for title
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    titleText,
                    1,  // min size in sp
                    16, // max size in sp
                    1,  // step size in sp
                    TypedValue.COMPLEX_UNIT_SP
                );

                // Set smaller text size for sell button
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    sellButton,
                    1,  // min size in sp
                    16, // max size in sp
                    1,  // step size in sp
                    TypedValue.COMPLEX_UNIT_SP
                );

                // Format price
                double priceValue = Double.parseDouble(price);
                String formattedPrice = formatter.format(priceValue);

                if (isBuilt) {
                    builtHouseCount++;
                    titleText.setText(String.format("HOUSE %s\n%s",
                            toRomanNumerals(builtHouseCount), formattedPrice));
                    propertyImage.setImageResource(R.drawable.main_logo);
                } else {
                    titleText.setText(String.format("%s\n%s",
                            propertyType.replace("PROPERTY TYPE: ", ""), formattedPrice));
                    if (imageResourceId != 0) {
                        propertyImage.setImageResource(imageResourceId);
                    }
                }

                final int houseNumber = i;
                sellButton.setOnClickListener(v -> handleSellHouse(houseNumber));

                houseContainer.addView(houseCard);
            }
        }

        return view;
    }

    private void handleSellHouse(int houseNumber) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Sale")
                .setMessage("Are you sure you want to sell this house?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // === Begin original house selling logic ===

                    SharedPreferences loginPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                    String userEmail = loginPrefs.getString("userEmail", "");

                    SharedPreferences assetPrefs = requireActivity().getSharedPreferences(
                            "asset_preferences_" + userEmail, Context.MODE_PRIVATE);
                    String priceStr = assetPrefs.getString("house_" + houseNumber + "_price", "0");
                    String location = assetPrefs.getString("house_" + houseNumber + "_location", "");
                    String propertyType = assetPrefs.getString("house_" + houseNumber + "_type", "");
                    double price = Double.parseDouble(priceStr);

                    DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                    DatabaseHelper.UserPreferences preferences = dbHelper.getUserPreferences(userEmail);
                    if (preferences == null || preferences.getBudget() == null) {
                        Toast.makeText(requireContext(), "Error retrieving user budget", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double currentBudget = Double.parseDouble(preferences.getBudget());
                    double newBudget = currentBudget + price;

                    boolean updateSuccess = dbHelper.saveUserPreferences(
                            userEmail,
                            String.valueOf(newBudget),
                            preferences.getIsland(),
                            preferences.getRegion(),
                            preferences.getRegionCode(),
                            preferences.getProvince(),
                            preferences.getMunicipality()
                    );

                    if (!updateSuccess) {
                        Toast.makeText(requireContext(), "Failed to update budget", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences historyPrefs = requireActivity().getSharedPreferences(
                            "budgetHistory_" + userEmail, Context.MODE_PRIVATE);
                    int historySize = historyPrefs.getInt("historySize", 0);
                    String historyEntry = "+" + formatter.format(price);
                    SharedPreferences.Editor historyEditor = historyPrefs.edit();
                    historyEditor.putString("entry_" + historySize, historyEntry);
                    historyEditor.putInt("historySize", historySize + 1);
                    historyEditor.apply();

                    int totalHouses = assetPrefs.getInt("house_count", 0);
                    SharedPreferences.Editor assetEditor = assetPrefs.edit();

                    for (int i = houseNumber; i < totalHouses; i++) {
                        String nextPrice = assetPrefs.getString("house_" + (i + 1) + "_price", "0");
                        String nextLocation = assetPrefs.getString("house_" + (i + 1) + "_location", "");
                        String nextType = assetPrefs.getString("house_" + (i + 1) + "_type", "");
                        int nextImage = assetPrefs.getInt("house_" + (i + 1) + "_image", 0);
                        boolean nextIsBuilt = assetPrefs.getBoolean("house_" + (i + 1) + "_isBuilt", false);

                        assetEditor.putString("house_" + i + "_price", nextPrice);
                        assetEditor.putString("house_" + i + "_location", nextLocation);
                        assetEditor.putString("house_" + i + "_type", nextType);
                        assetEditor.putInt("house_" + i + "_image", nextImage);
                        assetEditor.putBoolean("house_" + i + "_isBuilt", nextIsBuilt);
                    }

                    assetEditor.remove("house_" + totalHouses + "_price");
                    assetEditor.remove("house_" + totalHouses + "_location");
                    assetEditor.remove("house_" + totalHouses + "_type");
                    assetEditor.remove("house_" + totalHouses + "_image");
                    assetEditor.remove("house_" + totalHouses + "_isBuilt");

                    assetEditor.putInt("house_count", totalHouses - 1);
                    assetEditor.apply();

                    Toast.makeText(requireContext(),
                            "House is sold successfully!",
                            Toast.LENGTH_SHORT).show();

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, new AssetFragment());
                    transaction.commit();

                    // === End of house selling logic ===

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private String toRomanNumerals(int number) {
        String[] romanNumerals = {"I", "II", "III", "IV", "V"};
        return (number > 0 && number <= romanNumerals.length) ? romanNumerals[number - 1] : String.valueOf(number);
    }
}