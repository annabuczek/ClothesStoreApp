package com.example.android.clothesstoreapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;
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

        Cursor cursor = getContentResolver().query(ClothesEntry.CLOTHES_CONTENT_URI,
                null, null, null, null);

        ClothesCursorAdapter adapter = new ClothesCursorAdapter(this, cursor);

        ListView catalogListView = findViewById(R.id.catalog_list);

        catalogListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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

        // set values to be inserted
        ContentValues values = new ContentValues();
        values.put(ClothesEntry.COLUMN_NAME, "pierwsza dodana koszula");
        values.put(ClothesEntry.COLUMN_PRICE, price);
        values.put(ClothesEntry.COLUMN_SUPPLIER, "Zara");
        values.put(ClothesEntry.COLUMN_SUPPLIER_PHONE, "678342765");
        values.put(ClothesEntry.COLUMN_CATEGORY, ClothesEntry.CATEGORY_TSHIRT);

        // Insert Data using ContentProvider, return ContentUri of newly inserted product
        Uri newProductUri = getContentResolver().insert(ClothesEntry.CLOTHES_CONTENT_URI, values);

        Toast.makeText(this, "New product insertion successful for Uri " + newProductUri, Toast.LENGTH_SHORT).show();
        Log.v("CatalogActivity", "Uri for the inserted Product " + newProductUri);
    }
}
