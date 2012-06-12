package com.alex.recipemanager.ui.casehistory;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.CaseHistoryColumn;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.ui.recipe.RecipesListActivity;

public class CaseHistoryInfoViewActivity extends BaseActivity {

    private static final String TAG = "CaseHistoryInfoEditActivity";

    private static final int MENU_CREATE = 0;

    private long mCaseHistoryId;
    private long mPatientId;
    private TextView mSymptomView;
    private TextView mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.case_history_info_view_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        mSymptomView = (TextView) findViewById(R.id.case_history_symptom_view);
        mDescriptionView = (TextView) findViewById(R.id.case_history_description_view);
        mCaseHistoryId = getIntent().getLongExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, DEFAULT_ID_VALUE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCaseHistoryId == DEFAULT_ID_VALUE) {
            Log.e(TAG, "can not get case history id from intent");
            finish();
        }
        Uri uri = Uri.withAppendedPath(CaseHistoryColumn.CONTENT_URI, String.valueOf(mCaseHistoryId));
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    mPatientId = Long.valueOf(c.getLong(c.getColumnIndexOrThrow(CaseHistoryColumn.PATIENT_KEY)));
                    mSymptomView.setText(c.getString(c.getColumnIndexOrThrow(CaseHistoryColumn.SYMPTOM)));
                    mDescriptionView.setText(c.getString(c.getColumnIndexOrThrow(CaseHistoryColumn.DESCRIPTION)));
                }
            } finally {
                c.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_CREATE, 0, R.string.title_bar_text_edit).setIcon(
                android.R.drawable.ic_menu_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_CREATE:
            Intent intent = new Intent(this, CaseHistoryInfoEditActivity.class);
            intent.putExtra(EXTRA_LONG_VALUE_PATIENT_ID, mPatientId);
            intent.putExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, mCaseHistoryId);
            startActivity(intent);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onCheckRecipesListClicked(View view) {
        Intent intent = new Intent(this, RecipesListActivity.class);
        intent.putExtra(EXTRA_LONG_VALUE_CASE_HISOTRY_ID, mCaseHistoryId);
        startActivity(intent);
    }
}
