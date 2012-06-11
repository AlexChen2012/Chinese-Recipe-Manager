package com.alex.recipemanager.ui.medicine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.MedicineNameColumn;
import com.alex.recipemanager.ui.base.BaseListActivity;
import com.alex.recipemanager.util.MedicineUtil;

public class AliasListActivity extends BaseListActivity{
    public final static String EXTRA_STRING_VALUE_MEDICINE_NAME_ID = "extra_string_value_medicine_name_id";

    private static final int DIALOG_ADD_ALIAS    = 0;

    private static final int TOKEN_QUERY_UPDATE_LIST   = 0;
    private static final int TOKEN_QUERY_MEDICINE_NAME = 1;

    private static final int MENU_CREATE = 0;

    private static final int CONTEXT_MENU_DELETE = 0;

    private String mAliasId;
    private long mMedicineKey;
    private AliasAsyncQueryHandler mAsyncQuery;
    private AliasListAdapter mAdapter;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_alias_list_layout);
        setTitle(R.string.medicine_manage);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    protected void onPause() {
        if(mCursor != null){
            mCursor.close();
        }
        super.onPause();
    }

    private void initialize() {
        mAliasId = getIntent().getStringExtra(EXTRA_STRING_VALUE_MEDICINE_NAME_ID);
        Uri uri = Uri.withAppendedPath(MedicineNameColumn.FETCH_MEDICINE_AND_NAME_URI, mAliasId);
        Cursor c = getContentResolver().query(uri, MedicineListActivity.MEDINE_NAME_JOIN_AMOUNT_PROJECTION,
                null, null, null);
        if(c != null){
            try{
                c.moveToFirst();
                mMedicineKey = c.getLong(MedicineListActivity.MEDICINE_KEY_COLUMN);
                TextView name = (TextView) findViewById(R.id.medicine_name);
                TextView amount = (TextView) findViewById(R.id.medicine_amount);
                TextView grossWeight = (TextView) findViewById(R.id.medicine_gross_weight);
                TextView threshold = (TextView) findViewById(R.id.medicine_threshold);
                name.setText(c.getString(MedicineListActivity.MEDICINE_NAME_COLUMN));
                amount.setText(String.valueOf(c.getInt(MedicineListActivity.MEDICINE_AMOUNT_COLUMN)));
                grossWeight.setText(String.valueOf(c.getInt(MedicineListActivity.MEDICINE_GROSS_WEIGHT_COLUMN)));
                threshold.setText(String.valueOf(c.getInt(MedicineListActivity.MEDICINE_THRESHOLD_COLUMN)));
            } finally {
                c.close();
            }
        }
        mAsyncQuery = new AliasAsyncQueryHandler(getContentResolver());
        mAdapter = new AliasListAdapter(this, null);
        getListView().setAdapter(mAdapter);
        getListView().setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        menu.add(0, CONTEXT_MENU_DELETE, 1, R.string.context_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case CONTEXT_MENU_DELETE:
            showDialog(DIALOG_WAITING);
            Uri uri = Uri.withAppendedPath(MedicineNameColumn.CONTENT_URI, String.valueOf(info.id));
            mAsyncQuery.startDelete(0, null, uri, null, null);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_CREATE, 0, R.string.menu_create).setIcon(
                android.R.drawable.ic_menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_CREATE:
            showDialog(DIALOG_ADD_ALIAS);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ADD_ALIAS:
            return MedicineUtil.addDimissControl(createAliasDialog());
        default:
            return super.onCreateDialog(id);
        }
    }

    private Dialog createAliasDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_medicine_dialog_entry, null);
        textEntryView.findViewById(R.id.set_amount_layout).setVisibility(View.GONE);
        final TextView name = (TextView)textEntryView.findViewById(R.id.medicine_name_edit);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_add_alias_title);
        builder.setView(textEntryView);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(illegalInput(name.getText())){
                    showDialog(DIALOG_INPUT_EMPTY);
                } else {
                    ItemCache cookie = new ItemCache();
                    cookie.mName = name.getText().toString();
                    String selection = MedicineNameColumn.MEDICINE_NAME + " = ?";
                    String []selectionArgs = new String[]{cookie.mName};
                    mAsyncQuery.startQuery(TOKEN_QUERY_MEDICINE_NAME, cookie,
                                MedicineNameColumn.CONTENT_URI, null, selection, selectionArgs, null);
                    removeDialog(DIALOG_ADD_ALIAS);
                    showDialog(DIALOG_WAITING);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeDialog(DIALOG_ADD_ALIAS);
            }
        });
        return builder.create();
    }

    private boolean illegalInput(CharSequence name) {
        return TextUtils.isEmpty(name);
    }

    private void updateList() {
        showDialog(DIALOG_WAITING);
        String selection = MedicineNameColumn.MEDICINE_KEY + " = ? AND " + MedicineNameColumn._ID + " !=?";
        String selectionArgs[] = new String[] {String.valueOf(mMedicineKey), mAliasId};
        mAsyncQuery.startQuery(TOKEN_QUERY_UPDATE_LIST, null, MedicineNameColumn.CONTENT_URI, null,
                selection, selectionArgs, MedicineNameColumn.DEFAULT_ORDER);
    }

    private class AliasAsyncQueryHandler extends AsyncQueryHandler{

        public AliasAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if(token == TOKEN_QUERY_UPDATE_LIST){
                mCursor = cursor;
                mAdapter.changeCursor(mCursor);
                removeDialog(DIALOG_WAITING);
            }
            if(token  == TOKEN_QUERY_MEDICINE_NAME){
                if(cursor != null){
                    try{
                        if(cursor.getCount() > 0){
                            removeDialog(DIALOG_WAITING);
                            showDialog(DIALOG_NAME_EXSIT);
                        } else {
                            ItemCache cache = (ItemCache) cookie;
                            ContentValues values = new ContentValues();
                            values.put(MedicineNameColumn.MEDICINE_NAME, cache.mName);
                            values.put(MedicineNameColumn.MEDICINE_KEY, mMedicineKey);
                            startInsert(0, null, MedicineNameColumn.CONTENT_URI, values);
                        }
                    } finally {
                        cursor.close();
                    }
                } else {
                    removeDialog(DIALOG_WAITING);
                }
            }
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            if(uri != null){
                Toast.makeText(AliasListActivity.this, R.string.toast_add_success, Toast.LENGTH_LONG).show();
                updateList();
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if(result != -1){
                Toast.makeText(AliasListActivity.this, R.string.toast_delete_success, Toast.LENGTH_LONG).show();
                updateList();
            }
        }
    }

    static class ItemCache{
        String mName;
    }

}
