package com.jigga.mercatus.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.jigga.mercatus.R;
import com.jigga.mercatus.databinding.FragmentHomeBinding;
import com.jigga.mercatus.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String PREFS_NAME = "MercatusPrefs";
    private static final String KEY_USERNAME = "username";

    // Data holders
    private List<Product> allProducts = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private List<String> displayList = new ArrayList<>();
    private List<Product> displayedProductObjects = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final ListView listView = binding.listViewProducts;
        final ViewPager2 sliderViewPager = binding.sliderViewPager;
        final LinearLayout categoryContainer = binding.categoryContainer;

        // 1. Shared Preferences Logic
        setupWelcomeMessage(textView);

        // 2. Setup Slider
        setupSlider(sliderViewPager);

        // 3. SQLite & List Logic
        listAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                displayList
        );
        listView.setAdapter(listAdapter);

        // 4. Observe Data
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            allProducts = products;
            // Initially show all
            filterProductsByCategory("All");
            // Setup category buttons dynamically based on data (or hardcoded)
            setupCategories(categoryContainer);
        });

        // 5. Handle Product Clicks
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product clickedProduct = displayedProductObjects.get(position);
            showProductDetailsDialog(clickedProduct);
        });

        return root;
    }

    private void setupSlider(ViewPager2 viewPager) {
        List<Integer> sliderImages = new ArrayList<>();
        // Use standard Android drawables for demo, replace with R.drawable.your_image
        sliderImages.add(android.R.drawable.ic_menu_gallery);
        sliderImages.add(android.R.drawable.ic_menu_camera);
        sliderImages.add(android.R.drawable.ic_menu_compass);

        SliderAdapter sliderAdapter = new SliderAdapter(sliderImages);
        viewPager.setAdapter(sliderAdapter);
    }

    private void setupCategories(LinearLayout container) {
        container.removeAllViews();
        String[] categories = {"All", "Electronics", "Fashion", "Home"};

        for (String cat : categories) {
            Button btn = new Button(requireContext());
            btn.setText(cat);
            btn.setOnClickListener(v -> filterProductsByCategory(cat));
            container.addView(btn);
        }
    }

    private void filterProductsByCategory(String category) {
        displayList.clear();
        displayedProductObjects.clear();

        for (Product p : allProducts) {
            // Logic: If category is "All", add everything.
            // If specific, check if product name/desc contains the category text (Simple filter)
            // In a real app, your Product model should have a 'getCategory()' method.
            if (category.equals("All") || p.getName().contains(category)) {
                displayList.add(p.getName() + " - $" + p.getPrice());
                displayedProductObjects.add(p);
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    private void showProductDetailsDialog(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle(product.getName())
                .setMessage("Price: $" + product.getPrice() + "\n\nDescription: This is a high quality item suitable for your needs.")
                .setPositiveButton("Add to Cart", (dialog, which) -> {
                    Toast.makeText(requireContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void setupWelcomeMessage(TextView textView) {
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (!sharedPreferences.contains(KEY_USERNAME)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERNAME, "User");
            editor.apply();
        }

        String username = sharedPreferences.getString(KEY_USERNAME, "Guest");
        textView.setText("Welcome back, " + username + "!");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
