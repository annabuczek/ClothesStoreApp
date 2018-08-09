package com.example.android.clothesstoreapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
                insertExampleProduct();
                return true;
            case R.id.menu_catalog_delete_all:
                showDeleteAllDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Method used to insert example product into a database
     * For debugging purposes.
     */
    public void insertExampleProduct() {

        // set values to be inserted
        ContentValues values = new ContentValues();
        values.put(ClothesEntry.COLUMN_NAME, "Example Dress");
        values.put(ClothesEntry.COLUMN_PRICE, 89.99);
        values.put(ClothesEntry.COLUMN_QUANTITY, "45");
        values.put(ClothesEntry.COLUMN_SUPPLIER, "ExpressProduct");
        values.put(ClothesEntry.COLUMN_SUPPLIER_PHONE, "678342765");
        values.put(ClothesEntry.COLUMN_CATEGORY, ClothesEntry.CATEGORY_DRESS);

        // Insert Data using ContentProvider, return ContentUri of newly inserted product
        Uri newProductUri = getContentResolver().insert(ClothesEntry.CLOTHES_CONTENT_URI, values);

        Log.v("CatalogActivity", "Uri for the inserted Product " + newProductUri);
    }

    /**
     * Build Alert Dialog to show to the user before all of the products will be deleted
     */
    private void showDeleteAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set Dialog message
        builder.setMessage(R.string.dialog_delete_all_message);
        // Set positive button behaviour to delete Product from the database,
        // inform the user and exit activity
        builder.setPositiveButton(R.string.dialog_delete_all_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(ClothesEntry.CLOTHES_CONTENT_URI, null, null);
                Toast.makeText(CatalogActivity.this, getString(R.string.toast_all_products_delete), Toast.LENGTH_SHORT).show();
            }
        });
        // set negative button to dismiss dialog and make no changes in the database
        builder.setNegativeButton(R.string.dialog_delete_all_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
