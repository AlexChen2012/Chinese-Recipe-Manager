package com.alex.recipemanager.ui.patient;

import java.util.ArrayList;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.CaseHistoryColumn;
import com.alex.recipemanager.provider.RecipeContent.PatientColumns;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.ui.base.RemoveableLayoutView;
import com.alex.recipemanager.ui.casehistory.CaseHistoryInfoEditActivity;
import com.alex.recipemanager.util.MedicineUtil;

public class PatientInfoEditActivity extends BaseActivity{

    private static final String TAG = "PatientInfoEditActivity";

    private static final int TOKEN_NEED_DELETE_PATIENT = 1;

    private static final int REQUEST_CODE_SELECT_NATION     = 1;
    private static final int REQUEST_CODE_EDIT_HISTORY      = 2;
    private static final int REQUEST_CODE_EDIT_CASE_HISTORY = 3;

    private static final int DIALOG_INPUT_NAME_EMPTY  = 0;

    private PatientEditAsyncQueryHandler mAsyncQuery;
    private int mPatientId;
    private boolean mNewPatient;
    private String mHistory;
    private Button mNationButton;
    private EditText mNameEdit;
    private Spinner mGenderSpinner;
    private EditText mAgeEdit;
    private EditText mTelephoyEdit;
    private EditText mAddressEdit;
    private LinearLayout mAddHistoryLayout;
    private LinearLayout mAddCaseHistoryLayout;
    private LayoutInflater mInflater;
    private ArrayList<Integer> mCaseHistoryDeleteIds;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.create_or_edit_patient_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        mPatientId = getIntent().getIntExtra(EXTRA_INT_VALUE_PATIENT_ID, DEFAULT_ID_VALUE);
        setPatientState(mPatientId == DEFAULT_ID_VALUE);
        mInflater = LayoutInflater.from(this);
        setTitle();
        bindView();
        if(isCreatePatient()){
            ContentValues values = new ContentValues();
            Uri uri = getContentResolver().insert(PatientColumns.CONTENT_URI, values);
            mPatientId = Integer.valueOf(uri.getLastPathSegment());
            Log.v(TAG, "patient id = " + mPatientId);
        } else {
            setViewToData();
        }
        mAsyncQuery = new PatientEditAsyncQueryHandler(getContentResolver());
        mCaseHistoryDeleteIds = new ArrayList<Integer>();
    }

    private void deleteRemovedCaseHistory(int token) {
        String  where = MedicineUtil.getWhereClauseById(mCaseHistoryDeleteIds);
        mAsyncQuery.startDelete(token, null, CaseHistoryColumn.CONTENT_URI, where, null);
    }

    private void bindView() {
        mNationButton = (Button) findViewById(R.id.patient_nation_btn);
        mAddHistoryLayout = (LinearLayout) findViewById(R.id.add_patient_history_layout);
        mAddCaseHistoryLayout = (LinearLayout) findViewById(R.id.patient_case_history_layout);
        mNameEdit = (EditText) findViewById(R.id.patient_name_edit);
        mGenderSpinner = (Spinner) findViewById(R.id.patient_gender_spinner);
        mAgeEdit = (EditText) findViewById(R.id.patient_age_edit);
        mTelephoyEdit = (EditText) findViewById(R.id.patient_telephone_edit);
        mAddressEdit = (EditText) findViewById(R.id.patient_address_edit);
    }

    private void setViewToData() {
        Uri uri = Uri.withAppendedPath(PatientColumns.CONTENT_URI,
                String.valueOf(mPatientId));
        Cursor c = getContentResolver().query(uri, PATIENT_TABLE_PROJECTION,
                null, null, null);
        if(c != null){
            try{
                c.moveToFirst();
                mNameEdit.setText(c.getString(COLUMN_PATIENT_NAME));
                //TODO:
            } finally {
                c.close();
            }
        }
        String selection = CaseHistoryColumn.PATIENT_KEY + " =?";
        String []selectionArgs = new String[] {String.valueOf(mPatientId)};
        c = getContentResolver().query(CaseHistoryColumn.CONTENT_URI,
                CASE_HISTORY_TABLE_PROJECTION, selection, selectionArgs,
                CaseHistoryColumn.DEFAULT_ORDER);
        if(c != null) {
            try {
                while(c.moveToNext()) {
                    addCaseHistoryView(c.getInt(COLUMN_CASE_HISTORY_ID),
                            c.getString(COLUMN_CASE_HISTORY_DESCRIPTION));
                }
            } finally {
                c.close();
            }
        }
    }

    private void setTitle() {
        TextView title = (TextView) findViewById(R.id.title_bar_text);
        if(isCreatePatient()) {
            title.setText(R.string.title_bar_text_create);
        } else {
            title.setText(R.string.title_bar_text_edit);
        }
    }

    @Override
    public void onBackPressed() {
        showDialog(DIALOG_CONFIRM_QUIT);
    }

    @Override
    public void exitActivity() {
        deleteRemovedCaseHistory(isCreatePatient() ?
                TOKEN_NEED_DELETE_PATIENT : 0);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_INPUT_NAME_EMPTY:
            return MedicineUtil.createAlterDialog(this,
                    getString(R.string.dialog_alter_title),
                    getString(R.string.dialog_empty_name_message));
        default:
            return super.onCreateDialog(id);
        }
    }

    private boolean isCreatePatient() {
        return mNewPatient;
    }

    private void setPatientState(boolean newPatient) {
        mNewPatient = newPatient;
    }

    public void onNationButtonClick(View v){
        Intent intent = new Intent(this, NationSelectorActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_NATION);
    }

    public void onAddHistoryButtonClick(View v){
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        if(hasHistory()){
            intent.putExtra(PatientHistoryActivity.EXTRA_STRING_VALUE_EXSIT_HISTORY,
                    mHistory);
        }
        startActivityForResult(intent, REQUEST_CODE_EDIT_HISTORY);
    }

    public void onAddCaseHistoryButtonClick(View v){
        Intent intent = new Intent(this, CaseHistoryInfoEditActivity.class);
        intent.putExtra(EXTRA_INT_VALUE_PATIENT_ID, mPatientId);
        startActivityForResult(intent, REQUEST_CODE_EDIT_CASE_HISTORY);
    }

    private boolean hasHistory() {
        return mAddHistoryLayout.getChildCount() == 2;
    }

    public void confirmToSave(View v){
        String name = mNameEdit.getText().toString();
        if(TextUtils.isEmpty(name)){
            showDialog(DIALOG_INPUT_NAME_EMPTY);
            return ;
        }
        savePatient();
        deleteRemovedCaseHistory(0);
    }

    private void savePatient() {
        ContentValues values = new ContentValues();
        String name = mNameEdit.getText().toString();
        values.put(PatientColumns.NAME, name);
        int gender = (int) mGenderSpinner.getSelectedItemId();
        values.put(PatientColumns.GENDER, gender);
        String age = mAgeEdit.getText().toString();
        if(!TextUtils.isEmpty(age)){
            values.put(PatientColumns.AGE, age);
        }
        String telephony = mTelephoyEdit.getText().toString();
        if(!TextUtils.isEmpty(telephony)){
            values.put(PatientColumns.TELEPHONE, telephony);
        }
        String address = mAddressEdit.getText().toString();
        if(!TextUtils.isEmpty(address)){
            values.put(PatientColumns.ADDRESS, address);
        }
        String nation = mNationButton.getText().toString();
        values.put(PatientColumns.NATION, nation);
        if(hasHistory()){
            values.put(PatientColumns.HISTORY, mHistory);
        }
        Uri uri = Uri.withAppendedPath(PatientColumns.CONTENT_URI, String.valueOf(mPatientId));
        getContentResolver().update(uri, values, null, null);
    }

    public void onRemoveButtonClick(View v){
        RemoveableLayoutView view = (RemoveableLayoutView)v.getParent();
        if(view.getRecordId() == RemoveableLayoutView.NO_ID){
            mAddHistoryLayout.removeViewAt(1);
            mHistory = null;
        } else {
            mAddCaseHistoryLayout.removeView(view);
            Integer id = view.getRecordId();
            mCaseHistoryDeleteIds.add(id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        RemoveableLayoutView view;
        switch(requestCode) {
            case REQUEST_CODE_SELECT_NATION:
                String nation = data.getStringExtra(
                        NationSelectorActivity.EXTRA_STRING_VALUE_NATION_NAME);
                mNationButton.setText(nation);
                break;
            case REQUEST_CODE_EDIT_HISTORY:
                mHistory = data.getStringExtra(
                        PatientHistoryActivity.EXTRA_STRING_VALUE_EDIT_HISTORY);
                if(!hasHistory()){
                    //new history.
                    view = (RemoveableLayoutView) mInflater.inflate(
                        R.layout.removeable_single_item, null);
                    mAddHistoryLayout.addView(view, mAddHistoryLayout.getChildCount());
                } else {
                    // edit history.
                    view = (RemoveableLayoutView) mAddHistoryLayout.getChildAt(1);
                }
                TextView historyTextView = (TextView) view.findViewById(R.id.content_text_view);
                historyTextView.setText(mHistory);
                break;
            case REQUEST_CODE_EDIT_CASE_HISTORY:
                int id = data.getIntExtra(EXTRA_INT_VALUE_CASE_HISOTRY_ID, DEFAULT_ID_VALUE);
                Uri uri = Uri.withAppendedPath(CaseHistoryColumn.CONTENT_URI, String.valueOf(id));
                Cursor c = getContentResolver().query(uri, new String[] {CaseHistoryColumn.DESCRIPTION},
                        null, null, null);
                if(c != null ) {
                    try {
                        c.moveToFirst();
                        addCaseHistoryView(id, c.getString(0));
                    } finally {
                        c.close();
                    }
                }
            default:
                break;
        }
    }

    private void addCaseHistoryView(int id, String description) {
        RemoveableLayoutView view = (RemoveableLayoutView) mInflater.inflate(
                R.layout.removeable_single_item, null);
        view.setRecordId(id);
        TextView descripView = (TextView) view.findViewById(R.id.content_text_view);
        descripView.setText(description);
        mAddCaseHistoryLayout.addView(view, mAddHistoryLayout.getChildCount());
    }

    public class PatientEditAsyncQueryHandler extends AsyncQueryHandler{

        public PatientEditAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if(token == TOKEN_NEED_DELETE_PATIENT){
                Uri uri = Uri.withAppendedPath(PatientColumns.CONTENT_URI, String.valueOf(mPatientId));
                mAsyncQuery.startDelete(0, null, uri, null, null);
                return ;
            }
            removeDialog(DIALOG_WAITING);
            finish();
        }
    }
}
