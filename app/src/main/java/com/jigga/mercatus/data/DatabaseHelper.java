package com.jigga.mercatus.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jigga.mercatus.model.Product;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mercatus.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PRODUCTS = "products";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PRODUCTS + " (id INTEGER PRIMARY KEY, name TEXT, price REAL)";
        db.execSQL(createTable);

        // Insert dummy data for testing
        insertDummyData(db);
    }

    private void insertDummyData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_PRODUCTS + " (name, price) VALUES ('Wireless Headphones', 59.99)");
        db.execSQL("INSERT INTO " + TABLE_PRODUCTS + " (name, price) VALUES ('Smart Watch', 120.50)");
        db.execSQL("INSERT INTO " + TABLE_PRODUCTS + " (name, price) VALUES ('Gaming Mouse', 25.00)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int priceIndex = cursor.getColumnIndex("price");

                if(idIndex >=0 && nameIndex >=0 && priceIndex >= 0) {
                    productList.add(new Product(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getDouble(priceIndex)
                    ));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }
}
