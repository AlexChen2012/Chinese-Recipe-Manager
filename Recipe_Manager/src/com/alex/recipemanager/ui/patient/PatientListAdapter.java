package com.alex.recipemanager.ui.patient;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.util.TimeUtil;

public class PatientListAdapter extends CursorAdapter{

    private LayoutInflater mInflater;
    public PatientListAdapter(Context context, Cursor c) {
        super(context, c);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.patient_list_item, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor != null){
            TextView patientName = (TextView) view.findViewById(R.id.patient_name);
            TextView treatmentTime = (TextView) view.findViewById(R.id.first_time);
            patientName.setText(cursor.getString(PatientListActivity.COLUMN_PATIENT_NAME));
            long millis = cursor.getLong(PatientListActivity.COLUMN_PATIENT_FIRST_TIME);
            treatmentTime.setText(TimeUtil.translateTimeMillisToDate(millis));
            return ;
        }
        throw new RuntimeException("can not bind view by cursor");
    }

}
