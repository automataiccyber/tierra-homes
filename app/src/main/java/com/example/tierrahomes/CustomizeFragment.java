package com.example.tierrahomes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.TypedValue;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomizeFragment extends Fragment {

    private EditText windowsCount, doorCount;
    private LinearLayout balconyContainer;
    private CardView cardViewFloor, cardViewWalls, cardViewWindow, cardViewDoor, cardViewBalcony, cardViewStaircase;
    private LinearLayout staircaseContainer;
    private LinearLayout floorDetailsContainer;
    private Spinner spinnerFloor, spinnerWalls, spinnerWindow, spinnerDoor, spinnerBalcony, spinnerStaircase;

    private ImageView imageViewFloor, imageViewWalls, imageViewWindow, imageViewDoor, imageViewBalcony, imageViewStaircase;

    private TextView floorTitleTextView;
    private TextView text_price_floor, text_price_wall, text_price_window, text_price_door, text_price_balcony, text_price_staircase;

    private LinearLayout floorLeftColumn, floorRightColumn;
    private LinearLayout wallLeftColumn, wallRightColumn, windowLeftColumn, windowRightColumn, doorLeftColumn, doorRightColumn, balconyLeftColumn, balconyRightColumn, staircaseLeftColumn, staircaseRightColumn;
    private LinearLayout windowQuantityColumn, doorQuantityColumn;

    private int currentFloorNumber;
    private SharedPreferences sharedPreferences;
    private boolean isInitializing = true;
    private android.os.Handler saveHandler = new android.os.Handler();
    private Runnable saveRunnable;

    public CustomizeFragment() {}

    @Nullable
    private boolean areSelectionsValid() {
        // Check required spinners
        if (spinnerFloor.getSelectedItemPosition() == 0 ||
            spinnerWalls.getSelectedItemPosition() == 0 ||
            spinnerWindow.getSelectedItemPosition() == 0 ||
            spinnerDoor.getSelectedItemPosition() == 0) {
            return false;
        }

        // Check balcony spinner if visible
        if (balconyContainer.getVisibility() == View.VISIBLE &&
            spinnerBalcony.getSelectedItemPosition() == 0) {
            return false;
        }

        // Check staircase spinner if visible
        if (staircaseContainer.getVisibility() == View.VISIBLE &&
            spinnerStaircase.getSelectedItemPosition() == 0) {
            return false;
        }

        // Check window quantity - must have a value entered (0 is valid, but empty is not)
        String windowQuantityStr = windowsCount.getText().toString().trim();
        if (TextUtils.isEmpty(windowQuantityStr)) {
            return false;
        }

        // Check door quantity - must have a value entered (0 is valid, but empty is not)
        String doorQuantityStr = doorCount.getText().toString().trim();
        if (TextUtils.isEmpty(doorQuantityStr)) {
            return false;
        }

        return true;
    }

    private String getValidationErrorMessage() {
        // Check spinners first
        if (spinnerFloor.getSelectedItemPosition() == 0) {
            return "Please select a flooring type";
        }
        if (spinnerWalls.getSelectedItemPosition() == 0) {
            return "Please select a wall type";
        }
        if (spinnerWindow.getSelectedItemPosition() == 0) {
            return "Please select a window type";
        }
        if (spinnerDoor.getSelectedItemPosition() == 0) {
            return "Please select a door type";
        }

        // Check balcony spinner if visible
        if (balconyContainer.getVisibility() == View.VISIBLE &&
            spinnerBalcony.getSelectedItemPosition() == 0) {
            return "Please select a balcony type";
        }

        // Check staircase spinner if visible
        if (staircaseContainer.getVisibility() == View.VISIBLE &&
            spinnerStaircase.getSelectedItemPosition() == 0) {
            return "Please select a staircase type";
        }

        // Check quantity fields
        String windowQuantityStr = windowsCount.getText().toString().trim();
        if (TextUtils.isEmpty(windowQuantityStr)) {
            return "Please enter the number of windows";
        }

        String doorQuantityStr = doorCount.getText().toString().trim();
        if (TextUtils.isEmpty(doorQuantityStr)) {
            return "Please enter the number of doors";
        }

        return "Please complete all required fields";
    }

    private void highlightEmptyFields() {
        // Reset all field backgrounds first
        windowsCount.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_input_background));
        doorCount.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_input_background));
        
        // Highlight empty fields
        String windowQtyStr = windowsCount.getText().toString().trim();
        String doorQtyStr = doorCount.getText().toString().trim();
        
        if (TextUtils.isEmpty(windowQtyStr)) {
            // Create a red-tinted background for empty fields
            windowsCount.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorCard1));
        }
        
        if (TextUtils.isEmpty(doorQtyStr)) {
            // Create a red-tinted background for empty fields
            doorCount.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorCard1));
        }
    }

    private String currentCategory = "RURAL"; // Default category


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.customize_fragment, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("floor_customizations", Context.MODE_PRIVATE);

        windowsCount = view.findViewById(R.id.edittext_window_quantity);
        doorCount = view.findViewById(R.id.edittext_door_quantity);
        
        // Set responsive text sizing for EditText fields
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            windowsCount,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        // Also set a smaller default text size directly (matching build fragment)
        windowsCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            doorCount,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        // Also set a smaller default text size directly (matching build fragment)
        doorCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);

    // Add TextWatcher to window count
    windowsCount.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // Clear highlighting when user starts typing
            windowsCount.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_input_background));
            windowsCount.setBackgroundTintList(null);
            
            if (spinnerWindow.getSelectedItemPosition() > 0) {
                updateWindowPrice(spinnerWindow.getSelectedItemPosition());
            }
            if (!isInitializing) {
                // Debounce saving to prevent excessive I/O
                if (saveRunnable != null) {
                    saveHandler.removeCallbacks(saveRunnable);
                }
                saveRunnable = () -> saveCurrentSelections();
                saveHandler.postDelayed(saveRunnable, 500); // Save after 500ms of no changes
            }
        }
    });

    // Add TextWatcher to door count
    doorCount.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // Clear highlighting when user starts typing
            doorCount.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_input_background));
            doorCount.setBackgroundTintList(null);
            
            if (spinnerDoor.getSelectedItemPosition() > 0) {
                updateDoorPrice(spinnerDoor.getSelectedItemPosition());
            }
            if (!isInitializing) {
                // Debounce saving to prevent excessive I/O
                if (saveRunnable != null) {
                    saveHandler.removeCallbacks(saveRunnable);
                }
                saveRunnable = () -> saveCurrentSelections();
                saveHandler.postDelayed(saveRunnable, 500); // Save after 500ms of no changes
            }
        }
    });
        text_price_floor = view.findViewById(R.id.text_price_floor);
        text_price_wall = view.findViewById(R.id.text_price_wall);
        text_price_window = view.findViewById(R.id.text_price_window);
        text_price_door = view.findViewById(R.id.text_price_door);
        text_price_balcony = view.findViewById(R.id.text_price_balcony);
        text_price_staircase = view.findViewById(R.id.text_price_staircase);
        
        // Set responsive text sizing for price text views
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            text_price_floor,
            1,  // min size in sp
            10, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            text_price_wall,
            1,  // min size in sp
            10, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            text_price_window,
            1,  // min size in sp
            10, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            text_price_door,
            1,  // min size in sp
            10, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            text_price_balcony,
            1,  // min size in sp
            10, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            text_price_staircase,
            1,  // min size in sp
            10, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        // Get container references
        floorDetailsContainer = view.findViewById(R.id.floor_details_container);
        balconyContainer = view.findViewById(R.id.balcony_container);
        staircaseContainer = view.findViewById(R.id.staircase_container);
        floorTitleTextView = view.findViewById(R.id.floor_title);
        floorLeftColumn = view.findViewById(R.id.floor_left_column);
        floorRightColumn = view.findViewById(R.id.floor_right_column);
        
        // Set responsive text sizing for floor title
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            floorTitleTextView,
            1,  // min size in sp
            14, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        wallLeftColumn = view.findViewById(R.id.wall_left_column);
        wallRightColumn = view.findViewById(R.id.wall_right_column);
        windowLeftColumn = view.findViewById(R.id.window_left_column);
        windowRightColumn = view.findViewById(R.id.window_right_column);
        doorLeftColumn = view.findViewById(R.id.door_left_column);
        doorRightColumn = view.findViewById(R.id.door_right_column);
        balconyLeftColumn = view.findViewById(R.id.balcony_left_column);
        balconyRightColumn = view.findViewById(R.id.balcony_right_column);
        staircaseLeftColumn = view.findViewById(R.id.staircase_left_column);
        staircaseRightColumn = view.findViewById(R.id.staircase_right_column);
        windowQuantityColumn = view.findViewById(R.id.window_quantity_column);
        doorQuantityColumn = view.findViewById(R.id.door_quantity_column);

        // Initialize spinners
        cardViewFloor = view.findViewById(R.id.cardview_floor);
        cardViewWalls = view.findViewById(R.id.cardview_wall);
        cardViewWindow = view.findViewById(R.id.cardview_window);
        cardViewDoor = view.findViewById(R.id.cardview_door);
        cardViewBalcony = view.findViewById(R.id.cardview_balcony);
        cardViewStaircase = view.findViewById(R.id.cardview_staircase);
        


        imageViewFloor = view.findViewById(R.id.image_floor);
        imageViewWalls = view.findViewById(R.id.image_wall);
        imageViewWindow = view.findViewById(R.id.image_window);
        imageViewDoor = view.findViewById(R.id.image_door);
        imageViewBalcony = view.findViewById(R.id.image_balcony);
        imageViewStaircase = view.findViewById(R.id.image_staircase);

        spinnerFloor = view.findViewById(R.id.spinner_floor);
        spinnerWalls = view.findViewById(R.id.spinner_wall);
        spinnerWindow = view.findViewById(R.id.spinner_window);
        spinnerDoor = view.findViewById(R.id.spinner_door);
        spinnerBalcony = view.findViewById(R.id.spinner_balcony);
        spinnerStaircase = view.findViewById(R.id.spinner_staircase);

        // Setup spinners with options
        setupFlooringSpinner();
        setupWallsSpinner();
        setupWindowSpinner();
        setupDoorSpinner();
        setupBalconySpinner();
        setupStaircaseSpinner();
        

        // Get arguments from bundle
        Bundle args = getArguments();
        if (args != null) {
            int floorCount = args.getInt("floor_count", 1);
            currentFloorNumber = args.getInt("floor_number", 1);

            // Update floor title
            updateFloorTitle(currentFloorNumber);

            // Determine visibility based on floor configuration
            boolean showBalcony = false;
            boolean showStaircase = false;

            if (floorCount == 1) {
                // Floor one: no balcony, no staircase
                showBalcony = false;
                showStaircase = false;
            } else if (floorCount == 2) {
                if (currentFloorNumber == 3) {
                    // Floor one: no balcony, has staircase
                    showBalcony = false;
                    showStaircase = true;
                } else {
                    // Floor two: has balcony, no staircase
                    showBalcony = true;
                    showStaircase = false;
                }
            } else if (floorCount == 3) {
                if (currentFloorNumber == 3) {
                    // Floor one: no balcony, has staircase
                    showBalcony = false;
                    showStaircase = true;
                } else if (currentFloorNumber == 2) {
                    // Floor two: has both
                    showBalcony = true;
                    showStaircase = true;
                } else {
                    // Floor three: has balcony, no staircase
                    showBalcony = true;
                    showStaircase = false;
                }
            }

            // Update container visibility and weights
            updateContainerVisibility(showBalcony, showStaircase);

            // Restore saved selections for this floor
            restoreSavedSelections(currentFloorNumber);
            
            // Mark initialization as complete
            isInitializing = false;
            Log.d("CustomizeFragment", "Initialization complete for floor " + currentFloorNumber);
        }

        // Get the OKAY button reference and set up click listener
        Button btnOkay = view.findViewById(R.id.btn_okay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areSelectionsValid()) {
                    // Check specific validation failures and show appropriate messages
                    String errorMessage = getValidationErrorMessage();
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    // Highlight empty fields for visual feedback
                    highlightEmptyFields();
                    return;
                }

                saveCurrentSelections();

                // Get the floor count from arguments
                int floorCount = getArguments() != null ? getArguments().getInt("floor_count", 1) : 1;

                // Create new FloorsFragment instance and pass the floor count
                FloorsFragment floorsFragment = new FloorsFragment();
                Bundle bundleArgs = new Bundle();
                bundleArgs.putInt("floor_count", floorCount);
                floorsFragment.setArguments(bundleArgs);

                // Navigate back to FloorsFragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
                transaction.replace(R.id.content_frame, floorsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        updateLocationCategory();

        return view;


    }


    private void updateLocationCategory() {
        SharedPreferences locationPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String userEmail = locationPrefs.getString("userEmail", "");
    
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        DatabaseHelper.UserPreferences userPreferences = dbHelper.getUserPreferences(userEmail);
    
        if (userPreferences != null) {
            String region = userPreferences.getRegion();
            String municipality = userPreferences.getMunicipality();
            LocationClassifier.loadClassificationData(requireContext());
            currentCategory = LocationClassifier.getLocationCategory(region, municipality).toUpperCase();
        }
    }

    // ... existing code ...

    private void updateFloorPrice(int position) {
        int locationIndex = MaterialPriceCalculator.getLocationIndex(currentCategory);
        double price = 0.0;
        String lotSizeStr = requireActivity().getSharedPreferences("build_preferences", Context.MODE_PRIVATE)
                .getString("lot_size", "");
        double lotSize = 0.0;
        try {
            if (!TextUtils.isEmpty(lotSizeStr)) {
                lotSize = Double.parseDouble(lotSizeStr);
            }
        } catch (NumberFormatException e) {
            Log.e("CustomizeFragment", "Error parsing lot size: " + e.getMessage());
        }

        switch (position) {
            case 1: // Concrete Slab
                price = MaterialPriceCalculator.FlooringPrices.CONCRETE_SLAB[0][locationIndex] * lotSize;
                break;
            case 2: // Ceramic Tile
                price = MaterialPriceCalculator.FlooringPrices.CERAMIC_TILE[0][locationIndex] * lotSize;
                break;
            case 3: // Vinyl Plank
                price = MaterialPriceCalculator.FlooringPrices.VINYL_PLANK[0][locationIndex] * lotSize;
                break;
            case 4: // Natural Stone
                price = MaterialPriceCalculator.FlooringPrices.NATURAL_STONE[0][locationIndex] * lotSize;
                break;
            case 5: // Polished Concrete
                price = MaterialPriceCalculator.FlooringPrices.POLISHED_CONCRETE[0][locationIndex] * lotSize;
                break;
        }
        text_price_floor.setText(position == 0 ? "" : String.format(currencyFormat.format(price)));
    }

    private void updateWallPrice(int position) {
        int locationIndex = MaterialPriceCalculator.getLocationIndex(currentCategory);
        double price = 0.0;
        String lotSizeStr = requireActivity().getSharedPreferences("build_preferences", Context.MODE_PRIVATE)
                .getString("lot_size", "");
        double lotSize = 0.0;
        try {
            if (!TextUtils.isEmpty(lotSizeStr)) {
                lotSize = Double.parseDouble(lotSizeStr);
            }
        } catch (NumberFormatException e) {
            Log.e("CustomizeFragment", "Error parsing lot size: " + e.getMessage());
        }

        // Wall area is typically lotSize × 2.5 (standard wall height)
        double wallArea = lotSize * 2.5;

        switch (position) {
            case 1: // Painted Cement
                price = MaterialPriceCalculator.WallPrices.PAINTED_CEMENT[0][locationIndex] * wallArea;
                break;
            case 2: // Brick Cladding
                price = MaterialPriceCalculator.WallPrices.BRICK_CLADDING[0][locationIndex] * wallArea;
                break;
            case 3: // Stucco
                price = MaterialPriceCalculator.WallPrices.STUCCO[0][locationIndex] * wallArea;
                break;
            case 4: // Stone Finish
                price = MaterialPriceCalculator.WallPrices.STONE_FINISH[0][locationIndex] * wallArea;
                break;
            case 5: // Wood Siding
                price = MaterialPriceCalculator.WallPrices.WOOD_SIDING[0][locationIndex] * wallArea;
                break;
        }
        text_price_wall.setText(position == 0 ? "" : String.format(currencyFormat.format(price)));
    }

    private void updateWindowPrice(int position) {
        int locationIndex = MaterialPriceCalculator.getLocationIndex(currentCategory);
        double price = 0.0;

        // Get window count from EditText
        int windowCount = 0; // Default minimum value
        try {
            String countStr = windowsCount.getText().toString();
            if (!TextUtils.isEmpty(countStr)) {
                windowCount = Math.max(0, Integer.parseInt(countStr)); // Ensure minimum of 2 windows
            }
        } catch (NumberFormatException e) {
            Log.e("CustomizeFragment", "Error parsing window count: " + e.getMessage());
        }

        switch (position) {
            case 1: // Sliding Aluminum
                price = MaterialPriceCalculator.WindowPrices.SLIDING_ALUMINUM[0][locationIndex] * windowCount;
                break;
            case 2: // Fixed Picture
                price = MaterialPriceCalculator.WindowPrices.FIXED_PICTURE[0][locationIndex] * windowCount;
                break;
            case 3: // Casement UPVC
                price = MaterialPriceCalculator.WindowPrices.CASEMENT_UPVC[0][locationIndex] * windowCount;
                break;
            case 4: // Wooden Frame
                price = MaterialPriceCalculator.WindowPrices.WOODEN_FRAME[0][locationIndex] * windowCount;
                break;
            case 5: // Glass Security
                price = MaterialPriceCalculator.WindowPrices.GLASS_SECURITY[0][locationIndex] * windowCount;
                break;
        }
        text_price_window.setText(position == 0 ? "" : String.format(currencyFormat.format(price)));
    }

    private void updateDoorPrice(int position) {
        int locationIndex = MaterialPriceCalculator.getLocationIndex(currentCategory);
        double price = 0.0;

        // Get door count from EditText
        int doorCountValue = 0; // Default minimum value
        try {
            String countStr = doorCount.getText().toString();
            if (!TextUtils.isEmpty(countStr)) {
                doorCountValue = Math.max(0, Integer.parseInt(countStr)); // Ensure minimum of 2 doors
            }
        } catch (NumberFormatException e) {
            Log.e("CustomizeFragment", "Error parsing door count: " + e.getMessage());
        }

        switch (position) {
            case 1: // Solid Wooden
                price = MaterialPriceCalculator.DoorPrices.SOLID_WOODEN[0][locationIndex] * doorCountValue;
                break;
            case 2: // Steel Security
                price = MaterialPriceCalculator.DoorPrices.STEEL_SECURITY[0][locationIndex] * doorCountValue;
                break;
            case 3: // Glass Metal
                price = MaterialPriceCalculator.DoorPrices.GLASS_METAL[0][locationIndex] * doorCountValue;
                break;
            case 4: // French Doors
                price = MaterialPriceCalculator.DoorPrices.FRENCH_DOORS[0][locationIndex] * doorCountValue;
                break;
        }
        text_price_door.setText(position == 0 ? "" : String.format(currencyFormat.format(price)));
    }

    private void updateBalconyPrice(int position) {
        int locationIndex = MaterialPriceCalculator.getLocationIndex(currentCategory);
        double price = 0.0;

        switch (position) {
            case 2: // Small Front
                price = MaterialPriceCalculator.BalconyPrices.SMALL_FRONT[0][locationIndex];
                break;
            case 3: // Wrap Around
                price = MaterialPriceCalculator.BalconyPrices.WRAP_AROUND[0][locationIndex];
                break;
            case 4: // Side Railing
                price = MaterialPriceCalculator.BalconyPrices.SIDE_RAILING[0][locationIndex];
                break;
        }
        text_price_balcony.setText(position == 0 ? "" : String.format(currencyFormat.format(price)));
    }

    private void updateStaircasePrice(int position) {
        int locationIndex = MaterialPriceCalculator.getLocationIndex(currentCategory);
        double price = 0.0;

        switch (position) {
            case 1: // Concrete Tile
                price = MaterialPriceCalculator.StaircasePrices.CONCRETE_TILE[0][locationIndex];
                break;
            case 2: // Wooden
                price = MaterialPriceCalculator.StaircasePrices.WOODEN[0][locationIndex];
                break;
            case 3: // Spiral
                price = MaterialPriceCalculator.StaircasePrices.SPIRAL[0][locationIndex];
                break;
            case 4: // Steel Wood
                price = MaterialPriceCalculator.StaircasePrices.STEEL_WOOD[0][locationIndex];
                break;
        }
        text_price_staircase.setText(position == 0 ? "" : String.format(currencyFormat.format(price)));
    }
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

// ... existing code ...

    private void saveCurrentSelections() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String floorKey = "floor_" + currentFloorNumber;

        // Save spinner selections
        int flooring = spinnerFloor.getSelectedItemPosition();
        int walls = spinnerWalls.getSelectedItemPosition();
        int window = spinnerWindow.getSelectedItemPosition();
        int door = spinnerDoor.getSelectedItemPosition();
        
        editor.putInt(floorKey + "_flooring", flooring);
        editor.putInt(floorKey + "_walls", walls);
        editor.putInt(floorKey + "_window", window);
        editor.putInt(floorKey + "_door", door);
        
        // Save window and door quantity - only if they have values
        String windowQtyStr = windowsCount.getText().toString().trim();
        String doorQtyStr = doorCount.getText().toString().trim();
        
        if (!TextUtils.isEmpty(windowQtyStr)) {
            try {
                int windowQty = Integer.parseInt(windowQtyStr);
                editor.putInt(floorKey + "_window_quantity", windowQty);
            } catch (NumberFormatException ignored) {
                // Don't save if invalid number
            }
        }
        
        if (!TextUtils.isEmpty(doorQtyStr)) {
            try {
                int doorQty = Integer.parseInt(doorQtyStr);
                editor.putInt(floorKey + "_door_quantity", doorQty);
            } catch (NumberFormatException ignored) {
                // Don't save if invalid number
            }
        }

        // Only save balcony and staircase if they are visible
        if (balconyContainer.getVisibility() == View.VISIBLE) {
            editor.putInt(floorKey + "_balcony", spinnerBalcony.getSelectedItemPosition());
        }
        if (staircaseContainer.getVisibility() == View.VISIBLE) {
            editor.putInt(floorKey + "_staircase", spinnerStaircase.getSelectedItemPosition());
        }

        editor.apply();
        
        // Debug logging
        Log.d("CustomizeFragment", "Saved floor " + currentFloorNumber + " - flooring:" + flooring + 
              " walls:" + walls + " window:" + window + " door:" + door + 
              " windowQty:" + windowQtyStr + " doorQty:" + doorQtyStr);
    }

    private void restoreSavedSelections(int floorNumber) {
        String floorKey = "floor_" + floorNumber;

        // Restore spinner selections (default to 0 if not found)
        int flooringSelection = sharedPreferences.getInt(floorKey + "_flooring", 0);
        int wallsSelection = sharedPreferences.getInt(floorKey + "_walls", 0);
        int windowSelection = sharedPreferences.getInt(floorKey + "_window", 0);
        int doorSelection = sharedPreferences.getInt(floorKey + "_door", 0);
        int balconySelection = sharedPreferences.getInt(floorKey + "_balcony", 0);
        int staircaseSelection = sharedPreferences.getInt(floorKey + "_staircase", 0);
        
        // Restore quantities - only if they were actually saved (not default 0)
        int windowQty = sharedPreferences.getInt(floorKey + "_window_quantity", -1);
        int doorQty = sharedPreferences.getInt(floorKey + "_door_quantity", -1);

        // Set selections
        spinnerFloor.setSelection(flooringSelection);
        spinnerWalls.setSelection(wallsSelection);
        spinnerWindow.setSelection(windowSelection);
        spinnerDoor.setSelection(doorSelection);

        // Restore quantities only if they were previously saved
        if (windowQty >= 0) {
            windowsCount.setText(String.valueOf(windowQty));
        } else {
            windowsCount.setText(""); // Empty if never saved
        }
        if (doorQty >= 0) {
            doorCount.setText(String.valueOf(doorQty));
        } else {
            doorCount.setText(""); // Empty if never saved
        }

        // Only restore balcony and staircase if they are visible
        if (balconyContainer.getVisibility() == View.VISIBLE) {
            spinnerBalcony.setSelection(balconySelection);
        }
        if (staircaseContainer.getVisibility() == View.VISIBLE) {
            spinnerStaircase.setSelection(staircaseSelection);
        }

        // Show elements based on restored selections
        showFlooringElements(flooringSelection > 0);
        showWallElements(wallsSelection > 0);
        showWindowElements(windowSelection > 0);
        showDoorElements(doorSelection > 0);
        
        if (balconyContainer.getVisibility() == View.VISIBLE) {
            showBalconyElements(balconySelection > 0);
        }
        if (staircaseContainer.getVisibility() == View.VISIBLE) {
            showStaircaseElements(staircaseSelection > 0);
        }
        
        // Debug logging
        Log.d("CustomizeFragment", "Restored floor " + floorNumber + " - flooring:" + flooringSelection + 
              " walls:" + wallsSelection + " window:" + windowSelection + " door:" + doorSelection + 
              " windowQty:" + (windowQty >= 0 ? String.valueOf(windowQty) : "empty") + 
              " doorQty:" + (doorQty >= 0 ? String.valueOf(doorQty) : "empty"));
    }

    // Method to clear all saved selections (can be called when starting fresh)
    public static void clearAllSelections(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("floor_customizations", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void hideAndAdjustLayout(LinearLayout containerToHide) {
        if (containerToHide != null && floorDetailsContainer != null) {
            containerToHide.setVisibility(View.GONE);

            // Count visible containers
            int visibleContainers = 0;
            for (int i = 0; i < floorDetailsContainer.getChildCount(); i++) {
                View child = floorDetailsContainer.getChildAt(i);
                if (child.getVisibility() == View.VISIBLE) {
                    visibleContainers++;
                }
            }

            // Calculate new weight for visible containers
            float newWeight = 1.0f;
            if (visibleContainers > 0) {
                newWeight = 1.0f / visibleContainers;
            }

            // Apply new weights
            for (int i = 0; i < floorDetailsContainer.getChildCount(); i++) {
                View child = floorDetailsContainer.getChildAt(i);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

                if (child.getVisibility() == View.VISIBLE) {
                    params.weight = newWeight;
                } else {
                    params.weight = 0;
                }
                params.height = 0; // Use 0 for weight-based distribution
                child.setLayoutParams(params);
            }

            floorDetailsContainer.requestLayout();
        }
    }

    private void setupFlooringSpinner() {
        String[] flooringOptions = {
            "Select Flooring Type",
            "Concrete Slab",
            "Ceramic Tile",
            "Vinyl Plank",
            "Natural Stone",
            "Polished Concrete"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            flooringOptions
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                Context context = parent.getContext();

                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorCard1));
                } else {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText1));
                }

                // Set minimum text size for dropdown items
                tv.setTextSize(6f);
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);

                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorSecondaryText1): (R.color.colorPrimaryText1)));
                
                // Set minimum text size for selected text
                tv.setTextSize(6f);
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFloor.setAdapter(adapter);
        spinnerFloor.setSelection(0);
        


        spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int imageResId;
                switch (position) {
                    case 0:
                        imageResId = R.drawable.select;
                        break;
                    case 1:
                        imageResId = R.drawable.concreteslab;
                        break;
                    case 2:
                        imageResId = R.drawable.ceramictile;
                        break;
                    case 3:
                        imageResId = R.drawable.vinylplank;
                        break;
                    case 4:
                        imageResId = R.drawable.naturalstone;
                        break;
                    case 5:
                        imageResId = R.drawable.polishedconcrete;
                        break;
                    default:
                        imageResId = 0;
                }
                imageViewFloor.setImageResource(imageResId);
                updateFloorPrice(position);
                // Show/hide elements based on selection
                showFlooringElements(position > 0);
                if (!isInitializing) {
                    saveCurrentSelections();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupWallsSpinner() {
        String[] wallOptions = {
            "Select Wall Type",
            "Painted Cement",
            "Brick Cladding",
            "Stucco",
            "Stone Finish",
            "Wood Siding"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            wallOptions
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                Context context = parent.getContext(); // or requireContext() if accessible

                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorCard1));
                } else {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText1));
                }

                // Set minimum text size for dropdown items
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);

                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change this color as desired
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorSecondaryText1): (R.color.colorPrimaryText1)));
                
                // Set minimum text size for selected text
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWalls.setAdapter(adapter);
        spinnerWalls.setSelection(0);

        spinnerWalls.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int imageResId;
                switch (position) {
                    case 0:
                        imageResId = R.drawable.select;
                        break;
                    case 1:
                        imageResId = R.drawable.paintedcement;
                        break;
                    case 2:
                        imageResId = R.drawable.brickclading;
                        break;
                    case 3:
                        imageResId = R.drawable.stucco;
                        break;
                    case 4:
                        imageResId = R.drawable.stonefinish;
                        break;
                    case 5:
                        imageResId = R.drawable.woodsliding;
                        break;
                    default:
                        imageResId = 0;
                }
                imageViewWalls.setImageResource(imageResId);
                updateWallPrice(position);
                // Show/hide elements based on selection
                showWallElements(position > 0);
                if (!isInitializing) {
                    saveCurrentSelections();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupWindowSpinner() {
        String[] windowOptions = {
            "Select Window Type",
            "Sliding Aluminum Windows",
            "Fixed Picture Windows",
            "Casement uPVC Windows",
            "Wooden Frame Windows",
            "Glass with Security Grills"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            windowOptions
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                Context context = parent.getContext(); // or requireContext() if accessible

                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorCard1));
                } else {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText1));
                }

                // Set minimum text size for dropdown items
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);

                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change this color as desired
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorSecondaryText1): (R.color.colorPrimaryText1)));
                
                // Set minimum text size for selected text
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWindow.setAdapter(adapter);
        spinnerWindow.setSelection(0);

        spinnerWindow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int imageResId;
                switch (position) {
                    case 0:
                        imageResId = R.drawable.select;
                        break;
                    case 1:
                        imageResId = R.drawable.slidingaluminum;
                        break;
                    case 2:
                        imageResId = R.drawable.fixedposition;
                        break;
                    case 3:
                        imageResId = R.drawable.casementupvc;
                        break;
                    case 4:
                        imageResId = R.drawable.woodenframe;
                        break;
                    case 5:
                        imageResId = R.drawable.glasssecuritygrills;
                        break;
                    default:
                        imageResId = 0;
                }
                imageViewWindow.setImageResource(imageResId);
                updateWindowPrice(position);
                // Show/hide elements based on selection
                showWindowElements(position > 0);
                if (!isInitializing) {
                    saveCurrentSelections();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDoorSpinner() {
        String[] doorOptions = {
            "Select Door Type",
            "Solid Wooden Door",
            "Steel Security Door",
            "Glass with Metal Frame",
            "French Doors"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            doorOptions
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                Context context = parent.getContext(); // or requireContext() if accessible

                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorCard1));
                } else {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText1));
                }

                // Set minimum text size for dropdown items
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);

                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change this color as desired
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorSecondaryText1): (R.color.colorPrimaryText1)));
                
                // Set minimum text size for selected text
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoor.setAdapter(adapter);
        spinnerDoor.setSelection(0);
        spinnerDoor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int imageResId;
                switch (position) {
                    case 0:
                        imageResId = R.drawable.select;
                        break;
                    case 1:
                        imageResId = R.drawable.woodendoor;
                        break;
                    case 2:
                        imageResId = R.drawable.steelsecurity;
                        break;
                    case 3:
                        imageResId = R.drawable.glasswithmetalframe;
                        break;
                    case 4:
                        imageResId = R.drawable.frenchdoor;
                        break;
                    default:
                        imageResId = 0;
                }
                imageViewDoor.setImageResource(imageResId);
                updateDoorPrice(position);
                // Show/hide elements based on selection
                showDoorElements(position > 0);
                if (!isInitializing) {
                    saveCurrentSelections();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupBalconySpinner() {
        String[] balconyOptions = {
            "Select Balcony Type",
            "No Balcony",
            "Small Front Balcony",
            "Wrap-Around Balcony",
            "Side Balcony with Railing"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            balconyOptions
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                Context context = parent.getContext(); // or requireContext() if accessible

                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorCard1));
                } else {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText1));
                }

                // Set minimum text size for dropdown items
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);

                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change this color as desired
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorSecondaryText1): (R.color.colorPrimaryText1)));
                
                // Set minimum text size for selected text
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBalcony.setAdapter(adapter);
        spinnerBalcony.setSelection(0);

        spinnerBalcony.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int imageResId;
                switch (position) {
                    case 0:
                        imageResId = R.drawable.select;
                        break;
                    case 1:
                        imageResId = R.drawable.nobalcony;
                        break;
                    case 2:
                        imageResId = R.drawable.smallfrontbalcony;
                        break;
                    case 3:
                        imageResId = R.drawable.wraparoundbalcony;
                        break;
                    case 4:
                        imageResId = R.drawable.sidebalcony;
                        break;
                    default:
                        imageResId = 0;
                }
                imageViewBalcony.setImageResource(imageResId);
                updateBalconyPrice(position);
                // Show/hide elements based on selection
                showBalconyElements(position > 0);
                if (!isInitializing) {
                    saveCurrentSelections();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupStaircaseSpinner() {
        String[] staircaseOptions = {
            "Select Staircase Style",
            "Concrete with Tile Finish",
            "Wooden Stairs",
            "Spiral Staircase",
            "Steel Frame with Wood Steps"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            staircaseOptions
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                Context context = parent.getContext(); // or requireContext() if accessible

                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorCard1));
                } else {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText1));
                }

                // Set minimum text size for dropdown items
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);

                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change this color as desired
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorSecondaryText1): (R.color.colorPrimaryText1)));
                
                // Set minimum text size for selected text
                tv.setTextSize(6f); // Minimum 1sp
                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 1, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStaircase.setAdapter(adapter);
        spinnerStaircase.setSelection(0);

        spinnerStaircase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int imageResId;
                switch (position) {
                    case 0:
                        imageResId = R.drawable.select;
                        break;
                    case 1:
                        imageResId = R.drawable.concretetilefinish;
                        break;
                    case 2:
                        imageResId = R.drawable.woodenstairs;
                        break;
                    case 3:
                        imageResId = R.drawable.spiralstaircase;
                        break;
                    case 4:
                        imageResId = R.drawable.steelframewoodstep;
                        break;
                    default:
                        imageResId = 0;
                }
                imageViewStaircase.setImageResource(imageResId);
                updateStaircasePrice(position);
                // Show/hide elements based on selection
                showStaircaseElements(position > 0);
                if (!isInitializing) {
                    saveCurrentSelections();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateFloorTitle(int floorNumber) {
        String floorTitle;
        int floorCount = getArguments() != null ? getArguments().getInt("floor_count", 1) : 1;
        
        if (floorCount == 1) {
            floorTitle = "FLOOR ONE";
        } else if (floorCount == 2) {
            if (floorNumber == 3) {
                floorTitle = "FLOOR ONE";
            } else {
                floorTitle = "FLOOR TWO";
            }
        } else if (floorCount == 3) {
            if (floorNumber == 1) {
                floorTitle = "FLOOR THREE";
            } else if (floorNumber == 2) {
                floorTitle = "FLOOR TWO";
            } else {
                floorTitle = "FLOOR ONE";
            }
        } else {
            floorTitle = "FLOOR " + floorNumber;
        }
        
        if (floorTitleTextView != null) {
            floorTitleTextView.setText(floorTitle);
        }
    }

    private void showFlooringElements(boolean show) {
        if (show) {
            cardViewFloor.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) floorLeftColumn.getLayoutParams();
            LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) floorRightColumn.getLayoutParams();
            leftParams.weight = 2f;
            rightParams.weight = 1f;
            floorLeftColumn.setLayoutParams(leftParams);
            floorRightColumn.setLayoutParams(rightParams);
            floorRightColumn.setVisibility(View.VISIBLE);
        } else {
            cardViewFloor.setVisibility(View.GONE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) floorLeftColumn.getLayoutParams();
            leftParams.weight = 3f;
            floorLeftColumn.setLayoutParams(leftParams);
            floorRightColumn.setVisibility(View.GONE);
        }
    }

    private void showWallElements(boolean show) {
        if (show) {
            cardViewWalls.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) wallLeftColumn.getLayoutParams();
            LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) wallRightColumn.getLayoutParams();
            leftParams.weight = 2f;
            rightParams.weight = 1f;
            wallLeftColumn.setLayoutParams(leftParams);
            wallRightColumn.setLayoutParams(rightParams);
            wallRightColumn.setVisibility(View.VISIBLE);
        } else {
            cardViewWalls.setVisibility(View.GONE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) wallLeftColumn.getLayoutParams();
            leftParams.weight = 3f;
            wallLeftColumn.setLayoutParams(leftParams);
            wallRightColumn.setVisibility(View.GONE);
        }
    }

    private void showWindowElements(boolean show) {
        if (show) {
            cardViewWindow.setVisibility(View.VISIBLE);
            windowQuantityColumn.setVisibility(View.VISIBLE);
            windowRightColumn.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) windowLeftColumn.getLayoutParams();
            LinearLayout.LayoutParams quantityParams = (LinearLayout.LayoutParams) windowQuantityColumn.getLayoutParams();
            LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) windowRightColumn.getLayoutParams();
            leftParams.weight = 1f;
            quantityParams.weight = 1f;
            rightParams.weight = 1f;
            windowLeftColumn.setLayoutParams(leftParams);
            windowQuantityColumn.setLayoutParams(quantityParams);
            windowRightColumn.setLayoutParams(rightParams);
        } else {
            cardViewWindow.setVisibility(View.GONE);
            windowQuantityColumn.setVisibility(View.GONE);
            windowRightColumn.setVisibility(View.GONE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) windowLeftColumn.getLayoutParams();
            leftParams.weight = 3f;
            windowLeftColumn.setLayoutParams(leftParams);
        }
    }

    private void showDoorElements(boolean show) {
        if (show) {
            cardViewDoor.setVisibility(View.VISIBLE);
            doorQuantityColumn.setVisibility(View.VISIBLE);
            doorRightColumn.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) doorLeftColumn.getLayoutParams();
            LinearLayout.LayoutParams quantityParams = (LinearLayout.LayoutParams) doorQuantityColumn.getLayoutParams();
            LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) doorRightColumn.getLayoutParams();
            leftParams.weight = 1f;
            quantityParams.weight = 1f;
            rightParams.weight = 1f;
            doorLeftColumn.setLayoutParams(leftParams);
            doorQuantityColumn.setLayoutParams(quantityParams);
            doorRightColumn.setLayoutParams(rightParams);
        } else {
            cardViewDoor.setVisibility(View.GONE);
            doorQuantityColumn.setVisibility(View.GONE);
            doorRightColumn.setVisibility(View.GONE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) doorLeftColumn.getLayoutParams();
            leftParams.weight = 3f;
            doorLeftColumn.setLayoutParams(leftParams);
        }
    }

    private void showBalconyElements(boolean show) {
        if (show) {
            cardViewBalcony.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) balconyLeftColumn.getLayoutParams();
            LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) balconyRightColumn.getLayoutParams();
            leftParams.weight = 2f;
            rightParams.weight = 1f;
            balconyLeftColumn.setLayoutParams(leftParams);
            balconyRightColumn.setLayoutParams(rightParams);
            balconyRightColumn.setVisibility(View.VISIBLE);
        } else {
            cardViewBalcony.setVisibility(View.GONE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) balconyLeftColumn.getLayoutParams();
            leftParams.weight = 3f;
            balconyLeftColumn.setLayoutParams(leftParams);
            balconyRightColumn.setVisibility(View.GONE);
        }
    }

    private void showStaircaseElements(boolean show) {
        if (show) {
            cardViewStaircase.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) staircaseLeftColumn.getLayoutParams();
            LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) staircaseRightColumn.getLayoutParams();
            leftParams.weight = 2f;
            rightParams.weight = 1f;
            staircaseLeftColumn.setLayoutParams(leftParams);
            staircaseRightColumn.setLayoutParams(rightParams);
            staircaseRightColumn.setVisibility(View.VISIBLE);
        } else {
            cardViewStaircase.setVisibility(View.GONE);
            LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) staircaseLeftColumn.getLayoutParams();
            leftParams.weight = 3f;
            staircaseLeftColumn.setLayoutParams(leftParams);
            staircaseRightColumn.setVisibility(View.GONE);
        }
    }

    private void setContainerWeight(LinearLayout container, float weight) {
        if (container != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
            params.weight = weight;
            params.height = 0; // Use 0 for weight-based distribution
            container.setLayoutParams(params);
        }
    }

    private void updateContainerWeights() {
        // Count visible containers
        int visibleContainers = 4; // Start with 4 for the always-visible containers
        if (balconyContainer.getVisibility() == View.VISIBLE) visibleContainers++;
        if (staircaseContainer.getVisibility() == View.VISIBLE) visibleContainers++;

        // Calculate new weight
        float newWeight = 6.0f / visibleContainers; // Total weight is 6 as per layout

        // Update weights for all containers
        for (int i = 0; i < floorDetailsContainer.getChildCount(); i++) {
            View child = floorDetailsContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                if (child.getVisibility() == View.VISIBLE) {
                    params.weight = newWeight;
                    params.height = 0; // Use 0 for weight-based distribution
                } else {
                    params.weight = 0;
                    params.height = 0;
                }
                child.setLayoutParams(params);
            }
        }
    }

    private void updateContainerVisibility(boolean showBalcony, boolean showStaircase) {
        // Set visibility
        balconyContainer.setVisibility(showBalcony ? View.VISIBLE : View.GONE);
        staircaseContainer.setVisibility(showStaircase ? View.VISIBLE : View.GONE);

        // Update weights
        updateContainerWeights();

        // Request layout update
        floorDetailsContainer.requestLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler to prevent memory leaks
        if (saveHandler != null && saveRunnable != null) {
            saveHandler.removeCallbacks(saveRunnable);
        }
    }
}
