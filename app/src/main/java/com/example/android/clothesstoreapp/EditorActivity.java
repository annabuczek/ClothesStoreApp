package com.example.android.clothesstoreapp;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class EditorActivity extends AppCompatActivity {

    /** EditText for the Name of the Product */
    private EditText mNameEditText;

    /** EditText for the Price od the Product */
    private EditText mPriceEditText;

    /** EditText for the Quantity of the product */
    private EditText mQuantityEditText;

    /** EditText for the Supplier Name */
    private EditText mSupplierNameEditText;

    /** EditText for the Supplier Phone */
    private EditText mSupplierPhone;

    /** Spinner showing possible Product Categories */
    private Spinner mSpinner;

    // Constant to specify limit of digits user will be able to input into quantity edit text field.
    private static final int QUANTITY_MAX_LENGTH = 3;

    // Constant to specify limit of digits user will be able to input
    // into supplier phone number edit text field
    private static final int PHONE_NUMBER_MAX_LENGTH = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Find Views for further use
        mNameEditText = findViewById(R.id.name_edit_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mQuantityEditText = findViewById(R.id.quantity_edit_text);
        mSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        mSupplierPhone = findViewById(R.id.supplier_phone_edit_text);

        // Set InputFilter.LengthFilter on mQuantityEditText to limit number of characters user may input
        mQuantityEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(QUANTITY_MAX_LENGTH)});

        // Apply InputFilter.LengthFilter on mSupplierPhoneNumber to limit number of characters user may input
        mSupplierPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PHONE_NUMBER_MAX_LENGTH)});

        // Apply TwoDigitsDecimalInputFilter which implements InputFilter interface
        // in order to limit user input to mach pattern
        // which in this case is "xxx.xx"
        mPriceEditText.setFilters(new InputFilter[]{new TwoDigitsDecimalInputFilter(3, 2)});

        setupSpinner();

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
        switch(id) {
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

    /** Setup the dropdown Spinner which allow the user to select category of the Product */
    private void setupSpinner() {

        mSpinner = findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_spinner_array, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(spinnerAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /** Collects all the information from the user input and save in into database
     *
     * @return true if information is valid and saved
     *         false if information can not be saved
     */
    private boolean saveProduct() {

    return false;
    }
}
