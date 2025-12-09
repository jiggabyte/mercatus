package com.jigga.mercatus.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String PREFS_NAME = "MercatusPrefs";
    private static final String KEY_USERNAME = "username";

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
        final Button addProductBtn = binding.btnAddProduct;

        setupWelcomeMessage(textView);
        setupSlider(sliderViewPager);

        listAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                displayList
        );
        listView.setAdapter(listAdapter);

        homeViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            allProducts = products;
            filterProductsByCategory("All");
            setupCategories(categoryContainer);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product clickedProduct = displayedProductObjects.get(position);
            showProductDetailsDialog(clickedProduct);
        });

        addProductBtn.setOnClickListener(v -> openAddProductDialog(homeViewModel));

        return root;
    }

    private void openAddProductDialog(HomeViewModel homeViewModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Product");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Handle product addition
            addProductFromDialog(dialogView, homeViewModel);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addProductFromDialog(View dialogView, HomeViewModel homeViewModel) {
        android.widget.EditText nameInput = dialogView.findViewById(R.id.edit_product_name);
        android.widget.EditText priceInput = dialogView.findViewById(R.id.edit_product_price);
        android.widget.Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);

        String name = nameInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            Product newProduct = new Product(name, price, category);
            homeViewModel.addProduct(newProduct);
            Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid price", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSlider(ViewPager2 viewPager) {
        List<Integer> sliderImages = new ArrayList<>();
        sliderImages.add(R.drawable.slide1);
        sliderImages.add(R.drawable.slide2);
        sliderImages.add(R.drawable.slide3);
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
            if (category.equals("All") || p.getCategory().equals(category)) {
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
