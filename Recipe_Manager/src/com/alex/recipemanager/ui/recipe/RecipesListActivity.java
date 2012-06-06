package com.alex.recipemanager.ui.recipe;

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
import com.alex.recipemanager.provider.RecipeContent.RecipeColumn;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.util.TimeUtil;

public class RecipesListActivity extends BaseActivity {

    private static final String TAG = "RecipesListActivity";

    private RecipesAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_layout);
        setTitle(R.string.recipes_list);

        long caseHistoryId = getIntent().getLongExtra(BaseActivity.EXTRA_LONG_VALUE_CASE_HISOTRY_ID, DEFAULT_ID_VALUE);
        if (caseHistoryId == DEFAULT_ID_VALUE) {
            Log.e(TAG, "Can not get caseHistoryId from intent");
        }
        String selection = RecipeColumn.CASE_HISTORY_KEY + "=?";
        String []selectionArgs = {String.valueOf(caseHistoryId)};
        Cursor cursor = getContentResolver().query(
                RecipeColumn.CONTENT_URI,
                RECIPE_TABLE_PROJECTION,
                selection,
                selectionArgs,
                null);
        startManagingCursor(cursor);
        mAdapter = new RecipesAdapter(this, cursor);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecipesListActivity.this, RecipeInfoViewActivity.class);
                intent.putExtra(EXTRA_LONG_VALUE_RECIPE_ID, id);
                startActivity(intent);
            }
        });
    }

    private class RecipesAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        public RecipesAdapter(Context context, Cursor c) {
            super(context, c);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.patient_list_item, null);
            ViewHolder holder = new ViewHolder();
            holder.recipeNameView = (TextView) view.findViewById(R.id.patient_name);
            holder.timeView = (TextView) view.findViewById(R.id.first_time);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.recipeNameView.setText(cursor.getString(COLUMN_RECIPE_NAME));
            holder.timeView.setText(TimeUtil.translateTimeMillisToDate(cursor.getLong(COLUMN_RECIPE_TIMESTAMP)));
        }
    }

    private static class ViewHolder {
        private TextView recipeNameView;
        private TextView timeView;
    }
}
