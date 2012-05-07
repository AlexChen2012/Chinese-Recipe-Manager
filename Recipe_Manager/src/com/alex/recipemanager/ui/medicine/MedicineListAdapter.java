package com.alex.recipemanager.ui.medicine;

import com.alex.recipemanager.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MedicineListAdapter extends CursorAdapter{
    private LayoutInflater mInflater;

    public MedicineListAdapter(Context context, Cursor c) {
        super(context, c);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.medicine_list_item, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor != null){
            TextView medicineName = (TextView) view.findViewById(R.id.medicine_name);
            TextView medicineAmount = (TextView) view.findViewById(R.id.medicine_amount);
            medicineName.setText(cursor.getString(MedicineListActivity.MEDICINE_NAME_COLUMN));
            String content = context.getString(R.string.medicine_amount,
                    cursor.getInt(MedicineListActivity.MEDICINE_AMOUNT_COLUMN));
            medicineAmount.setText(content);
            return ;
        }
        throw new RuntimeException("can not bind view by cursor");
    }
}
