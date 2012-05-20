package com.alex.recipemanager.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class RemoveableLayoutView extends LinearLayout{

    public static final long NO_ID = -1L;

    private long mCaseHistoryId = NO_ID;

    public RemoveableLayoutView(Context context) {
        super(context);
    }

    public RemoveableLayoutView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setRecordId(long id){
        mCaseHistoryId = id;
    }

    public long getRecordId(){
        return mCaseHistoryId;
    }
}
