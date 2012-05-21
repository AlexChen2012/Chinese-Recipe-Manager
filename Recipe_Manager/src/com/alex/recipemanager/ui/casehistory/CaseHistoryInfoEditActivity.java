package com.alex.recipemanager.ui.casehistory;

import java.util.ArrayList;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.CaseHistoryColumn;
import com.alex.recipemanager.provider.RecipeContent.RecipeColumn;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.ui.base.RemoveableLayoutView;
import com.alex.recipemanager.ui.recipe.RecipeInfoEditActivity;
import com.alex.recipemanager.util.MedicineUtil;

public class CaseHistoryInfoEditActivity extends BaseActivity{

    private static final String TAG = "CaseHistoryInfoEditActivity";

    private static final int REQUEST_CODE_EDIT_RECIPE = 0;

    private static final int TOKEN_NEED_DELETE_CASE_HISTORY = 1;

    private long mPatientId;
    private long mCaseHistoryId;
    private boolean mNewCaseHistory;
    private EditText mSymptomEdit;
    private EditText mDesriptionEidt;
    private LayoutInflater mInflater;
    private LinearLayout mRecipeLayout;
    private ArrayList<Long> mDeleteRecipeIds;
    private CaseHistoryAsyncQueryHandler mAsyncQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.case_history_info_eidt_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        mPatientId = getIntent().getLongExtra(EXTRA_LONG_VALUE_PATIENT_ID, DEFAULT_ID_VALUE);
        mCaseHistoryId = getIntent().getLongExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, DEFAULT_ID_VALUE);
        setCaseHistoryState(mCaseHistoryId == DEFAULT_ID_VALUE);
        setTitle();
        mInflater = LayoutInflater.from(this);
        mDeleteRecipeIds = new ArrayList<Long>();
        bindView();
        if(isCreateCaseHistory()) {
            ContentValues values = new ContentValues();
            values.put(CaseHistoryColumn.PATIENT_KEY, mPatientId);
            Uri uri = getContentResolver().insert(CaseHistoryColumn.CONTENT_URI, values);
            mCaseHistoryId = Integer.valueOf(uri.getLastPathSegment());
            Log.v(TAG, "case history id = " + mCaseHistoryId);
        } else {
            setValueToView();
        }
        mAsyncQuery = new CaseHistoryAsyncQueryHandler(getContentResolver());
    }

    @Override
    public void onBackPressed() {
        showDialog(DIALOG_CONFIRM_QUIT);
    }

    @Override
    public void exitActivityWithoutSave() {
        showDialog(DIALOG_WAITING);
        deleteRemovedRecipe(isCreateCaseHistory() ?
                TOKEN_NEED_DELETE_CASE_HISTORY : 0);
    }

    public void onRemoveableItemClicked(View v) {
        RemoveableLayoutView view = (RemoveableLayoutView)v.getParent();
        addOrEditRecipe(view.getRecordId());
    }

    public void onDeleteButtonClick(View v){
        RemoveableLayoutView view = (RemoveableLayoutView)v.getParent().getParent();
        if(view.getRecordId() != RemoveableLayoutView.NO_ID){
            mRecipeLayout.removeView(view);
            Long id = view.getRecordId();
            mDeleteRecipeIds.add(id);
        }
    }

    private void deleteRemovedRecipe(int token) {
        String  where = MedicineUtil.getWhereClauseById(mDeleteRecipeIds);
        mAsyncQuery.startDelete(token, null, RecipeColumn.CONTENT_URI, where, null);
    }

    public void confirmToSave(View v){
        String symptom = mSymptomEdit.getText().toString();
        String description = mDesriptionEidt.getText().toString();
        if(TextUtils.isEmpty(symptom) || TextUtils.isEmpty(description)){
            showDialog(DIALOG_INPUT_EMPTY);
            return ;
        }
        saveCaseHistory();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, mCaseHistoryId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void saveCaseHistory() {
        ContentValues values = new ContentValues();
        String symptom = mSymptomEdit.getText().toString();
        String description = mDesriptionEidt.getText().toString();
        if(!TextUtils.isEmpty(symptom)){
            values.put(CaseHistoryColumn.SYMPTOM, symptom);
            String abbr = MedicineUtil.getPinyinAbbr(symptom);
            values.put(CaseHistoryColumn.SYMPTOM_ABBR, abbr);
        }
        if(!TextUtils.isEmpty(description)){
            values.put(CaseHistoryColumn.DESCRIPTION, description);
        }
        Uri uri = Uri.withAppendedPath(CaseHistoryColumn.CONTENT_URI, String.valueOf(mCaseHistoryId));
        getContentResolver().update(uri, values, null, null);
        deleteRemovedRecipe(0);
    }

    private void bindView() {
        mSymptomEdit = (EditText) findViewById(R.id.case_history_symptom_edit);
        mDesriptionEidt = (EditText) findViewById(R.id.case_history_description_edit);
        mRecipeLayout = (LinearLayout) findViewById(R.id.recipe_layout);
    }

    private void setValueToView() {
        Uri uri = Uri.withAppendedPath(CaseHistoryColumn.CONTENT_URI, String.valueOf(mCaseHistoryId));
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    mSymptomEdit.setText(c.getString(c.getColumnIndexOrThrow(CaseHistoryColumn.SYMPTOM)));
                    mDesriptionEidt.setText( c.getString(c.getColumnIndexOrThrow(CaseHistoryColumn.DESCRIPTION)));
                }
            } finally {
                c.close();
                c = null;
            }
        }
        String selection = RecipeColumn.CASE_HISTORY_KEY + "=?";
        String[] selectionArgs = {String.valueOf(mCaseHistoryId)};
        c = getContentResolver().query(RecipeColumn.CONTENT_URI, RECIPE_TABLE_PROJECTION, selection, selectionArgs, null);
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    addRecipeView(c.getLong(COLUMN_RECIPE_ID),
                            c.getString(COLUMN_RECIPE_NAME),
                            c.getInt(COLUMN_RECIPE_COUNT));
                }
            } finally {
                c.close();
            }
        }
    }

    private void setTitle() {
        TextView title = (TextView) findViewById(R.id.title_bar_text);
        if(isCreateCaseHistory()) {
            title.setText(R.string.title_bar_text_create);
        } else {
            title.setText(R.string.title_bar_text_edit);
        }
    }

    private boolean isCreateCaseHistory() {
        return mNewCaseHistory;
    }

    private void setCaseHistoryState(boolean state) {
        mNewCaseHistory = state;
    }

    public void onAddRecipeClick(View v){
        addOrEditRecipe(RemoveableLayoutView.NO_ID);
    }

    private void addOrEditRecipe(long id) {
        Intent intent = new Intent(this, RecipeInfoEditActivity.class);
        intent.putExtra(EXTRA_LONG_VALUE_PATIENT_ID, mPatientId);
        intent.putExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, mCaseHistoryId);
        if (id != RemoveableLayoutView.NO_ID) {
            intent.putExtra(EXTRA_LONG_VALUE_RECIPE_ID, id);
        }
        startActivityForResult(intent, REQUEST_CODE_EDIT_RECIPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch(requestCode) {
            case REQUEST_CODE_EDIT_RECIPE:
                removeAllRecipeViews();
                String selection = RecipeColumn.CASE_HISTORY_KEY + "=?";
                String selectionArgs[] = {String.valueOf(mCaseHistoryId)};
                Cursor c = getContentResolver().query(RecipeColumn.CONTENT_URI, RECIPE_TABLE_PROJECTION,
                        selection, selectionArgs, null);
                if(c != null ) {
                    try {
                        while (c.moveToNext()) {
                            addRecipeView(c.getLong(COLUMN_RECIPE_ID),
                                    c.getString(COLUMN_RECIPE_NAME), c.getInt(COLUMN_RECIPE_COUNT));
                        }
                    } finally {
                        c.close();
                    }
                }
            default:
                break;
        }
    }

    private void removeAllRecipeViews() {
        int count = mRecipeLayout.getChildCount();
        if (count > 1) {
            mRecipeLayout.removeViews(1, count - 1);
        }
    }

    private void addRecipeView(long id, String name, int count) {
        RemoveableLayoutView view = (RemoveableLayoutView) mInflater.inflate(
                R.layout.removeable_single_item, null);
        view.setRecordId(id);
        TextView nameView = (TextView) view.findViewById(R.id.content_text_view);
        TextView countView = (TextView) view.findViewById(R.id.append_info_view);
        nameView.setText(name);
        countView.setText(getString(R.string.recipe_count_with_unit, count));
        mRecipeLayout.addView(view, mRecipeLayout.getChildCount());
    }

    public class CaseHistoryAsyncQueryHandler extends AsyncQueryHandler{

        public CaseHistoryAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if(token == TOKEN_NEED_DELETE_CASE_HISTORY) {
                Uri uri = Uri.withAppendedPath(CaseHistoryColumn.CONTENT_URI, String.valueOf(mCaseHistoryId));
                mAsyncQuery.startDelete(0, null, uri, null, null);
                return ;
            }
            removeDialog(DIALOG_WAITING);
            finish();
        }
    }
}
