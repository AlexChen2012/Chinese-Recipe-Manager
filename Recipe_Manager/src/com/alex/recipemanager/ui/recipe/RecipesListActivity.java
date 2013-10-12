package com.alex.recipemanager.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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

    private static final int CONTEXT_MENU_EDIT   = 0;
    private static final int CONTEXT_MENU_DELETE = 1;

    private RecipesAdapter mAdapter;
    private ListView mListView;
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_layout);
        setTitle(R.string.recipes_list);

      //TODO: Forbid other mode for version only has pricing recipe function.
//        mMode = getIntent().getIntExtra(EXTRA_INT_VALUE_RECIPE_MODE, MODE_CASE_HISTROY);
        mMode = RecipeColumn.RECIPE_TYPE_CHARGE;
        long caseHistoryId = getIntent().getLongExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, DEFAULT_ID_VALUE);
        if (caseHistoryId == DEFAULT_ID_VALUE) {
            Log.d(TAG, "Can not get caseHistoryId from intent");
        }
        String selection;
        String []selectionArgs;
        if (mMode == RecipeColumn.RECIPE_TYPE_CASE_HISTORY) {
            selection = RecipeColumn.CASE_HISTORY_KEY + "=?";
            selectionArgs = new String[]{String.valueOf(caseHistoryId)};
        } else {
            selection = RecipeColumn.RECIPE_TYPE + "=?";
            selectionArgs = new String[]{String.valueOf(RecipeColumn.RECIPE_TYPE_CHARGE)};
        }
        Cursor cursor = getContentResolver().query(
                RecipeColumn.CONTENT_URI,
                RECIPE_TABLE_PROJECTION,
                null,
                null,
                null);
        startManagingCursor(cursor);
        mAdapter = new RecipesAdapter(this, cursor);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long recipeId = mAdapter.getItemId(position);
                Log.d(TAG, "id from onItemClick = " + id + " Adapter.getItemId() = " + recipeId);
                Intent intent = new Intent(RecipesListActivity.this, RecipeInfoViewActivity.class);
                intent.putExtra(EXTRA_LONG_VALUE_RECIPE_ID, id);
                if (mMode == RecipeColumn.RECIPE_TYPE_CHARGE) {
                    intent.putExtra(EXTRA_INT_VALUE_RECIPE_MODE, RecipeColumn.RECIPE_TYPE_CHARGE);
                }
                startActivity(intent);
            }
        });
        registerForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        if(mMode == RecipeColumn.RECIPE_TYPE_CASE_HISTORY) {
            return ;
        }
        menu.add(0, CONTEXT_MENU_EDIT, 0, R.string.context_menu_edit);
        menu.add(0, CONTEXT_MENU_DELETE, 1, R.string.context_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Log.d(TAG, "context menu info.id = " + info.id);
        switch (item.getItemId()) {
        case CONTEXT_MENU_EDIT:
            Intent intent = new Intent(this, RecipeInfoEditActivity.class);
            intent.putExtra(EXTRA_INT_VALUE_RECIPE_MODE, RecipeColumn.RECIPE_TYPE_CHARGE);
            intent.putExtra(EXTRA_LONG_VALUE_RECIPE_ID, info.id);
            startActivity(intent);
            return true;
        case CONTEXT_MENU_DELETE:
            Uri uri = Uri.withAppendedPath(RecipeColumn.CONTENT_URI, String.valueOf(info.id));
            getContentResolver().delete(uri, null, null);
            mAdapter.getCursor().requery();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private class RecipesAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        public RecipesAdapter(Context context, Cursor c) {
            super(context, c, true);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.patient_list_item, null);
            ViewHolder holder = new ViewHolder();
            holder.recipeNameView = (TextView) view.findViewById(R.id.patient_name);
            holder.timeView = (TextView) view.findViewById(R.id.first_time);
            holder.warehouseView = view.findViewById(R.id.warehouse_view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.recipeNameView.setText(cursor.getString(COLUMN_RECIPE_NAME));
            holder.timeView.setText(TimeUtil.translateTimeMillisToDate(cursor.getLong(COLUMN_RECIPE_TIMESTAMP)));
            if (cursor.getInt(COLUMN_RECIPE_TYPE) == RecipeColumn.RECIPE_TYPE_CHARGE) {
                holder.warehouseView.setVisibility(View.VISIBLE);
                if (cursor.getInt(COLUMN_RECIPE_IS_STORAGE) == RecipeColumn.RECIPE_NOT_STORAGE) {
                    holder.warehouseView.setBackgroundColor(getResources().getColor(R.color.orange));
                } else {
                    holder.warehouseView.setBackgroundColor(getResources().getColor(R.color.springgreen));
                }
            } else {
                holder.warehouseView.setVisibility(View.GONE);
            }
        }
    }

    private static class ViewHolder {
        private TextView recipeNameView;
        private TextView timeView;
        private View warehouseView;
    }
}
