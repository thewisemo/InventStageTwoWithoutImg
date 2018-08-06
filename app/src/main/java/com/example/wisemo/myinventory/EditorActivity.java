package com.example.wisemo.myinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wisemo.myinventory.data.ItemContract.ItemEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Loader constant
    private static final int EXISTING_ITEM_LOADER = 0;

    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentItemUri;

    /* Spinner field to enter the product item's category Type */
    private Spinner mTypeSpinner;

    /* EditText field to enter the product item's Name */
    private EditText mProductNameEditText;

    /* EditText field to enter the product item's Description */
    private EditText mDescriptionEditText;

    /* EditText field to enter the product item's Price */
    private EditText mPriceEditText;

    /* Spinner field to enter the product item's Availability */
    private Spinner mInStockSpinner;

    /* EditText field to enter the product item's Quantity */
    private EditText mQuantityEditText;

    /* Spinner field to enter the product item's Supplier Name */
    private Spinner mSupplierNameSpinner;

    /* EditText field to enter the product item's Supplier Phone Number */
    private EditText mSupplierPhoneNumberEditText;

    /* Quantity change buttons */
    private Button decQuantityBtn;
    private Button incQuantityBtn;

    /**
     * Type of the item. The possible valid values are in the ItemContract.java file:
     * {@link ItemEntry#TYPE_CASUAL}, OR
     * {@link ItemEntry#TYPE_FORMAL}, OR
     * {@link ItemEntry#TYPE_COTTON_SOCKS}, OR
     * {@link ItemEntry#TYPE_CARE_PRODUCTS}, OR
     * {@link ItemEntry#TYPE_BELTS_AND_WALLETS}.
     */
    private int mType = ItemEntry.TYPE_CASUAL;

    /**
     * Availability of the item. The possible valid values are in the ItemContract.java file:
     * {@link ItemEntry#ITEM_IN_STOCK}, OR
     * {@link ItemEntry#ITEM_OUT_STOCK}.
     */
    private int mInStock = ItemEntry.ITEM_IN_STOCK;

    /**
     * Suppliers of the item. The possible valid values are in the ItemContract.java file:
     * * {@link ItemEntry#SUPPLIER_MAIN}, OR
     * {@link ItemEntry#SUPPLIER_NUMBER_ONE}, OR
     * {@link ItemEntry#SUPPLIER_NUMBER_TWO}, OR
     * {@link ItemEntry#SUPPLIER_NUMBER_THREE}.
     */
    private int mSupplierName = ItemEntry.SUPPLIER_MAIN;

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mItemHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Use getIntent() & getData() to get the associated URI
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // if the intent doesn't contain an item content URI
        // the we add a new item
        if (mCurrentItemUri == null) {
            // This is the new product case (Fab button is clicked)
            setTitle(getString(R.string.editor_activity_title_new_product));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // This is the edit product case
            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTypeSpinner = findViewById(R.id.spinner_category_type);
        mProductNameEditText = findViewById(R.id.edit_item_name);
        mDescriptionEditText = findViewById(R.id.edit_item_description);
        mPriceEditText = findViewById(R.id.edit_item_price);
        mInStockSpinner = findViewById(R.id.spinner_stock_availability);
        mQuantityEditText = findViewById(R.id.edit_item_quantity);
        // Quantity increment button
        incQuantityBtn = findViewById(R.id.increment_btn);
        // Quantity decrement button
        decQuantityBtn = findViewById(R.id.decrement_btn);
        mSupplierNameSpinner = findViewById(R.id.spinner_item_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_item_phone_number);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mInStockSpinner.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameSpinner.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        incQuantityBtn.setOnTouchListener(mTouchListener);
        decQuantityBtn.setOnTouchListener(mTouchListener);

        setupTypeSpinner();
        setupAvailabilitySpinner();
        setupSupplierSpinner();
    }

    /* Setup the dropdown spinner that allows the user to select the item Type. */
    private void setupTypeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(typeSpinnerAdapter);
        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_belts_and_wallets))) {
                        mType = ItemEntry.TYPE_BELTS_AND_WALLETS; // Belts And Wallets
                    } else if (selection.equals(getString(R.string.type_care_products))) {
                        mType = ItemEntry.TYPE_CARE_PRODUCTS;  // Care Products
                    } else if (selection.equals(getString(R.string.type_cotton_socks))) {
                        mType = ItemEntry.TYPE_COTTON_SOCKS;  // Cotton Socks
                    } else if (selection.equals(getString(R.string.type_life_style))) {
                        mType = ItemEntry.TYPE_LIFE_STYLE;  // Life style
                    } else if (selection.equals(getString(R.string.type_formal))) {
                        mType = ItemEntry.TYPE_FORMAL;  // Formal
                    } else {
                        mType = ItemEntry.TYPE_CASUAL;  // Casual
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = ItemEntry.TYPE_CASUAL;
            }
        });
    }

    /* Setup the dropdown spinner that allows the user to select the item Availability. */
    private void setupAvailabilitySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter availabilitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_availability_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        availabilitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        mInStockSpinner.setAdapter(availabilitySpinnerAdapter);
        // Set the integer mSelected to the constant values
        mInStockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.item_in_stock))) {
                        mInStock = ItemEntry.ITEM_IN_STOCK; // In Stock
                    } else {
                        mInStock = ItemEntry.ITEM_OUT_STOCK; // Out Of Stock
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mInStock = ItemEntry.ITEM_OUT_STOCK;
            }
        });
    }

    /* Setup the dropdown spinner that allows the user to select the item Supplier Name. */
    private void setupSupplierSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_name_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        mSupplierNameSpinner.setAdapter(supplierSpinnerAdapter);
        // Set the integer mSelected to the constant values
        mSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_number_one))) {
                        mSupplierName = ItemEntry.SUPPLIER_NUMBER_ONE; // Number One
                        mSupplierPhoneNumberEditText.setText(R.string.phone_supplier_number_one);
                    } else if (selection.equals(getString(R.string.supplier_number_two))) {
                        mSupplierName = ItemEntry.SUPPLIER_NUMBER_TWO;  // Number Two
                        mSupplierPhoneNumberEditText.setText(R.string.phone_supplier_number_two);
                    } else if (selection.equals(getString(R.string.supplier_number_three))) {
                        mSupplierName = ItemEntry.SUPPLIER_NUMBER_THREE;  // Number Three
                        mSupplierPhoneNumberEditText.setText(R.string.phone_supplier_number_three);
                    } else {
                        mSupplierName = ItemEntry.SUPPLIER_MAIN; // Main Supplier
                        mSupplierPhoneNumberEditText.setText(R.string.phone_supplier_main);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSupplierName = ItemEntry.SUPPLIER_MAIN;
            }
        });
    }

    /* Get user input from editor and save new item into database. */
    private void saveItem() {
        // Read from input fields
        String itemNameString = mProductNameEditText.getText().toString().trim();
        String itemDescriptionString = mDescriptionEditText.getText().toString().trim();
        String itemPriceString = mPriceEditText.getText().toString().trim();
        String itemQuantityString = mQuantityEditText.getText().toString().trim();
        // TODO show the supplier phone number once the user select the supplier name_tv from its spinner
        String itemSupplierPhoneString = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(itemNameString) && TextUtils.isEmpty(itemDescriptionString) &&
                TextUtils.isEmpty(itemPriceString) && TextUtils.isEmpty(itemQuantityString)) {
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object "values" where column names are the keys,
        // and product item attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY_TYPE, mType);
        values.put(ItemEntry.COLUMN_ITEM_PRODUCT_NAME, itemNameString);
        values.put(ItemEntry.COLUMN_ITEM_DESCRIPTION, itemDescriptionString);
        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 80 by default.
        int price = 80;
        if (!TextUtils.isEmpty(itemPriceString)) {
            price = Integer.parseInt(itemPriceString);
        }
        values.put(ItemEntry.COLUMN_ITEM_PRICE, price);
        values.put(ItemEntry.COLUMN_ITEM_AVAILABILITY, mInStock);
        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 50 by default.
        int quantity = 50;
        if (!TextUtils.isEmpty(itemQuantityString)) {
            quantity = Integer.parseInt(itemQuantityString);
        }
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME, mSupplierName);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER, itemSupplierPhoneString);

        // Determine if this is a new or existing item by checking if mCurrentPetUri is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW product, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the item with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product item to database
                saveItem();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
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
                ItemEntry.COLUMN_ITEM_AVAILABILITY,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SUPPLIER_NAME,
                ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,      // Parent activity context
                mCurrentItemUri,             // Provider content URI to query
                projection,                        // Columns to include in the resulting cursor
                null,                     // No selection clause
                null,                  // No selection arguments
                null);                   // Default sort order
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            final int itemColumnId = cursor.getColumnIndex(ItemEntry._ID);
            int typeColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_CATEGORY_TYPE);
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRODUCT_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_DESCRIPTION);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int availabilityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_AVAILABILITY);
            final int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            int type = cursor.getInt(typeColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int availability = cursor.getInt(availabilityColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierColumnIndex);
            final int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(name);
            mDescriptionEditText.setText(description);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierPhoneNumberEditText.setText(Integer.toString(supplierPhone));

            // Type is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Casual, 1 is Formal, 2 is Cotton Socks ,.. etc in the ItemContract.java).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (type) {
                case ItemEntry.TYPE_FORMAL:
                    mTypeSpinner.setSelection(1);
                    break;
                case ItemEntry.TYPE_LIFE_STYLE:
                    mTypeSpinner.setSelection(2);
                    break;
                case ItemEntry.TYPE_COTTON_SOCKS:
                    mTypeSpinner.setSelection(3);
                    break;
                case ItemEntry.TYPE_CARE_PRODUCTS:
                    mTypeSpinner.setSelection(4);
                    break;
                case ItemEntry.TYPE_BELTS_AND_WALLETS:
                    mTypeSpinner.setSelection(5);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }

            // InStock spinner setters
            switch (availability) {
                case ItemEntry.ITEM_IN_STOCK:
                    mInStockSpinner.setSelection(1);
                    break;
                default:
                    mInStockSpinner.setSelection(0);
                    break;
            }

            // Supplier Name spinner setters
            switch (supplierName) {
                case ItemEntry.SUPPLIER_NUMBER_ONE:
                    mSupplierNameSpinner.setSelection(1);
                    break;
                case ItemEntry.SUPPLIER_NUMBER_TWO:
                    mSupplierNameSpinner.setSelection(2);
                    break;
                case ItemEntry.SUPPLIER_NUMBER_THREE:
                    mSupplierNameSpinner.setSelection(3);
                    break;
                default:
                    mSupplierNameSpinner.setSelection(0);
                    break;
            }

            // Quantity increment button click listener
            incQuantityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    incItemQuantity(itemColumnId, quantity);
                }
            });

            // Quantity decrement button click listener
            decQuantityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    decItemQuantity(itemColumnId, quantity);
                }
            });

            // Call button
            Button callSupplier = findViewById(R.id.call_btn);
            // CallBtn click listener
            callSupplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = String.valueOf(supplierPhone);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });
        }
    }

    // Helper method that increase the item quantity by one item
    public void incItemQuantity(int itemID, int itemQuantity) {
        itemQuantity++;
        if (itemQuantity >= 0) {
            updateItemQuantity(itemQuantity);

            Log.d("Log msg", " Item " + itemID + "which quantity " + itemQuantity + " increment has been called.");
        }
    }

    // Helper method that increase the item quantity by one item
    public void decItemQuantity(int itemID, int itemQuantity) {
        itemQuantity--;
        if (itemQuantity >= 0) {
            updateItemQuantity(itemQuantity);

            Log.d("Log msg", " Item " + itemID + "which quantity " + itemQuantity + " decrement has been called.");
        }
    }

    // Helper method that update the quantity with the current item URI
    private void updateItemQuantity(int itemQuantity) {

        if (mCurrentItemUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, itemQuantity);

        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTypeSpinner.setSelection(0);
        mProductNameEditText.setText("");
        mDescriptionEditText.setText("");
        mPriceEditText.setText("");
        mInStockSpinner.setSelection(0);
        mQuantityEditText.setText("");
        mSupplierNameSpinner.setSelection(0);
        mSupplierPhoneNumberEditText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
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

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}