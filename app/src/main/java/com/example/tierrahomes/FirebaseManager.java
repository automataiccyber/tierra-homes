package com.example.tierrahomes;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {
    private static final String USERS_LOCATION_REF = "users_locations";
    private static final String HOUSE_SALES_REF = "house_sales";

    private final DatabaseReference databaseRef;

    public FirebaseManager() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void updateUserLocation(String userEmail, String deviceId, String island, String region,
                                 String province, String municipality) {
        // Create a unique key combining email and device ID
        String userDeviceKey = sanitizeEmail(userEmail) + "_" + deviceId;

        DatabaseReference userLocRef = databaseRef.child(USERS_LOCATION_REF)
                .child(userDeviceKey);

        UserLocation location = new UserLocation(island, region, province, municipality, deviceId);
        userLocRef.setValue(location);
    }

    private String sanitizeEmail(String email) {
        return email.replace(".", ",").replace("@", "_");
    }

    public static class UserLocation {
        public String island;
        public String region;
        public String province;
        public String municipality;
        public String deviceId;

        public UserLocation() {}

        public UserLocation(String island, String region, String province, String municipality, String deviceId) {
            this.island = island;
            this.region = region;
            this.province = province;
            this.municipality = municipality;
            this.deviceId = deviceId;
        }
    }

    public void resetAllUserLocations() {
        // Remove all data from users_locations node
        databaseRef.child(USERS_LOCATION_REF).removeValue();
    }
    public void incrementHouseSales(String houseType) {
        Log.d("FirebaseManager", "Attempting to increment sales for: " + houseType);
        
        DatabaseReference salesRef = databaseRef.child(HOUSE_SALES_REF)
                .child(houseType);

        salesRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                Log.d("FirebaseManager", "Current value for " + houseType + ": " + currentValue);
                
                if (currentValue == null) {
                    mutableData.setValue(1);
                    Log.d("FirebaseManager", "Setting initial value to 1 for " + houseType);
                } else {
                    mutableData.setValue(currentValue + 1);
                    Log.d("FirebaseManager", "Incrementing value from " + currentValue + " to " + (currentValue + 1) + " for " + houseType);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e("FirebaseManager", "Failed to increment house sales for " + houseType, databaseError.toException());
                } else {
                    Log.d("FirebaseManager", "Successfully incremented sales for " + houseType + ". Committed: " + committed);
                }
            }
        });
    }

    public void getHouseSales(String houseType, HouseSalesCallback callback) {
        Log.d("FirebaseManager", "Getting house sales for: " + houseType);
        DatabaseReference salesRef = databaseRef.child(HOUSE_SALES_REF)
                .child(houseType);

        salesRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer sales = dataSnapshot.getValue(Integer.class);
                Log.d("FirebaseManager", "Data received for " + houseType + ": " + sales);
                callback.onSalesReceived(houseType, sales != null ? sales : 0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseManager", "Failed to get house sales for " + houseType, databaseError.toException());
                callback.onSalesReceived(houseType, 0);
            }
        });
    }

    public void getAllHouseSales(HouseSalesMapCallback callback) {
        Log.d("FirebaseManager", "Getting all house sales data");
        DatabaseReference salesRef = databaseRef.child(HOUSE_SALES_REF);

        salesRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("FirebaseManager", "All house sales data received. Children count: " + dataSnapshot.getChildrenCount());
                Map<String, Integer> salesMap = new HashMap<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String houseType = child.getKey();
                    Integer sales = child.getValue(Integer.class);
                    if (houseType != null && sales != null) {
                        salesMap.put(houseType, sales);
                        Log.d("FirebaseManager", "House: " + houseType + " = " + sales);
                    }
                }
                Log.d("FirebaseManager", "Total houses in map: " + salesMap.size());
                callback.onSalesMapReceived(salesMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseManager", "Failed to get all house sales", databaseError.toException());
                callback.onSalesMapReceived(new HashMap<>());
            }
        });
    }

    public void initializeHouseSales() {
        Log.d("FirebaseManager", "Starting house sales initialization...");
        
        // Urban Properties
        String[] urbanHouses = {"APARTMENT", "CONDOMINIUM", "LOFT", "PENTHOUSE", "TOWNHOUSE", "STUDIO FLAT"};
        // Suburban Properties
        String[] suburbanHouses = {"DETACHED HOUSE", "SEMI-DETACHED", "DUPLEX", "BUNGALOW", "SPLIT-LEVEL HOME", "MCMANSION"};
        // Rural Properties
        String[] ruralHouses = {"FARMHOUSE", "COTTAGE", "CABIN", "RANCH HOUSE", "HOMESTEAD", "BARN CONVERSION"};

        Map<String, Integer> initialSales = new HashMap<>();
        
        // Initialize all house types with 0 sales
        for (String house : urbanHouses) {
            initialSales.put(house, 0);
            Log.d("FirebaseManager", "Added urban house: " + house);
        }
        for (String house : suburbanHouses) {
            initialSales.put(house, 0);
            Log.d("FirebaseManager", "Added suburban house: " + house);
        }
        for (String house : ruralHouses) {
            initialSales.put(house, 0);
            Log.d("FirebaseManager", "Added rural house: " + house);
        }

        Log.d("FirebaseManager", "Setting Firebase data with " + initialSales.size() + " house types");
        databaseRef.child(HOUSE_SALES_REF).setValue(initialSales)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseManager", "House sales initialization successful");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseManager", "House sales initialization failed", e);
                });
    }

    public interface HouseSalesCallback {
        void onSalesReceived(String houseType, int sales);
    }

    public interface HouseSalesMapCallback {
        void onSalesMapReceived(Map<String, Integer> salesMap);
    }
}