package com.example.wisemo.myinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/** API Contract for the Inventory app. */
public final class ItemContract {

    // Private empty constructor.
    private ItemContract() {
    }

    /**
     * The "Content authority" is a name_tv for the entire content provider, similar to the
     * relationship between a domain name_tv and its website.  A convenient string to use for the
     * content authority is the package name_tv for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.wisemo.myinventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.wisemo.myinventory/items/ is a valid path for
     * looking at item data. content://com.example.wisemo.myinventory/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_ITEMS = "items";

    /* Inner class that defines the ItemEntry table contents */
    public static class ItemEntry implements BaseColumns {

        /** The content URI to access the item data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /** Name of database table for items */
        public static final String TABLE_NAME = "items";

        /**
         * Unique ID number for the item (only for use in the database table).
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ITEM_CATEGORY_TYPE = "type";
        public static final String COLUMN_ITEM_PRODUCT_NAME = "product_name";
        public static final String COLUMN_ITEM_DESCRIPTION = "description";
        public static final String COLUMN_ITEM_AVAILABILITY = "availability";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_ITEM_SUPPLIER_PHONE_NUMBER = "phone_number";

        /* Additional constance for product item type */
        public static final int TYPE_CASUAL = 0;
        public static final int TYPE_FORMAL = 1;
        public static final int TYPE_LIFE_STYLE = 2;
        public static final int TYPE_COTTON_SOCKS = 3;
        public static final int TYPE_CARE_PRODUCTS = 4;
        public static final int TYPE_BELTS_AND_WALLETS = 5;

        public static final int ITEM_IN_STOCK = 1;
        public static final int ITEM_OUT_STOCK = 0;

        /* Additional constance for product item Suppliers Names*/
        public final static int SUPPLIER_MAIN = 0;
        public final static int SUPPLIER_NUMBER_ONE = 1;
        public final static int SUPPLIER_NUMBER_TWO = 2;
        public final static int SUPPLIER_NUMBER_THREE = 3;

        /**
         * Returns whether or not the given availability is {@link #ITEM_IN_STOCK}, {@link #ITEM_OUT_STOCK},
         */
        public static boolean isValidAvailability(int availability) {
            if (availability == ITEM_OUT_STOCK || availability == ITEM_IN_STOCK) {
                return true;
            }
            return false;
        }
    }


}
