package com.example.tierrahomes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;

public class BuyFragment extends Fragment {
    private String currentCategory = "RURAL"; // Default category

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buy_fragment, container, false);

        // Get the location category
        updateLocationCategory();

        // Set up title
        TextView titleText = view.findViewById(R.id.titleText);
        titleText.setText(currentCategory);

        // Set up houses based on category
        setupHousesForCategory(view);

        return view;
    }

    private void updateLocationCategory() {
        // Get user's location from SharedPreferences
        SharedPreferences locationPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String userEmail = locationPrefs.getString("userEmail", "");

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        DatabaseHelper.UserPreferences userPreferences = dbHelper.getUserPreferences(userEmail);

        if (userPreferences != null) {
            String region = userPreferences.getRegion();
            String municipality = userPreferences.getMunicipality();

            // Load classification data if not already loaded
            LocationClassifier.loadClassificationData(requireContext());
            
            // Get the category based on current region and municipality
            currentCategory = LocationClassifier.getLocationCategory(region, municipality).toUpperCase();
        }
    }

    private void setupHousesForCategory(View view) {
        switch (currentCategory) {
            case "RURAL":
                setupRuralHouses(view);
                break;
            case "URBAN":
                setupUrbanHouses(view);
                break;
            case "SUBURBAN":
                setupSuburbanHouses(view);
                break;
        }
    }

    private void setupRuralHouses(View view) {
        setupHouse(view, R.id.house1, R.id.textView2, "FARMHOUSE", R.drawable.farmhouse1);
        setupHouse(view, R.id.house2, R.id.textView3, "COTTAGE", R.drawable.cottage1);
        setupHouse(view, R.id.house3, R.id.textView4, "CABIN", R.drawable.cabinhouse1);
        setupHouse(view, R.id.house4, R.id.textView5, "RANCH HOUSE", R.drawable.ranchhouse1);
        setupHouse(view, R.id.house5, R.id.textView6, "HOMESTEAD", R.drawable.homestead1);
        setupHouse(view, R.id.house6, R.id.textView7, "BARN CONVERSION", R.drawable.barnconversionhouse1);
    }

    private void setupUrbanHouses(View view) {
        setupHouse(view, R.id.house1, R.id.textView2, "APARTMENT", R.drawable.apartment1);
        setupHouse(view, R.id.house2, R.id.textView3, "CONDOMINIUM", R.drawable.condominium1);
        setupHouse(view, R.id.house3, R.id.textView4, "LOFT", R.drawable.loft1);
        setupHouse(view, R.id.house4, R.id.textView5, "PENTHOUSE", R.drawable.penthouse1);
        setupHouse(view, R.id.house5, R.id.textView6, "TOWNHOUSE", R.drawable.townhouse1);
        setupHouse(view, R.id.house6, R.id.textView7, "STUDIO", R.drawable.studioflat1);
    }

    private void setupSuburbanHouses(View view) {
        setupHouse(view, R.id.house1, R.id.textView2, "DETACHED", R.drawable.detachedhouse1);
        setupHouse(view, R.id.house2, R.id.textView3, "SEMI-DET", R.drawable.semidetachedhouse1);
        setupHouse(view, R.id.house3, R.id.textView4, "DUPLEX", R.drawable.duplex1);
        setupHouse(view, R.id.house4, R.id.textView5, "BUNGALOW", R.drawable.bungalow1);
        setupHouse(view, R.id.house5, R.id.textView6, "SPLIT-LEVEL", R.drawable.splilevelhouse1);
        setupHouse(view, R.id.house6, R.id.textView7, "MCMANSION", R.drawable.mcmansion1);
    }

    private void setupHouse(View view, int imageViewId, int textViewId, String houseName, int imageResource) {
        ImageView houseImage = view.findViewById(imageViewId);
        TextView houseText = view.findViewById(textViewId);

        // Set house name
        houseText.setText(houseName);

        // Set image resource
        houseImage.setImageResource(imageResource);

        // Set click listener
        houseImage.setOnClickListener(v -> {
            HouseFragment houseFragment = new HouseFragment();
            Bundle args = new Bundle();
            args.putString("name", houseName);
            args.putInt("imageResourceId", imageResource);
            args.putString("category", currentCategory);
            houseFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            );
            transaction.replace(R.id.content_frame, houseFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}
