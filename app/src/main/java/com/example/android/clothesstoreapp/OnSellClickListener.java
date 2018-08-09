package com.example.android.clothesstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.clothesstoreapp.data.ClothesContract;

/**
 * Custom implementation of OnClickListener
 */

public class OnSellClickListener implements View.OnClickListener {

    /**
     * Context of the app
     */
    private Context mContext;

    /**
     * Variable storing Integer value of the current quantity of the product
     */
    private int mQuantity;

    /**
     * Variable storing the id value in the database of the current product
     */
    private int mId;

    public OnSellClickListener(Context context, int quantity, int id) {
        mContext = context;
        mQuantity = quantity;
        mId = id;
    }

    // Triggered when user click on the sellButton which OnSellClickListener is attached to.
    @Override
    public void onClick(View v) {

        Log.d("OnSellClickListener", "Show id " + mId);
        // if quantity value is 0, then nothing changes
        if (mQuantity <= 0) {
            Toast.makeText(mContext, mContext.getString(R.string.toast_sell_zero), Toast.LENGTH_SHORT).show();

        } else {
            // In other case we lower quantity by 1
            mQuantity -= 1;

            // Put new value in ContentValues
            ContentValues values = new ContentValues();
            values.put(ClothesContract.ClothesEntry.COLUMN_QUANTITY, mQuantity);

            // And update product quantity
            mContext.getContentResolver().update(ContentUris.withAppendedId(ClothesContract.ClothesEntry.CLOTHES_CONTENT_URI,
                    mId), values, null, null);

            Toast.makeText(mContext, mContext.getString(R.string.toast_sell_sold), Toast.LENGTH_SHORT).show();
        }
    }
}
