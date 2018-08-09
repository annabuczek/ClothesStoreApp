package com.example.android.clothesstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    double price = 23.67;

    /** Id of the Loader used to load the data into ListView */
    private static final int CATEGORY_LOADER_ID = 1;

    /** Adapter for the ListView */
    private ClothesCursorAdapter mAdapter;


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

        // Find ListView witch will be populated with data
        ListView catalogListView = findViewById(R.id.catalog_list);

        //TODO Set Empty view on the ListView

        // Create new instance of ClothesCursorAdapter object
        // And set adapter on the listView
        mAdapter = new ClothesCursorAdapter(this, null);
        catalogListView.setAdapter(mAdapter);

        // Set OnCItemClickListener and a catalogListView list item
        // Open DetailActivity and send current item Content Uri
        catalogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(CatalogActivity.this, DetailActivity.class);
                i.setData(ContentUris.withAppendedId(ClothesEntry.CLOTHES_CONTENT_URI, id));
                startActivity(i);
            }
        });

        getLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);
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

    /** Method used to insert example product into a database
     * For developing purposes.
     */
    public void insertExampleProduct(double price) {

        // set values to be inserted
        ContentValues values = new ContentValues();
        values.put(ClothesEntry.COLUMN_NAME, "pierwsza dodana koszula");
        values.put(ClothesEntry.COLUMN_PRICE, price);
        values.put(ClothesEntry.COLUMN_QUANTITY, "45");
        values.put(ClothesEntry.COLUMN_SUPPLIER, "Zara");
        values.put(ClothesEntry.COLUMN_SUPPLIER_PHONE, "678342765");
        values.put(ClothesEntry.COLUMN_CATEGORY, ClothesEntry.CATEGORY_TSHIRT);

        // Insert Data using ContentProvider, return ContentUri of newly inserted product
        Uri newProductUri = getContentResolver().insert(ClothesEntry.CLOTHES_CONTENT_URI, values);

        Toast.makeText(this, "New product insertion successful for Uri " + newProductUri, Toast.LENGTH_SHORT).show();
        Log.v("CatalogActivity", "Uri for the inserted Product " + newProductUri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define which data want the query to return
        String[] projection = {ClothesEntry._ID,
                ClothesEntry.COLUMN_NAME,
                ClothesEntry.COLUMN_QUANTITY,
                ClothesEntry.COLUMN_CATEGORY,
                ClothesEntry.COLUMN_PRICE};

        // Loader to execute query method on ContentProvider on a background thread
        return new CursorLoader(this, // the Context of the app
                ClothesEntry.CLOTHES_CONTENT_URI,  // ContentUri to perform search on
                projection,  // columns to include in the returned cursor
                null,  // selection
                null, // selectionArgs
                null); // defaultSortOrder
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
