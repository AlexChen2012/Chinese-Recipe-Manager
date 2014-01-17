package com.alex.recipemanager.test;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.ProviderTestCase2;

import android.util.Log;
import com.alex.recipemanager.provider.RecipeContent;
import com.alex.recipemanager.provider.RecipeContent.MedicineColumn;
import com.alex.recipemanager.provider.RecipeContent.MedicineNameColumn;
import com.alex.recipemanager.provider.RecipeProvider;

@SuppressWarnings("rawtypes")
public class RecipeProviderTest extends ProviderTestCase2 {

    static final String TAG = "RecipeProviderTest";

    private static final String DEFAULT_MEDICINE_NAME1 = "juhua";
    private static final String DEFAULT_MEDICINE_NAME2 = "guihua";
    private static final int DEFAULT_MEDICINE_MOUNT = 40;

    private ContentResolver mResolver;
    private ContentValues mValues = new ContentValues();

    @TargetApi(Build.VERSION_CODES.CUPCAKE) @SuppressWarnings("unchecked")
    public RecipeProviderTest() {
        super(RecipeProvider.class, RecipeContent.AUTHORITY);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mResolver = getMockContentResolver();
        cleanTables();
        dump();
    }

    private void cleanTables() {
        Log.d(TAG, "clean tables");
        mResolver.delete(MedicineColumn.CONTENT_URI, null, null);
        mResolver.delete(MedicineNameColumn.CONTENT_URI, null, null);
        mResolver.delete(RecipeContent.RecipeColumn.CONTENT_URI, null, null);
        mResolver.delete(RecipeContent.RecipeMedicineColumn.CONTENT_URI, null, null);
    }

    public void testInsertMedicine() {
        String id = insertMedicine(DEFAULT_MEDICINE_MOUNT);
        assertEquals(true, isMedicineExist(id));
        dump();
    }

    public void testInsertMedicineName() {
        String id = insertMedicine(DEFAULT_MEDICINE_MOUNT);
        mValues.clear();
        mValues.put(MedicineNameColumn.MEDICINE_KEY, id);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, DEFAULT_MEDICINE_NAME1);
        String nameId = insertMedicineName(mValues);
        Uri uri = Uri.withAppendedPath(MedicineNameColumn.CONTENT_URI, nameId);
        Cursor c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                assertEquals(c.getCount(), 1);
                c.moveToFirst();
                assertEquals(c.getString(c.getColumnIndex(MedicineNameColumn.MEDICINE_KEY)), nameId);
                assertEquals(c.getString(c.getColumnIndex(MedicineNameColumn.MEDICINE_NAME)), DEFAULT_MEDICINE_NAME1);
            } finally {
                c.close();
            }
        }
    }

    public void testMedicineNameDeleteTrigger() {
        String id = insertMedicine(DEFAULT_MEDICINE_MOUNT);
        mValues.clear();
        mValues.put(MedicineNameColumn.MEDICINE_KEY, id);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, DEFAULT_MEDICINE_NAME1);
        String nameId = insertMedicineName(mValues);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, DEFAULT_MEDICINE_NAME2);
        String aliasId = insertMedicineName(mValues);
        deleteMedicineName(nameId);
        assertEquals(true, isMedicineExist(id));
        deleteMedicineName(aliasId);
        assertEquals(false, isMedicineExist(id));
    }

    private Object isMedicineExist(String id) {
        Uri uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, id);
        Cursor c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                return c.getCount() == 1;
            } finally {
                c.close();
            }
        }
        return false;
    }

    private void deleteMedicineName(String id) {
        Uri uri = Uri.withAppendedPath(MedicineNameColumn.CONTENT_URI, id);
        mResolver.delete(uri, null, null);
    }

    private String insertMedicineName(ContentValues values) {
        Log.d(TAG, "insert medicine name mValues = " + values.toString());
        Uri uri = mResolver.insert(MedicineNameColumn.CONTENT_URI, values);
        Log.d(TAG, "inserted uri = " + uri);
        return uri.getLastPathSegment();
    }

    private String insertMedicine(int amount) {
        mValues.clear();
        mValues.put(MedicineColumn.AMOUNT, amount);
        Uri uri = mResolver.insert(MedicineColumn.CONTENT_URI, mValues);
        return uri.getLastPathSegment();
    }

    private void dump() {
        Log.d(TAG, "dumping MedicineName table");
        Cursor c = mResolver.query(MedicineNameColumn.CONTENT_URI, null, null, null, null);
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    Log.d(TAG, c.toString());
                }
            } finally {
                c.close();
            }
        }
    }
}
