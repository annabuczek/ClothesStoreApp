package com.example.android.clothesstoreapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

/**
 * Adapter class used to populate list in CatalogActivity with data
 * which comes as a Cursor object.
 * Each list item correspond with a single product.
 */

public class ClothesCursorAdapter extends CursorAdapter {

    /**
     * Constructs new {@link ClothesCursorAdapter} object
     *
     * @param context context of the app
     * @param cursor  cursor to get data from
     */
    public ClothesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * Inflates new View from provided layout
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.catalog_list_item, parent, false);
    }

    /**
     * Populate View with the data from Cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find Views in newly created or reused View,
        // which will be populated with data from the Cursor.
        TextView productNameTv = view.findViewById(R.id.list_item_name);
        TextView quantityTv = view.findViewById(R.id.list_item_quantity);
        TextView categoryTv = view.findViewById(R.id.list_item_category);
        TextView priceTv = view.findViewById(R.id.list_item_price);
        Button sellButton = view.findViewById(R.id.list_item_sell_button);

        // Get Product Name String from the Cursor
        String productNameString = cursor.getString(cursor.getColumnIndex(ClothesEntry.COLUMN_NAME));
        // Get Quantity int from the Cursor and parse to String
        int quantity = cursor.getInt(cursor.getColumnIndex(ClothesEntry.COLUMN_QUANTITY));
        String quantityString = String.valueOf(quantity);
        // Get Product Category String using helper method.
        String categoryString = findCategoryString(cursor, context);
        // Get Price double value from the Cursor and parse to String
        String priceString = String.valueOf(cursor.getDouble(cursor.getColumnIndex(ClothesEntry.COLUMN_PRICE)));
        int id = cursor.getInt(cursor.getColumnIndex(ClothesEntry._ID));

        // Populate Views with data.
        productNameTv.setText(productNameString);
        quantityTv.setText(quantityString);
        categoryTv.setText(categoryString);
        priceTv.setText(priceString);

        /** Set {@link OnSellClickListener} on a sellButton to update the quantity of the Product */
        sellButton.setOnClickListener(new OnSellClickListener(context, quantity, id));
    }

    /**
     * Product Category is saved in database as a INTEGER data type.
     * This method match INTEGER value with corresponding String value.
     * @param cursor cursor object to get data from
     * @param context context of the app
     * @return String value for Category of the Product
     */
    private String findCategoryString(Cursor cursor, Context context) {

        int categoryInt = cursor.getInt(cursor.getColumnIndex(ClothesEntry.COLUMN_CATEGORY));
        String categoryString;

        switch (categoryInt) {
            case ClothesEntry.CATEGORY_TSHIRT:
                categoryString = context.getString(R.string.category_tshirt);
                break;
            case ClothesEntry.CATEGORY_SHIRT:
                categoryString = context.getString(R.string.category_shirt);
                break;
            case ClothesEntry.CATEGORY_TROUSERS:
                categoryString = context.getString(R.string.category_trousers);
                break;
            case ClothesEntry.CATEGORY_SKIRT:
                categoryString = context.getString(R.string.category_skirt);
                break;
            case ClothesEntry.CATEGORY_DRESS:
                categoryString = context.getString(R.string.category_dress);
                break;
            default:
                categoryString = context.getString(R.string.category_other);
        }
        return categoryString;
    }
}
