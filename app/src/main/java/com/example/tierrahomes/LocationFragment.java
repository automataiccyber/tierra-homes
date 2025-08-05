package com.example.tierrahomes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LocationFragment extends Fragment {

    private static final String TAG = "LocationFragment";

    // UI components
    private Spinner islandSpinner, regionSpinner, provinceSpinner, municipalitySpinner;
    private Button changeButton;
    private TextView locationPlaceholder;

    // Selection data
    // With other member variables
    private String selectedMunicipality = "";
    private String selectedRegionName = "";
    private String selectedRegionCode = "";
    private String selectedIslandGroup = "";
    private int selectedIslandIndex = -1;

    // Maps for efficient data lookup
    private final Map<String, String[]> regionToProvincesMap = new HashMap<>();
    private final Map<String, String[][]> regionToMunicipalitiesMap = new HashMap<>();
    private final Map<String, String> regionNameToCodeMap = new HashMap<>();

    // Current user location data
    private String currentIsland = "";
    private String currentRegion = "";
    private String currentProvince = "";
    private String currentMunicipality = "";
    private String currentBudget = "0";

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.location_fragment, container, false);

        initializeUIComponents(view);
        initializeDataMaps();
        loadCurrentLocation();
        setupSpinners();
        setupChangeButton();

        return view;
    }

    private void initializeUIComponents(View view) {
        islandSpinner = view.findViewById(R.id.spinner_island);
        regionSpinner = view.findViewById(R.id.spinner_region);
        provinceSpinner = view.findViewById(R.id.spinner_province);
        municipalitySpinner = view.findViewById(R.id.spinner_municipality);
        changeButton = view.findViewById(R.id.change);
        locationPlaceholder = view.findViewById(R.id.location_placeholder);
    }

    private void loadCurrentLocation() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        DatabaseHelper.UserPreferences userPreferences = dbHelper.getUserPreferences(userEmail);

        if (userPreferences != null) {
            currentIsland = userPreferences.getIsland() != null ? userPreferences.getIsland() : "";
            currentRegion = userPreferences.getRegion() != null ? userPreferences.getRegion() : "";
            currentProvince = userPreferences.getProvince() != null ? userPreferences.getProvince() : "";
            currentMunicipality = userPreferences.getMunicipality() != null ? userPreferences.getMunicipality() : "";
            currentBudget = userPreferences.getBudget() != null ? userPreferences.getBudget() : "0";

            // Update the location display
            updateLocationDisplay();
        }
    }

    private void updateLocationDisplay() {
        String locationText = String.format("%s, %s\n%s, %s",
                currentIsland, currentRegion, currentProvince, currentMunicipality);
        locationPlaceholder.setText(locationText);
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

        // Remove these array declarations in populateRegionsByIsland()
        // final String[] selectedRegionName = new String[1];
        // final String[] selectedMunicipality = new String[1];

        // Modify the region spinner listener
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedRegionName = "";
                    return;
                }
                selectedRegionName = parent.getItemAtPosition(position).toString();
                extractRegionCode(selectedRegionName);

                // Reset dependent spinners
                setupSpinnerWithPlaceholder(provinceSpinner, "Select Province");
                setupSpinnerWithPlaceholder(municipalitySpinner, "Select City/Municipality");

                if ("NCR".equals(selectedRegionCode)) {
                    handleNCRSelection();
                } else {
                    populateProvinces();
                    provinceSpinner.setEnabled(true);
                }
                Log.d(TAG, "Region selected: " + selectedRegionName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRegionName = "";
            }
        });

        // Modify the municipality spinner listener
        municipalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedMunicipality = "";
                    return;
                }
                selectedMunicipality = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Municipality selected: " + selectedMunicipality);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMunicipality = "";
            }
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
        return new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
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
        return new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
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

    private void setupChangeButton() {
        changeButton.setOnClickListener(v -> {
            if (validateSelections()) {
                saveLocationChanges();
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

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveLocationChanges() {
        // Get user email
        SharedPreferences preferences = requireActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");
        // In the method where you save the location selection:
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("build_preferences", MODE_PRIVATE).edit();
        editor.putString("current_region", selectedRegionName);
        editor.putString("current_municipality", selectedMunicipality);
        editor.apply();
        Log.d(TAG, "Saved location: " + selectedRegionName + ", " + selectedMunicipality);

        if (userEmail.isEmpty()) {
            Log.e(TAG, "Error: User email is empty, cannot save selections");
            return;
        }

        // Get values to save
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

        // Save to database using the existing saveUserPreferences method
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        boolean success = dbHelper.saveUserPreferences(
                userEmail, currentBudget, island, region, selectedRegionCode, province, municipality);

        if (success) {
            // Update current location variables
            currentIsland = island;
            currentRegion = region;
            currentProvince = province;
            currentMunicipality = municipality;

            // Update the location display
            updateLocationDisplay();

            showToast("Location updated successfully");

            // Navigate to HomeFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new HomeFragment())
                    .commit();
        } else {
            showToast("Failed to update your location. Please try again.");
        }
    }

    public String getCurrentLocationCategory() {
        // Load classification data if not already loaded
        LocationClassifier.loadClassificationData(requireContext());

        // Get the category based on current region and municipality
        return LocationClassifier.getLocationCategory(currentRegion, currentMunicipality);
    }
}

