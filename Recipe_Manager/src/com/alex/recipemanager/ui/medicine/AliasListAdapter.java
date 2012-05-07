package com.alex.recipemanager.ui.medicine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.MedicineNameColumn;

public class AliasListAdapter extends CursorAdapter{

    private LayoutInflater mInflater;

    public AliasListAdapter(Context context, Cursor c) {
        super(context, c);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.single_item_layout, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView alias = (TextView) view;
        alias.setText(cursor.getString(cursor.getColumnIndex(MedicineNameColumn.MEDICINE_NAME)));
    }

}
