package com.alex.recipemanager.ui.casehistory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.CaseHistoryColumn;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.util.TimeUtil;

public class CaseHistoryListActivity extends BaseActivity {

    private static final String TAG = "CaseHistoryListActivity";

    private CaseHistoryAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_layout);
        setTitle(R.string.patient_case_history);

        long patientId = getIntent().getLongExtra(BaseActivity.EXTRA_LONG_VALUE_PATIENT_ID, DEFAULT_ID_VALUE);
        if (patientId == DEFAULT_ID_VALUE) {
            Log.e(TAG, "Can not get mPatientId from intent");
        }
        String selection = CaseHistoryColumn.PATIENT_KEY + "=?";
        String []selectionArgs = {String.valueOf(patientId)};
        Cursor cursor = getContentResolver().query(
                CaseHistoryColumn.CONTENT_URI,
                CASE_HISTORY_TABLE_PROJECTION,
                selection,
                selectionArgs,
                null);
        startManagingCursor(cursor);
        mAdapter = new CaseHistoryAdapter(this, cursor);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CaseHistoryListActivity.this, CaseHistoryInfoViewActivity.class);
                intent.putExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, id);
                startActivity(intent);
            }
        });
    }

    private class CaseHistoryAdapter extends CursorAdapter {

        private LayoutInflater mInflater;
        public CaseHistoryAdapter(Context context, Cursor c) {
            super(context, c);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.patient_list_item, null);
            ViewHolder holder = new ViewHolder();
            holder.symptomView = (TextView) view.findViewById(R.id.patient_name);
            holder.timeView = (TextView) view.findViewById(R.id.first_time);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.symptomView.setText(cursor.getString(COLUMN_CASE_HISTORY_SYMPTOM));
            holder.timeView.setText(TimeUtil.translateTimeMillisToDate(cursor.getLong(COLUMN_CASE_HISTORY_TIMESTAMP)));
        }
    }

    private static class ViewHolder {
        private TextView symptomView;
        private TextView timeView;
    }

}
