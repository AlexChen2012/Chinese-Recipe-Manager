package com.alex.recipemanager.ui.patient;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.NationColumn;

public class NationSelectorActivity extends ListActivity{

    public static final String EXTRA_STRING_VALUE_NATION_NAME = "extra_value_string_nation_name";

    private Cursor mCursor;
    private NationListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nation_selector_activity);
        mCursor = managedQuery(NationColumn.CONTENT_URI, null, null, null, NationColumn.DEFAULT_ORDER);
        mAdapter = new NationListAdapter(this, mCursor);
        getListView().setAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String name = mAdapter.getNation(position);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STRING_VALUE_NATION_NAME, name);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class NationListAdapter extends CursorAdapter{

        private LayoutInflater mInflater;

        private NationListAdapter(Context context, Cursor c) {
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
            if(cursor != null){
                String nation = cursor.getString(cursor.getColumnIndex(NationColumn.NATION_NAME));
                TextView nationView = (TextView) view;
                nationView.setText(nation);
                return ;
            }
            throw new RuntimeException("can not bind view by cursor");
        }

        private String getNation(int position){
            Cursor c = (Cursor) getItem(position);
            return c.getString(c.getColumnIndex(NationColumn.NATION_NAME));
        }
    }
}
