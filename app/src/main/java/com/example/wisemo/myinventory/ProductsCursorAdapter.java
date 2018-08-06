package com.example.wisemo.myinventory;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.wisemo.myinventory.data.ItemContract;

/**
 * {@link ProductsCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product item data as its data source. This adapter knows
 * how to create list items for each row of item data in the {@link Cursor}.
 */
public class ProductsCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductsCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name_tv for the current item can be set on the item name_tv TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView itemTypeTv = view.findViewById(R.id.type_tv);
        TextView itemNameTv = view.findViewById(R.id.name_tv);
        TextView itemDescriptionTv = view.findViewById(R.id.description_tv);
        TextView itemPriceTv = view.findViewById(R.id.price_tv);
        TextView itemQuantityTv = view.findViewById(R.id.quantity_tv);
        TextView supplierNameTv = view.findViewById(R.id.supplier_name_tv);
        TextView supplierPhoneTv = view.findViewById(R.id.phone_tv);
        Button saleBtn = view.findViewById(R.id.sale_btn);
        Button editItemBtn = view.findViewById(R.id.edit_btn);


        // Find the columns of item attributes that we're interested in
        final int idColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
        int typeColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_CATEGORY_TYPE);
        int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRODUCT_NAME);
        int descriptionColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DESCRIPTION);
        int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
        int sNameColumnIndex =  cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_NAME);
        int sPhoneColumnIndex =  cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER);

        // Read the item attributes from the Cursor for the current item
        final String itemId = cursor.getString(idColumnIndex);
        int itemType = cursor.getInt(typeColumnIndex);
        String itemName = cursor.getString(nameColumnIndex);
        String itemDescription = cursor.getString(descriptionColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        final String itemQuantity = cursor.getString(quantityColumnIndex);
        int supplierName = cursor.getInt(sNameColumnIndex);
        String supplierPhone = cursor.getString(sPhoneColumnIndex);

        // If the item name is empty string or null, then use some default text
        // that says "No description", so the TextView isn't blank.
        if (TextUtils.isEmpty(itemDescription)) {
            itemDescription = context.getString(R.string.no_description);
        }

        // Sale button click listener to:
        // change the quantity of current item & reduce it by One item sold
        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryActivity activity = (InventoryActivity) context;
                activity.quantitySale(Integer.valueOf(itemId),Integer.valueOf(itemQuantity));
            }
        });

        // Update the TextViews with the attributes for the current item
        switch (itemType){
            case 1:
                itemTypeTv.setText(R.string.type_formal);
                break;
            case 2:
                itemTypeTv.setText(R.string.type_life_style);
                break;
            case 3:
                itemTypeTv.setText(R.string.type_cotton_socks);
                break;
            case 4:
                itemTypeTv.setText(R.string.type_care_products);
                break;
            case 5:
                itemTypeTv.setText(R.string.type_belts_and_wallets);
                break;
            default:
                itemTypeTv.setText(R.string.type_casual);
                break;
        }
        itemNameTv.setText(itemName);
        itemDescriptionTv.setText(itemDescription);
        itemPriceTv.setText(itemPrice);
        itemQuantityTv.setText(itemQuantity);
        switch (supplierName){
            case 1:
                supplierNameTv.setText(R.string.supplier_number_one);
                break;
            case 2:
                supplierNameTv.setText(R.string.supplier_number_two);
                break;
            case 3:
                supplierNameTv.setText(R.string.supplier_number_three);
                break;
            default:
                supplierNameTv.setText(R.string.supplier_main);
                break;
        }
        supplierPhoneTv.setText(supplierPhone);
        // Edit item on click listener to
        // open the editor activity for the selected item in the list to be edited
        editItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, Long.parseLong(itemId));
                intent.setData(currentItemUri);
                context.startActivity(intent);
            }
        });
    }
}
