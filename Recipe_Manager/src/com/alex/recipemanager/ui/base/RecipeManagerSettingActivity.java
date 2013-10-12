package com.alex.recipemanager.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.alex.recipemanager.R;
import com.alex.recipemanager.util.Consts;

public class RecipeManagerSettingActivity extends Activity {

    private EditText mRegisterFeeEdit;
    private EditText mBagFeeEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity_layout);
        mRegisterFeeEdit = (EditText) findViewById(R.id.setting_register_fee_edit);
        mBagFeeEdit = (EditText) findViewById(R.id.setting_bag_price_edit);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mRegisterFeeEdit.setText(sp.getString(Consts.EXTRA_STRING_VALUE_REGISTER_FEE, "10"));
        mBagFeeEdit.setText(sp.getString(Consts.EXTRA_STRING_VALUE_BAG_FEE, "0.2"));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_alter_title)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setMessage(R.string.dialog_quit_with_save)
        .setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Editor editor = PreferenceManager.getDefaultSharedPreferences(RecipeManagerSettingActivity.this).edit();
                editor.putString(Consts.EXTRA_STRING_VALUE_REGISTER_FEE, mRegisterFeeEdit.getText().toString());
                editor.putString(Consts.EXTRA_STRING_VALUE_BAG_FEE, mBagFeeEdit.getText().toString());
                editor.commit();
                finish();
            }
        })
        .setNegativeButton(android.R.string.cancel, null)
        .create()
        .show();
    }
}
