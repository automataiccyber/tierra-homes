package com.example.tierrahomes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BudgetAndLocationActivity extends AppCompatActivity {

    private static final String TAG = "BudgetAndLocation";

    // UI components
    private Spinner islandSpinner, regionSpinner, provinceSpinner, municipalitySpinner;
    private EditText budgetDisplay;
    private Button doneButton;
    private TextView budgetLabel, locationLabel;

    // Selection data
    private String selectedRegionName = "";
    private String selectedRegionCode = "";
    private String selectedIslandGroup = "";
    private int selectedIslandIndex = -1;

    // Maps for efficient data lookup
    private final Map<String, String[]> regionToProvincesMap = new HashMap<>();
    private final Map<String, String[][]> regionToMunicipalitiesMap = new HashMap<>();
    private final Map<String, String> regionNameToCodeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_and_location_screen);

        initializeUIComponents();
        initializeDataMaps();
        setupSpinners();
        setupBudgetEditText();
        setupDoneButton();
    }

    private void initializeUIComponents() {
        islandSpinner = findViewById(R.id.spinner_island);
        regionSpinner = findViewById(R.id.spinner_region);
        provinceSpinner = findViewById(R.id.spinner_province);
        municipalitySpinner = findViewById(R.id.spinner_municipality);
        budgetDisplay = findViewById(R.id.budget_display);
        doneButton = findViewById(R.id.btn_done);
        budgetLabel = findViewById(R.id.budget_label);
        locationLabel = findViewById(R.id.location_label);

        // Make EditText field responsive to smaller devices
        budgetDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f); // Set minimum size
        budgetDisplay.setAutoSizeTextTypeWithDefaults(android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        budgetDisplay.setAutoSizeTextTypeUniformWithConfiguration(1, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        // Make button text responsive and limit to one line
        doneButton.setMaxLines(1);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            doneButton,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        // Make TextView labels responsive to smaller devices
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            budgetLabel,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        budgetLabel.setMaxLines(1);

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            locationLabel,
            1,  // min size in sp
            40, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        locationLabel.setMaxLines(1);
    }

    private void initializeDataMaps() {
        // Initialize region name to code mapping
        setupRegionCodeMap();

        // Initialize region to provinces mapping
        regionToProvincesMap.put("CAR", PhilippineLocationData.LOCATION_DATA.CAR_PROVINCES);
        regionToProvincesMap.put("Ilocos", PhilippineLocationData.LOCATION_DATA.ILOCOS_PROVINCES);
        regionToProvincesMap.put("Cagayan", PhilippineLocationData.LOCATION_DATA.CAGAYAN_VALLEY_PROVINCES);
        regionToProvincesMap.put("Visayas", PhilippineLocationData.LOCATION_DATA.CENTRAL_VISAYAS_PROVINCES);
        regionToProvincesMap.put("Luzon", PhilippineLocationData.LOCATION_DATA.CENTRAL_LUZON_PROVINCES);
        regionToProvincesMap.put("CALABARZON", PhilippineLocationData.LOCATION_DATA.CALABARZON_PROVINCES);
        regionToProvincesMap.put("MIMAROPA", PhilippineLocationData.LOCATION_DATA.MIMAROPA_PROVINCES);
        regionToProvincesMap.put("Bicol", PhilippineLocationData.LOCATION_DATA.BICOL_PROVINCES);
        regionToProvincesMap.put("Western", PhilippineLocationData.LOCATION_DATA.WESTERN_VISAYAS_PROVINCES);
        regionToProvincesMap.put("Eastern", PhilippineLocationData.LOCATION_DATA.EASTERN_VISAYAS_PROVINCES);
        regionToProvincesMap.put("Zamboanga", PhilippineLocationData.LOCATION_DATA.ZAMBOANGA_PENINSULA_PROVINCES);
        regionToProvincesMap.put("Northern", PhilippineLocationData.LOCATION_DATA.NORTHERN_MINDANAO_PROVINCES);
        regionToProvincesMap.put("Davao", PhilippineLocationData.LOCATION_DATA.DAVAO_PROVINCES);
        regionToProvincesMap.put("SOCCSKSARGEN", PhilippineLocationData.LOCATION_DATA.SOCCSKSARGEN_PROVINCES);
        regionToProvincesMap.put("Caraga", PhilippineLocationData.LOCATION_DATA.CARAGA_PROVINCES);
        regionToProvincesMap.put("BARMM", PhilippineLocationData.LOCATION_DATA.BARMM_PROVINCES);

        // Initialize region to municipalities mapping
        regionToMunicipalitiesMap.put("CAR", PhilippineLocationData.LOCATION_DATA.CAR_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Ilocos", PhilippineLocationData.LOCATION_DATA.ILOCOS_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Cagayan", PhilippineLocationData.LOCATION_DATA.CAGAYAN_VALLEY_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Luzon", PhilippineLocationData.LOCATION_DATA.CENTRAL_LUZON_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("CALABARZON", PhilippineLocationData.LOCATION_DATA.CALABARZON_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("MIMAROPA", PhilippineLocationData.LOCATION_DATA.MIMAROPA_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Bicol", PhilippineLocationData.LOCATION_DATA.BICOL_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Western", PhilippineLocationData.LOCATION_DATA.WESTERN_VISAYAS_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Visayas", PhilippineLocationData.LOCATION_DATA.CENTRAL_VISAYAS_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Eastern", PhilippineLocationData.LOCATION_DATA.EASTERN_VISAYAS_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Zamboanga", PhilippineLocationData.LOCATION_DATA.ZAMBOANGA_PENINSULA_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Northern", PhilippineLocationData.LOCATION_DATA.NORTHERN_MINDANAO_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Davao", PhilippineLocationData.LOCATION_DATA.DAVAO_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("SOCCSKSARGEN", PhilippineLocationData.LOCATION_DATA.SOCCSKSARGEN_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("Caraga", PhilippineLocationData.LOCATION_DATA.CARAGA_MUNICIPALITIES);
        regionToMunicipalitiesMap.put("BARMM", PhilippineLocationData.LOCATION_DATA.BARMM_MUNICIPALITIES);
    }

    private void setupRegionCodeMap() {
        regionNameToCodeMap.put("NCR", "NCR");
        regionNameToCodeMap.put("National Capital Region", "NCR");
        regionNameToCodeMap.put("CAR", "CAR");
        regionNameToCodeMap.put("Cordillera", "CAR");
        regionNameToCodeMap.put("MIMAROPA", "MIMAROPA");
        regionNameToCodeMap.put("IV-B", "MIMAROPA");
        regionNameToCodeMap.put("CALABARZON", "CALABARZON");
        regionNameToCodeMap.put("IV-A", "CALABARZON");
        regionNameToCodeMap.put("SOCCSKSARGEN", "SOCCSKSARGEN");
        regionNameToCodeMap.put("XII", "SOCCSKSARGEN");
        regionNameToCodeMap.put("BARMM", "BARMM");
        regionNameToCodeMap.put("Bangsamoro", "BARMM");
        regionNameToCodeMap.put("Ilocos", "Ilocos");
        regionNameToCodeMap.put("I", "Ilocos");
        regionNameToCodeMap.put("Cagayan", "Cagayan");
        regionNameToCodeMap.put("II", "Cagayan");
        regionNameToCodeMap.put("Central Luzon", "Luzon");
        regionNameToCodeMap.put("III", "Luzon");
        regionNameToCodeMap.put("Bicol", "Bicol");
        regionNameToCodeMap.put("V", "Bicol");
        regionNameToCodeMap.put("Western Visayas", "Western");
        regionNameToCodeMap.put("VI", "Western");
        regionNameToCodeMap.put("Central Visayas", "Visayas");
        regionNameToCodeMap.put("VII", "Visayas");
        regionNameToCodeMap.put("Eastern Visayas", "Eastern");
        regionNameToCodeMap.put("VIII", "Eastern");
        regionNameToCodeMap.put("Zamboanga", "Zamboanga");
        regionNameToCodeMap.put("IX", "Zamboanga");
        regionNameToCodeMap.put("Northern Mindanao", "Northern");
        regionNameToCodeMap.put("X", "Northern");
        regionNameToCodeMap.put("Davao", "Davao");
        regionNameToCodeMap.put("XI", "Davao");
        regionNameToCodeMap.put("Caraga", "Caraga");
        regionNameToCodeMap.put("XIII", "Caraga");
    }

    private void setupSpinners() {
        // Initially create all spinners with placeholder values
        setupSpinnerWithPlaceholder(islandSpinner, "Select Island Group");
        setupSpinnerWithPlaceholder(regionSpinner, "Select Region");
        setupSpinnerWithPlaceholder(provinceSpinner, "Select Province");
        setupSpinnerWithPlaceholder(municipalitySpinner, "Select City/Municipality");

        // Initially enable only the first spinner
        islandSpinner.setEnabled(true);
        regionSpinner.setEnabled(false);
        provinceSpinner.setEnabled(false);
        municipalitySpinner.setEnabled(false);

        // Setup island spinner with data
        setupIslandSpinner();
    }

    private void setupSpinnerWithPlaceholder(Spinner spinner, String placeholder) {
        List<String> items = new ArrayList<>();
        items.add(placeholder);

        spinner.setAdapter(createHintAdapter(items));
    }

    private void setupIslandSpinner() {
        List<String> islandGroups = new ArrayList<>();
        islandGroups.add("Select Island Group");
        islandGroups.addAll(Arrays.asList(PhilippineLocationData.ISLAND_GROUPS));

        islandSpinner.setAdapter(createHintAdapter(islandGroups));

        islandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Reset dependent spinners
                resetDependentSpinners();

                if (position == 0) {
                    return; // No selection made
                }

                selectedIslandGroup = islandGroups.get(position);
                selectedIslandIndex = position - 1;  // -1 because of hint item

                populateRegionsByIsland();
                regionSpinner.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void resetDependentSpinners() {
        setupSpinnerWithPlaceholder(regionSpinner, "Select Region");
        setupSpinnerWithPlaceholder(provinceSpinner, "Select Province");
        setupSpinnerWithPlaceholder(municipalitySpinner, "Select City/Municipality");

        regionSpinner.setEnabled(false);
        provinceSpinner.setEnabled(false);
        municipalitySpinner.setEnabled(false);
    }

    private void populateRegionsByIsland() {
        String[] regions = PhilippineLocationData.ISLAND_TO_REGIONS[selectedIslandIndex];

        List<String> regionNames = new ArrayList<>();
        regionNames.add("Select Region");
        regionNames.addAll(Arrays.asList(regions));

        regionSpinner.setAdapter(createHintAdapter(regionNames));

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Reset dependent spinners
                setupSpinnerWithPlaceholder(provinceSpinner, "Select Province");
                setupSpinnerWithPlaceholder(municipalitySpinner, "Select City/Municipality");

                provinceSpinner.setEnabled(false);
                municipalitySpinner.setEnabled(false);

                if (position == 0) {
                    return; // No selection made
                }

                selectedRegionName = regionSpinner.getSelectedItem().toString();

                // Extract region code using the map
                extractRegionCode(selectedRegionName);

                // Handle NCR special case
                if ("NCR".equals(selectedRegionCode)) {
                    handleNCRSelection();
                } else {
                    populateProvinces();
                    provinceSpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void extractRegionCode(String fullRegionName) {
        // First try to find direct match in the map
        if (regionNameToCodeMap.containsKey(fullRegionName)) {
            selectedRegionCode = regionNameToCodeMap.get(fullRegionName);
            return;
        }

        // If no direct match, search for partial matches
        for (Map.Entry<String, String> entry : regionNameToCodeMap.entrySet()) {
            if (fullRegionName.contains(entry.getKey())) {
                selectedRegionCode = entry.getValue();
                return;
            }
        }

        // Default to the full name if no match found
        selectedRegionCode = fullRegionName;
    }

    private void handleNCRSelection() {
        // For NCR, disable province spinner and set special value
        List<String> noProvinceList = new ArrayList<>();
        noProvinceList.add("No Province in NCR");
        provinceSpinner.setAdapter(createNonSelectableAdapter(noProvinceList));
        provinceSpinner.setEnabled(false);

        // Update municipality spinner prompt for NCR
        setupSpinnerWithPlaceholder(municipalitySpinner, "Select City");

        // Populate NCR cities
        populateNCRCities();
        municipalitySpinner.setEnabled(true);
    }

    private void populateNCRCities() {
        List<String> cityNames = new ArrayList<>();
        cityNames.add("Select City");

        if (PhilippineLocationData.LOCATION_DATA.NCR_CITIES != null) {
            cityNames.addAll(Arrays.asList(PhilippineLocationData.LOCATION_DATA.NCR_CITIES));
        }

        municipalitySpinner.setAdapter(createHintAdapter(cityNames));
    }

    private void populateProvinces() {
        String[] provinces = regionToProvincesMap.getOrDefault(selectedRegionCode, new String[0]);

        List<String> provinceNames = new ArrayList<>();
        provinceNames.add("Select Province");
        provinceNames.addAll(Arrays.asList(provinces));

        provinceSpinner.setAdapter(createHintAdapter(provinceNames));

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setupSpinnerWithPlaceholder(municipalitySpinner, "Select City/Municipality");
                municipalitySpinner.setEnabled(false);

                if (position == 0) {
                    return; // No selection made
                }

                int selectedProvinceIndex = position - 1;  // -1 because of hint item
                populateMunicipalities(selectedProvinceIndex);
                municipalitySpinner.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void populateMunicipalities(int provinceIndex) {
        String[] municipalities = new String[0];

        try {
            String[][] municipalitiesArray = regionToMunicipalitiesMap.get(selectedRegionCode);
            if (municipalitiesArray != null && provinceIndex < municipalitiesArray.length) {
                municipalities = municipalitiesArray[provinceIndex];
            }
        } catch (Exception e) {
            Log.e(TAG, "Error accessing municipalities: " + e.getMessage());
        }

        List<String> municipalityNames = new ArrayList<>();
        municipalityNames.add("Select City/Municipality");

        if (municipalities != null) {
            municipalityNames.addAll(Arrays.asList(municipalities));
        }

        municipalitySpinner.setAdapter(createHintAdapter(municipalityNames));
    }

    private ArrayAdapter<String> createHintAdapter(List<String> items) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // disable first item
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorCard1) : (R.color.colorPrimaryText1)));
                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change this color as desired
                tv.setTextColor(getResources().getColor(position == 0 ?
                        (R.color.colorSecondaryText1): (R.color.colorPrimaryText1)));
                return view;
            }
        };
    }

    private ArrayAdapter<String> createNonSelectableAdapter(List<String> items) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                return false; // disable all items
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                return view;
            }
        };
    }

    private void setupBudgetEditText() {
        budgetDisplay.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;

                isEditing = true;

                // Remove all formatting characters (commas and periods)
                String str = s.toString().replaceAll("[,.]", "");

                if (str.isEmpty()) {
                    isEditing = false;
                    return;
                }

                try {
                    // Parse as long to avoid floating point issues
                    long value = Long.parseLong(str);

                    // Format as a whole number with commas
                    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                    formatter.setGroupingUsed(true); // Enable commas for thousands

                    // Format the whole number part
                    String wholeNumber = formatter.format(value / 100);

                    // Get the decimal part (last 2 digits)
                    String decimal = String.format("%02d", value % 100);

                    // Combine with decimal point
                    String formatted = wholeNumber + "." + decimal;

                    // Replace the text with formatted value
                    s.replace(0, s.length(), formatted);
                    current = str;
                } catch (NumberFormatException e) {
                    // If parsing fails and the string is not empty, show at least "0.00"
                    if (!str.isEmpty()) {
                        s.replace(0, s.length(), "0.00");
                    }
                }

                isEditing = false;
            }
        });
    }

    private void setupDoneButton() {
        doneButton.setOnClickListener(v -> {
            if (validateSelections()) {
                saveSelections();
                navigateToMainScreen();
            }

        });
    }

    private boolean validateSelections() {
        // Check island group selection
        if (islandSpinner.getSelectedItemPosition() == 0) {
            showToast("Please select an island group");
            islandSpinner.requestFocus();
            return false;
        }

        // Check region selection
        if (regionSpinner.getSelectedItemPosition() == 0) {
            showToast("Please select a region");
            regionSpinner.requestFocus();
            return false;
        }

        // For NCR, skip province validation
        boolean isNCR = "NCR".equals(selectedRegionCode);

        // Check province only if it's enabled and not NCR
        if (!isNCR && provinceSpinner.isEnabled() && provinceSpinner.getSelectedItemPosition() == 0) {
            showToast("Please select a province");
            provinceSpinner.requestFocus();
            return false;
        }

        // Check municipality/city selection
        if (municipalitySpinner.isEnabled() && municipalitySpinner.getSelectedItemPosition() == 0) {
            String entityType = isNCR ? "city" : "city/municipality";
            showToast("Please select a " + entityType);
            municipalitySpinner.requestFocus();
            return false;
        }

        // Check budget
        String budget = budgetDisplay.getText().toString().trim();
        if (budget.isEmpty() || budget.equals("0.00")) {
            showToast("Please enter your budget");
            budgetDisplay.requestFocus();
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveSelections() {
        // Get user email
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");

        if (userEmail.isEmpty()) {
            Log.e(TAG, "Error: User email is empty, cannot save selections");
            return;
        }

        // Get values to save - clean up the decimal format for storing in the database
        String displayBudget = budgetDisplay.getText().toString();
        Log.d(TAG, "Original budget input: " + displayBudget);

        // Clean the budget string - remove commas but keep decimal point
        String budget = displayBudget.replaceAll("[,]", "");
        Log.d(TAG, "Cleaned budget for DB storage: " + budget);

        String island = islandSpinner.getSelectedItemPosition() > 0 ? islandSpinner.getSelectedItem().toString() : "";
        String region = regionSpinner.getSelectedItemPosition() > 0 ? regionSpinner.getSelectedItem().toString() : "";

        // Handle province for NCR
        boolean isNCR = "NCR".equals(selectedRegionCode);
        String province = "";

        if (!isNCR && provinceSpinner.isEnabled() && provinceSpinner.getSelectedItemPosition() > 0) {
            province = provinceSpinner.getSelectedItem().toString();
        } else if (isNCR) {
            province = "NCR";
        }

        String municipality = municipalitySpinner.getSelectedItemPosition() > 0 ?
                municipalitySpinner.getSelectedItem().toString() : "";

        // Save to database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean success = dbHelper.saveUserPreferences(
                userEmail, budget, island, region, selectedRegionCode, province, municipality);

        if (success) {
            // Update setup status
            dbHelper.updateUserSetupStatus(userEmail, true);

            // Update SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isSetupComplete", true);
            editor.apply();
        } else {
            showToast("Failed to save your preferences. Please try again.");
        }
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(BudgetAndLocationActivity.this, MainScreenActivity.class);
        startActivity(intent);
        finish();
    }
}