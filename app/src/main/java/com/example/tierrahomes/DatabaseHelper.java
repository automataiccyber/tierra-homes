package com.example.tierrahomes;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.example.tierrahomes.NetworkUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "tierra_homes.db";
    private static final int DATABASE_VERSION = 2;
    private Context context = null;  // Add this field
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;  // Store the context
    }
    

    // Tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_USER_PREFERENCES = "user_preferences";

    // User table columns
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_SETUP_COMPLETE = "setup_complete";

    // User preferences table columns
    private static final String COLUMN_PREF_ID = "id";
    private static final String COLUMN_PREF_USER_EMAIL = "user_email";
    private static final String COLUMN_PREF_BUDGET = "budget";
    private static final String COLUMN_PREF_ISLAND = "island";
    private static final String COLUMN_PREF_REGION = "region";
    private static final String COLUMN_PREF_REGION_CODE = "region_code";
    private static final String COLUMN_PREF_PROVINCE = "province";
    private static final String COLUMN_PREF_MUNICIPALITY = "municipality";
    private static final String COLUMN_PREF_LAST_UPDATED = "last_updated";
    private static final String TABLE_HOUSE_SALES = "house_sales";
    private static final String COLUMN_HOUSE_TYPE = "house_type";
    private static final String COLUMN_SALES_COUNT = "sales_count";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table with username field
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_SETUP_COMPLETE + " INTEGER DEFAULT 0"
                + ")";

        // Create user preferences table
        String CREATE_PREFERENCES_TABLE = "CREATE TABLE " + TABLE_USER_PREFERENCES + "("
                + COLUMN_PREF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PREF_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PREF_BUDGET + " TEXT,"
                + COLUMN_PREF_ISLAND + " TEXT,"
                + COLUMN_PREF_REGION + " TEXT,"
                + COLUMN_PREF_REGION_CODE + " TEXT,"
                + COLUMN_PREF_PROVINCE + " TEXT,"
                + COLUMN_PREF_MUNICIPALITY + " TEXT,"
                + COLUMN_PREF_LAST_UPDATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_PREF_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_EMAIL + ")"
                + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PREFERENCES_TABLE);

        String CREATE_HOUSE_SALES_TABLE = "CREATE TABLE " + TABLE_HOUSE_SALES + "("
                + COLUMN_HOUSE_TYPE + " TEXT PRIMARY KEY,"
                + COLUMN_SALES_COUNT + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_HOUSE_SALES_TABLE);

        // Initialize sales data
        initializeHouseSales(db);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                // Add the setup_complete column to existing users table if it doesn't exist
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " +
                        COLUMN_SETUP_COMPLETE + " INTEGER DEFAULT 0");
                Log.i(TAG, "Added setup_complete column to users table");
            } catch (Exception e) {
                // Column might already exist
                Log.e(TAG, "Error adding column: " + e.getMessage());
            }

            // Add the preferences table if upgrading from version 1
            String CREATE_PREFERENCES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_PREFERENCES + "("
                    + COLUMN_PREF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PREF_USER_EMAIL + " TEXT UNIQUE,"
                    + COLUMN_PREF_BUDGET + " TEXT,"
                    + COLUMN_PREF_ISLAND + " TEXT,"
                    + COLUMN_PREF_REGION + " TEXT,"
                    + COLUMN_PREF_REGION_CODE + " TEXT,"
                    + COLUMN_PREF_PROVINCE + " TEXT,"
                    + COLUMN_PREF_MUNICIPALITY + " TEXT,"
                    + COLUMN_PREF_LAST_UPDATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY(" + COLUMN_PREF_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_EMAIL + ")"
                    + ")";
            db.execSQL(CREATE_PREFERENCES_TABLE);
            
            // Add house_sales table
            String CREATE_HOUSE_SALES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_HOUSE_SALES + "("
                    + COLUMN_HOUSE_TYPE + " TEXT PRIMARY KEY,"
                    + COLUMN_SALES_COUNT + " INTEGER DEFAULT 0"
                    + ")";
            db.execSQL(CREATE_HOUSE_SALES_TABLE);

            // Initialize the sales data
            initializeHouseSales(db);

            Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
        }
    }
    
    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    
    private void createHouseSalesTable(SQLiteDatabase db) {
        String CREATE_HOUSE_SALES_TABLE = "CREATE TABLE " + TABLE_HOUSE_SALES + "("
                + COLUMN_HOUSE_TYPE + " TEXT PRIMARY KEY,"
                + COLUMN_SALES_COUNT + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_HOUSE_SALES_TABLE);
        
        // Initialize the sales data
        initializeHouseSales(db);
        Log.d(TAG, "House sales table created");
    }
    public int getHouseSales(String houseType) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Check if table exists, if not create it
        if (!tableExists(db, TABLE_HOUSE_SALES)) {
            createHouseSalesTable(db);
        }
        
        Cursor cursor = db.query(TABLE_HOUSE_SALES,
                new String[]{COLUMN_SALES_COUNT},
                COLUMN_HOUSE_TYPE + " = ?",
                new String[]{houseType},
                null, null, null);

        int sales = 0;
        if (cursor.moveToFirst()) {
            sales = cursor.getInt(0);
        }
        cursor.close();
        return sales;
    }

    public void getHouseSalesFromFirebase(String houseType, FirebaseManager.HouseSalesCallback callback) {
        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.getHouseSales(houseType, callback);
    }

    public void getAllHouseSalesFromFirebase(FirebaseManager.HouseSalesMapCallback callback) {
        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.getAllHouseSales(callback);
    }

    public boolean incrementHouseSales(String houseType) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if table exists, if not create it
        if (!tableExists(db, TABLE_HOUSE_SALES)) {
            createHouseSalesTable(db);
        }
        
        int currentSales = getHouseSales(houseType);

        ContentValues values = new ContentValues();
        values.put(COLUMN_SALES_COUNT, currentSales + 1);

        int rowsAffected = db.update(TABLE_HOUSE_SALES,
                values,
                COLUMN_HOUSE_TYPE + " = ?",
                new String[]{houseType});

        // Check network availability
        if (rowsAffected > 0) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Network is available, update Firebase immediately
                Log.d("DatabaseHelper", "Network available, updating Firebase for: " + houseType);
                FirebaseManager firebaseManager = new FirebaseManager();
                firebaseManager.incrementHouseSales(houseType);
            } else {
                // Network is offline, add to queue for later sync
                Log.d("DatabaseHelper", "Network offline, adding to queue for: " + houseType);
                NetworkUtils.OfflineQueue.addToQueue(context, houseType);
            }
        } else {
            Log.e("DatabaseHelper", "Failed to update local sales for: " + houseType);
        }

        return rowsAffected > 0;
    }


    private void initializeHouseSales(SQLiteDatabase db) {
        // Urban Properties
        String[] urbanHouses = {"APARTMENT", "CONDOMINIUM", "LOFT", "PENTHOUSE", "TOWNHOUSE", "STUDIO FLAT"};
        // Suburban Properties
        String[] suburbanHouses = {"DETACHED HOUSE", "SEMI-DETACHED", "DUPLEX", "BUNGALOW", "SPLIT-LEVEL HOME", "MCMANSION"};
        // Rural Properties
        String[] ruralHouses = {"FARMHOUSE", "COTTAGE", "CABIN", "RANCH HOUSE", "HOMESTEAD", "BARN CONVERSION"};

        for (String house : urbanHouses) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_HOUSE_TYPE, house);
            values.put(COLUMN_SALES_COUNT, 0);
            db.insert(TABLE_HOUSE_SALES, null, values);
        }
        // Do the same for suburban and rural houses
        for (String house : suburbanHouses) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_HOUSE_TYPE, house);
            values.put(COLUMN_SALES_COUNT, 0);
            db.insert(TABLE_HOUSE_SALES, null, values);
        }
        for (String house : ruralHouses) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_HOUSE_TYPE, house);
            values.put(COLUMN_SALES_COUNT, 0);
            db.insert(TABLE_HOUSE_SALES, null, values);
        }
    }

    // User Registration Method
    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_SETUP_COMPLETE, 0); // Default to not setup

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // returns true if insert is successful
    }

    // User Login Method
    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if email exists
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_EMAIL + " = ?", new String[]{email},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Check if username exists
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?", new String[]{username},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Get old password for comparison
    public String getOldPassword(String email, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_PASSWORD + " FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, username});

        String password = null;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_USER_PASSWORD);
            if (columnIndex != -1) {
                password = cursor.getString(columnIndex);
            }
        }

        cursor.close();
        return password;
    }

    // Reset password
    public boolean resetPassword(String email, String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the user with the specified email and username exists
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, username});

        if (cursor.moveToFirst()) {
            // User exists, update the password
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_PASSWORD, newPassword);

            int rowsAffected = db.update(TABLE_USERS, values,
                    COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USERNAME + " = ?",
                    new String[]{email, username});
            cursor.close();
            return rowsAffected > 0; // returns true if the password was updated successfully
        }

        cursor.close();
        return false; // returns false if the email and username don't match
    }

    /**
     * Check if setup is complete for a specific user
     * @param email The user's email
     * @return true if setup is complete, false otherwise
     */
    public boolean isUserSetupComplete(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            // Add logging for debugging
            Log.d(TAG, "Checking setup status for: " + email);
            Cursor cursor = db.rawQuery("SELECT " + COLUMN_SETUP_COMPLETE +
                            " FROM " + TABLE_USERS +
                            " WHERE " + COLUMN_USER_EMAIL + " = ?",
                    new String[]{email});

            boolean isComplete = false;
            if (cursor.moveToFirst()) {
                isComplete = cursor.getInt(0) == 1;
                Log.d(TAG, "Found setup status in database: " + isComplete);
            } else {
                Log.d(TAG, "User not found in database: " + email);
            }

            cursor.close();
            return isComplete;
        } catch (Exception e) {
            Log.e(TAG, "Error checking setup status: " + e.getMessage());
            e.printStackTrace(); // Added stack trace for better debugging
            // If the column doesn't exist, assume setup is not complete
            return false;
        }
    }

    /**
     * Update user setup status in database
     * @param email The user's email
     * @param isComplete Whether setup is complete
     * @return boolean indicating if update was successful
     */
    public boolean updateUserSetupStatus(String email, boolean isComplete) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SETUP_COMPLETE, isComplete ? 1 : 0);

        try {
            Log.d(TAG, "Updating setup status for " + email + " to " + isComplete);
            // First verify the user exists
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ?",
                    new String[]{email});

            if (cursor.getCount() > 0) {
                int rows = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + " = ?", new String[]{email});
                Log.d(TAG, "Updated setup status: " + rows + " rows affected");
                cursor.close();
                return rows > 0;
            } else {
                Log.e(TAG, "User not found, can't update setup status: " + email);
                cursor.close();
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating setup status: " + e.getMessage());
            e.printStackTrace(); // Added stack trace for better debugging
            return false;
        }
    }

    // Method to save or update user preferences
    public boolean saveUserPreferences(String email, String budget, String island, String region,
                                       String regionCode, String province, String municipality) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PREF_USER_EMAIL, email);
        values.put(COLUMN_PREF_BUDGET, budget);
        values.put(COLUMN_PREF_ISLAND, island);
        values.put(COLUMN_PREF_REGION, region);
        values.put(COLUMN_PREF_REGION_CODE, regionCode);
        values.put(COLUMN_PREF_PROVINCE, province);
        values.put(COLUMN_PREF_MUNICIPALITY, municipality);

        // Check if the user already has preferences
        Cursor cursor = db.query(TABLE_USER_PREFERENCES, new String[]{COLUMN_PREF_ID},
                COLUMN_PREF_USER_EMAIL + " = ?", new String[]{email}, null, null, null);

        int rowsAffected;
        if (cursor.getCount() > 0) {
            // Update existing record
            rowsAffected = db.update(TABLE_USER_PREFERENCES, values, COLUMN_PREF_USER_EMAIL + " = ?", new String[]{email});
            Log.d(TAG, "Updated preferences for user: " + email);
        } else {
            // Insert new record
            rowsAffected = db.insert(TABLE_USER_PREFERENCES, null, values) > 0 ? 1 : 0;
            Log.d(TAG, "Inserted new preferences for user: " + email);
        }

        cursor.close();
        db.close();

        boolean success = rowsAffected > 0;
        
        if (success) {
            // Sync with Firebase
            // Get device ID using the stored context
            String deviceId = Settings.Secure.getString(
                    this.context.getContentResolver(),  // Use the stored context
                    Settings.Secure.ANDROID_ID
            );
            FirebaseManager firebaseManager = new FirebaseManager();
            firebaseManager.updateUserLocation(email, deviceId, island, region, province, municipality);
        }

        return success;
    }

    // Method to get user preferences
    public UserPreferences getUserPreferences(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserPreferences preferences = null;

        Cursor cursor = db.query(TABLE_USER_PREFERENCES, null, COLUMN_PREF_USER_EMAIL + " = ?",
                new String[]{email}, null, null, null);

        if (cursor.moveToFirst()) {
            preferences = new UserPreferences();

            int emailIndex = cursor.getColumnIndex(COLUMN_PREF_USER_EMAIL);
            int budgetIndex = cursor.getColumnIndex(COLUMN_PREF_BUDGET);
            int islandIndex = cursor.getColumnIndex(COLUMN_PREF_ISLAND);
            int regionIndex = cursor.getColumnIndex(COLUMN_PREF_REGION);
            int regionCodeIndex = cursor.getColumnIndex(COLUMN_PREF_REGION_CODE);
            int provinceIndex = cursor.getColumnIndex(COLUMN_PREF_PROVINCE);
            int municipalityIndex = cursor.getColumnIndex(COLUMN_PREF_MUNICIPALITY);

            if (emailIndex >= 0) preferences.setUserEmail(cursor.getString(emailIndex));
            if (budgetIndex >= 0) preferences.setBudget(cursor.getString(budgetIndex));
            if (islandIndex >= 0) preferences.setIsland(cursor.getString(islandIndex));
            if (regionIndex >= 0) preferences.setRegion(cursor.getString(regionIndex));
            if (regionCodeIndex >= 0) preferences.setRegionCode(cursor.getString(regionCodeIndex));
            if (provinceIndex >= 0) preferences.setProvince(cursor.getString(provinceIndex));
            if (municipalityIndex >= 0) preferences.setMunicipality(cursor.getString(municipalityIndex));

            Log.d(TAG, "Retrieved preferences for user: " + email);
        } else {
            Log.d(TAG, "No preferences found for user: " + email);
        }

        cursor.close();
        db.close();

        return preferences;
    }

    public Map<String, Integer> getUserCountByIsland() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> islandCounts = new HashMap<>();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PREF_ISLAND + ", COUNT(*) as count " +
                        "FROM " + TABLE_USER_PREFERENCES +
                        " GROUP BY " + COLUMN_PREF_ISLAND, null);

        while(cursor.moveToNext()) {
            String island = cursor.getString(0);
            int count = cursor.getInt(1);
            if (island != null && !island.isEmpty()) {
                islandCounts.put(island, count);
            }
        }
        cursor.close();
        return islandCounts;
    }

    public Map<String, Integer> getUserCountByRegion(String island) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> regionCounts = new HashMap<>();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PREF_REGION + ", COUNT(*) as count " +
                        "FROM " + TABLE_USER_PREFERENCES +
                        " WHERE " + COLUMN_PREF_ISLAND + " = ? " +
                        " GROUP BY " + COLUMN_PREF_REGION,
                new String[]{island});

        while(cursor.moveToNext()) {
            String region = cursor.getString(0);
            int count = cursor.getInt(1);
            if (region != null && !region.isEmpty()) {
                regionCounts.put(region, count);
            }
        }
        cursor.close();
        return regionCounts;
    }

    public Map<String, Integer> getUserCountByProvince(String region) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> provinceCounts = new HashMap<>();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PREF_PROVINCE + ", COUNT(*) as count " +
                        "FROM " + TABLE_USER_PREFERENCES +
                        " WHERE " + COLUMN_PREF_REGION + " = ? " +
                        " GROUP BY " + COLUMN_PREF_PROVINCE,
                new String[]{region});

        while(cursor.moveToNext()) {
            String province = cursor.getString(0);
            int count = cursor.getInt(1);
            if (province != null && !province.isEmpty()) {
                provinceCounts.put(province, count);
            }
        }
        cursor.close();
        return provinceCounts;
    }

    public Map<String, Integer> getUserCountByMunicipality(String province) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> municipalityCounts = new HashMap<>();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PREF_MUNICIPALITY + ", COUNT(*) as count " +
                        "FROM " + TABLE_USER_PREFERENCES +
                        " WHERE " + COLUMN_PREF_PROVINCE + " = ? " +
                        " GROUP BY " + COLUMN_PREF_MUNICIPALITY,
                new String[]{province});

        while(cursor.moveToNext()) {
            String municipality = cursor.getString(0);
            int count = cursor.getInt(1);
            if (municipality != null && !municipality.isEmpty()) {
                municipalityCounts.put(municipality, count);
            }
        }
        cursor.close();
        return municipalityCounts;
    }

    public String getParentIsland(String region) {
        SQLiteDatabase db = this.getReadableDatabase();
        String island = null;

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PREF_ISLAND +
                        " FROM " + TABLE_USER_PREFERENCES +
                        " WHERE " + COLUMN_PREF_REGION + " = ? " +
                        " LIMIT 1",
                new String[]{region});

        if (cursor.moveToFirst()) {
            island = cursor.getString(0);
        }
        cursor.close();
        return island;
    }

    public String getParentRegion(String province) {
        SQLiteDatabase db = this.getReadableDatabase();
        String region = null;

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PREF_REGION +
                        " FROM " + TABLE_USER_PREFERENCES +
                        " WHERE " + COLUMN_PREF_PROVINCE + " = ? " +
                        " LIMIT 1",
                new String[]{province});

        if (cursor.moveToFirst()) {
            region = cursor.getString(0);
        }
        cursor.close();
        return region;
    }

    public String getParentProvince(String municipality) {
        SQLiteDatabase db = this.getReadableDatabase();
        String province = null;

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PREF_PROVINCE +
                        " FROM " + TABLE_USER_PREFERENCES +
                        " WHERE " + COLUMN_PREF_MUNICIPALITY + " = ? " +
                        " LIMIT 1",
                new String[]{municipality});

        if (cursor.moveToFirst()) {
            province = cursor.getString(0);
        }
        cursor.close();
        return province;
    }

    // Inner class to store user preferences
    public static class UserPreferences implements Cursor {
        private String userEmail;
        private String budget;
        private String island;
        private String region;
        private String regionCode;
        private String province;
        private String municipality;

        // Getters and setters
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public String getBudget() { return budget; }
        public void setBudget(String budget) { this.budget = budget; }

        public String getIsland() { return island; }
        public void setIsland(String island) { this.island = island; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getRegionCode() { return regionCode; }
        public void setRegionCode(String regionCode) { this.regionCode = regionCode; }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getMunicipality() { return municipality; }
        public void setMunicipality(String municipality) { this.municipality = municipality; }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public int getPosition() {
            return 0;
        }

        @Override
        public boolean move(int i) {
            return false;
        }

        @Override
        public boolean moveToPosition(int i) {
            return false;
        }

        @Override
        public boolean moveToFirst() {
            return false;
        }

        @Override
        public boolean moveToLast() {
            return false;
        }

        @Override
        public boolean moveToNext() {
            return false;
        }

        @Override
        public boolean moveToPrevious() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return false;
        }

        @Override
        public boolean isLast() {
            return false;
        }

        @Override
        public boolean isBeforeFirst() {
            return false;
        }

        @Override
        public boolean isAfterLast() {
            return false;
        }

        @Override
        public int getColumnIndex(String s) {
            return 0;
        }

        @Override
        public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public String getColumnName(int i) {
            return "";
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public byte[] getBlob(int i) {
            return new byte[0];
        }

        @Override
        public String getString(int i) {
            return "";
        }

        @Override
        public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {

        }

        @Override
        public short getShort(int i) {
            return 0;
        }

        @Override
        public int getInt(int i) {
            return 0;
        }

        @Override
        public long getLong(int i) {
            return 0;
        }

        @Override
        public float getFloat(int i) {
            return 0;
        }

        @Override
        public double getDouble(int i) {
            return 0;
        }

        @SuppressLint("WrongConstant")
        @Override
        public int getType(int i) {
            return 0;
        }

        @Override
        public boolean isNull(int i) {
            return false;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public boolean requery() {
            return false;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void registerContentObserver(ContentObserver contentObserver) {

        }

        @Override
        public void unregisterContentObserver(ContentObserver contentObserver) {

        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void setNotificationUri(ContentResolver contentResolver, Uri uri) {

        }

        @Override
        public Uri getNotificationUri() {
            return null;
        }

        @Override
        public boolean getWantsAllOnMoveCalls() {
            return false;
        }

        @Override
        public void setExtras(Bundle bundle) {

        }

        @Override
        public Bundle getExtras() {
            return null;
        }

        @Override
        public Bundle respond(Bundle bundle) {
            return null;
        }
    }
    public void resetAllDatabases() {
        // Reset SQLite database
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOUSE_SALES);

        // Recreate tables
        onCreate(db);

        // Reset Firebase database
        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.resetAllUserLocations();
    }
    
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
        context.deleteDatabase(DATABASE_NAME);
        Log.d(TAG, "Database cleared and will be recreated");
    }

    public void syncOfflinePurchases() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d("DatabaseHelper", "Network still unavailable, skipping sync");
            return;
        }
        
        List<String> offlineQueue = NetworkUtils.OfflineQueue.getQueue(context);
        if (offlineQueue.isEmpty()) {
            Log.d("DatabaseHelper", "No offline purchases to sync");
            return;
        }
        
        Log.d("DatabaseHelper", "Syncing " + offlineQueue.size() + " offline purchases");
        FirebaseManager firebaseManager = new FirebaseManager();
        
        for (String houseType : offlineQueue) {
            Log.d("DatabaseHelper", "Syncing offline purchase for: " + houseType);
            firebaseManager.incrementHouseSales(houseType);
        }
        
        // Clear the queue after successful sync
        NetworkUtils.OfflineQueue.clearQueue(context);
        Log.d("DatabaseHelper", "Offline purchases synced successfully");
        
        // Show success message if context is an activity
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(() -> {
                android.widget.Toast.makeText(context, 
                    "Synced " + offlineQueue.size() + " offline purchases", 
                    android.widget.Toast.LENGTH_SHORT).show();
            });
        }
    }}


