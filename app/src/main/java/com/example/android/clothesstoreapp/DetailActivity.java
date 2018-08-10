package com.example.android.clothesstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The Id of the Loader used to load data from the query into views
     */
    private static final int DETAIL_LOADER_ID = 1;

    /**
     * Maximum quantity number of products that can be stored in a database
     */
    private static final int MAX_PRODUCT_QUANTITY = 999;

    /**
     * Minimum quantity number of products that can be stored in a database
     */
    private static final int MIN_PRODUCT_QUANTITY = 0;

    /**
     * Content Uri of the currently shown Product
     */
    private Uri mCurrentUri;

    /**
     * TextView showing the name of the product
     */
    private TextView mNameTv;

    /**
     * TextView showing the price of the product
     */
    private TextView mPriceTv;

    /**
     * TextView showing the quantity of the product
     */
    private TextView mQuantityTv;

    /**
     * TextView showing the category of the product
     */
    private TextView mCategoryTv;

    /**
     * TextView showing the supplier of the product
     */
    private TextView mSupplierTv;

    /**
     * TextView showing the supplier phone
     */
    private TextView mSupplierPhoneTv;

    /**
     * String variable storing value of the Supplier Phone Number for the currently shown product
     */
    private String mSupplierPhone;

    /**
     * Integer variable storing value of the current quantity of the Product
     */
    private int mQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Find views for further use
        ImageButton quantityIncreaseButton = findViewById(R.id.quantity_button_up);
        ImageButton quantityDecreaseButton = findViewById(R.id.quantity_button_down);
        ImageButton supplierCallButton = findViewById(R.id.detail_call_button);
        // Find views
        mNameTv = findViewById(R.id.detail_name);
        mCategoryTv = findViewById(R.id.detail_category);
        mPriceTv = findViewById(R.id.detail_price);
        mQuantityTv = findViewById(R.id.detail_quantity);
        mSupplierTv = findViewById(R.id.detail_supplier);
        mSupplierPhoneTv = findViewById(R.id.detail_supplier_phone);

        // Receive intent with ContentUri for the currently shown product
        Intent i = getIntent();
        mCurrentUri = i.getData();

        // If ContentUri was properly received kick off the loader to load data
        if (mCurrentUri != null) {
            Log.v("DetailActivity", "Uri of the currently showed Product is " + mCurrentUri);

            getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        }

        // Set OnClickListener on the increase button to increase quantity of the product
        // and update it in a database
        quantityIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increase quantity number only if quantity is not bigger than maximum value
                if (!(mQuantity >= MAX_PRODUCT_QUANTITY)) {
                    mQuantity += 1;

                    ContentValues values = new ContentValues();
                    values.put(ClothesEntry.COLUMN_QUANTITY, mQuantity);

                    getContentResolver().update(mCurrentUri, values, null, null);
                }
            }
        });

        // Set OnClickListener on the decrease button to decrease quantity of the product
        // and update it in a database
        quantityDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrease quantity number only if quantity is not equal or lower than minimum value
                if (!(mQuantity <= MIN_PRODUCT_QUANTITY)) {
                    mQuantity -= 1;

                    ContentValues values = new ContentValues();
                    values.put(ClothesEntry.COLUMN_QUANTITY, mQuantity);

                    getContentResolver().update(mCurrentUri, values, null, null);
                }
            }
        });

        // Set OnClickListener on a call button to call supplier
        supplierCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_DIAL);
                Uri supplierPhoneUri = Uri.parse("tel:" + mSupplierPhone);
                i.setData(supplierPhoneUri);
                startActivity(i);
            }
        });

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
                // Sent intent to the Editor Activity containing current Product Uri
                Intent i = new Intent(DetailActivity.this, EditorActivity.class);
                i.setData(mCurrentUri);
                startActivity(i);
                return true;
            case R.id.menu_detail_delete:
                showDeleteDialog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Build Alert Dialog to show to the user before product will be deleted
     */
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set Dialog message
        builder.setMessage(R.string.dialog_delete_message);
        // Set positive button behaviour to delete Product from the database,
        // inform the user and exit activity
        builder.setPositiveButton(R.string.dialog_delete_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(mCurrentUri, null, null);
                DetailActivity.this.finish();
                Toast.makeText(DetailActivity.this, getString(R.string.toast_single_product_delete), Toast.LENGTH_SHORT).show();
            }
        });
        // set negative button to dismiss dialog and make no changes in the database
        builder.setNegativeButton(R.string.dialog_delete_negative_button, new DialogInterface.OnClickListener() {
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
            mQuantity = cursor.getInt(cursor.getColumnIndex(ClothesEntry.COLUMN_QUANTITY));
            String supplier = cursor.getString(cursor.getColumnIndex(ClothesEntry.COLUMN_SUPPLIER));
            mSupplierPhone = cursor.getString(cursor.getColumnIndex(ClothesEntry.COLUMN_SUPPLIER_PHONE));
            int category = cursor.getInt(cursor.getColumnIndex(ClothesEntry.COLUMN_CATEGORY));

            // Set values on the views
            mNameTv.setText(productName);
            mPriceTv.setText(String.valueOf(price));
            mQuantityTv.setText(String.valueOf(mQuantity));
            mSupplierTv.setText(supplier);
            mSupplierPhoneTv.setText(mSupplierPhone);

            // Variable storing String value of the category to be displayed in the categoryTv
            String categoryString;
            switch (category) {
                case ClothesEntry.CATEGORY_TSHIRT:
                    categoryString = getString(R.string.category_tshirt);
                    break;
                case ClothesEntry.CATEGORY_SHIRT:
                    categoryString = getString(R.string.category_shirt);
                    break;
                case ClothesEntry.CATEGORY_TROUSERS:
                    categoryString = getString(R.string.category_trousers);
                    break;
                case ClothesEntry.CATEGORY_SKIRT:
                    categoryString = getString(R.string.category_skirt);
                    break;
                case ClothesEntry.CATEGORY_DRESS:
                    categoryString = getString(R.string.category_dress);
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
