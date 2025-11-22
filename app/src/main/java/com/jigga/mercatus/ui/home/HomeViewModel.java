package com.jigga.mercatus.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.jigga.mercatus.data.DatabaseHelper;
import com.jigga.mercatus.model.Product;
import java.util.List;

// Changed to AndroidViewModel to access Context/Application
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
        // In a real app, do this on a background thread
        List<Product> data = dbHelper.getAllProducts();
        products.setValue(data);
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }
}
