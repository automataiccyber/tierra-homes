package com.example.tierrahomes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FloorsFragment extends Fragment {

    private int floorCount;

    public FloorsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.floors_fragment, container, false);

        // Get user's location and determine category
        SharedPreferences loginPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String userEmail = loginPrefs.getString("userEmail", "");
        
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        DatabaseHelper.UserPreferences userPreferences = dbHelper.getUserPreferences(userEmail);

        if (userPreferences != null) {
            String region = userPreferences.getRegion();
            String municipality = userPreferences.getMunicipality();

            // Load classification data
            LocationClassifier.loadClassificationData(requireContext());
            
            // Get the category
            String category = LocationClassifier.getLocationCategory(region, municipality);
            
            // Update the category TextView
            TextView categoryText = view.findViewById(R.id.text);
            if (categoryText != null) {
                categoryText.setText(category.toUpperCase());
            }
        }

        Button floor1Button = view.findViewById(R.id.button1);
        Button floor2Button = view.findViewById(R.id.button2);
        Button floor3Button = view.findViewById(R.id.button3);
        Button doneButton = view.findViewById(R.id.button4);

        CardView floor1Card = view.findViewById(R.id.floor1Card);
        CardView floor2Card = view.findViewById(R.id.floor2Card);
        CardView floor3Card = view.findViewById(R.id.floor3Card);
        ViewGroup buttonContainer = view.findViewById(R.id.button_container);

        floorCount = getArguments() != null ? getArguments().getInt("floor_count", 1) : 1;

        if (floorCount < 3) {
            buttonContainer.removeView(floor1Card);
        }

        if (floorCount < 2) {
            buttonContainer.removeView(floor2Card);
        }

        floor1Button.setOnClickListener(v -> openCustomizeFragment(1));
        floor2Button.setOnClickListener(v -> openCustomizeFragment(2));
        floor3Button.setOnClickListener(v -> openCustomizeFragment(3));

        doneButton.setOnClickListener(v -> {
            SharedPreferences customizations = requireContext().getSharedPreferences("floor_customizations", Context.MODE_PRIVATE);
            StringBuilder incompleteFloors = new StringBuilder();
            // Determine which floor keys and labels to check
            int[] floorKeys;
            String[] floorLabels;
            if (floorCount == 1) {
                floorKeys = new int[]{3};
                floorLabels = new String[]{"FLOOR ONE"};
            } else if (floorCount == 2) {
                floorKeys = new int[]{3, 2};
                floorLabels = new String[]{"FLOOR ONE", "FLOOR TWO"};
            } else {
                floorKeys = new int[]{1, 2, 3};
                floorLabels = new String[]{"FLOOR THREE", "FLOOR TWO", "FLOOR ONE"};
            }
            for (int idx = 0; idx < floorKeys.length; idx++) {
                int i = floorKeys[idx];
                String floorLabel = floorLabels[idx];
                // Determine which features are required for this floor (balcony/staircase)
                boolean showBalcony = false;
                boolean showStaircase = false;
                if (floorCount == 1) {
                    showBalcony = false;
                    showStaircase = false;
                } else if (floorCount == 2) {
                    if (i == 3) {
                        showBalcony = false;
                        showStaircase = true;
                    } else {
                        showBalcony = true;
                        showStaircase = false;
                    }
                } else if (floorCount == 3) {
                    if (i == 3) {
                        showBalcony = false;
                        showStaircase = true;
                    } else if (i == 2) {
                        showBalcony = true;
                        showStaircase = true;
                    } else {
                        showBalcony = true;
                        showStaircase = false;
                    }
                }
                String floorKey = "floor_" + i;
                int flooring = customizations.getInt(floorKey + "_flooring", 0);
                int walls = customizations.getInt(floorKey + "_walls", 0);
                int window = customizations.getInt(floorKey + "_window", 0);
                int door = customizations.getInt(floorKey + "_door", 0);
                boolean valid = flooring != 0 && walls != 0 && window != 0 && door != 0;
                if (showBalcony) {
                    int balcony = customizations.getInt(floorKey + "_balcony", 0);
                    valid = valid && (balcony != 0);
                }
                if (showStaircase) {
                    int staircase = customizations.getInt(floorKey + "_staircase", 0);
                    valid = valid && (staircase != 0);
                }
                if (!valid) {
                    if (incompleteFloors.length() > 0) incompleteFloors.append(", ");
                    incompleteFloors.append(floorLabel);
                }
            }
            if (incompleteFloors.length() > 0) {
                android.widget.Toast.makeText(requireContext(), "Please complete customization for: " + incompleteFloors, android.widget.Toast.LENGTH_LONG).show();
                return;
            }
            // All floors completed, proceed
            TotalFragment totalFragment = new TotalFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
            transaction.replace(R.id.content_frame, totalFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void openCustomizeFragment(int floorNumber) {
        CustomizeFragment customizeFragment = new CustomizeFragment();
        Bundle args = new Bundle();
        args.putInt("floor_number", floorNumber);
        args.putInt("floor_count", floorCount);

        // Pass visibility flags based on floor configuration
        if (floorCount == 1) {
            // Floor one: no balcony, no staircase
            args.putBoolean("show_balcony", false);
            args.putBoolean("show_staircase", false);
        } else if (floorCount == 2) {
            // Floor 1: no balcony, has staircase
            // Floor 2: has balcony, no staircase
            args.putBoolean("show_balcony", floorNumber == 2);
            args.putBoolean("show_staircase", floorNumber == 1);
        } else if (floorCount == 3) {
            // Inverted mapping for 3 floors
            // floorNumber: 1 (FLOOR THREE), 2 (FLOOR TWO), 3 (FLOOR ONE)
            if (floorNumber == 1) {
                // FLOOR THREE: no balcony, has staircase
                args.putBoolean("show_balcony", false);
                args.putBoolean("show_staircase", true);
            } else if (floorNumber == 2) {
                // FLOOR TWO: has both
                args.putBoolean("show_balcony", true);
                args.putBoolean("show_staircase", true);
            } else {
                // FLOOR ONE: has balcony, no staircase
                args.putBoolean("show_balcony", true);
                args.putBoolean("show_staircase", false);
            }
        }

        customizeFragment.setArguments(args);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
        transaction.replace(R.id.content_frame, customizeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
