package com.example.tierrahomes;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LocationClassifier {
    private static Map<String, String> locationClassifications = new HashMap<>();

    public static void loadClassificationData(Context context) {
        try {
            InputStream is = context.getAssets().open("locationclassification.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            
            // Skip header
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String location = parts[0].trim();
                    String classification = parts[2].trim();
                    
                    // Store the exact name from the file
                    locationClassifications.put(location, classification);
                    
                    // Store a normalized version (uppercase, no special chars)
                    String normalizedLocation = normalizeLocationName(location);
                    locationClassifications.put(normalizedLocation, classification);
                    
                    // Handle city name variations
                    if (location.startsWith("City of ")) {
                        String shortName = location.replace("City of ", "").trim();
                        locationClassifications.put(shortName, classification);
                        locationClassifications.put(shortName + " City", classification);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String normalizeLocationName(String name) {
        // Remove parentheses and their contents
        name = name.replaceAll("\\([^\\)]*\\)", "").trim();
        // Convert to uppercase
        name = name.toUpperCase();
        // Remove special characters but keep spaces
        name = name.replaceAll("[^A-Z0-9\\s]", "");
        return name;
    }

    public static String getLocationCategory(String region, String municipality) {
        if (region == null && municipality == null) {
            return "Rural"; // Default if no location provided
        }

        // Try to find municipality classification first
        if (municipality != null) {
            // Try exact match first
            String classification = locationClassifications.get(municipality);
            if (classification != null) {
                return classification;
            }

            // Try normalized version
            classification = locationClassifications.get(normalizeLocationName(municipality));
            if (classification != null) {
                return classification;
            }

            // Try with "City of" prefix
            classification = locationClassifications.get("City of " + municipality);
            if (classification != null) {
                return classification;
            }
        }

        // Try to find region classification
        if (region != null) {
            // Special case for NCR
            if (region.equals("NCR") || 
                region.contains("NATIONAL CAPITAL REGION") || 
                region.contains("NCR")) {
                return "Urban";
            }

            // Try exact match
            String classification = locationClassifications.get(region);
            if (classification != null) {
                return classification;
            }

            // Try normalized version
            classification = locationClassifications.get(normalizeLocationName(region));
            if (classification != null) {
                return classification;
            }
        }

        // If no match found, return Rural as default
        return "Rural";
    }
}