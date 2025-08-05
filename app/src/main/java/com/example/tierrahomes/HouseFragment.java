package com.example.tierrahomes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.Toast;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.example.tierrahomes.NetworkUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HouseFragment extends Fragment {
    private Button buyButton;
    private Button insightsButton;
    private TextView titleText;
    private ImageView houseImage;
    private TextView propertyType;
    private TextView lotSize;
    private TextView bedBathCount;
    private TextView price;
    private TextView salesRate;
    private String currentCategory = "RURAL"; // Add this field



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.house_fragment, container, false);

        // Initialize views
        titleText = view.findViewById(R.id.textView);
        houseImage = view.findViewById(R.id.imageView2);
        propertyType = view.findViewById(R.id.propertyType);
        lotSize = view.findViewById(R.id.lotSize);
        bedBathCount = view.findViewById(R.id.bedBathCount);
        price = view.findViewById(R.id.price);
        buyButton = view.findViewById(R.id.done_button);
        insightsButton = view.findViewById(R.id.insight_button);
        salesRate = view.findViewById(R.id.salesRate);


        // Get arguments passed from BuyFragment
        Bundle args = getArguments();
        if (args != null) {
            String houseName = args.getString("name", "");
            int imageResourceId = args.getInt("imageResourceId");
            currentCategory = args.getString("category", "RURAL"); // Store category in class field

            // Set the house details based on the house type
            setHouseDetails(houseName, imageResourceId, currentCategory);
        }

        // Set OnClickListener for the buttons
        setupButtonListeners();

        return view;
    }

    private void setHouseDetails(String houseName, int imageResourceId, String category) {
        titleText.setText(category);
        houseImage.setImageResource(imageResourceId);
        propertyType.setText("PROPERTY TYPE: " + houseName.toUpperCase());

        // Set details based on house type
        switch (houseName) {
            case "FARMHOUSE":
                lotSize.setText("LOT SIZE: 5,000 SQM");
                bedBathCount.setText("BEDROOMS: 4  |  BATHROOMS: 2");
                price.setText("PRICE: ₱25,860,000");
                break;
            case "COTTAGE":
                lotSize.setText("LOT SIZE: 300 SQM");
                bedBathCount.setText("BEDROOMS: 2  |  BATHROOMS: 1");
                price.setText("PRICE: ₱1,567,800");
                break;
            case "CABIN":
                lotSize.setText("LOT SIZE: 250 SQM");
                bedBathCount.setText("BEDROOMS: 1  |  BATHROOMS: 1");
                price.setText("PRICE: ₱1,322,250");
                break;
            case "RANCH HOUSE":
                lotSize.setText("LOT SIZE: 10,000 SQM");
                bedBathCount.setText("BEDROOMS: 5  |  BATHROOMS: 3");
                price.setText("PRICE: ₱53,410,000");
                break;
            case "HOMESTEAD":
                lotSize.setText("LOT SIZE: 7,000 SQM");
                bedBathCount.setText("BEDROOMS: 4  |  BATHROOMS: 2");
                price.setText("PRICE: ₱37,982,000");
                break;
            case "BARN CONVERSION":
                lotSize.setText("LOT SIZE: 600 SQM");
                bedBathCount.setText("BEDROOMS: 3  |  BATHROOMS: 2");
                price.setText("PRICE: ₱3,338,400");
                break;
            // Urban Properties
            case "APARTMENT":
                lotSize.setText("LOT SIZE: 80 SQM");
                bedBathCount.setText("BEDROOMS: 2  |  BATHROOMS: 1");
                price.setText("PRICE: ₱882,560");
                break;
            case "CONDOMINIUM":
                lotSize.setText("LOT SIZE: 60 SQM");
                bedBathCount.setText("BEDROOMS: 2  |  BATHROOMS: 2");
                price.setText("PRICE: ₱1,923,300");
                break;
            case "LOFT":
                lotSize.setText("LOT SIZE: 100 SQM");
                bedBathCount.setText("BEDROOMS: 2  |  BATHROOMS: 2");
                price.setText("PRICE: ₱1,150,200");
                break;
            case "PENTHOUSE":
                lotSize.setText("LOT SIZE: 180 SQM");
                bedBathCount.setText("BEDROOMS: 4  |  BATHROOMS: 3");
                price.setText("PRICE: ₱5,846,940");
                break;
            case "TOWNHOUSE":
                lotSize.setText("LOT SIZE: 90 SQM");
                bedBathCount.setText("BEDROOMS: 3  |  BATHROOMS: 2");
                price.setText("PRICE: ₱1,086,930");
                break;
            case "STUDIO FLAT":
                lotSize.setText("LOT SIZE: 30 SQM");
                bedBathCount.setText("BEDROOMS: 0  |  BATHROOMS: 1");
                price.setText("PRICE: ₱322,950");
                break;
            // Suburban Properties
            case "DETACHED HOUSE":
                lotSize.setText("LOT SIZE: 150 SQM");
                bedBathCount.setText("BEDROOMS: 3  |  BATHROOMS: 2");
                price.setText("PRICE: ₱1,902,000");
                break;
            case "SEMI-DETACHED":
                lotSize.setText("LOT SIZE: 120 SQM");
                bedBathCount.setText("BEDROOMS: 3  |  BATHROOMS: 2");
                price.setText("PRICE: ₱1,416,120");
                break;
            case "DUPLEX":
                lotSize.setText("LOT SIZE: 180 SQM");
                bedBathCount.setText("BEDROOMS: 3  |  BATHROOMS: 2");
                price.setText("PRICE: ₱2,532,060");
                break;
            case "BUNGALOW":
                lotSize.setText("LOT SIZE: 130 SQM");
                bedBathCount.setText("BEDROOMS: 2  |  BATHROOMS: 1");
                price.setText("PRICE: ₱1,555,190");
                break;
            case "SPLIT-LEVEL HOME":
                lotSize.setText("LOT SIZE: 160 SQM");
                bedBathCount.setText("BEDROOMS: 3  |  BATHROOMS: 2");
                price.setText("PRICE: ₱1,985,120");
                break;
            case "MCMANSION":
                lotSize.setText("LOT SIZE: 300 SQM");
                bedBathCount.setText("BEDROOMS: 5  |  BATHROOMS: 4");
                price.setText("PRICE: ₱5,752,500");
                break;
        }

        // Get sales data from Firebase for global tracking
        Log.d("HouseFragment", "Fetching Firebase sales data for: " + houseName);
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        
        // Check network availability
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            dbHelper.getHouseSalesFromFirebase(houseName, new FirebaseManager.HouseSalesCallback() {
                @Override
                public void onSalesReceived(String houseType, int sales) {
                    Log.d("HouseFragment", "Received Firebase sales data for " + houseType + ": " + sales);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            String salesStatus = getSalesStatusFromFirebase(houseType, sales);
                            salesRate.setText(String.format("GLOBAL SALES: %d | %s", sales, salesStatus));
                            Log.d("HouseFragment", "Updated sales display: " + sales + " | " + salesStatus);
                        });
                    }
                }
            });
        } else {
            // Show offline message with queue status
            Log.d("HouseFragment", "Network offline, showing offline message");
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    int queueSize = NetworkUtils.OfflineQueue.getQueueSize(requireContext());
                    if (queueSize > 0) {
                        salesRate.setText("GLOBAL SALES: Data is not available (" + queueSize + " pending sync)");
                    } else {
                        salesRate.setText("GLOBAL SALES: Data is not available");
                    }
                });
            }
        }
    }
    private String getSalesStatus(String houseName, int currentSales) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("house_sales", new String[]{"sales_count"}, null, null, null, null, null);

        int maxSales = 0;
        int minSales = Integer.MAX_VALUE;
        while (cursor.moveToNext()) {
            int sales = cursor.getInt(0);
            maxSales = Math.max(maxSales, sales);
            minSales = Math.min(minSales, sales);
        }
        cursor.close();

        if (currentSales == maxSales && currentSales > 0) {
            return "BEST SELLER!";
        } else if (currentSales == minSales) {
            return "LEAST POPULAR";
        } else if (currentSales > (maxSales + minSales) / 2) {
            return "HIGH DEMAND";
        } else {
            return "MODERATE DEMAND";
        }
    }

    private String getSalesStatusFromFirebase(String houseName, int currentSales) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.getAllHouseSalesFromFirebase(new FirebaseManager.HouseSalesMapCallback() {
            @Override
            public void onSalesMapReceived(Map<String, Integer> salesMap) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String status = calculateSalesStatus(houseName, currentSales, salesMap);
                        salesRate.setText(String.format("GLOBAL SALES: %d | %s", currentSales, status));
                    });
                }
            }
        });
        return "LOADING..."; // Temporary status while loading
    }

    private String calculateSalesStatus(String houseName, int currentSales, Map<String, Integer> salesMap) {
        if (salesMap.isEmpty()) {
            return "NO DATA";
        }

        int maxSales = Collections.max(salesMap.values());
        int minSales = Collections.min(salesMap.values());

        if (currentSales == maxSales && currentSales > 0) {
            return "BEST SELLER!";
        } else if (currentSales == minSales) {
            return "LEAST POPULAR";
        } else if (currentSales > (maxSales + minSales) / 2) {
            return "HIGH DEMAND";
        } else {
            return "MODERATE DEMAND";
        }
    }


    private void initializeSalesData(Map<String, Integer> salesCount) {
        // Urban Properties
        salesCount.put("APARTMENT", 45);
        salesCount.put("CONDOMINIUM", 78);
        salesCount.put("LOFT", 32);
        salesCount.put("PENTHOUSE", 15);
        salesCount.put("TOWNHOUSE", 56);
        salesCount.put("STUDIO FLAT", 89);

        // Suburban Properties
        salesCount.put("DETACHED HOUSE", 67);
        salesCount.put("SEMI-DETACHED", 43);
        salesCount.put("DUPLEX", 38);
        salesCount.put("BUNGALOW", 51);
        salesCount.put("SPLIT-LEVEL HOME", 29);
        salesCount.put("MCMANSION", 12);
    }
    private String getSalesStatus(String houseName, Map<String, Integer> salesCount) {
        int currentSales = salesCount.get(houseName);
        int maxSales = Collections.max(salesCount.values());
        int minSales = Collections.min(salesCount.values());

        if (currentSales == maxSales) {
            return "BEST SELLER!";
        } else if (currentSales == minSales) {
            return "LEAST POPULAR";
        } else if (currentSales > (maxSales + minSales) / 2) {
            return "HIGH DEMAND";
        } else {
            return "MODERATE DEMAND";
        }
    }


    private void initializePriceData(Map<String, float[]> propertyPrices) {
        // Urban Properties
        propertyPrices.put("APARTMENT", new float[]{736800, 789760, 723280, 839600, 853120, 882560}); // 80 SQM
        propertyPrices.put("CONDOMINIUM", new float[]{749700, 1032480, 1227180, 1331700, 1773540, 1923300}); // 60 SQM
        propertyPrices.put("LOFT", new float[]{934200, 956100, 987500, 1098300, 1124500, 1150200}); // 100 SQM
        propertyPrices.put("PENTHOUSE", new float[]{2391120, 3220560, 3799260, 4226580, 5384160, 5846940}); // 180 SQM
        propertyPrices.put("TOWNHOUSE", new float[]{868950, 1009260, 973800, 1067760, 1027980, 1086930}); // 90 SQM
        propertyPrices.put("STUDIO FLAT", new float[]{268320, 276300, 271230, 299310, 311670, 322950}); // 30 SQM

        // Suburban Properties
        propertyPrices.put("DETACHED HOUSE", new float[]{1464000, 1557600, 1633500, 1758150, 1835250, 1902000}); // 150 SQM
        propertyPrices.put("SEMI-DETACHED", new float[]{1126800, 1161960, 1190520, 1265640, 1342800, 1416120}); // 120 SQM
        propertyPrices.put("DUPLEX", new float[]{1346580, 1583640, 1947600, 2444040, 2412900, 2532060}); // 180 SQM
        propertyPrices.put("BUNGALOW", new float[]{1255150, 1457820, 1372410, 1542320, 1484860, 1555190}); // 130 SQM
        propertyPrices.put("SPLIT-LEVEL HOME", new float[]{1579840, 1620000, 1812640, 1938880, 1887200, 1985120}); // 160 SQM
        propertyPrices.put("MCMANSION", new float[]{3868200, 4215600, 4611000, 4872000, 5367900, 5752500}); // 300 SQM

        // Rural Properties
        propertyPrices.put("FARMHOUSE", new float[]{25030000, 25745000, 26525000, 40380000, 23665000, 25860000}); // 5000 SQM
        propertyPrices.put("COTTAGE", new float[]{1541400, 1587900, 1646100, 2442600, 1469100, 1567800}); // 300 SQM
        propertyPrices.put("CABIN", new float[]{1298000, 1337000, 1383250, 2056250, 1240750, 1322250}); // 250 SQM
        propertyPrices.put("RANCH HOUSE", new float[]{52860000, 54770000, 56420000, 83310000, 50480000, 53410000}); // 10,000 SQM
        propertyPrices.put("HOMESTEAD", new float[]{37499000, 38654000, 39858000, 59129000, 35903000, 37982000}); // 7000 SQM
        propertyPrices.put("BARN CONVERSION", new float[]{3290000, 3407400, 3499200, 5158800, 3138600, 3338400}); // 600 SQM
    }

    private void setupButtonListeners() {
        // In setupButtonListeners() method
        buyButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Purchase")
                .setMessage("Are you sure you want to buy this house?")
                .setPositiveButton("Yes", (dialog, which) -> {
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

                        // Get house price (remove currency symbol and commas)
                        String priceText = price.getText().toString()
                                .replace("PRICE: ", "")
                                .replace("₱", "")
                                .replace(",", "");
                        double housePrice = Double.parseDouble(priceText);

                        // Check if user has enough budget
                        if (currentBudget < housePrice) {
                            Toast.makeText(requireContext(),
                                    "Insufficient funds. You need ₱" + String.format("%,.2f", housePrice) +
                                            " but your budget is ₱" + String.format("%,.2f", currentBudget),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Calculate new budget
                        double newBudget = currentBudget - housePrice;

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

                        Bundle args = getArguments();
                        int imageResourceId = args != null ? args.getInt("imageResourceId", 0) : 0;

                        SharedPreferences.Editor assetEditor = assetPrefs.edit();
                        assetEditor.putInt("house_count", newHouseNumber);
                        assetEditor.putString("house_" + newHouseNumber + "_type", propertyType.getText().toString());
                        assetEditor.putString("house_" + newHouseNumber + "_price", String.valueOf(housePrice));
                        assetEditor.putInt("house_" + newHouseNumber + "_image", imageResourceId);
                        assetEditor.putString("house_" + newHouseNumber + "_location",
                                preferences.getMunicipality());
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
                        String historyEntry = "-₱" + formatter.format(housePrice); // Remove property type and house number

                        // Get house name from arguments
                        String houseName = args != null ? args.getString("name", "") : "";
                        Log.d("HouseFragment", "Purchase successful for house: " + houseName);
                        
                        // Save history entry
                        SharedPreferences.Editor historyEditor = historyPrefs.edit();
                        historyEditor.putString("entry_" + historySize, historyEntry);
                        historyEditor.putInt("historySize", historySize + 1);
                        historyEditor.apply();
                        
                        // Increment sales and refresh display
                        boolean salesUpdated = dbHelper.incrementHouseSales(houseName);
                        if (!salesUpdated) {
                            Toast.makeText(requireContext(), "Warning: Failed to update sales statistics", Toast.LENGTH_SHORT).show();
                        }

                        // Check if purchase was made offline
                        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                            Toast.makeText(requireContext(),
                                    "Purchase successful! Will sync when online.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Purchase successful!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // Refresh the Firebase data display
                        refreshSalesDisplay(houseName);
                        // When navigating to HomeFragment after successful purchase
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                        );
                        transaction.replace(R.id.content_frame, new HomeFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();

                    } catch (ParseException | NumberFormatException e) {
                        Toast.makeText(requireContext(), "Error processing purchase: Invalid number format",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error processing purchase", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
        });

        insightsButton.setOnClickListener(v -> {
            // Get the arguments that were passed to HouseFragment
            Bundle currentArgs = getArguments();
            String houseName = currentArgs != null ? currentArgs.getString("name", "") : "";
            int imageResourceId = currentArgs != null ? currentArgs.getInt("imageResourceId", 0) : 0;
            String category = currentArgs != null ? currentArgs.getString("category", "RURAL") : "RURAL";

            // Create InsightsFragment and pass all the property data
            InsightsFragment insightsFragment = new InsightsFragment();
            Bundle insightArgs = new Bundle();
            insightArgs.putString("propertyType", houseName);
            insightArgs.putInt("imageResourceId", imageResourceId);
            insightArgs.putString("category", category);
            insightsFragment.setArguments(insightArgs);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
            transaction.replace(R.id.content_frame, insightsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private String toRomanNumerals(int number) {
        String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return (number > 0 && number <= romanNumerals.length) ? romanNumerals[number - 1] : String.valueOf(number);
    }

    private void refreshSalesDisplay(String houseName) {
        Log.d("HouseFragment", "Refreshing sales display for: " + houseName);
        
        // Check network availability
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Log.d("HouseFragment", "Network offline, showing offline message");
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    int queueSize = NetworkUtils.OfflineQueue.getQueueSize(requireContext());
                    if (queueSize > 0) {
                        salesRate.setText("GLOBAL SALES: Data is not available (" + queueSize + " pending sync)");
                    } else {
                        salesRate.setText("GLOBAL SALES: Data is not available");
                    }
                });
            }
            return;
        }
        
        // Show loading state
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                salesRate.setText("GLOBAL SALES: UPDATING...");
            });
        }
        
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.getHouseSalesFromFirebase(houseName, new FirebaseManager.HouseSalesCallback() {
            @Override
            public void onSalesReceived(String houseType, int sales) {
                Log.d("HouseFragment", "Refreshed Firebase sales data for " + houseType + ": " + sales);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String salesStatus = getSalesStatusFromFirebase(houseType, sales);
                        salesRate.setText(String.format("GLOBAL SALES: %d | %s", sales, salesStatus));
                        Log.d("HouseFragment", "Updated sales display after purchase: " + sales + " | " + salesStatus);
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh sales display when fragment resumes (e.g., when coming back online)
        Bundle args = getArguments();
        if (args != null) {
            String houseName = args.getString("name", "");
            if (!houseName.isEmpty()) {
                refreshSalesDisplay(houseName);
            }
        }
    }
}
