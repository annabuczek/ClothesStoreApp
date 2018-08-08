package com.example.android.clothesstoreapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

public class EditorActivity extends AppCompatActivity {

    // Constant to specify limit of digits user will be able to input into quantity edit text field.
    private static final int QUANTITY_MAX_LENGTH = 3;
    // Constant to specify limit of digits user will be able to input
    // into supplier phone number edit text field
    private static final int PHONE_NUMBER_MAX_LENGTH = 9;
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

        // Set InputFilter.LengthFilter on mQuantityEditText to limit number of characters user may input
        mQuantityEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(QUANTITY_MAX_LENGTH)});

        // Apply InputFilter.LengthFilter on mSupplierPhoneNumber to limit number of characters user may input
        mSupplierPhoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PHONE_NUMBER_MAX_LENGTH)});

        // Apply TwoDigitsDecimalInputFilter which implements InputFilter interface
        // in order to limit user input to mach pattern
        // which in this case is "xxx.xx"
        mPriceEditText.setFilters(new InputFilter[]{new TwoDigitsDecimalInputFilter(3, 2)});

        setupSpinner();

        if (mCurrentUri == null) {
            setTitle(R.string.editor_activity_add);
        }

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
                boolean isSaved = saveProduct();
                if (isSaved) {
                    finish();
                }
                return true;
            case android.R.id.home:
                // Navigate back to the previous activity
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup the dropdown Spinner which allow the user to select category of the Product
     */
    private void setupSpinner() {

        mSpinner = findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_spinner_array, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
        }
        return false;
    }
}
