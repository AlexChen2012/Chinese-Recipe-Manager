package com.alex.recipemanager.ui.recipe;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.RecipeColumn;
import com.alex.recipemanager.provider.RecipeContent.RecipeMedicineColumn;
import com.alex.recipemanager.ui.base.BaseActivity;

public class RecipeInfoViewActivity extends BaseActivity {

    private static final String TAG = "RecipeInfoViewActivity";

    private long mRecipeId;
    private TextView mRecipeCountView;
    private RecipeMedicineAdapter mAdapter;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.recipe_info_view_layout);
        mRecipeCountView = (TextView) findViewById(R.id.recipe_count_view);
        mGridView = (GridView) findViewById(R.id.recipe_info_grid_view);
        mRecipeId = getIntent().getLongExtra(EXTRA_LONG_VALUE_RECIPE_ID, DEFAULT_ID_VALUE);

        if( mRecipeId == DEFAULT_ID_VALUE) {
            Log.e(TAG, "Can not get RecipeId from intent");
        }
        setValueToView();
    }

    private void setValueToView() {
        Uri uri = Uri.withAppendedPath(RecipeColumn.CONTENT_URI, String.valueOf(mRecipeId));
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    setTitle(c.getString(c.getColumnIndexOrThrow(RecipeColumn.NAME)));
                    mRecipeCountView.setText( c.getString(c.getColumnIndexOrThrow(RecipeColumn.NUMBER)));
                }
            } finally {
                c.close();
                c = null;
            }
        }
        String selection = RecipeMedicineColumn.RECIPE_KEY + "=?";
        String[] selectionArgs = {String.valueOf(mRecipeId)};
        c = getContentResolver().query(RecipeMedicineColumn.CONTENT_URI,
                RECIPE_MEDINE_JOIN_MEDICINE_NAME_PROJECTION,
                selection,
                selectionArgs,
                null);
        startManagingCursor(c);
        mAdapter = new RecipeMedicineAdapter(this, c);
        mGridView.setAdapter(mAdapter);
    }

    private class RecipeMedicineAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        public RecipeMedicineAdapter(Context context, Cursor c) {
            super(context, c);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.recipe_medicine_list_item, null);
            ViewHolder holder = new ViewHolder();
            holder.medicineNameView = (TextView) view.findViewById(R.id.medicine_name);
            holder.weightView = (TextView) view.findViewById(R.id.medicine_weight);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.medicineNameView.setText(cursor.getString(COLUMN_RECIPE_MEDICINE_NAME));
            StringBuilder sb = new StringBuilder();
            sb.append(cursor.getInt(COLUMN_RECIPE_MEDICINE_WEIGHT))
                .append(" ")
                .append(getString(R.string.recipe_medicine_unit));
            holder.weightView.setText(sb.toString());
        }
    }

    private static class ViewHolder {
        private TextView medicineNameView;
        private TextView weightView;
    }
}
