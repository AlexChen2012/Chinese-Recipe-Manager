package com.alex.recipemanager.util;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alex.recipemanager.util.HanziToPinyin.Token;

public class MedicineUtil {

    public static final int SELECTION_MAX_LEN = 800;

    private MedicineUtil(){
        //Forbid create object.
    }

    public static Dialog createAlterDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok, null);
        return builder.create();
    }

    public static String getWhereClauseById(ArrayList<Long> ids) {
        StringBuilder sql = new StringBuilder();
        sql.append("_id in (");
        while (sql.length() <= SELECTION_MAX_LEN && ids.size() > 0) {
            sql.append(ids.remove(0));
            sql.append(",");
        }
        if (sql.charAt(sql.length() - 1) == ',') {
            sql.deleteCharAt(sql.length() - 1); // trim the trailing ','
        }
        sql.append(")");
        return sql.toString();
    }

    public static String getPinyinAbbr(String name) {
        StringBuilder abbrSb = new StringBuilder();
        if (TextUtils.isEmpty(name)) {
            return abbrSb.toString();
        }
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(name);
        for (Token token : tokens) {
            abbrSb.append(token.target.charAt(0));
        }
        return abbrSb.toString();
    }

    public static Dialog addDimissControl(Dialog dialog) {
        try {
            Field field = dialog.getClass().getDeclaredField("mAlert");
            field.setAccessible(true);
            Object obj = field.get(dialog);
            field = obj.getClass().getDeclaredField("mHandler");
            field.setAccessible(true);
            field.set(obj, new ButtonHandler(dialog));
        } catch (Exception e) {}
        return dialog;
    }


    private static class ButtonHandler extends Handler {

        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            mDialog = new WeakReference<DialogInterface>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DialogInterface.BUTTON_POSITIVE:
                case DialogInterface.BUTTON_NEGATIVE:
                case DialogInterface.BUTTON_NEUTRAL:
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
                    break;
            }
        }
    }
}
