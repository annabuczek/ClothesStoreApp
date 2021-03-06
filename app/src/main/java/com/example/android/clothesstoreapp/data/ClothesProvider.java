package com.example.android.clothesstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

/**
 * CntentProvider for Clothes Store App.
 */

public class ClothesProvider extends ContentProvider {

    /**
     * Uri matcher code for the clothes table content Uri
     */
    private final static int CLOTHES_CASE = 100;
    /**
     * Uri matcher code for the content Uri corresponding with the single cloth row
     */
    private final static int CLOTHES_ID_CASE = 101;
    /**
     * Uri Matcher responsible for checking content Uri and returning particular case code
     */
    private final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Add all of the patterns for Content Uri that ContentProvider should recognize.

        /**Uri matching this pattern will be used to access multiple rows from the clothes table.
         * UriMatcher will return code {@link CLOTHES_CASE}
         */
        sUriMatcher.addURI(ClothesContract.CONTENT_AUTHORITY, ClothesContract.CLOTHES_PATH, CLOTHES_CASE);

        /**Uri matching this pattern will be used to access only 1 row from the clothes table.
         * # stands for integer number of the id of the row
         * UriMatcher will return code {@link CLOTHES_ID_CASE}
         */
        sUriMatcher.addURI(ClothesContract.CONTENT_AUTHORITY, ClothesContract.CLOTHES_PATH + "/#", CLOTHES_ID_CASE);
    }

    /**
     * Tag for Logs
     */
    private String LOG_TAG = ClothesProvider.class.getSimpleName();
    /**
     * ClothesDbHelper object used to access database
     */
    private ClothesDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        // Create new instance of ClothesDbHelper object to be able to access database.
        mDbHelper = new ClothesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get database in readable mode.
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Check Content Uri using UriMatcher and get the code corresponding with the given ContentUri
        int match = sUriMatcher.match(uri);

        // Cursor object which will be storing data from the query.
        Cursor cursor;

        switch (match) {
            case CLOTHES_CASE:
                // Perform query on the database and return Cursor object.
                // Query performed on the whole table returning Cursor containing multiple rows.
                cursor = db.query(ClothesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case CLOTHES_ID_CASE:
                // Query performed for the single product ContentUri.
                // If successful will return Cursor containing only one row.
                // selection and selectionArgs are defined to perform query for according to the
                // ContentUri by given Product ID
                selection = ClothesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(ClothesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot perform query - given Uri unknown  " + uri);
        }

        // Set notifications on a given uri to inform the loader about changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        // Check ContentUri to get the code corresponding with the given ContentUri
        int match = sUriMatcher.match(uri);

        switch (match) {
            case CLOTHES_CASE:
                return insertNewProduct(uri, values);
            default:
                throw new IllegalArgumentException("Can not perform insertion - given Uri unknown " + uri);
        }
    }

    /**
     * Helper method for performing insertion of new Product into database
     *
     * @return Content Uri value for the newly inserted Product.
     */
    private Uri insertNewProduct(Uri uri, ContentValues values) {

        // Check if the data which is going to be inserted into database in not null.
        // All the fields in the new database row must be filled with data.
        String productName = values.getAsString(ClothesEntry.COLUMN_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Double price = values.getAsDouble(ClothesEntry.COLUMN_PRICE);
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        Integer quantity = values.getAsInteger(ClothesEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        String supplier = values.getAsString(ClothesEntry.COLUMN_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Product requires a supplier name");
        }

        String supplierPhone = values.getAsString(ClothesEntry.COLUMN_SUPPLIER);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Product requires a supplier phone number");
        }

        Integer category = values.getAsInteger(ClothesEntry.COLUMN_CATEGORY);
        if (category == null || !ClothesEntry.isValidCategory(category)) {
            throw new IllegalArgumentException("Product requires a valid category");
        }

        // Get database in writable mode to be able to insert data into it.
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Perform insertion.
        // Return newRowId number which will be id for the newly inserted product.
        long newRowId = db.insert(ClothesEntry.TABLE_NAME, null, values);

        // If the insertion isn't successful, newIdRow will be -1
        // Then return null
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert new Product for Uri " + uri);
            return null;
        }

        // Notify changes for a given uri to reload Cursor
        getContext().getContentResolver().notifyChange(uri, null);

        // Return newly created full ContentUri for new Product inserted into database
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Check the Content Uri against Uri Matcher to see if it falls in one of the specified cases
        int match = sUriMatcher.match(uri);

        // Set database in a writable mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int numOfRowsDeleted;

        switch (match) {
            // Perform deletion on the whole table based on gives selection and selection arguments
            // Return number of rows deleted
            case CLOTHES_CASE:
                numOfRowsDeleted = db.delete(ClothesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // Delete single row in the tale based on given ContentUri
            case CLOTHES_ID_CASE:
                // Selection will be ID of the Product
                selection = ClothesEntry._ID + "=?";
                // Selection argument will ve ID value extracted from the ContentUri
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numOfRowsDeleted = db.delete(ClothesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Con not delete for uri " + uri);
        }

        // if there were any rows deleted, then send notification to the CursorLoader
        // to reload the Cursor
        if (numOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Check the Content Uri against Uri Matcher to see if it falls in one of the specified cases
        int match = sUriMatcher.match(uri);

        switch (match) {
            // Update data in the whole table
            case CLOTHES_CASE:
                return updateProduct(uri, values, selection, selectionArgs);
            // Update data for the Content Uri for a single product
            case CLOTHES_ID_CASE:
                // Selection will be ID of the Product
                selection = ClothesEntry._ID + "=?";
                // Selection argument will ve ID value extracted from the ContentUri
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not possible for uri " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        // Perform sanity check for all of the values to prevent from inserting null values into database
        if (values.containsKey(ClothesEntry.COLUMN_NAME)) {
            String name = values.getAsString(ClothesEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(ClothesEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ClothesEntry.COLUMN_PRICE);
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if (values.containsKey(ClothesEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(ClothesEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Product requires a quantity");
            }
        }

        if (values.containsKey(ClothesEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(ClothesEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires a supplier");
            }
        }

        if (values.containsKey(ClothesEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(ClothesEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Product requires Supplier Phone");
            }
        }

        if (values.containsKey(ClothesEntry.COLUMN_CATEGORY)) {
            Integer category = values.getAsInteger(ClothesEntry.COLUMN_CATEGORY);
            if (category == null || !ClothesEntry.isValidCategory(category)) {
                throw new IllegalArgumentException("Product needs a valid category");
            }
        }

        // No need to update database if there are no values to put into in
        if (values.size() == 0) {
            return 0;
        }

        // Set database in a writable mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Update product.
        // Return number of rows updated
        int numOfRowsUpdated = db.update(ClothesEntry.TABLE_NAME, values, selection, selectionArgs);

        // If numOfRowsUpdated was different than 0, it means there were some products updated,
        // then send notification to the CursorLoader that data for Uri changed and Cursor must be reloaded
        if (numOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numOfRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        // Check the Content Uri against Uri Matcher to see if it falls in one of the specified cases
        int match = sUriMatcher.match(uri);

        switch (match) {
            case CLOTHES_CASE:
                return ClothesEntry.CONTENT_LIST_TYPE;
            case CLOTHES_ID_CASE:
                return ClothesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown Uri " + uri + "with match " + match);
        }
    }
}
