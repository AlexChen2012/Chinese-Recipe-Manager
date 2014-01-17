package com.alex.recipemanager.ui.medicine;

import android.preference.PreferenceManager;
import com.alex.recipemanager.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.alex.recipemanager.util.Consts;

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
            TextView warningInfo = (TextView) view.findViewById(R.id.warning_info);

            medicineName.setText(cursor.getString(MedicineListActivity.MEDICINE_NAME_COLUMN));
            String content = context.getString(R.string.medicine_amount,
                    cursor.getInt(MedicineListActivity.MEDICINE_AMOUNT_COLUMN));
            medicineAmount.setText(content);

            int weight = cursor.getInt(MedicineListActivity.MEDICINE_GROSS_WEIGHT_COLUMN);
            int threshold = cursor.getInt(MedicineListActivity.MEDICINE_THRESHOLD_COLUMN);
            int delta = weight - threshold;
            int lack = PreferenceManager.getDefaultSharedPreferences(context).getInt(Consts.PREFERENCE_INT_VALUE_MEDICINE_LACK, 500);
            warningInfo.setVisibility(View.VISIBLE);
            if (delta < 0) {
                warningInfo.setText(context.getString(R.string.medicine_danger, weight));
                warningInfo.setTextColor(context.getResources().getColor(R.color.medicine_danger));
            } else if (delta < lack) {
                warningInfo.setText(context.getString(R.string.medicine_lack, weight));
                warningInfo.setTextColor(context.getResources().getColor(R.color.medicine_lack));
            } else {
                warningInfo.setVisibility(View.GONE);
            }
            return ;
        }
        throw new RuntimeException("can not bind view by cursor");
    }
}
