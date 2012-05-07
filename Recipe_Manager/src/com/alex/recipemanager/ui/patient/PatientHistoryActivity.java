package com.alex.recipemanager.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.alex.recipemanager.R;
import com.alex.recipemanager.ui.base.BaseActivity;

public class PatientHistoryActivity extends BaseActivity{

    public static final String EXTRA_STRING_VALUE_EDIT_HISTORY = "extra_string_value_edit_histroy";
    public static final String EXTRA_STRING_VALUE_EXSIT_HISTORY = "extra_string_value_exsit_histroy";

    private EditText mHistoryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.patient_history_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        TextView title = (TextView) findViewById(R.id.title_bar_text);
        title.setText(R.string.patient_history);
        mHistoryEdit = (EditText) findViewById(R.id.patient_history_edit_text);
        String exsitHistory = getIntent().getStringExtra(EXTRA_STRING_VALUE_EXSIT_HISTORY);
        mHistoryEdit.setText(exsitHistory);
    }

    public void confirmToSave(View v){
        String history = mHistoryEdit.getText().toString();
        if(TextUtils.isEmpty(history)){
            showDialog(DIALOG_INPUT_EMPTY);
            return ;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STRING_VALUE_EDIT_HISTORY, history);
        setResult(RESULT_OK, intent);
        finish();
    }
}
