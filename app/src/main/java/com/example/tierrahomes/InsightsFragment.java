package com.example.tierrahomes;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.TextViewCompat;
import android.util.TypedValue;
import android.widget.ScrollView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Update import
import com.google.android.material.button.MaterialButton;

public class InsightsFragment extends Fragment {
    private MaterialButton doneButton;
    private BarChart priceChart;
    private TextView insightText;
    private Map<String, float[]> propertyPrices;
    private String currentPropertyType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insights_fragment, container, false);

        doneButton = view.findViewById(R.id.button_done);
        priceChart = view.findViewById(R.id.priceChart);
        insightText = view.findViewById(R.id.textView2);
        TextView titleText = view.findViewById(R.id.titleText);
        
        // Set responsive text sizing for insights text
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            insightText,
            8,  // min size in sp
            12, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );
        
        // Find the ScrollView and ensure it starts at the top
        ScrollView scrollView = view.findViewById(R.id.textView2).getParent() instanceof ScrollView ? 
            (ScrollView) view.findViewById(R.id.textView2).getParent() : null;
        if (scrollView != null) {
            scrollView.post(() -> scrollView.scrollTo(0, 0));
        }
        
        // Set responsive text sizing for title
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            titleText,
            10, // min size in sp
            16, // max size in sp
            1,  // step size in sp
            TypedValue.COMPLEX_UNIT_SP
        );

        // Initialize data first
        initializePriceData();

        // Get property type from arguments
        Bundle args = getArguments();
        if (args != null) {
            currentPropertyType = args.getString("propertyType", "Apartment");
        } else {
            currentPropertyType = "Apartment"; // Set a default that exists in our data
        }

        // Set the title text
        titleText.setText(currentPropertyType.toUpperCase());
        
        // Initialize insights text with a loading message
        insightText.setText("Loading insights...");

        // Setup chart before updating data
        setupChart();

        // Update chart with data
        if (propertyPrices != null && propertyPrices.containsKey(currentPropertyType)) {
            updateChartData(currentPropertyType);
            generateInsights(currentPropertyType);
        } else {
            // Debug: Log the issue and try with uppercase
            String upperCaseType = currentPropertyType.toUpperCase();
            if (propertyPrices != null && propertyPrices.containsKey(upperCaseType)) {
                updateChartData(upperCaseType);
                generateInsights(upperCaseType);
            } else {
                // Set a default message if no data is found
                insightText.setText("No insights available for this property type.");
            }
        }

        // Update click listener for CardView
        // Update click listener for done button
        doneButton.setOnClickListener(v -> {
            // Get the original arguments
            Bundle originalArgs = getArguments();
            if (originalArgs != null) {
                // Create new HouseFragment with all the necessary data
                HouseFragment houseFragment = new HouseFragment();
                Bundle houseArgs = new Bundle();
                houseArgs.putString("name", currentPropertyType);
                houseArgs.putInt("imageResourceId", originalArgs.getInt("imageResourceId"));
                houseArgs.putString("category", originalArgs.getString("category", "RURAL"));
                houseFragment.setArguments(houseArgs);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
                transaction.replace(R.id.content_frame, houseFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void setupChart() {
        if (priceChart == null) return;

        // Basic chart setup
        priceChart.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        priceChart.getDescription().setEnabled(false);
        priceChart.setTouchEnabled(true);
        priceChart.setDrawGridBackground(false);
        priceChart.setDragEnabled(true);
        priceChart.setScaleEnabled(true);
        priceChart.setPinchZoom(false);
        priceChart.setViewPortOffsets(60f, 20f, 60f, 60f);

        // X-Axis setup
        XAxis xAxis = priceChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(getResources().getColor(R.color.colorCard));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) (2020 + value));
            }
        });

        // Y-Axis setup
        YAxis leftAxis = priceChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(getResources().getColor(R.color.colorCard));
        leftAxis.setTextSize(6f);
        leftAxis.setLabelCount(6, true); // Add this line to force more labels
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART); // Add this line to ensure labels are visible
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 1000000) {
                    return "₱" + String.format("%.1fM", value / 1000000);
                } else if (value >= 1000) {
                    return "₱" + String.format("%.0fk", value / 1000);
                } else {
                    return "₱" + String.format("%.0f", value);
                }
            }
        });

        priceChart.getAxisRight().setEnabled(false);
        priceChart.getLegend().setEnabled(false);
    }

    private void updateChartData(String propertyType) {
        if (priceChart == null || propertyPrices == null) return;

        float[] prices = propertyPrices.get(propertyType);
        if (prices == null) return;

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < prices.length; i++) {
            entries.add(new BarEntry(i, prices[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.colorCard));
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(8f);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorCard));
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 1000000) {
                    return String.format("%.1fM", value / 1000000);
                } else if (value >= 1000) {
                    return String.format("%.0fk", value / 1000);
                } else {
                    return String.format("%.0f", value);
                }
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        // Find min and max values for Y-axis scaling
        float minPrice = Float.MAX_VALUE;
        float maxPrice = Float.MIN_VALUE;
        for (float price : prices) {
            minPrice = Math.min(minPrice, price);
            maxPrice = Math.max(maxPrice, price);
        }

        // Set Y-axis range with some padding
        YAxis leftAxis = priceChart.getAxisLeft();
        leftAxis.setAxisMinimum(minPrice * 0.9f); // 10% below minimum
        leftAxis.setAxisMaximum(maxPrice * 1.1f); // 10% above maximum

        priceChart.setData(barData);
        priceChart.animateY(1000);
        priceChart.invalidate();
    }

    private void initializePriceData() {
        propertyPrices = new HashMap<>();

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


    private void generateInsights(String propertyType) {
        float[] prices = propertyPrices.get(propertyType);
        if (prices == null) return;

        // Basic calculations
        float latestPrice = prices[prices.length - 1];
        float oldestPrice = prices[0];
        float yearlyGrowth = ((latestPrice - oldestPrice) / oldestPrice) * 100 / 5;
        float recentChange = ((prices[prices.length - 1] - prices[prices.length - 2]) / prices[prices.length - 2]) * 100;

        // Calculate market metrics
        float maxPrice = prices[0];
        float minPrice = prices[0];
        float totalVolatility = 0;

        for (int i = 1; i < prices.length; i++) {
            maxPrice = Math.max(maxPrice, prices[i]);
            minPrice = Math.min(minPrice, prices[i]);
            totalVolatility += Math.abs(prices[i] - prices[i-1]) / prices[i-1];
        }

        float averageVolatility = totalVolatility / (prices.length - 1);
        float priceRange = ((maxPrice - minPrice) / minPrice) * 100;

        StringBuilder insight = new StringBuilder();

        // Market Summary Section
        insight.append("━━━ MARKET SUMMARY ━━━\n\n");
        insight.append(String.format("CURRENT PRICE: ₱%,.0f\n", latestPrice));
        insight.append(String.format("YEARLY GROWTH: %s%.1f%%\n",
                yearlyGrowth >= 0 ? "+" : "", yearlyGrowth));
        insight.append(String.format("RECENT CHANGE: %s%.1f%%\n\n",
                recentChange >= 0 ? "↑" : "↓", Math.abs(recentChange)));


        // Market Phase Section
        insight.append("━━━ MARKET PHASE ━━━\n\n");
        if (averageVolatility < 0.05 && yearlyGrowth > 0) {
            insight.append("📈 STABLE GROWTH\n\n");
            insight.append("• STEADY PRICE APPRECIATION\n");
            insight.append("• LOW MARKET VOLATILITY\n");
            insight.append("• REDUCED INVESTMENT RISK\n");
        } else if (averageVolatility < 0.1 && yearlyGrowth > 5) {
            insight.append("🚀 EXPANSION\n\n");
            insight.append("• STRONG GROWTH MOMENTUM\n");
            insight.append("• MODERATE VOLATILITY\n");
            insight.append("• HIGH POTENTIAL RETURNS\n");
        } else if (averageVolatility > 0.15) {
            insight.append("📊 DYNAMIC\n\n");
            insight.append("• HIGH MARKET ACTIVITY\n");
            insight.append("• PRICE FLUCTUATIONS\n");
            insight.append("• TRADING OPPORTUNITIES\n");
        } else {
            insight.append("⚖️ CONSOLIDATION\n\n");
            insight.append("• MARKET EQUILIBRIUM\n");
            insight.append("• PRICE STABILIZATION\n");
            insight.append("• LONG-TERM POTENTIAL\n");
        }
        insight.append("\n");

// Risk Analysis Section
        insight.append("━━━ RISK ANALYSIS ━━━\n\n");
        insight.append(String.format("VOLATILITY: %.1f%%\n", averageVolatility * 100));
        insight.append(String.format("PRICE RANGE: %.1f%%\n\n", priceRange));

// Investment Rating Section
        insight.append("━━━ INVESTMENT RATING ━━━\n\n");
        if (yearlyGrowth > 8 && averageVolatility < 0.1) {
            insight.append("⭐⭐⭐⭐⭐\n");
            insight.append("STRONG BUY\n\n");
            insight.append("• EXCEPTIONAL GROWTH\n");
            insight.append("• OPTIMAL ENTRY POINT\n");
        } else if (yearlyGrowth > 5 || (yearlyGrowth > 3 && averageVolatility < 0.08)) {
            insight.append("⭐⭐⭐⭐\n");
            insight.append("BUY\n\n");
            insight.append("• POSITIVE OUTLOOK\n");
            insight.append("• GOOD OPPORTUNITY\n");
        } else if (yearlyGrowth > 0) {
            insight.append("⭐⭐⭐\n");
            insight.append("HOLD\n\n");
            insight.append("• STABLE PERFORMANCE\n");
            insight.append("• MONITOR TRENDS\n");
        } else {
            insight.append("⭐⭐\n");
            insight.append("WATCH\n\n");
            insight.append("• AWAIT BETTER ENTRY\n");
            insight.append("• REVIEW IN 3-6 MONTHS\n");
        }


        insightText.setText(insight.toString());
        
        // Scroll to top after setting the text
        if (getView() != null) {
            ScrollView scrollView = getView().findViewById(R.id.textView2).getParent() instanceof ScrollView ? 
                (ScrollView) getView().findViewById(R.id.textView2).getParent() : null;
            if (scrollView != null) {
                scrollView.post(() -> scrollView.scrollTo(0, 0));
            }
        }
    }
}
