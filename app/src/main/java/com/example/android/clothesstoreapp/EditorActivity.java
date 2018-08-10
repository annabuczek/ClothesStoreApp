package com.example.android.clothesstoreapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Constant to specify limit of digits user will be able to input into quantity edit text field.
    private static final int QUANTITY_MAX_LENGTH = 3;
    // Constant to specify limit of digits user will be able to input
    // into supplier phone number edit text field
    private static final int PHONE_NUMBER_MAX_LENGTH = 9;

    /** The ID of the Loader used to load data for the existing product if Activity id 'edit' mode */
    private static final int EDITOR_LOADER_ID = 1;

    /**
     * EditText for the Name of the Product
     */
    private EditText mNameEditText;
    /**
     * EditText for the Price od the Product
     */
    private EditText mPriceEditText;
    /**
     * EditText for the Quantity of the product
     */
    private EditText mQuantityEditText;
    /**
     * EditText for the Supplier Name
     */
    private EditText mSupplierNameEditText;
    /**
     * EditText for the Supplier Phone
     */
    private EditText mSupplierPhoneEditText;
    /**
     * Spinner showing possible Product Categories
     */
    private Spinner mSpinner;
    /**
     * Number value of the category String
     */
    private int mCategoryValue;
    /**
     * Content Uri for the currently edited product
     */
    private Uri mCurrentUri;

    /**
     * Boolean that keeps information wheather the Product was edited (true) or not (false)
     */
    private boolean hasProductChanged = false;

    /**
     * OnTouchListener which listen for any user touch on Views that it is attached to.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hasProductChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Find Views for further use
        mNameEditText = findViewById(R.id.name_edit_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mQuantityEditText = findViewById(R.id.quantity_edit_text);
        mSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone_edit_text);

        setupSpinner();

        Intent i = getIntent();
        mCurrentUri = i.getData();

        if (mCurrentUri == null) {
            setTitle(R.string.editor_activity_add_title);
        } else {
            setTitle(R.string.editor_activity_edit_title);
            getLoaderManager().initLoader(EDITOR_LOADER_ID, null, this);
        }

        // Set OnTouchListener to be notified weather the user changed any data
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mSpinner.setOnTouchListener(mTouchListener);

        // Set InputFilter.LengthFilter on mQuantityEditText to limit number of characters user may input
        mQuantityEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(QUANTITY_MAX_LENGTH)});

        // Apply InputFilter.LengthFilter on mSupplierPhoneNumber to limit number of characters user may input
        mSupplierPhoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PHONE_NUMBER_MAX_LENGTH)});

        // Apply TwoDigitsDecimalInputFilter which implements InputFilter interface
        // in order to limit user input to mach pattern
        // which in this case is "xxx.xx"
        mPriceEditText.setFilters(new InputFilter[]{new TwoDigitsDecimalInputFilter(3, 2)});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate Options Menu from the resource
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_editor_save:
                if (saveProduct()) {
                    finish();
                }
                return true;
            case android.R.id.home:
                if (!hasProductChanged) {
                    // If user hasn't put any data, then navigate back to the previous activity
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    // otherwise show dialog
                    // if user clicked 'dismiss' button - navigate back from the same task
                    showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If any data changes, procees normal back press behaviour
        if (!hasProductChanged) {
            super.onBackPressed();
        } else {
            // Show dialog
            // If user clicked "discard" button - finish activity
            showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }

    /**
     * Setup the dropdown Spinner which allow the user to select category of the Product
     */

    private void setupSpinner() {

        mSpinner = findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_spinner_array, R.layout.spinner_item);

        mSpinner.setAdapter(spinnerAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_tshirt))) {
                        mCategoryValue = ClothesEntry.CATEGORY_TSHIRT;
                    } else if (selection.equals(getString(R.string.category_shirt))) {
                        mCategoryValue = ClothesEntry.CATEGORY_SHIRT;
                    } else if (selection.equals(getString(R.string.category_trousers))) {
                        mCategoryValue = ClothesEntry.CATEGORY_TROUSERS;
                    } else if (selection.equals(getString(R.string.category_skirt))) {
                        mCategoryValue = ClothesEntry.CATEGORY_SKIRT;
                    } else if (selection.equals(getString(R.string.category_dress))) {
                        mCategoryValue = ClothesEntry.CATEGORY_DRESS;
                    } else
                        mCategoryValue = ClothesEntry.CATEGORY_OTHER;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategoryValue = ClothesEntry.CATEGORY_OTHER;
            }
        });
    }

    /**
     * Collects all the information from the user input and save in into database
     *
     * @return true if information is valid and saved
     * false if information can not be saved
     */
    private boolean saveProduct() {

        // Get value from the Name Edit Text and check if is valid
        // Inform the user if not and return early from the method
        String nameString = mNameEditText.getText().toString();
        if (TextUtils.isEmpty(nameString) || nameString.length() < 2) {
            Toast.makeText(this, getString(R.string.toast_invalid_name), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get the value from the Price Edit Text and check if is valid
        // If not inform the user and return early from the method
        String priceString = mPriceEditText.getText().toString().trim();
        Double price;
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.toast_invalid_price), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            price = Double.parseDouble(priceString);
            if (price <= 0) {
                Toast.makeText(this, getString(R.string.toast_invalid_price_number), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        //Get the value from Quantity Edit Text and check if is valid
        String quantityString = mQuantityEditText.getText().toString().trim();
        Integer quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        // Get the value from the Supplier Edit Text and check if it's valid
        // If not inform the user and return early from the method
        String supplierNameString = mSupplierNameEditText.getText().toString();
        if (TextUtils.isEmpty(supplierNameString) || supplierNameString.length() < 2) {
            Toast.makeText(this, getString(R.string.toast_invalid_supplier), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get the value from the Supplier PhoneEdit Text and check if it's valid
        // If not inform the user and return early from the method
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString();
        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, getString(R.string.toast_invalid_phone), Toast.LENGTH_SHORT).show();
            return false;
        } else if (supplierPhoneString.length() < 9) {
            Toast.makeText(this, getString(R.string.toast_invalid_phone_digits), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Create ContentValues object for data provided by the user
        ContentValues values = new ContentValues();
        values.put(ClothesEntry.COLUMN_NAME, nameString);
        values.put(ClothesEntry.COLUMN_PRICE, price);
        values.put(ClothesEntry.COLUMN_QUANTITY, quantity);
        values.put(ClothesEntry.COLUMN_SUPPLIER, supplierNameString);
        values.put(ClothesEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        values.put(ClothesEntry.COLUMN_CATEGORY, mCategoryValue);

        // If Current Uri is null it meant that we are inserting new Product into database.
        if (mCurrentUri == null) {

            // Insert new pet into a database
            // Return Uri for newly created Product
            Uri newUri = getContentResolver().insert(ClothesEntry.CLOTHES_CONTENT_URI, values);

            // Check if insertion was successful
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.toast_new_insertion_failed), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this, getString(R.string.toast_new_insertion_successful), Toast.LENGTH_SHORT).show();
                return true;
            }
            // If mCurrentUri has a value it means that we are editing existing product
            // so we need to perform update instead of insert
        } else {

            // Perform update only if user changed any data
            if (hasProductChanged) {
                int numOfRowsUpdated = getContentResolver().update(mCurrentUri, values, null, null);

                if (numOfRowsUpdated == 0) {
                    Toast.makeText(this, getString(R.string.toast_update_failed), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    Toast.makeText(this, getString(R.string.toast_update_successful), Toast.LENGTH_SHORT).show();
                    return true;
                }

                // if user hasn't changed any data, do not perform update and inform the user
            } else {
                Toast.makeText(this, getString(R.string.toast_update_change_data), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    /**
     * Method that builds dialog to show if the user attempts to leave the activity without saving data
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_unsaved_message);
        builder.setNegativeButton(R.string.dialog_unsaved_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton(R.string.dialog_unsaved_positive_button, discardButtonClickListener);

        // Create and show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Use Loader to Load existing Product data into EditText views
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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
            String supplierPhone = cursor.getString(cursor.getColumnIndex(ClothesEntry.COLUMN_SUPPLIER_PHONE));
            int category = cursor.getInt(cursor.getColumnIndex(ClothesEntry.COLUMN_CATEGORY));

            // Set values on the EditText views
            mNameEditText.setText(productName);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierNameEditText.setText(supplier);
            mSupplierPhoneEditText.setText(supplierPhone);

            // Set spinner for a proper category
            switch (category) {
                case ClothesEntry.CATEGORY_TSHIRT:
                    mSpinner.setSelection(ClothesEntry.CATEGORY_TSHIRT);
                    break;
                case ClothesEntry.CATEGORY_SHIRT:
                    mSpinner.setSelection(ClothesEntry.CATEGORY_SHIRT);
                    break;
                case ClothesEntry.CATEGORY_TROUSERS:
                    mSpinner.setSelection(ClothesEntry.CATEGORY_TROUSERS);
                    break;
                case ClothesEntry.CATEGORY_SKIRT:
                    mSpinner.setSelection(ClothesEntry.CATEGORY_SKIRT);
                    break;
                case ClothesEntry.CATEGORY_DRESS:
                    mSpinner.setSelection(ClothesEntry.CATEGORY_DRESS);
                    break;
                default:
                    mSpinner.setSelection(ClothesEntry.CATEGORY_OTHER);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText(String.valueOf(""));
        mQuantityEditText.setText(String.valueOf(""));
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mSpinner.setSelection(ClothesEntry.CATEGORY_OTHER);
    }
}
