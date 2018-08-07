package com.example.android.clothesstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.clothesstoreapp.data.ClothesContract.ClothesEntry;

/**
 * Class used to create database and version management.
 * Also used by ContentProvider to make connection with a database.
 */

public class ClothesDbHelper extends SQLiteOpenHelper {

    /**
     * The name of the whole database used by the Clothes Store App
     */
    private static final String DATABASE_NAME = "clothes_shop.db";

    /**
     * Version of the database.
     * Incremented when the schema changes.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Public constructor of {@link ClothesDbHelper}
     */
    public ClothesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time
     *
     * @param db object representing database, blank at the beginning
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String containing schema for clothes table creation
        String SQL_CREATE_TABLE_ENTRY = "CREATE TABLE " + ClothesEntry.TABLE_NAME + "("
                + ClothesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ClothesEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ClothesEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ClothesEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ClothesEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + ClothesEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + ClothesEntry.COLUMN_CATEGORY + " INTEGER NOT NULL);";

        // Execute statement to create a table.
        db.execSQL(SQL_CREATE_TABLE_ENTRY);
    }

    /**
     * Called when the database needs to be upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // String for SQL statement deleting the table.
        String SQL_DELETE_TABLE_ENTRY = "DROP TABLE " + ClothesEntry.TABLE_NAME;
        // Delete and then recreate table if version changed.
        db.execSQL(SQL_DELETE_TABLE_ENTRY);
        onCreate(db);
    }
}
