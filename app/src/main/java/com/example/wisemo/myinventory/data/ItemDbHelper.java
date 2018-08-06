package com.example.wisemo.myinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wisemo.myinventory.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the products items table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_CATEGORY_TYPE + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_PRODUCT_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_DESCRIPTION + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_ITEM_AVAILABILITY + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_ITEM_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER + " NUM NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}