package com.example.android.clothesstoreapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

import com.example.android.clothesstoreapp.data.ClothesContract;
import com.example.android.clothesstoreapp.data.ClothesDbHelper;

public class CatalogActivity extends AppCompatActivity {

    double price = 23.67;
    // ClothesDbHelper object used to connect to the database
    private ClothesDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        /** Setup FAB button to start new activity {@link EditorActivity} */
        FloatingActionButton fabButton = findViewById(R.id.fab_add);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_catalog_insert:
                insertExampleProduct(price);
                price += 5;
                return true;
            case R.id.menu_catalog_delete_all:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertExampleProduct(double price) {

        // Create a instance of ClothesDbHelper class
        mDbHelper = new ClothesDbHelper(this);

        // get SQLitedatabase in writable mode to inser data
        SQLiteDatabase dbObject = mDbHelper.getWritableDatabase();

        // set values to be inserted
        ContentValues values = new ContentValues();
        values.put(ClothesEntry.COLUMN_NAME, "pierwsza dodana koszula");
        values.put(ClothesEntry.COLUMN_PRICE, price);
        values.put(ClothesEntry.COLUMN_SUPPLIER, "Zara");
        values.put(ClothesEntry.COLUMN_SUPPLIER_PHONE, "678342765");
        values.put(ClothesEntry.COLUMN_CATEGORY, ClothesEntry.CATEGORY_TSHIRT);

        // insert data, return id of the inserted row
        long idOfRow = dbObject.insert(ClothesEntry.TABLE_NAME, null, values);

        Log.v("CatalogActivity", "Num of Rows inserted " + idOfRow);
    }
}
