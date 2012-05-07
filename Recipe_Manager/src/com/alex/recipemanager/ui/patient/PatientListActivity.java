package com.alex.recipemanager.ui.patient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.PatientColumns;
import com.alex.recipemanager.ui.base.BaseActivity;
import com.alex.recipemanager.ui.base.BaseListActivity;

public class PatientListActivity extends BaseListActivity{

    public static final String[] PATIENT_PROJECTION = new String[]{
        PatientColumns._ID,
        PatientColumns.NAME,
        PatientColumns.FIRST_TIME
    };
    public static final int COLUMN_PATIENT_ID         = 0;
    public static final int COLUMN_PATIENT_NAME       = 1;
    public static final int COLUMN_PATIENT_FIRST_TIME = 2;

    private static final int CONTEXT_MENU_EDIT   = 0;
    private static final int CONTEXT_MENU_DELETE = 1;

    private static final int MENU_CREATE = 0;

    private static final int DIALOG_DELETE_CONFIRM = 0;

    private static final int ERROR_VALUE = -1;

    private PatientAsyncQueryHandler mAsyncQuery;
    private PatientListAdapter mAdapter;
    private Cursor mCursor;
    private long mDeletePatientId = ERROR_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_list_layout);
        setTitle(R.string.recipe);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList(null);
    }

    @Override
    protected void onPause() {
        if(mCursor != null){
            mCursor.close();
        }
        super.onPause();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //TODO
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        menu.add(0, CONTEXT_MENU_EDIT, 0, R.string.context_menu_edit);
        menu.add(0, CONTEXT_MENU_DELETE, 1, R.string.context_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case CONTEXT_MENU_EDIT:
            Intent intent = new Intent(this, PatientInfoEditActivity.class);
            intent.putExtra(BaseActivity.EXTRA_INT_VALUE_PATIENT_ID, (int)info.id);
            startActivity(intent);
            return true;
        case CONTEXT_MENU_DELETE:
            mDeletePatientId = info.id;
            showDialog(DIALOG_DELETE_CONFIRM);
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
            Intent intent = new Intent(this, PatientInfoEditActivity.class);
            startActivity(intent);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        switch (id) {
        case DIALOG_DELETE_CONFIRM:
            return createDeleteAlterDialog();
        default:
            return super.onCreateDialog(id);
        }
    }

    private Dialog createDeleteAlterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete_patient_title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(R.string.dialog_delete_patient_message);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(mDeletePatientId != ERROR_VALUE){
                    showDialog(DIALOG_WAITING);
                    Uri uri = Uri.withAppendedPath(PatientColumns.CONTENT_URI, String.valueOf(mDeletePatientId));
                    mAsyncQuery.startDelete(0, null, uri, null, null);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private void updateList(String selection) {
        showDialog(DIALOG_WAITING);
        mAsyncQuery.startQuery(
                0,
                null,
                PatientColumns.CONTENT_URI,
                PATIENT_PROJECTION,
                selection,
                null,
                PatientColumns.DEFAULT_ORDER);
    }

    private void initialize() {
        mAsyncQuery = new PatientAsyncQueryHandler(getContentResolver());
        mAdapter = new PatientListAdapter(this, null);
        getListView().setAdapter(mAdapter);
        getListView().setOnCreateContextMenuListener(this);
    }

    private class PatientAsyncQueryHandler extends AsyncQueryHandler{

        public PatientAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            mCursor = cursor;
            mAdapter.changeCursor(mCursor);
            removeDialog(DIALOG_WAITING);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            Toast.makeText(PatientListActivity.this, R.string.toast_delete_success, Toast.LENGTH_LONG).show();
            updateList(null);
        }
    }
}
