package com.example.android.clothesstoreapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The Id of the Loader used to load data from the query into views
     */
    private static final int DETAIL_LOADER_ID = 1;

    /**
     * Content Uri of the currently shown Product
     */
    Uri mCurrentUri;

    /**
     * TextView showing the name of the product
     */
    TextView mNameTv;

    /**
     * TextView showing the price of the product
     */
    TextView mPriceTv;

    /**
     * TextView showing the quantity of the product
     */
    TextView mQuantityTv;

    /**
     * TextView showing the category of the product
     */
    TextView mCategoryTv;

    /**
     * TextView showing the supplier of the product
     */
    TextView mSupplierTv;

    /**
     * TextView showing the supplier phone
     */
    TextView mSupplierPhoneTv;

    /**
     * String variable storing value of the Supplier Phone Number for the currently shown product
     */
    String mSupplierPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        mCurrentUri = i.getData();

        if (mCurrentUri != null) {
            Log.v("DetailActivity", "Uri of the currently showed Product is " + mCurrentUri);

            getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_detail_edit:
                // TODO edit item
                // Sent intent to edit activity
                return true;
            case R.id.menu_detail_delete:
                //TODO delete item
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define what data needs to be received from the query

        String[] projection = {ClothesEntry._ID,
                ClothesEntry.COLUMN_NAME,
                ClothesEntry.COLUMN_PRICE,
                ClothesEntry.COLUMN_QUANTITY,
                ClothesEntry.COLUMN_SUPPLIER,
                ClothesEntry.COLUMN_SUPPLIER_PHONE,
                ClothesEntry.COLUMN_CATEGORY};

        return new CursorLoader(this, // the Context of the app
                mCurrentUri,                  // ContentUri for which query needs to be performed
                projection,                   // which columns query should include
                null,                // selection - specified by Uri
                null,             // selection arguments - specified by Uri
                null);               // sort Order null
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        // Of the Cursor object is null or there is no data in the Cursor, return early;
        if (cursor == null || cursor.getCount() < 0) {
            return;
        }
        // Move cursor to the first position and read data from it
        if (cursor.moveToFirst()) {
            String productName = cursor.getString(cursor.getColumnIndex(ClothesEntry.COLUMN_NAME));
            double price = cursor.getDouble(cursor.getColumnIndex(ClothesEntry.COLUMN_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndex(ClothesEntry.COLUMN_QUANTITY));
            String supplier = cursor.getString(cursor.getColumnIndex(ClothesEntry.COLUMN_SUPPLIER));
            mSupplierPhone = cursor.getString(cursor.getColumnIndex(ClothesEntry.COLUMN_SUPPLIER_PHONE));
            int category = cursor.getInt(cursor.getColumnIndex(ClothesEntry.COLUMN_CATEGORY));

            // Find views
            mNameTv = findViewById(R.id.detail_name);
            mCategoryTv = findViewById(R.id.detail_category);
            mPriceTv = findViewById(R.id.detail_price);
            mQuantityTv = findViewById(R.id.detail_quantity);
            mSupplierTv = findViewById(R.id.detail_supplier);
            mSupplierPhoneTv = findViewById(R.id.detail_supplier_phone);

            // Set values on the views
            mNameTv.setText(productName);
            mPriceTv.setText(String.valueOf(price));
            mQuantityTv.setText(String.valueOf(quantity));
            mSupplierTv.setText(supplier);
            mSupplierPhoneTv.setText(mSupplierPhone);

            // Variable storing String value of the category to be displayed in the categoryTv
            String categoryString;
            switch (category) {
                case ClothesEntry.CATEGORY_TSHIRT:
                    categoryString = getString(R.string.category_tshirt);
                    break;
                case ClothesEntry.CATEGORY_SHIRT:
                    categoryString = getString(R.string.category_tshirt);
                    break;
                case ClothesEntry.CATEGORY_TROUSERS:
                    categoryString = getString(R.string.category_tshirt);
                    break;
                case ClothesEntry.CATEGORY_SKIRT:
                    categoryString = getString(R.string.category_tshirt);
                    break;
                case ClothesEntry.CATEGORY_DRESS:
                    categoryString = getString(R.string.category_tshirt);
                    break;
                default:
                    categoryString = getString(R.string.category_other);
            }
            mCategoryTv.setText(categoryString);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNameTv.setText("");
        mQuantityTv.setText("");
        mPriceTv.setText("");
        mCategoryTv.setText("");
        mSupplierTv.setText("");
        mSupplierPhoneTv.setText("");
    }
}
