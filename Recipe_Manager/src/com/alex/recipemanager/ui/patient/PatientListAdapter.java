package com.alex.recipemanager.ui.patient;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.ui.patient.PatientListActivity.SearchItem;
import com.alex.recipemanager.util.TimeUtil;

public class PatientListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private  ArrayList<SearchItem> mItems;

    public PatientListAdapter(Context context, ArrayList<SearchItem> items) {
        super();
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public int getCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).id;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup arg2) {
        if (contentView == null) {
            contentView = mInflater.inflate(R.layout.patient_list_item, null);
        }
        TextView patientName = (TextView) contentView.findViewById(R.id.patient_name);
        TextView treatmentTime = (TextView) contentView.findViewById(R.id.first_time);
        patientName.setText(((SearchItem)getItem(position)).name);
        long millis = ((SearchItem)getItem(position)).time;
        treatmentTime.setText(TimeUtil.translateTimeMillisToDate(millis));
        return contentView;
    }

    public void dataChanged(ArrayList<SearchItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public int getType(int position) {
        return mItems.get(position).type;
    }
}
