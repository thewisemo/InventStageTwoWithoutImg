package com.example.wisemo.myinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wisemo.myinventory.data.ItemContract.ItemEntry;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Loader constant
    private static final int ITEM_LOADER = 0;

    // CursorAdapter global variable
    ProductsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.add_item);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the listView which will be populated with the item data.
        ListView itemsListView = findViewById(R.id.list);

        // Find & setting the empty view layout
        View emptyView = findViewById(R.id.empty_view);
        itemsListView.setEmptyView(emptyView);

        // Setup an adapter to create a list item for each row of products items data in the Cursor.
        // There is no item data till the loader finishes so Cursor is passed as null.
        mCursorAdapter = new ProductsCursorAdapter(this, null);
        itemsListView.setAdapter(mCursorAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    // This helper method to reduce the item quantity by one item.
    public void quantitySale(int itemID, int itemQuantity) {
        itemQuantity--;
        if (itemQuantity >= 50) {
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
            Uri updateUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, itemID);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, "Quantity was reduced by one piece", Toast.LENGTH_SHORT).show();
            // Log to show the number of affected rows in the inventory database
            Log.d("Log msg", "rowsAffected " + rowsAffected + " with ID " + itemID + " Which Quantity value is " + itemQuantity + " has been reduced.");
        } else if (itemQuantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
            Uri updateUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, itemID);
            getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, "Minimum quantity reached, Please order from the supplier", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // Create a factory object as a LayoutInflater from the MainActivity.class
                LayoutInflater factory = LayoutInflater.from(InventoryActivity.this);
                // Create a View object to the factory LayoutInflater and use the .inflate method to the about_layout.xml, null for the root view.
                final View aboutDialogView = factory.inflate(R.layout.about_layout, null);
                // Create an AlertDialog object and pass this as the .Builder context params then create it by .create().
                final AlertDialog aboutDialog = new AlertDialog.Builder(InventoryActivity.this).create();
                // Sets the AlertDialog view to the aboutDialogView View object.
                aboutDialog.setView(aboutDialogView);
                // Assign a setOnClickListener (new View) to the Linkedin button to open my in URL with an ACTION_VIEW intent, and leave the user to choose a browser.
                aboutDialogView.findViewById(R.id.in_connect_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String moInUrl = "https://www.linkedin.com/in/wisemo/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(moInUrl));
                        startActivity(i);
                        aboutDialog.dismiss();
                    }
                });
                // Assign a setOnClickListener (new View) to dismiss the about view.
                aboutDialogView.findViewById(R.id.about_back_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aboutDialog.dismiss();
                    }
                });
                // Make the About view background transparent.
                aboutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                aboutDialog.show();
                break;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
            default:
                break;
        }
        return true;
    }

    /**
     * Helper method to delete all items in the database.
     */
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
        Log.v("InventoryActivity", rowsDeleted + " rows deleted from inventory database");
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteAllItems();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_CATEGORY_TYPE,
                ItemEntry.COLUMN_ITEM_PRODUCT_NAME,
                ItemEntry.COLUMN_ITEM_DESCRIPTION,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SUPPLIER_NAME,
                ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,      // Parent activity context
                ItemEntry.CONTENT_URI,             // Provider content URI to query
                projection,                        // Columns to include in the resulting cursor
                null,                     // No selection clause
                null,                  // No selection arguments
                null);                   // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link ProductsCursorAdapter} with this new cursor containing updated item data.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // This callback called when data need to be deleted
        mCursorAdapter.swapCursor(null);
    }
}