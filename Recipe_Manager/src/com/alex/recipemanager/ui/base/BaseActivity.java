package com.alex.recipemanager.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.CaseHistoryColumn;
import com.alex.recipemanager.provider.RecipeContent.MedicineColumn;
import com.alex.recipemanager.provider.RecipeContent.MedicineNameColumn;
import com.alex.recipemanager.provider.RecipeContent.PatientColumns;
import com.alex.recipemanager.provider.RecipeContent.RecipeColumn;
import com.alex.recipemanager.provider.RecipeContent.RecipeMedicineColumn;
import com.alex.recipemanager.util.MedicineUtil;

public class BaseActivity extends Activity{
    public static final String EXTRA_LONG_VALUE_PATIENT_ID = "extra_long_value_patient_id";
    public static final String EXTRA_LONG_VALUE_CASE_HISOTRY_ID = "extra_long_value_case_history_id";
    public static final String EXTRA_LONG_VALUE_RECIPE_ID = "extra_long_value_recipe_id";
    public static final String EXTRA_INT_VALUE_RECIPE_MODE = "extra_long_value_recipe_mode";
    public static final long DEFAULT_ID_VALUE = -1L;

    public static final String[] PATIENT_TABLE_PROJECTION = new String[] {
        PatientColumns._ID,
        PatientColumns.NAME,
        PatientColumns.GENDER,
        PatientColumns.NATION,
        PatientColumns.AGE,
        PatientColumns.ADDRESS,
        PatientColumns.HISTORY,
        PatientColumns.TELEPHONE,
        PatientColumns.FIRST_TIME
    };

    public static final int COLUMN_PATIENT_ID         = 0;
    public static final int COLUMN_PATIENT_NAME       = 1;
    public static final int COLUMN_PATIENT_GENDER     = 2;
    public static final int COLUMN_PATIENT_NATION     = 3;
    public static final int COLUMN_PATIENT_AGE        = 4;
    public static final int COLUMN_PATIENT_ADDRESS    = 5;
    public static final int COLUMN_PATIENT_HISTORY    = 6;
    public static final int COLUMN_PATIENT_TELEPHONE  = 7;
    public static final int COLUMN_PATIENT_FIRST_TIME = 8;

    public static final String[] CASE_HISTORY_TABLE_PROJECTION = new String[]{
        CaseHistoryColumn._ID,
        CaseHistoryColumn.PATIENT_KEY,
        CaseHistoryColumn.SYMPTOM,
        CaseHistoryColumn.DESCRIPTION,
        CaseHistoryColumn.FIRST_TIME,
        CaseHistoryColumn.TIMESTAMP
    };

    public static final int COLUMN_CASE_HISTORY_ID          = 0;
    public static final int COLUMN_CASE_HISTORY_PATIENT_KEY = 1;
    public static final int COLUMN_CASE_HISTORY_SYMPTOM     = 2;
    public static final int COLUMN_CASE_HISTORY_DESCRIPTION = 3;
    public static final int COLUMN_CASE_HISTORY_FIRST_TIME  = 4;
    public static final int COLUMN_CASE_HISTORY_TIMESTAMP   = 5;

    public static final String[] RECIPE_TABLE_PROJECTION = new String[]{
        RecipeColumn._ID,
        RecipeColumn.NAME,
        RecipeColumn.COUNT,
        RecipeColumn.TIMESTAMP,
        RecipeColumn.IS_STORAGE,
        RecipeColumn.RECIPE_TYPE
    };

    public static final int COLUMN_RECIPE_ID    = 0;
    public static final int COLUMN_RECIPE_NAME  = 1;
    public static final int COLUMN_RECIPE_COUNT = 2;
    public static final int COLUMN_RECIPE_TIMESTAMP = 3;
    public static final int COLUMN_RECIPE_IS_STORAGE = 4;
    public static final int COLUMN_RECIPE_TYPE = 5;

    public static final String[] RECIPE_MEDICINE_JOIN_MEDICINE_NAME_PROJECTION = new String[] {
        MedicineNameColumn._ID,
        MedicineNameColumn.MEDICINE_NAME,
        RecipeMedicineColumn.MEDICINE_NAME_KEY,
        RecipeMedicineColumn.RECIPE_KEY,
        RecipeMedicineColumn.WEIGHT,
        MedicineColumn.AMOUNT,
        MedicineColumn.GROSS_WEIGHT,
        MedicineColumn.THRESHOLD
    };

    public static final int COLUMN_RECIPE_MEDICINE_NAME_ID      = 0;
    public static final int COLUMN_RECIPE_MEDICINE_NAME         = 1;
    public static final int COLUMN_RECIPE_MEDICINE_KEY          = 2;
    public static final int COLUMN_RECIPE_MEDICINE_RECIPE_KEY   = 3;
    public static final int COLUMN_RECIPE_MEDICINE_WEIGHT       = 4;
    public static final int COLUMN_RECIPE_MEDICINE_AMOUNT       = 5;
    public static final int COLUMN_RECIPE_MEDICINE_GROSS_WEIGHT = 6;
    public static final int COLUMN_RECIPE_MEDICINE_THRESHOLD    = 7;

    //use negative number to define dialog, since subclass may define it's own dialog.
    protected static final int DIALOG_WAITING      = -1;
    protected static final int DIALOG_INPUT_EMPTY  = -2;
    protected static final int DIALOG_NAME_EXSIT   = -3;
    protected static final int DIALOG_CONFIRM_QUIT = -4;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_WAITING:
            ProgressDialog waitingDialog = new ProgressDialog(this);
            waitingDialog.setMessage(this.getString(R.string.dialog_waiting));
            return waitingDialog;
        case DIALOG_INPUT_EMPTY:
            return MedicineUtil.createAlterDialog(this,
                    getString(R.string.dialog_alter_title), getString(R.string.dialog_empty_message));
        case DIALOG_NAME_EXSIT:
            return MedicineUtil.createAlterDialog(this,
                    getString(R.string.dialog_alter_title), getString(R.string.dialog_exsit_message));
        case DIALOG_CONFIRM_QUIT:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_alter_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.dialog_quit_edit_patient_message)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        confirmToExistActivity();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        default:
            return super.onCreateDialog(id);
        }
    }

    public void confirmToExistActivity() {
        //Need be implement in subclass.
    }
}
