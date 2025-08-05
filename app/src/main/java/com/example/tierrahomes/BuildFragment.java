package com.example.tierrahomes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;

import com.google.android.material.slider.Slider;

import java.text.NumberFormat;
import java.util.Locale;

public class BuildFragment extends Fragment {
    // Updated with 2023-2024 Philippine real estate prices (PHP per sqm)
    public static final double URBAN_PRICE_PER_SQM = 196410.0;
    public static final double SUBURBAN_PRICE_PER_SQM = 75000.0;
    public static final double RURAL_PRICE_PER_SQM = 30000.0;
    private TextView titleText;
    private Slider floorSizeSlider;
    private TextView floorSizeLabel;
    private Button okayButton;
    private EditText lotSizeInput;
    private boolean isEditing = false;
    private SharedPreferences sharedPreferences;

    public BuildFragment() {}
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.build_fragment, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("build_preferences", Context.MODE_PRIVATE);

        // Initialize UI elements\
        TextView lotPriceIndicator = view.findViewById(R.id.lotPriceIndicator);
        titleText = view.findViewById(R.id.titleText);
        floorSizeSlider = view.findViewById(R.id.floorSizeSlider);
        floorSizeLabel = view.findViewById(R.id.floorSizeLabel);
        okayButton = view.findViewById(R.id.okayButton);
        lotSizeInput = view.findViewById(R.id.lotSizeInput);
        
        // Set responsive text sizing for EditText and other text elements
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            lotSizeInput,
            1,  // min size in sp
            10, // max size in sp (reduced from 14)
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        // Also set a smaller default text size directly
        lotSizeInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);
        
        // Set responsive text sizing for lot price indicator
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            lotPriceIndicator,
            1,  // min size in sp (reduced from 8)
            10, // max size in sp (reduced from 14)
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        // Set responsive text sizing for title
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            titleText,
            10, // min size in sp
            16, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        // Set responsive text sizing for floor size label
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            floorSizeLabel,
            1,  // min size in sp (reduced from 8)
            10, // max size in sp (reduced from 14)
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        // Load and display location category
        updateLocationCategory();
        

        // Set initial label
        updateFloorSizeLabel((int) floorSizeSlider.getValue());

        // Listen for slider value changes
        floorSizeSlider.addOnChangeListener((slider, value, fromUser) -> {
            updateFloorSizeLabel((int) value);
            // Save slider value
            saveBuildPreferences();
        });

        lotSizeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("BuildFragment", "onTextChanged: " + s);
                saveBuildPreferences();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String input = s.toString().replace(" m²", "").trim();

                if (input.isEmpty()) {
                    lotSizeInput.setText("");
                    lotPriceIndicator.setText("₱0.00");
                } else {
                    lotSizeInput.setText(input + " m²");
                    lotSizeInput.setSelection(input.length());

                    try {
                        double lotSize = Double.parseDouble(input);

                        // Get location category
                        String locationCategory = titleText.getText().toString().toLowerCase();
                        double pricePerSqm;
                        switch (locationCategory) {
                            case "urban":
                                pricePerSqm = URBAN_PRICE_PER_SQM;
                                break;
                            case "suburban":
                                pricePerSqm = SUBURBAN_PRICE_PER_SQM;
                                break;
                            case "rural":
                                pricePerSqm = RURAL_PRICE_PER_SQM;
                                break;
                            default:
                                pricePerSqm = SUBURBAN_PRICE_PER_SQM;
                        }

                        double lotPrice = lotSize * pricePerSqm;
                        String formatted = currencyFormatter.format(lotPrice);
                        lotPriceIndicator.setText(formatted);

                    } catch (NumberFormatException e) {
                        lotPriceIndicator.setText("₱0.00");
                    }
                }

                isEditing = false;
                saveBuildPreferences();
            }
        });




        // Handle OK button click
        okayButton.setOnClickListener(v -> {
            String rawLotSize = lotSizeInput.getText().toString().trim();
            String cleanedLotSize = rawLotSize.replace(" m²", "").trim();
            
            if (cleanedLotSize.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), 
                    "Please enter a lot size",
                    android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            // Clear previous floor customizations when starting fresh
            CustomizeFragment.clearAllSelections(requireContext());

            int selectedFloors = (int) floorSizeSlider.getValue();
            String formattedLotSize = cleanedLotSize + " m²";

            Bundle args = new Bundle();
            args.putInt("floor_count", selectedFloors);
            args.putString("lot_size", formattedLotSize);

            FloorsFragment floorsFragment = new FloorsFragment();
            floorsFragment.setArguments(args);

            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
            transaction.replace(R.id.content_frame, floorsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void saveBuildPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("floor_size", floorSizeSlider.getValue());
        
        String lotSizeText = lotSizeInput.getText().toString().replace(" m²", "").trim();
        editor.putString("lot_size", lotSizeText);
        
        editor.apply();
        Log.d("BuildFragment", "Saved lot size: " + lotSizeInput.getText().toString());
    }

    private void restoreSavedValues() {
        // Restore floor size slider
        float savedFloorSize = sharedPreferences.getFloat("floor_size", 1.0f);
        floorSizeSlider.setValue(savedFloorSize);
        
        // Restore lot size
        String savedLotSize = sharedPreferences.getString("lot_size", "");
        if (!savedLotSize.isEmpty()) {
            lotSizeInput.setText(savedLotSize + " m²");
        }
    }

    private void updateFloorSizeLabel(int floorSize) {
        floorSizeLabel.setText("FLOOR COUNT: " + floorSize + (floorSize == 1 ? " FLOOR" : " FLOORS"));
    }

    private void updateLocationCategory() {
        // Get user's location from SharedPreferences
        SharedPreferences locationPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String userEmail = locationPrefs.getString("userEmail", "");

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        DatabaseHelper.UserPreferences userPreferences = dbHelper.getUserPreferences(userEmail);

        if (userPreferences != null) {
            String region = userPreferences.getRegion();
            String municipality = userPreferences.getMunicipality();

            // Load classification data if not already loaded
            LocationClassifier.loadClassificationData(requireContext());
            
            // Get the category based on current region and municipality
            String category = LocationClassifier.getLocationCategory(region, municipality);
            
            // Update the title text
            titleText.setText(category.toUpperCase());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Do NOT clear SharedPreferences or EditText here!
    }

    // Regional price adjustments based on PSA construction data trends
    private double getRegionalAdjustment(String region) {
        switch(region.toLowerCase()) {
            case "ncr": return 1.25;  // Metro Manila premium
            case "calabarzon": return 1.15;
            case "central luzon": return 1.10;
            default: return 1.0;
        }
    }

    // Updated calculation method with regional adjustments
    private double calculateLotPrice(double areaSqm, String locationCategory, String region) {
        double basePrice;

        switch(locationCategory.toLowerCase()) {
            case "urban":
                basePrice = areaSqm * URBAN_PRICE_PER_SQM;
                break;
            case "suburban":
                basePrice = areaSqm * SUBURBAN_PRICE_PER_SQM;
                break;
            case "rural":
                basePrice = areaSqm * RURAL_PRICE_PER_SQM;
                break;
            default:
                basePrice = areaSqm * SUBURBAN_PRICE_PER_SQM;
        }

        return basePrice * getRegionalAdjustment(region);
    }
}
