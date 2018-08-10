package com.example.android.clothesstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Id of the Loader used to load the data of all the Products into ListView
     */
    private static final int CATALOG_LOADER_ALL_ID = 1;

    /**
     * Adapter for the ListView
     */
    private ClothesCursorAdapter mAdapter;

    /**
     * Variable storing value for categoryCode retrieved from SharedPreferences
     */
    private int categoryCode;

    /** View to be showed in the ListView when no data are available to display */
    private View mEmptyView;


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

        mEmptyView = findViewById(R.id.empty_view);
        catalogListView.setEmptyView(mEmptyView);

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

        // Get the data from the SharedPreferences in our app
        // Data we retrieve with the key stands for category user chose in the AlertDialog
        // and which category products user wants to see
        // categoryCode is stored as Integer variable
        SharedPreferences getSharedPreferences = this.getPreferences(MODE_PRIVATE);
        int defaultCategoryCode = ClothesEntry.CATEGORY_ALL;
        categoryCode = getSharedPreferences.getInt(getString(R.string.shared_preference_category_key), defaultCategoryCode);
        Toast.makeText(this, "CategoryCode " + categoryCode, Toast.LENGTH_SHORT).show();

        // Set proper title for the activity based on the categoryCode
        showTitle(categoryCode);
        //Set proper message on the empty view if it shows up
        setEmptyViewMessage(categoryCode);
        // Init Loader to show the data
        getLoaderManager().initLoader(CATALOG_LOADER_ALL_ID, null, this);
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
            case R.id.menu_catalog_filter:
                //TODO Show Dialog to filter by category
                showFilterDialog();
                return true;
            case R.id.menu_catalog_insert:
                insertExampleProduct();
                return true;
            case R.id.menu_catalog_delete_all:
                showDeleteAllDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to insert example product into a database
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

    /**
     * Method showing the AlertDialog where user can pick which Product category want to display
     */
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_filter_title);
        // Set items user can choose from, get from Array resource
        builder.setSingleChoiceItems(R.array.category_filter_array, categoryCode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save data into Shared Preference
                SharedPreferences setSharedPreferences = CatalogActivity.this.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor spEditor = setSharedPreferences.edit();
                spEditor.putInt(getString(R.string.shared_preference_category_key), which);
                spEditor.apply();
            }
        });
        builder.setPositiveButton(R.string.dialog_filter_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve data from Shared Preference to update categoryCode variable with new data
                SharedPreferences getSharedPreferences = CatalogActivity.this.getPreferences(MODE_PRIVATE);
                int defaultCategoryCode = ClothesEntry.CATEGORY_ALL;
                categoryCode = getSharedPreferences.getInt(getString(R.string.shared_preference_category_key), defaultCategoryCode);

                // Set proper title for the activity based on the categoryCode
                showTitle(categoryCode);

                //Set proper message on the empty view if it shows up
                setEmptyViewMessage(categoryCode);
                // Restart Loader to load new data
                getLoaderManager().restartLoader(CATALOG_LOADER_ALL_ID, null, CatalogActivity.this);
            }
        });

        builder.setNegativeButton(R.string.dialog_filter_nagetive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel action and dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Method for setting proper title to activity based on the current category code */
    public void showTitle(int categoryCode) {
        String title;
        switch(categoryCode) {
            case ClothesEntry.CATEGORY_OTHER:
                title = getString(R.string.catalog_activity_title_other);
                break;
            case ClothesEntry.CATEGORY_TSHIRT:
                title = getString(R.string.catalog_activity_title_tshirt);
                break;
            case ClothesEntry.CATEGORY_SHIRT:
                title = getString(R.string.catalog_activity_title_shirt);
                break;
            case ClothesEntry.CATEGORY_TROUSERS:
                title = getString(R.string.catalog_activity_title_trousers);
                break;
            case ClothesEntry.CATEGORY_SKIRT:
                title = getString(R.string.catalog_activity_title_skirt);
                break;
            case ClothesEntry.CATEGORY_DRESS:
                title = getString(R.string.catalog_activity_title_dress);
                break;
            default:
                title = getString(R.string.catalog_activity_title_all);
                break;
        }
        setTitle(title);
    }

    /**Method for setting message in empty view based on the current category code */
    private void setEmptyViewMessage(int categoryCode) {

        // Find views inside emptyView
        TextView titleTv = mEmptyView.findViewById(R.id.empty_view_title);
        TextView bodyTv = mEmptyView.findViewById(R.id.empty_view_body);

        // Set proper values depending on the categoryCode
        // 6 category code shows message fot all catalog
        // other category code shows message for particular category
        if(categoryCode != 6) {
            titleTv.setText(R.string.empty_view_title_category);
            bodyTv.setText(R.string.empty_view_body_category);
        } else {
            titleTv.setText(R.string.empty_view_title_all);
            bodyTv.setText(R.string.empty_view_body_all);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Set selection and selection Args to be default null
        String selection = null;
        String[] selectionArgs = null;
        // Check the categoryCode retrieved from SharedPreferences,
        // 6 stands for all of the products so we exclude this case, because we do not need to
        // specify selection and selectionArgs, we can use default null
        if (categoryCode != 6) {
            // if categoryCode is different than 6, we specify selection and selectionArgs
            // to show results for only for specified category
            selection = ClothesEntry.COLUMN_CATEGORY + "=?";
            selectionArgs = new String[]{String.valueOf(categoryCode)};
        }
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
                selection,  // selection
                selectionArgs, // selectionArgs
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
