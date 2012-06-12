package com.alex.recipemanager.ui.medicine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.MedicineColumn;
import com.alex.recipemanager.provider.RecipeContent.MedicineNameColumn;
import com.alex.recipemanager.ui.base.BaseListActivity;
import com.alex.recipemanager.util.MedicineUtil;

public class MedicineListActivity extends BaseListActivity{

    public static final String EXTRA_BOOLEAN_VALUE_START_FROM_MEDICINE_SELECTOR = "exatra_boolean_value_selector";
    public static final String EXTRA_STRING_VALUE_MEDICINE_NAME = "exatra_string_value_medicine_name";
    public static final String EXTRA_LONG_VALUE_MEDICINE_ID = "exatra_long_value_medicine_id";

    public static final String[] MEDINE_NAME_JOIN_AMOUNT_PROJECTION = new String[]{
        MedicineNameColumn._ID,
        MedicineColumn.AMOUNT,
        MedicineNameColumn.MEDICINE_NAME,
        MedicineNameColumn.MEDICINE_KEY,
        MedicineColumn.GROSS_WEIGHT,
        MedicineColumn.THRESHOLD
    };
    public static final int MEDICINE_NAME_ID_COLUMN      = 0;
    public static final int MEDICINE_AMOUNT_COLUMN       = 1;
    public static final int MEDICINE_NAME_COLUMN         = 2;
    public static final int MEDICINE_KEY_COLUMN          = 3;
    public static final int MEDICINE_GROSS_WEIGHT_COLUMN = 4;
    public static final int MEDICINE_THRESHOLD_COLUMN    = 5;

    private static final int MENU_CREATE = 0;
//    private static final int MENU_SEARCH = 1;

    private static final int DIALOG_ADD_MEDICINE = 0;

    private static final int INVALIDE_POSITION = -1;

    private static final int TOKEN_QUERY_MEDICINE_NAME     = 0;
    private static final int TOKEN_INSERT_MEDICINE_TABLE  = 1;
    private static final int TOKEN_INSERT_MEDICINE_NAME    = 2;
    private static final int TOKEN_UPGRADE_MEDICINE_NAME   = 3;
    private static final int TOKEN_UPGRADE_MEDICINE_AMOUNT = 4;
    private static final int TOKEN_QUERY_FOR_UPDATE_LIST   = 5;

    private static final int CONTEXT_MENU_EDIT   = 0;
    private static final int CONTEXT_MENU_DELETE = 1;

    private Cursor mCursor;
    private MedicineListAdapter mAdapter;
    private MedicineAsyncQueryHandler mQueryHandler;
    private static ItemCache mItemCache;
    private boolean mSelectorMode;
    private int mPosition;
    private String mOldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_list_layout);
        setTitle(R.string.medicine_manage);
        mPosition = INVALIDE_POSITION;
        mSelectorMode = getIntent().getBooleanExtra(EXTRA_BOOLEAN_VALUE_START_FROM_MEDICINE_SELECTOR, false);
        mAdapter = new MedicineListAdapter(this, null);
        getListView().setAdapter(mAdapter);
        getListView().setOnCreateContextMenuListener(this);
        mQueryHandler = new MedicineAsyncQueryHandler(getContentResolver());
        mItemCache = new ItemCache();
        mOldName = "";
        initSearchView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList(null);
    }

    private void updateList(String selection) {
        showDialog(DIALOG_WAITING);
        mQueryHandler.startQuery(TOKEN_QUERY_FOR_UPDATE_LIST,
                null,
                MedicineNameColumn.FETCH_MEDICINE_AND_NAME_URI,
                MEDINE_NAME_JOIN_AMOUNT_PROJECTION,
                selection,
                null,
                MedicineNameColumn.DEFAULT_ORDER);
    }

    private void initSearchView() {
        EditText editText = (EditText) findViewById(R.id.search_edit_view);
        editText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            public void afterTextChanged(Editable s) {
                String selection = null;
                if(!TextUtils.isEmpty(s)){
                    //XXX: we do not use selectionArgs to set name since this is a android bug.
                    //get more info from url: http://code.google.com/p/android/issues/detail?id=3153.
                    //String selectionArgs[] = new String[] {name};
                    selection = MedicineNameColumn.MEDICINE_NAME + " LIKE '%" + s.toString() + "%' OR "
                        + MedicineNameColumn.MEDICINE_NAME_ABBR + " LIKE '" + s.toString() + "%'";
                }
                updateList(selection);
            }
        });
    }

    @Override
    protected void onPause() {
        if(mCursor != null){
            mCursor.close();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(mSelectorMode){
            Cursor c = (Cursor) mAdapter.getItem(position);
            if(c != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_STRING_VALUE_MEDICINE_NAME, c.getString(MEDICINE_NAME_COLUMN));
                intent.putExtra(EXTRA_LONG_VALUE_MEDICINE_ID, c.getLong(MEDICINE_KEY_COLUMN));
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            Intent intent = new Intent(this, AliasListActivity.class);
            intent.putExtra(AliasListActivity.EXTRA_STRING_VALUE_MEDICINE_NAME_ID, String.valueOf(id));
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mSelectorMode) {
            return true;
        }
        menu.add(0, MENU_CREATE, 0, R.string.menu_create).setIcon(
                android.R.drawable.ic_menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_CREATE:
            showDialog(DIALOG_ADD_MEDICINE);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        if(mSelectorMode) {
            return ;
        }
        menu.add(0, CONTEXT_MENU_EDIT, 0, R.string.context_menu_edit);
        menu.add(0, CONTEXT_MENU_DELETE, 1, R.string.context_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        mPosition = info.position;
        switch (item.getItemId()) {
        case CONTEXT_MENU_EDIT:
            showDialog(DIALOG_ADD_MEDICINE);
            return true;
        case CONTEXT_MENU_DELETE:
            showDialog(DIALOG_WAITING);
            Uri uri = Uri.withAppendedPath(MedicineNameColumn.CONTENT_URI, String.valueOf(info.id));
            mQueryHandler.startDelete(0, null, uri, null, null);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ADD_MEDICINE:
            return MedicineUtil.addDimissControl(createAddOrEditMedicineDialog());
        default:
            return super.onCreateDialog(id);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if(id == DIALOG_ADD_MEDICINE){
            EditText name = (EditText)dialog.findViewById(R.id.medicine_name_edit);
            EditText amount = (EditText)dialog.findViewById(R.id.medicine_amount_edit);
            EditText grossWeight = (EditText)dialog.findViewById(R.id.medicine_gross_weight_edit);
            EditText threshold = (EditText)dialog.findViewById(R.id.medicine_threshold_edit);

            mItemCache.clean();
            if (mPosition != INVALIDE_POSITION) {
                Cursor c = (Cursor) mAdapter.getItem(mPosition);
                mItemCache.mIsUpdated = true;
                mItemCache.mMedicineNameId = String.valueOf(c.getLong(MEDICINE_NAME_ID_COLUMN));
                mItemCache.mMedicineId = String.valueOf(c.getLong(MEDICINE_KEY_COLUMN));
                mItemCache.mGrossWeight = String.valueOf(c.getLong(MEDICINE_GROSS_WEIGHT_COLUMN));
                mItemCache.mThreshold = String.valueOf(c.getLong(MEDICINE_THRESHOLD_COLUMN));

                mOldName = c.getString(MEDICINE_NAME_COLUMN);
                name.setText(mOldName);
                amount.setText(String.valueOf(c.getInt(MEDICINE_AMOUNT_COLUMN)));
                grossWeight.setText(String.valueOf(c.getInt(MEDICINE_GROSS_WEIGHT_COLUMN)));
                threshold.setText(String.valueOf(c.getInt(MEDICINE_THRESHOLD_COLUMN)));
                cleanPosition();
            } else {
                mItemCache.mIsUpdated = false;

                name.setText("");
                amount.setText("");
            }
        }
        super.onPrepareDialog(id, dialog);
    }

    private void cleanPosition() {
        mPosition = INVALIDE_POSITION;
    }

    private Dialog createAddOrEditMedicineDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_medicine_dialog_entry, null);
        final EditText name = (EditText)textEntryView.findViewById(R.id.medicine_name_edit);
        final EditText amount = (EditText)textEntryView.findViewById(R.id.medicine_amount_edit);
        final EditText grossWeight = (EditText)textEntryView.findViewById(R.id.medicine_gross_weight_edit);
        final EditText threshold = (EditText)textEntryView.findViewById(R.id.medicine_threshold_edit);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_add_medicine_title);
        builder.setView(textEntryView);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(isEmptyInput(name.getText(), amount.getText(), grossWeight.getText(), threshold.getText())){
                    Toast.makeText(MedicineListActivity.this, getString(R.string.dialog_empty_message),
                            Toast.LENGTH_LONG).show();
                } else {
                    mItemCache.mAmount = amount.getText().toString();
                    mItemCache.mName = (String) name.getText().toString();
                    mItemCache.mAbbr = MedicineUtil.getPinyinAbbr(mItemCache.mName);
                    mItemCache.mGrossWeight = grossWeight.getText().toString();
                    mItemCache.mThreshold = threshold.getText().toString();

                    String selection = MedicineNameColumn.MEDICINE_NAME + " =?";
                    String[] selectionArgs = new String[] { mItemCache.mName };
                    mQueryHandler.startQuery(TOKEN_QUERY_MEDICINE_NAME,
                            mItemCache,
                            MedicineNameColumn.FETCH_MEDICINE_AND_NAME_URI,
                            MEDINE_NAME_JOIN_AMOUNT_PROJECTION,
                            selection,
                            selectionArgs,
                            null);
                    showDialog(DIALOG_WAITING);
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeDialog(DIALOG_ADD_MEDICINE);
            }
        });
        return builder.create();
    }

    private boolean isEmptyInput(CharSequence name, CharSequence amount, CharSequence weight, CharSequence threshold) {
        return TextUtils.isEmpty(name) || TextUtils.isEmpty(amount)
                || TextUtils.isEmpty(weight) || TextUtils.isEmpty(threshold);
    }

    private class MedicineAsyncQueryHandler extends AsyncQueryHandler{

        public MedicineAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if(token == TOKEN_QUERY_MEDICINE_NAME){
                ItemCache cache = (ItemCache) cookie;
                if(cursor != null){
                    try{
                        if(isIlleagelInput(cursor, cache)){
                            removeDialog(DIALOG_WAITING);
                            Toast.makeText(MedicineListActivity.this, getString(R.string.dialog_exsit_message),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            mOldName = "";
                            ContentValues values = new ContentValues(1);
                            if(cache.mIsUpdated){
                                values.put(MedicineNameColumn.MEDICINE_NAME, cache.mName);
                                Uri uri = Uri.withAppendedPath(MedicineNameColumn.CONTENT_URI, cache.mMedicineNameId);
                                startUpdate(TOKEN_UPGRADE_MEDICINE_NAME, cookie, uri, values, null, null);
                            } else {
                                values.put(MedicineColumn.AMOUNT, cache.mAmount);
                                values.put(MedicineColumn.GROSS_WEIGHT, cache.mGrossWeight);
                                values.put(MedicineColumn.THRESHOLD, cache.mThreshold);
                                startInsert(TOKEN_INSERT_MEDICINE_TABLE, cookie, MedicineColumn.CONTENT_URI, values);
                            }
                            removeDialog(DIALOG_ADD_MEDICINE);
                        }
                    } finally {
                        cursor.close();
                    }
                } else {
                    removeDialog(DIALOG_WAITING);
                }
            }

            if(token == TOKEN_QUERY_FOR_UPDATE_LIST){
                if(cursor != null){
                    mCursor = cursor;
                    mAdapter.changeCursor(mCursor);
                }
                removeDialog(DIALOG_WAITING);
            }
        }

        private boolean isIlleagelInput(Cursor c, ItemCache cache) {
            if (cache.mIsUpdated == true) {
                return (isNameExist(c) && c.moveToFirst()
                        && !TextUtils.equals(c.getString(MEDICINE_NAME_COLUMN), mOldName));
            } else {
                return isNameExist(c);
            }
        }

        private boolean isNameExist(Cursor c) {
            return c.getCount() > 0;
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            if(token == TOKEN_INSERT_MEDICINE_TABLE){
                if(uri != null) {
                    ItemCache cache = (ItemCache) cookie;
                    int id = Integer.valueOf(uri.getPathSegments().get(1));
                    ContentValues values = new ContentValues();
                    values.put(MedicineNameColumn.MEDICINE_KEY, id);
                    values.put(MedicineNameColumn.MEDICINE_NAME, cache.mName);
                    values.put(MedicineNameColumn.MEDICINE_NAME_ABBR, cache.mAbbr);
                    startInsert(TOKEN_INSERT_MEDICINE_NAME, null, MedicineNameColumn.CONTENT_URI, values);
                }else {
                    //something wrong.
                    removeDialog(DIALOG_WAITING);
                }
            }
            if(token == TOKEN_INSERT_MEDICINE_NAME){
                Toast.makeText(MedicineListActivity.this, R.string.toast_add_success, Toast.LENGTH_LONG).show();
                updateList(null);
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if(token == TOKEN_UPGRADE_MEDICINE_NAME){
                ItemCache cache = (ItemCache) cookie;
                ContentValues values = new ContentValues();
                values.put(MedicineColumn.AMOUNT, cache.mAmount);
                values.put(MedicineColumn.GROSS_WEIGHT, cache.mGrossWeight);
                values.put(MedicineColumn.THRESHOLD, cache.mThreshold);
                Uri uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, cache.mMedicineId);
                startUpdate(TOKEN_UPGRADE_MEDICINE_AMOUNT, cookie, uri, values, null, null);
            }
            if(token == TOKEN_UPGRADE_MEDICINE_AMOUNT){
                Toast.makeText(MedicineListActivity.this, R.string.toast_edit_success, Toast.LENGTH_LONG).show();
                updateList(null);
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            Toast.makeText(MedicineListActivity.this, R.string.toast_delete_success, Toast.LENGTH_LONG).show();
            updateList(null);
        }
    }

    static class ItemCache{
        String mName;
        String mAbbr;
        String mAmount;
        String mGrossWeight;
        String mThreshold;
        boolean mIsUpdated;
        String mMedicineNameId;
        String mMedicineId;

        void clean() {
            mName = null;
            mAbbr = null;
            mAmount = null;
            mMedicineId = null;
            mMedicineNameId = null;
            mGrossWeight = null;
            mThreshold = null;
            mIsUpdated = false;
        }
    }

}
