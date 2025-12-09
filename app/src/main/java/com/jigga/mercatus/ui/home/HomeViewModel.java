package com.jigga.mercatus.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.jigga.mercatus.data.DatabaseHelper;
import com.jigga.mercatus.model.Product;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Product>> products;
    private final DatabaseHelper dbHelper;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
        products = new MutableLiveData<>();
        loadProducts();
    }

    private void loadProducts() {
        List<Product> data = dbHelper.getAllProducts();
        products.setValue(data);
    }

    private void loadProductsByCategory(String category) {
        List<Product> data = dbHelper.getProductsByCategory(category);
        products.setValue(data);
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        dbHelper.addProduct(product);
        loadProducts();
    }
}
