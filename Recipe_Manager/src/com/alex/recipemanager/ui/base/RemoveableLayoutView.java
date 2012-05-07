package com.alex.recipemanager.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class RemoveableLayoutView extends LinearLayout{

    public static final int NO_ID = -1;

    private int mCaseHistoryId = NO_ID;
    public RemoveableLayoutView(Context context) {
        super(context);
    }

    public RemoveableLayoutView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setRecordId(int id){
        mCaseHistoryId = id;
    }

    public int getRecordId(){
        return mCaseHistoryId;
    }
}
