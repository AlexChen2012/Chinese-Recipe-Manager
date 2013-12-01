package com.alex.recipemanager.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.alex.recipemanager.util.Consts;

public class RecipeInfoViewActivity extends BaseActivity {

    private static final String TAG = "RecipeInfoViewActivity";

    private static final int MENU_EDIT = 0;

    private long mRecipeId;
    private long mPatientId;
    private long mCaseHistoryId;
    private TextView mRecipeCountView;
    private RecipeMedicineAdapter mAdapter;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.recipe_info_view_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_recipe);
        mRecipeCountView = (TextView) findViewById(R.id.recipe_count_view);
        mGridView = (GridView) findViewById(R.id.recipe_info_grid_view);
        mRecipeId = getIntent().getLongExtra(EXTRA_LONG_VALUE_RECIPE_ID, DEFAULT_ID_VALUE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mRecipeId == DEFAULT_ID_VALUE) {
            Log.e(TAG, "Can not get RecipeId from intent");
        }
        setValueToView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_EDIT, 0, R.string.title_bar_text_edit).setIcon(
                android.R.drawable.ic_menu_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_EDIT:
            Intent intent = new Intent(this, RecipeInfoEditActivity.class);
            intent.putExtra(EXTRA_LONG_VALUE_PATIENT_ID, mPatientId);
            intent.putExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, mCaseHistoryId);
            intent.putExtra(EXTRA_LONG_VALUE_RECIPE_ID, mRecipeId);
            intent.putExtra(EXTRA_INT_VALUE_RECIPE_MODE, RecipeColumn.RECIPE_TYPE_CHARGE);
            startActivity(intent);
            finish();
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setValueToView() {
        Uri uri = Uri.withAppendedPath(RecipeColumn.CONTENT_URI, String.valueOf(mRecipeId));
        Cursor recipeCursor = getContentResolver().query(uri, null, null, null, null);
        Cursor medicineCursor = null;
        String recipeName = null;
        int type = RecipeColumn.RECIPE_TYPE_CHARGE;
        int count = 0;
        if (recipeCursor != null) {
            try {
                if (recipeCursor.moveToFirst()) {
                    count = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(RecipeColumn.COUNT));
                    recipeName = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeColumn.NAME));
                    type = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(RecipeColumn.RECIPE_TYPE));
                    mRecipeCountView.setText(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeColumn.COUNT)));
                    mPatientId = recipeCursor.getLong(recipeCursor.getColumnIndexOrThrow(RecipeColumn.PATIENT_KEY));
                    mCaseHistoryId = recipeCursor.getLong(recipeCursor.getColumnIndexOrThrow(RecipeColumn.CASE_HISTORY_KEY));
                }
                String selection = RecipeMedicineColumn.RECIPE_KEY + "=?";
                String[] selectionArgs = {String.valueOf(mRecipeId)};
                medicineCursor = getContentResolver().query(RecipeMedicineColumn.CONTENT_URI,
                        RECIPE_MEDICINE_JOIN_MEDICINE_NAME_PROJECTION,
                        selection,
                        selectionArgs,
                        RecipeMedicineColumn.DEFAULT_ORDER);
                String price = getPrice(recipeCursor, medicineCursor);
                String registerFee = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeColumn.REGISTER_FEE));
                if (TextUtils.isEmpty(registerFee)) {
                    registerFee = "0";
                }
                String otherFee = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeColumn.OTHER_FEE));
                if (TextUtils.isEmpty(otherFee)) {
                    otherFee = "0";
                }
                setTitle(recipeName, count, type, price, registerFee, otherFee);
                Log.d(TAG, "recipe price is: " + price);
            } finally {
                recipeCursor.close();
            }
        }
        startManagingCursor(medicineCursor);
        mAdapter = new RecipeMedicineAdapter(this, medicineCursor);
        mGridView.setAdapter(mAdapter);
    }

    private void setTitle(String recipeName, int count, int type, String price, String registerFee, String otherFee) {
        TextView nameView = (TextView) findViewById(R.id.title_bar_name_view);
        nameView.setText(recipeName);
        TextView countView = (TextView) findViewById(R.id.title_bar_count_view);
        countView.setText(String.valueOf(count));
        if (type == RecipeColumn.RECIPE_TYPE_CASE_HISTORY) {
            findViewById(R.id.title_bar_recipe_fee_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.title_bar_recipe_fee_layout).setVisibility(View.VISIBLE);
            TextView priceView = (TextView) findViewById(R.id.title_bar_price_view);
            TextView registerFeeView = (TextView) findViewById(R.id.title_bar_register_fee_view);
            registerFeeView.setText(registerFee);
            TextView otherFeeTextView = (TextView) findViewById(R.id.title_bar_other_fee_view);
            otherFeeTextView.setText(otherFee);
            priceView.setText(price);
        }
    }

    private String getPrice(Cursor recipeCursor, Cursor medicineCursor) {
        int medicinePrice = 0;
        while (medicineCursor.moveToNext()) {
            medicinePrice += medicineCursor.getInt(COLUMN_RECIPE_MEDICINE_WEIGHT)
                    * medicineCursor.getInt(COLUMN_RECIPE_MEDICINE_AMOUNT);
        }
        int count = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(RecipeColumn.COUNT));
        medicinePrice = medicinePrice * count;
        String register = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeColumn.REGISTER_FEE));
        String other = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(RecipeColumn.OTHER_FEE));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String bagFee = sp.getString(Consts.EXTRA_STRING_VALUE_BAG_FEE, "0.2");
        Log.d(TAG, "sharepreference bag fee = " + bagFee);
        String bagPrice = String.valueOf(count * Float.valueOf(bagFee));
        String price = String.valueOf(Float.valueOf(register) + Float.valueOf(other) + Float.valueOf(bagPrice)
                + Float.valueOf(formatPrice(medicinePrice)));
        int index = price.indexOf('.');
        return index != -1 && price.length() - index > 3 ? price.substring(0, index + 3): price;
    }

    private String formatPrice(int medicinePrice) {
        medicinePrice = medicinePrice / 10;
        String price = String.valueOf(medicinePrice);
        StringBuilder sb = new StringBuilder();
        if (price.length() == 1) {
            sb.append("0.0");
            sb.append(price);
        } else if (price.length() == 2) {
            sb.append("0.");
            sb.append(price);
        } else {
            sb.append(price.substring(0, price.length() - 2));
            sb.append(".");
            sb.append(price.substring(price.length() - 2));
        }
        return sb.toString();
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
