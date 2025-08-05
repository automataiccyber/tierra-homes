package com.example.tierrahomes;

import android.content.SharedPreferences;

public class MaterialPriceCalculator {
    // Updated Price constants for flooring materials
    public static final class FlooringPrices {
        static final double[][] CONCRETE_SLAB = {{750, 850, 950}};
        static final double[][] CERAMIC_TILE = {{1050, 1200, 1350}};
        static final double[][] VINYL_PLANK = {{850, 1000, 1150}};
        static final double[][] NATURAL_STONE = {{1600, 1800, 2000}};
        static final double[][] POLISHED_CONCRETE = {{950, 1100, 1250}};
    }

    // Updated Price constants for wall finishes
    public static final class WallPrices {
        static final double[][] PAINTED_CEMENT = {{350, 400, 450}};
        static final double[][] BRICK_CLADDING = {{1400, 1600, 1800}};
        static final double[][] STUCCO = {{750, 850, 950}};
        static final double[][] STONE_FINISH = {{2000, 2200, 2500}};
        static final double[][] WOOD_SIDING = {{1300, 1500, 1700}};
    }

    // Updated Price constants for windows
    public static final class WindowPrices {
        static final double[][] SLIDING_ALUMINUM = {{4500, 5500, 6500}};
        static final double[][] FIXED_PICTURE = {{4000, 4800, 5800}};
        static final double[][] CASEMENT_UPVC = {{5000, 6000, 7000}};
        static final double[][] WOODEN_FRAME = {{5200, 6200, 7200}};
        static final double[][] GLASS_SECURITY = {{6000, 7000, 8000}};
    }

    // Updated Price constants for doors
    public static final class DoorPrices {
        static final double[][] SOLID_WOODEN = {{6500, 7500, 8500}};
        static final double[][] STEEL_SECURITY = {{7500, 8500, 9500}};
        static final double[][] GLASS_METAL = {{7000, 8000, 9000}};
        static final double[][] FRENCH_DOORS = {{8500, 9500, 10500}};
    }

    // Updated Price constants for balconies
    public static final class BalconyPrices {
        static final double[][] SMALL_FRONT = {{15000, 20000, 25000}};
        static final double[][] WRAP_AROUND = {{28000, 35000, 40000}};
        static final double[][] SIDE_RAILING = {{20000, 25000, 30000}};
    }

    // Updated Price constants for staircases
    public static final class StaircasePrices {
        static final double[][] CONCRETE_TILE = {{40000, 48000, 55000}};
        static final double[][] WOODEN = {{35000, 42000, 50000}};
        static final double[][] SPIRAL = {{45000, 52000, 60000}};
        static final double[][] STEEL_WOOD = {{42000, 50000, 58000}};
    }

    public static int getLocationIndex(String category) {
        switch (category.toLowerCase()) {
            case "urban": return 2;
            case "suburban": return 1;
            default: return 0; // rural
        }
    }

    public static double calculateFloorPrice(String locationCategory, double lotSize, int floorNumber,
                                             SharedPreferences floorCustomizations) {
        String floorKey = "floor_" + floorNumber;
        int locationIndex = getLocationIndex(locationCategory);

        int flooringType = floorCustomizations.getInt(floorKey + "_flooring", 0);
        int wallType = floorCustomizations.getInt(floorKey + "_walls", 0);
        int windowType = floorCustomizations.getInt(floorKey + "_window", 0);
        int doorType = floorCustomizations.getInt(floorKey + "_door", 0);
        int balconyType = floorCustomizations.getInt(floorKey + "_balcony", 0);
        int staircaseType = floorCustomizations.getInt(floorKey + "_staircase", 0);

        double flooringCost = getFlooringCost(flooringType, locationIndex) * lotSize;
        double wallCost = getWallCost(wallType, locationIndex) * (lotSize * 2.5);

        int windowCount = calculateWindowCount(lotSize);
        int doorCount = calculateDoorCount(lotSize);

        double windowCost = getWindowCost(windowType, locationIndex) * windowCount;
        double doorCost = getDoorCost(doorType, locationIndex) * doorCount;

        double balconyCost = 0;
        if (balconyType > 1) {
            balconyCost = getBalconyCost(balconyType, locationIndex);
        }

        double staircaseCost = 0;
        if (staircaseType > 0) {
            staircaseCost = getStaircaseCost(staircaseType, locationIndex);
        }

        return flooringCost + wallCost + windowCost + doorCost + balconyCost + staircaseCost;
    }

    private static int calculateWindowCount(double lotSize) {
        return Math.max(2, (int)(lotSize / 20));
    }

    private static int calculateDoorCount(double lotSize) {
        return Math.max(2, (int)(lotSize / 30));
    }

    private static double getFlooringCost(int type, int locationIndex) {
        switch (type) {
            case 1: return FlooringPrices.CONCRETE_SLAB[0][locationIndex];
            case 2: return FlooringPrices.CERAMIC_TILE[0][locationIndex];
            case 3: return FlooringPrices.VINYL_PLANK[0][locationIndex];
            case 4: return FlooringPrices.NATURAL_STONE[0][locationIndex];
            case 5: return FlooringPrices.POLISHED_CONCRETE[0][locationIndex];
            default: return 0;
        }
    }

    private static double getWallCost(int type, int locationIndex) {
        switch (type) {
            case 1: return WallPrices.PAINTED_CEMENT[0][locationIndex];
            case 2: return WallPrices.BRICK_CLADDING[0][locationIndex];
            case 3: return WallPrices.STUCCO[0][locationIndex];
            case 4: return WallPrices.STONE_FINISH[0][locationIndex];
            case 5: return WallPrices.WOOD_SIDING[0][locationIndex];
            default: return 0;
        }
    }

    private static double getWindowCost(int type, int locationIndex) {
        switch (type) {
            case 1: return WindowPrices.SLIDING_ALUMINUM[0][locationIndex];
            case 2: return WindowPrices.FIXED_PICTURE[0][locationIndex];
            case 3: return WindowPrices.CASEMENT_UPVC[0][locationIndex];
            case 4: return WindowPrices.WOODEN_FRAME[0][locationIndex];
            case 5: return WindowPrices.GLASS_SECURITY[0][locationIndex];
            default: return 0;
        }
    }

    private static double getDoorCost(int type, int locationIndex) {
        switch (type) {
            case 1: return DoorPrices.SOLID_WOODEN[0][locationIndex];
            case 2: return DoorPrices.STEEL_SECURITY[0][locationIndex];
            case 3: return DoorPrices.GLASS_METAL[0][locationIndex];
            case 4: return DoorPrices.FRENCH_DOORS[0][locationIndex];
            default: return 0;
        }
    }

    private static double getBalconyCost(int type, int locationIndex) {
        switch (type) {
            case 2: return BalconyPrices.SMALL_FRONT[0][locationIndex];
            case 3: return BalconyPrices.WRAP_AROUND[0][locationIndex];
            case 4: return BalconyPrices.SIDE_RAILING[0][locationIndex];
            default: return 0;
        }
    }

    private static double getStaircaseCost(int type, int locationIndex) {
        switch (type) {
            case 1: return StaircasePrices.CONCRETE_TILE[0][locationIndex];
            case 2: return StaircasePrices.WOODEN[0][locationIndex];
            case 3: return StaircasePrices.SPIRAL[0][locationIndex];
            case 4: return StaircasePrices.STEEL_WOOD[0][locationIndex];
            default: return 0;
        }
    }
}
