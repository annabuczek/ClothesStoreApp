package com.example.android.clothesstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * CntentProvider for Clothes Store App.
 */

public class ClothesProvider extends ContentProvider {

    /** ClothesDbHelper object used to access database */
    private ClothesDbHelper mDbelper;

    /** Uri matcher code for the clothes table content Uri */
    private final static int CLOTHES_CASE = 100;

    /** Uri matcher code for the content Uri corresponding with the single cloth row */
    private final static int CLOTHES_ID_CASE = 101;

    /** Uri Matcher responsible for checking content Uri and returning particular case code */
    private final static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Add all of the patterns for Content Uri that ContentProvider should recognize.

        /**Uri matching this pattern will be used to access multiple rows from the clothes table.
         * UriMatcher will return code {@link CLOTHES_CASE}
         */
        mUriMatcher.addURI(ClothesContract.CONTENT_AUTHORITY, ClothesContract.CLOTHES_PATH, CLOTHES_CASE);

        /**Uri matching this pattern will be used to access only 1 row from the clothes table.
         * # stands for integer number of the id of the row
         * UriMatcher will return code {@link CLOTHES_ID_CASE}
         */
        mUriMatcher.addURI(ClothesContract.CONTENT_AUTHORITY, ClothesContract.CLOTHES_PATH + "/#", CLOTHES_ID_CASE);
    }

    @Override
    public boolean onCreate() {
        // Create new instance of ClothesDbHelper object to be able to access database.
        mDbelper = new ClothesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
