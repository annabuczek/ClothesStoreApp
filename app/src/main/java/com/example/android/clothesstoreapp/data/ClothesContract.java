package com.example.android.clothesstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Api contract for the Clothes Shop App
 */

public class ClothesContract {

    /**
     * Content Authority unique for the app, part of Uri used to make connection with ContentProvider
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.clothesstoreapp";
    /**
     * Basic Uri which will be used by the app to connect with ContentProvider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Clothes database table path
     */
    public static final String CLOTHES_PATH = "clothes";

    /**
     * Private Constructor as this class should never be instantiated
     */
    private ClothesContract() {
    }

    /**
     * Inner class containing constants for the Clothes database table
     * Each entry will represent single product.
     */
    public static final class ClothesEntry implements BaseColumns {

        /**
         * Uri which will be used by the app to connect with clothes table, through ContentProvider
         */
        public static final Uri CLOTHES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CLOTHES_PATH);

        /** The MIME type of the {@link #CLOTHES_CONTENT_URI} for a list of Products */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + CLOTHES_PATH;

        /** The MIME type of the {@link #CLOTHES_CONTENT_URI} for a list of Products */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + CLOTHES_PATH;

        /**
         * The name of the table in the database
         */
        public static final String TABLE_NAME = "clothes";

        /**
         * Column for the Name of the Product.
         * Type: TEXT
         */
        public static final String COLUMN_NAME = "product_name";

        /**
         * Column for the Product Price.
         * Type: INTEGER
         */
        public static final String COLUMN_PRICE = "price";

        /**
         * Column for the Product quantity.
         * Type: INTEGER
         */
        public static final String COLUMN_QUANTITY = "quantity";

        /**
         * Column for the Name of the Product Supplier
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER = "supplier";

        /**
         * Column for the Phone Number of the Product Supplier
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        /**
         * Column for the category of the Product
         * Type: INTEGER
         */
        public static final String COLUMN_CATEGORY = "category";

        /**
         * Constants representing possible integer values for each Product
         * Also integer values to store in Shared Preferences
         */
        public static final int CATEGORY_OTHER = 0;
        public static final int CATEGORY_TSHIRT = 1;
        public static final int CATEGORY_SHIRT = 2;
        public static final int CATEGORY_TROUSERS = 3;
        public static final int CATEGORY_SKIRT = 4;
        public static final int CATEGORY_DRESS = 5;
        /**
         * Constant representing all products to store in Shared Preferences
         */
        public static final int CATEGORY_ALL = 6;

        /**
         * Helper method for checking is category is valid
         *
         * @return true if categoryCode is valid or false if is not.
         */
        public static boolean isValidCategory(int categoryCode) {
            if (categoryCode == CATEGORY_OTHER || categoryCode == CATEGORY_TSHIRT
                    || categoryCode == CATEGORY_SHIRT || categoryCode == CATEGORY_TROUSERS
                    || categoryCode == CATEGORY_SKIRT || categoryCode == CATEGORY_DRESS) {
                return true;
            }
            return false;
        }
    }
}
