package com.alex.recipemanager.ui.base;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;

import com.alex.recipemanager.R;
import com.alex.recipemanager.util.MedicineUtil;

public class BaseListActivity extends ListActivity{
    //use negative number to define dialog, since subclass may define it's own dialog.
    protected static final int DIALOG_WAITING      = -1;
    protected static final int DIALOG_INPUT_EMPTY  = -2;
    protected static final int DIALOG_NAME_EXSIT   = -3;

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
        default:
            return super.onCreateDialog(id);
        }
    }
}
