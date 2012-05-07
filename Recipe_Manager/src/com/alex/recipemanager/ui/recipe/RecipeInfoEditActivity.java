package com.alex.recipemanager.ui.recipe;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.RecipeColumn;
import com.alex.recipemanager.provider.RecipeContent.RecipeMedicineColumn;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.ui.base.RemoveableLayoutView;
import com.alex.recipemanager.ui.medicine.MedicineListActivity;
import com.alex.recipemanager.util.MedicineUtil;

public class RecipeInfoEditActivity extends BaseActivity{

    private static final String TAG = "RecipeInfoEditActivity";

    private static final int DIALOG_ILLEGAL_INPUT = 0;
    private static final int DIALOG_EMPTY_WEIGHT  = 1;

    private static final int REQUEST_CODE_MEDICINE_ID_AND_NAME = 0;

    private static final int TOKEN_UPGRATE_RECIPE_TABLE = 0;

    private int mPatientId;
    private int mCaseHistoryId;
    private int mRecipeId;
    private boolean mNewRecipe;
    private EditText mNameEdit;
    private EditText mCountEidt;
    private LinearLayout mRecipeLayout;
    private LayoutInflater mInflater;
    private RecipeAsyncQueryHandler mAsyncQuery;
    private ArrayList<MedicineInfo> mMedicineInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.recipe_info_edit_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        mPatientId = getIntent().getIntExtra(EXTRA_INT_VALUE_PATIENT_ID, DEFAULT_ID_VALUE);
        mCaseHistoryId = getIntent().getIntExtra(EXTRA_INT_VALUE_CASE_HISOTRY_ID, DEFAULT_ID_VALUE);
        mRecipeId = getIntent().getIntExtra(EXTRA_INT_VALUE_RECIPE_ID, DEFAULT_ID_VALUE);
        setRecipeState(mRecipeId == DEFAULT_ID_VALUE);
        setTitle();
        mInflater = LayoutInflater.from(this);
        bindView();
        if(isNewRecipe()){
            ContentValues values = new ContentValues();
            values.put(RecipeColumn.PATIENT_KEY, mPatientId);
            values.put(RecipeColumn.CASE_HISTORY_KEY, mCaseHistoryId);
            Uri uri = getContentResolver().insert(RecipeColumn.CONTENT_URI, values);
            mRecipeId = Integer.valueOf(uri.getLastPathSegment());
            Log.v(TAG, "new recipe id = " + mRecipeId);
        } else {
            setValueToView();
        }
        mAsyncQuery = new RecipeAsyncQueryHandler(getContentResolver());
        mMedicineInfo = new ArrayList<MedicineInfo>();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ILLEGAL_INPUT:
            return MedicineUtil.createAlterDialog(this,
                getString(R.string.dialog_alter_title),
                getString(R.string.dialog_illegal_input));
        case DIALOG_EMPTY_WEIGHT:
            return MedicineUtil.createAlterDialog(this,
                    getString(R.string.dialog_alter_title),
                    getString(R.string.dialog_empty_weight));
        default:
            return super.onCreateDialog(id);
        }
    }

    public void onAddRecipeClick(View v){
        Intent intent = new Intent(this, MedicineListActivity.class);
        intent.putExtra(MedicineListActivity.EXTRA_BOOLEAN_VALUE_START_FROM_MEDICINE_SELECTOR, true);
        startActivityForResult(intent, REQUEST_CODE_MEDICINE_ID_AND_NAME);
    }

    private void setValueToView() {
        // TODO:
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch(requestCode) {
            case REQUEST_CODE_MEDICINE_ID_AND_NAME:
                int id = data.getIntExtra(MedicineListActivity.EXTRA_INT_VALUE_MEDICINE_ID, DEFAULT_ID_VALUE);
                if(!matchMedicineId(id)){
                    MedicineInfo info = new MedicineInfo();
                    info.mId = id;
                    info.mName = data.getStringExtra(MedicineListActivity.EXTRA_STRING_VALUE_MEDICINE_NAME);
                    mMedicineInfo.add(info);
                    addMedicineEditView(info);
                } else {
                    showDialog(DIALOG_NAME_EXSIT);
                }
                break;
            default:
                break;
        }
    }

    private void addMedicineEditView(final MedicineInfo info) {
        RemoveableLayoutView view = (RemoveableLayoutView) mInflater.inflate(
                R.layout.removeable_recipe_medicine_item, null);
        TextView name = (TextView)view.findViewById(R.id.recipe_medicine_name);
        name.setText(info.mName);
        view.setRecordId(info.mId);
        EditText medicineWeight = (EditText) view.findViewById(R.id.recipe_medicine_weight_edit);
        medicineWeight.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing.
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing.
            }

            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s)){
                    info.mWeight = 0;
                } else {
                    info.mWeight = Integer.valueOf(s.toString());
                }
                Log.v(TAG, "weight changed to: " + info.mWeight + "the info.id = " + info.mId);
            }
        });
        mRecipeLayout.addView(view, mRecipeLayout.getChildCount());
    }

    public void onRemoveButtonClick(View v){
        RemoveableLayoutView view = (RemoveableLayoutView)v.getParent();
        mRecipeLayout.removeView(view);
        deleteMeidincineInfoById(view.getRecordId());
    }

    private boolean matchMedicineId(int id) {
        for(MedicineInfo info : mMedicineInfo) {
            if(info.mId == id){
                Log.v(TAG, "matched id = " + info.mId);
                return true;
            }
        }
        return false;
    }

    private void deleteMeidincineInfoById(int id) {
        Log.v(TAG, "view id = " + id);
        for(MedicineInfo info : mMedicineInfo) {
            if(info.mId == id){
                mMedicineInfo.remove(info);
                Log.v(TAG, "matched delete MedicineInfo where id = " + info.mId);
                return ;
            }
        }
        Log.v(TAG, "view id can not be matched");
    }

    private void bindView() {
        mNameEdit = (EditText) findViewById(R.id.recipe_name_edit);
        mCountEidt = (EditText) findViewById(R.id.recipe_count_edit);
        mRecipeLayout = (LinearLayout) findViewById(R.id.add_recipe_layout);
    }

    private void setTitle() {
        TextView title = (TextView) findViewById(R.id.title_bar_text);
        if(isNewRecipe()) {
            title.setText(R.string.title_bar_text_create);
        } else {
            title.setText(R.string.title_bar_text_edit);
        }
    }

    private boolean isNewRecipe() {
        return mNewRecipe;
    }

    private void setRecipeState(boolean newRecipe) {
        mNewRecipe = newRecipe;
    }

    @Override
    public void onBackPressed() {
        showDialog(DIALOG_CONFIRM_QUIT);
    }

    @Override
    public void exitActivity() {
        if(isNewRecipe()){
            showDialog(DIALOG_WAITING);
            Uri uri = Uri.withAppendedPath(RecipeColumn.CONTENT_URI, String.valueOf(mRecipeId));
            mAsyncQuery.startDelete(0, null, uri, null, null);
        } else {
            super.onBackPressed();
        }
    }

    public void confirmToSave(View v){
        String name = mNameEdit.getText().toString();
        String count = mCountEidt.getText().toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(count)){
            showDialog(DIALOG_INPUT_EMPTY);
            return ;
        }
        if(illegalInput(Integer.valueOf(count))){
            showDialog(DIALOG_ILLEGAL_INPUT);
            return ;
        }
        if(hasEmptyWeight()) {
            showDialog(DIALOG_EMPTY_WEIGHT);
            return ;
        }
        saveReicpe();
    }

    private boolean hasEmptyWeight() {
        for(MedicineInfo info : mMedicineInfo) {
            if(info.mWeight <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean illegalInput(int count) {
        return count <= 0;
    }

    private void saveReicpe() {
        ContentValues values = new ContentValues();
        String name = mNameEdit.getText().toString();
        String count = mCountEidt.getText().toString();
        if(!TextUtils.isEmpty(name)){
            values.put(RecipeColumn.NAME, name);
        }
        if(!TextUtils.isEmpty(count) && !illegalInput(Integer.valueOf(count))){
            values.put(RecipeColumn.NUMBER, count);
        }
        showDialog(DIALOG_WAITING);
        Uri uri = Uri.withAppendedPath(RecipeColumn.CONTENT_URI, String.valueOf(mRecipeId));
        mAsyncQuery.startUpdate(TOKEN_UPGRATE_RECIPE_TABLE, null, uri, values, null, null);
    }


    private void insertRecipeMedicines() {
        ContentValues[] contentValues = new ContentValues[mMedicineInfo.size()];
        int i = 0;
        for(MedicineInfo info : mMedicineInfo) {
            ContentValues values = new ContentValues();
            values.put(RecipeMedicineColumn.PATIENT_KEY, mPatientId);
            values.put(RecipeMedicineColumn.CASE_HISTORY_KEY, mCaseHistoryId);
            values.put(RecipeMedicineColumn.RECIPE_KEY, mRecipeId);
            values.put(RecipeMedicineColumn.WEIGHT, info.mName);
            values.put(RecipeMedicineColumn.MEDICINE_KEY, info.mId);
            contentValues[i] = values;
            i++;
        }
        AsyncInsertRecipeMedicine insertMedicine = new AsyncInsertRecipeMedicine();
        insertMedicine.execute(contentValues);
    }

    public class RecipeAsyncQueryHandler extends AsyncQueryHandler{

        public RecipeAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            removeDialog(DIALOG_WAITING);
            finish();
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if(token == TOKEN_UPGRATE_RECIPE_TABLE) {
                insertRecipeMedicines();
            }
        }
    }

    static class MedicineInfo{
        int mId;
        String mName;
        int mWeight;
    }

    private class AsyncInsertRecipeMedicine extends AsyncTask<ContentValues[], Void, Integer>{

        @Override
        protected Integer doInBackground(ContentValues[]... params) {
            ContentValues values[] = params[0];
            int count = getContentResolver().bulkInsert(RecipeMedicineColumn.CONTENT_URI, values);
            Log.v(TAG, count + " Recipe_Medicine records be inserted.");
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            removeDialog(DIALOG_WAITING);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_INT_VALUE_RECIPE_ID, mRecipeId);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}