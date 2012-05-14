package com.alex.recipemanager.util;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

    public static String getWhereClauseById(ArrayList<Integer> ids) {
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
}
