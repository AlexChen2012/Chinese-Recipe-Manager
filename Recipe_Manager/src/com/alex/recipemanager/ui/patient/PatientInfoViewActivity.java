package com.alex.recipemanager.ui.patient;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.PatientColumns;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.ui.casehistory.CaseHistoryListActivity;

public class PatientInfoViewActivity extends BaseActivity{

    private static final String TAG = "PatientInfoViewActivity";

    private TextView mNameView;
    private TextView mGenderView;
    private TextView mAgeView;
    private TextView mNationView;
    private TextView mAddressView;
    private TextView mTelephoneView;
    private View mHistroyLayout;
    private long mPatientId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_info_view_layout);
        setTitle(R.string.recipe);
        initialize();
    }

    public void onCaseHistroyButtonClick(View v) {
        Intent intent = new Intent(this, CaseHistoryListActivity.class);
        intent.putExtra(BaseActivity.EXTRA_LONG_VALUE_PATIENT_ID, mPatientId);
        startActivity(intent);
    }

    private void initialize() {
        mNameView = (TextView) findViewById(R.id.patient_name_view);
        mGenderView = (TextView) findViewById(R.id.patient_gender_view);
        mAgeView = (TextView) findViewById(R.id.patient_age_view);
        mNationView = (TextView) findViewById(R.id.patient_nation_view);
        mAddressView = (TextView) findViewById(R.id.patient_address_view);
        mTelephoneView = (TextView) findViewById(R.id.patient_telephone_view);
        mHistroyLayout = (View) findViewById(R.id.patient_history_layout);
        mPatientId = getIntent().getLongExtra(BaseActivity.EXTRA_LONG_VALUE_PATIENT_ID, DEFAULT_ID_VALUE);
        if (mPatientId == DEFAULT_ID_VALUE) {
            Log.e(TAG, "Can not get mPatientId from intent");
        }
        Uri uri = Uri.withAppendedPath(PatientColumns.CONTENT_URI, String.valueOf(mPatientId));
        Cursor c = getContentResolver().query(uri, PATIENT_TABLE_PROJECTION, null, null, null);
        if (c != null) {
            c.moveToFirst();
            startManagingCursor(c);
            fillDataToView(c);
        } else {
            finish();
            Log.e(TAG, "Can not get the patient info from Patient table, mPatientId: " + mPatientId);
        }
    }

    private void fillDataToView(Cursor c) {
        mNameView.setText(c.getString(COLUMN_PATIENT_NAME));
        mGenderView.setText(c.getString(COLUMN_PATIENT_GENDER));
        mAgeView.setText(String.valueOf(c.getInt(COLUMN_PATIENT_AGE)));
        mNationView.setText(c.getString(COLUMN_PATIENT_NATION));
        mAddressView.setText(c.getString(COLUMN_PATIENT_ADDRESS));
        mTelephoneView.setText(c.getString(COLUMN_PATIENT_TELEPHONE));
        String history = c.getString(COLUMN_PATIENT_HISTORY);
        if (TextUtils.isEmpty(history)) {
            mHistroyLayout.setVisibility(View.GONE);
        } else {
            mHistroyLayout.setVisibility(View.VISIBLE);
            TextView histroyView = (TextView) findViewById(R.id.patient_history_view);
            histroyView.setText(history);
        }
    }
}
