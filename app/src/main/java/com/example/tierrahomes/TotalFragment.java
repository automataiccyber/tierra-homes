package com.example.tierrahomes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tierrahomes.DatabaseHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.NumberFormat;
import java.util.Locale;

public class TotalFragment extends Fragment {
    private static final String TAG = "TotalFragment";

    private Button buildButton;
    private TextView totalAmount;
    private SharedPreferences sharedPreferences;

    private double calculateLaborCost(String locationCategory, double lotSize) {
        if (lotSize <= 0) {
            return 0.0;
        }

        double laborCostPerSqm;
        switch (locationCategory.toLowerCase()) {
            case "urban":
                laborCostPerSqm = 1124.0;
                break;
            case "suburban":
                laborCostPerSqm = 980.0;
                break;
            default: // rural
                laborCostPerSqm = 880.0;
                break;
        }

        // Get floor count from SharedPreferences using the correct key
        float floorCount = sharedPreferences.getFloat("floor_size", 1.0f);

        // Calculate floor multiplier based on count
        double floorMultiplier;
        switch ((int)floorCount) {
            case 3:
                floorMultiplier = 2.5; // ~2.5× for 3 floors
                break;
            case 2:
                floorMultiplier = 1.8; // ~1.8× for 2 floors
                break;
            default:
                floorMultiplier = 1.0; // Base rate for 1 floor
                break;
        }

        Log.d(TAG, String.format("Labor cost calculation - Category: %s, Lot Size: %.2f, Floors: %d, Multiplier: %.2f",
                locationCategory, lotSize, (int)floorCount, floorMultiplier));

        return lotSize * laborCostPerSqm * floorMultiplier;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.total_fragment, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("build_preferences", Context.MODE_PRIVATE);

        // Get location data from arguments and store it
        Bundle args = getArguments();
        if (args != null) {
            String region = args.getString("region", "");
            String municipality = args.getString("municipality", "");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("current_region", region);
            editor.putString("current_municipality", municipality);
            editor.apply();
        }

        buildButton = view.findViewById(R.id.buildButton);
        buildButton.setOnClickListener(v -> handleBuildButtonClick());

        totalAmount = view.findViewById(R.id.totalAmount);

        TextView lotPriceTextView = view.findViewById(R.id.lotPrice);
        TextView firstFloorPriceView = view.findViewById(R.id.firstFloorPrice);
        TextView secondFloorPriceView = view.findViewById(R.id.secondFloorPrice);
        TextView thirdFloorPriceView = view.findViewById(R.id.thirdFloorPrice);
        TextView laborCostView = view.findViewById(R.id.laborCost);

        // Get floor count and lot size
        float floorCount = sharedPreferences.getFloat("floor_size", 1.0f);
        String lotSizeStr = sharedPreferences.getString("lot_size", "");
        double lotSize = 0.0;
        try {
            if (!TextUtils.isEmpty(lotSizeStr)) {
                lotSize = Double.parseDouble(lotSizeStr);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing lot size: " + e.getMessage());
        }

        // Get location category
        String region = sharedPreferences.getString("current_region", "");
        String municipality = sharedPreferences.getString("current_municipality", "");
        LocationClassifier.loadClassificationData(requireContext());
        String locationCategory = LocationClassifier.getLocationCategory(region, municipality);

        // Get customizations preferences
        SharedPreferences customizations = requireContext().getSharedPreferences("floor_customizations", Context.MODE_PRIVATE);

        // Calculate prices for each floor
        // Calculate prices for each floor
        double firstFloorPrice = calculateFloorPrice(locationCategory, lotSize, 3, customizations);  // Use local method
        double secondFloorPrice = floorCount >= 2 ? calculateFloorPrice(locationCategory, lotSize, 2, customizations) : 0;
        double thirdFloorPrice = floorCount >= 3 ? calculateFloorPrice(locationCategory, lotSize, 1, customizations) : 0;
        // Calculate lot price
        double pricePerSqm = getPricePerSqm(locationCategory);
        double lotPrice = lotSize * pricePerSqm;

        // Calculate labor cost
        double laborCost = calculateLaborCost(locationCategory, lotSize);

        // Log all price components
        Log.d(TAG, String.format("Price breakdown - Lot: %.2f, Labor: %.2f", lotPrice, laborCost));
        Log.d(TAG, String.format("Floor prices - 1st: %.2f, 2nd: %.2f, 3rd: %.2f", firstFloorPrice, secondFloorPrice, thirdFloorPrice));

        // Total calculation
        double totalPrice = firstFloorPrice + secondFloorPrice + thirdFloorPrice + lotPrice + laborCost;
        Log.d(TAG, String.format("Total price: %.2f", totalPrice));

        // Update UI with currency formatting
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        if (floorCount >= 3) {
            thirdFloorPriceView.setText("FLOOR THREE: " + currencyFormat.format(thirdFloorPrice));
            thirdFloorPriceView.setVisibility(View.VISIBLE);
        } else {
            thirdFloorPriceView.setVisibility(View.GONE);
        }

        if (floorCount >= 2) {
            secondFloorPriceView.setText("FLOOR TWO: " + currencyFormat.format(secondFloorPrice));
            secondFloorPriceView.setVisibility(View.VISIBLE);
        } else {
            secondFloorPriceView.setVisibility(View.GONE);
        }

        firstFloorPriceView.setText("FLOOR ONE: " + currencyFormat.format(firstFloorPrice));
        lotPriceTextView.setText("LOT PRICE: " + currencyFormat.format(lotPrice));
        laborCostView.setText("LABOR COST: " + currencyFormat.format(laborCost));
        totalAmount.setText("TOTAL: " + currencyFormat.format(totalPrice));

        // Adjust visibility and weights based on floor count
        LinearLayout costBreakdownContainer = view.findViewById(R.id.costBreakdownContainer);

        // Set visibility based on floor count
        thirdFloorPriceView.setVisibility(floorCount >= 3 ? View.VISIBLE : View.GONE);
        secondFloorPriceView.setVisibility(floorCount >= 2 ? View.VISIBLE : View.GONE);

        // Calculate weight for visible items
        int visibleItems = 2; // lotPrice and laborCost are always visible
        visibleItems += (int)floorCount; // add number of visible floors
        float weight = 1.0f / visibleItems;

        // Update weights for all views
        if (thirdFloorPriceView.getVisibility() == View.VISIBLE) {
            ((LinearLayout.LayoutParams) thirdFloorPriceView.getLayoutParams()).weight = weight;
        }
        if (secondFloorPriceView.getVisibility() == View.VISIBLE) {
            ((LinearLayout.LayoutParams) secondFloorPriceView.getLayoutParams()).weight = weight;
        }
        ((LinearLayout.LayoutParams) firstFloorPriceView.getLayoutParams()).weight = weight;
        ((LinearLayout.LayoutParams) lotPriceTextView.getLayoutParams()).weight = weight;
        ((LinearLayout.LayoutParams) laborCostView.getLayoutParams()).weight = weight;

        // Remove all duplicate declarations after this point
        // Delete the second set of declarations for lotSizeStr, lotSize, region, municipality, locationCategory, and pricePerSqm
        return view;
    }

    private double getPricePerSqm(String locationCategory) {
        Log.d(TAG, "Location category received: " + locationCategory);
        if (locationCategory == null || locationCategory.isEmpty()) {
            Log.w(TAG, "Empty location category, defaulting to rural price");
            return BuildFragment.RURAL_PRICE_PER_SQM;
        }

        switch (locationCategory.toLowerCase()) {
            case "urban":
                Log.d(TAG, "Using urban price");
                return BuildFragment.URBAN_PRICE_PER_SQM;
            case "suburban":
                Log.d(TAG, "Using suburban price");
                return BuildFragment.SUBURBAN_PRICE_PER_SQM;
            default:
                Log.d(TAG, "Using rural price as default");
                return BuildFragment.RURAL_PRICE_PER_SQM;
        }
    }

    private void handleBuildButtonClick() {
        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirm Building")
            .setMessage("Are you sure you want to build this house?")
            .setPositiveButton("Yes", (dialog, which) -> {
                proceedWithHouseBuilding();
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void proceedWithHouseBuilding() {
        try {
            // Get user email
            SharedPreferences loginPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            String userEmail = loginPrefs.getString("userEmail", "");

            if (userEmail.isEmpty()) {
                Toast.makeText(requireContext(), "User session error. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current budget from database
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            DatabaseHelper.UserPreferences preferences = dbHelper.getUserPreferences(userEmail);
            if (preferences == null || preferences.getBudget() == null) {
                Toast.makeText(requireContext(), "Error retrieving user budget", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse current budget (remove any formatting)
            String currentBudgetStr = preferences.getBudget();
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number number = format.parse(currentBudgetStr);
            double currentBudget = number.doubleValue();

            // Get total price (remove currency symbol and commas)
            String totalAmountText = totalAmount.getText().toString()
                    .replace("TOTAL: ", "")
                    .replace("₱", "");
            Number totalNumber = format.parse(totalAmountText);
            double totalPrice = totalNumber.doubleValue();

            Log.d(TAG, String.format("Comparing budget %.2f with price %.2f", currentBudget, totalPrice));

            // Check if user has enough budget
            if (currentBudget < totalPrice) {
                Toast.makeText(requireContext(),
                        "Insufficient funds. You need ₱" + String.format("%,.2f", totalPrice) +
                                " but your budget is ₱" + String.format("%,.2f", currentBudget),
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Calculate new budget
            double newBudget = currentBudget - totalPrice;

            // Update budget in database
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

            // Save house in asset preferences
            SharedPreferences assetPrefs = requireActivity().getSharedPreferences(
                    "asset_preferences_" + userEmail, Context.MODE_PRIVATE);
            int houseCount = assetPrefs.getInt("house_count", 0);
            int newHouseNumber = houseCount + 1;
    
            SharedPreferences.Editor assetEditor = assetPrefs.edit();
            assetEditor.putInt("house_count", newHouseNumber);
            assetEditor.putString("house_" + newHouseNumber + "_price", String.valueOf(totalPrice));
            assetEditor.putString("house_" + newHouseNumber + "_location", 
                sharedPreferences.getString("current_municipality", ""));
            assetEditor.putBoolean("house_" + newHouseNumber + "_isBuilt", true);  // Add this line
            assetEditor.putInt("house_" + newHouseNumber + "_image", R.drawable.main_logo);  // Add this line
            assetEditor.apply();

            // Add transaction to budget history
            SharedPreferences historyPrefs = requireActivity().getSharedPreferences(
                    "budgetHistory_" + userEmail, Context.MODE_PRIVATE);
            int historySize = historyPrefs.getInt("historySize", 0);

            // Format the negative amount with currency symbol
            NumberFormat formatter = NumberFormat.getInstance(Locale.US);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            // Format the negative amount
            String historyEntry = "-₱" + formatter.format(totalPrice); // Remove house number

            // Save history entry
            SharedPreferences.Editor historyEditor = historyPrefs.edit();
            historyEditor.putString("entry_" + historySize, historyEntry);
            historyEditor.putInt("historySize", historySize + 1);
            historyEditor.apply();

            Toast.makeText(requireContext(),
                    "House is built successfully!",
                    Toast.LENGTH_SHORT).show();

            // When navigating to HomeFragment after successful purchase
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
            transaction.replace(R.id.content_frame, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing numbers: " + e.getMessage());
            Toast.makeText(requireContext(), "Error processing purchase: Invalid number format", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error processing purchase: " + e.getMessage());
            Toast.makeText(requireContext(), "Error processing purchase", Toast.LENGTH_SHORT).show();
        }
    }

    private String toRomanNumerals(int number) {
        String[] romanNumerals = {"I", "II", "III", "IV", "V"};
        return (number > 0 && number <= romanNumerals.length) ? romanNumerals[number - 1] : String.valueOf(number);
    }

    // ... existing code ...
    private double calculateFloorPrice(String locationCategory, double lotSize, int floorNumber,
                                       SharedPreferences floorCustomizations) {
        String floorKey = "floor_" + floorNumber;
        int locationIndex = MaterialPriceCalculator.getLocationIndex(locationCategory);

        // Get customization selections
        int flooringType = floorCustomizations.getInt(floorKey + "_flooring", 0);
        int wallType = floorCustomizations.getInt(floorKey + "_walls", 0);
        int windowType = floorCustomizations.getInt(floorKey + "_window", 0);
        int doorType = floorCustomizations.getInt(floorKey + "_door", 0);
        int balconyType = floorCustomizations.getInt(floorKey + "_balcony", 0);
        int staircaseType = floorCustomizations.getInt(floorKey + "_staircase", 0);

        // Calculate individual prices using the same logic as CustomizeFragment
        double flooringPrice = 0;
        switch (flooringType) {
            case 1: flooringPrice = MaterialPriceCalculator.FlooringPrices.CONCRETE_SLAB[0][locationIndex] * lotSize; break;
            case 2: flooringPrice = MaterialPriceCalculator.FlooringPrices.CERAMIC_TILE[0][locationIndex] * lotSize; break;
            case 3: flooringPrice = MaterialPriceCalculator.FlooringPrices.VINYL_PLANK[0][locationIndex] * lotSize; break;
            case 4: flooringPrice = MaterialPriceCalculator.FlooringPrices.NATURAL_STONE[0][locationIndex] * lotSize; break;
            case 5: flooringPrice = MaterialPriceCalculator.FlooringPrices.POLISHED_CONCRETE[0][locationIndex] * lotSize; break;
        }

        double wallPrice = 0;
        double wallArea = lotSize * 2.5;
        switch (wallType) {
            case 1: wallPrice = MaterialPriceCalculator.WallPrices.PAINTED_CEMENT[0][locationIndex] * wallArea; break;
            case 2: wallPrice = MaterialPriceCalculator.WallPrices.BRICK_CLADDING[0][locationIndex] * wallArea; break;
            case 3: wallPrice = MaterialPriceCalculator.WallPrices.STUCCO[0][locationIndex] * wallArea; break;
            case 4: wallPrice = MaterialPriceCalculator.WallPrices.STONE_FINISH[0][locationIndex] * wallArea; break;
            case 5: wallPrice = MaterialPriceCalculator.WallPrices.WOOD_SIDING[0][locationIndex] * wallArea; break;
        }

        int windowCount = Math.max(2, (int)(lotSize / 20));
        double windowPrice = 0;
        switch (windowType) {
            case 1: windowPrice = MaterialPriceCalculator.WindowPrices.SLIDING_ALUMINUM[0][locationIndex] * windowCount; break;
            case 2: windowPrice = MaterialPriceCalculator.WindowPrices.FIXED_PICTURE[0][locationIndex] * windowCount; break;
            case 3: windowPrice = MaterialPriceCalculator.WindowPrices.CASEMENT_UPVC[0][locationIndex] * windowCount; break;
            case 4: windowPrice = MaterialPriceCalculator.WindowPrices.WOODEN_FRAME[0][locationIndex] * windowCount; break;
            case 5: windowPrice = MaterialPriceCalculator.WindowPrices.GLASS_SECURITY[0][locationIndex] * windowCount; break;
        }

        int doorCount = Math.max(2, (int)(lotSize / 30));
        double doorPrice = 0;
        switch (doorType) {
            case 1: doorPrice = MaterialPriceCalculator.DoorPrices.SOLID_WOODEN[0][locationIndex] * doorCount; break;
            case 2: doorPrice = MaterialPriceCalculator.DoorPrices.STEEL_SECURITY[0][locationIndex] * doorCount; break;
            case 3: doorPrice = MaterialPriceCalculator.DoorPrices.GLASS_METAL[0][locationIndex] * doorCount; break;
            case 4: doorPrice = MaterialPriceCalculator.DoorPrices.FRENCH_DOORS[0][locationIndex] * doorCount; break;
        }

        double balconyPrice = 0;
        if (balconyType > 1) {
            switch (balconyType) {
                case 2: balconyPrice = MaterialPriceCalculator.BalconyPrices.SMALL_FRONT[0][locationIndex]; break;
                case 3: balconyPrice = MaterialPriceCalculator.BalconyPrices.WRAP_AROUND[0][locationIndex]; break;
                case 4: balconyPrice = MaterialPriceCalculator.BalconyPrices.SIDE_RAILING[0][locationIndex]; break;
            }
        }

        double staircasePrice = 0;
        if (staircaseType > 0) {
            switch (staircaseType) {
                case 1: staircasePrice = MaterialPriceCalculator.StaircasePrices.CONCRETE_TILE[0][locationIndex]; break;
                case 2: staircasePrice = MaterialPriceCalculator.StaircasePrices.WOODEN[0][locationIndex]; break;
                case 3: staircasePrice = MaterialPriceCalculator.StaircasePrices.SPIRAL[0][locationIndex]; break;
                case 4: staircasePrice = MaterialPriceCalculator.StaircasePrices.STEEL_WOOD[0][locationIndex]; break;
            }
        }

        // Return total price for this floor
        return flooringPrice + wallPrice + windowPrice + doorPrice + balconyPrice + staircasePrice;
    }
// ... existing code ...
}
