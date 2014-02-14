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
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanTables();
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void cleanTables() {
        Log.d(TAG, "clean tables");
        mResolver.delete(RecipeContent.RecipeMedicineColumn.CONTENT_URI, null, null);
        mResolver.delete(RecipeContent.RecipeColumn.CONTENT_URI, null, null);
        mResolver.delete(MedicineNameColumn.CONTENT_URI, null, null);
        mResolver.delete(MedicineColumn.CONTENT_URI, null, null);
    }

    public void testInsertMedicine() {
        String id = insertMedicine(DEFAULT_MEDICINE_MOUNT, 100, 200);
        assertEquals(true, isRowExist(MedicineColumn.CONTENT_URI, id));
    }

    public void testInsertMedicineName() {
        String id = insertMedicine(DEFAULT_MEDICINE_MOUNT, 100, 200);
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
                assertEquals(c.getString(c.getColumnIndex(MedicineNameColumn.MEDICINE_KEY)), id);
                assertEquals(c.getString(c.getColumnIndex(MedicineNameColumn.MEDICINE_NAME)), DEFAULT_MEDICINE_NAME1);
            } finally {
                c.close();
            }
        }
    }

    public void testMedicineNameDeleteTrigger() {
        String id = insertMedicine(DEFAULT_MEDICINE_MOUNT, 100, 200);
        mValues.clear();
        mValues.put(MedicineNameColumn.MEDICINE_KEY, id);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, DEFAULT_MEDICINE_NAME1);
        String nameId = insertMedicineName(mValues);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, DEFAULT_MEDICINE_NAME2);
        String aliasId = insertMedicineName(mValues);
        deleteRow(MedicineNameColumn.CONTENT_URI, nameId);
        assertEquals(true, isRowExist(MedicineColumn.CONTENT_URI, id));
        deleteRow(MedicineNameColumn.CONTENT_URI, aliasId);
        assertEquals(false, isRowExist(MedicineColumn.CONTENT_URI, id));
    }

    public void testMedicineNameDeleteTrigger2() {
        String id = insertMedicine(DEFAULT_MEDICINE_MOUNT, 100, 200);
        mValues.clear();
        mValues.put(MedicineNameColumn.MEDICINE_KEY, id);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, DEFAULT_MEDICINE_NAME1);
        String nameId = insertMedicineName(mValues);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, DEFAULT_MEDICINE_NAME2);
        String aliasId = insertMedicineName(mValues);

        assertEquals(true, isRowExist(MedicineNameColumn.CONTENT_URI, nameId));
        assertEquals(true, isRowExist(MedicineNameColumn.CONTENT_URI, aliasId));
        assertEquals(true, isRowExist(MedicineColumn.CONTENT_URI, id));

        deleteRow(MedicineColumn.CONTENT_URI, id);

        assertEquals(false, isRowExist(MedicineColumn.CONTENT_URI, id));
        assertEquals(false, isRowExist(MedicineNameColumn.CONTENT_URI, nameId));
        assertEquals(false, isRowExist(MedicineNameColumn.CONTENT_URI, aliasId));
    }

    public void testRecipeFunction() {
        String recipeId = buildRecipe(2, "2", "3");

        //create medicines
        String medicineId1 = insertMedicine(40, 100, 101);
        String medicineId2 = insertMedicine(50, 120, 121);
        String medicineId3 = insertMedicine(60, 130, 131);

        //create medicine names
        mValues.clear();
        mValues.put(MedicineNameColumn.MEDICINE_KEY, medicineId1);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, "gui_hua_1");
        String nameId1 = insertMedicineName(mValues);mValues.clear();

        mValues.put(MedicineNameColumn.MEDICINE_KEY, medicineId2);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, "gui_hua_2");
        String nameId2 = insertMedicineName(mValues);mValues.clear();

        mValues.put(MedicineNameColumn.MEDICINE_KEY, medicineId3);
        mValues.put(MedicineNameColumn.MEDICINE_NAME, "gui_hua_3");
        String nameId3 = insertMedicineName(mValues);

        Uri uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, medicineId1);
        Cursor c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                assertEquals(c.getCount(), 1);
                c.moveToFirst();
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.GROSS_WEIGHT)), 100);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.THRESHOLD)), 101);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.AMOUNT)), 40);
            } finally {
                c.close();
            }
        }

        //add recipeMedicineId1 in recipe.
        String recipeMedicineId1 = buildRecipeMedicine(nameId1, recipeId, 1, 40);

        uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, medicineId1);
        c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                assertEquals(c.getCount(), 1);
                c.moveToFirst();
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.GROSS_WEIGHT)), 20);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.THRESHOLD)), 101);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.AMOUNT)), 40);
            } finally {
                c.close();
            }
        }

        uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, medicineId3);
        c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                assertEquals(c.getCount(), 1);
                c.moveToFirst();
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.GROSS_WEIGHT)), 130);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.THRESHOLD)), 131);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.AMOUNT)), 60);
            } finally {
                c.close();
            }
        }

        //add recipeMedicineId2 in recipe.
        String recipeMedicineId2 = buildRecipeMedicine(nameId2, recipeId, 2, 51);

        uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, medicineId1);
        c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                assertEquals(c.getCount(), 1);
                c.moveToFirst();
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.GROSS_WEIGHT)), 20);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.THRESHOLD)), 101);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.AMOUNT)), 40);
            } finally {
                c.close();
            }
        }

        uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, medicineId2);
        c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                assertEquals(c.getCount(), 1);
                c.moveToFirst();
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.GROSS_WEIGHT)), 18);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.THRESHOLD)), 121);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.AMOUNT)), 50);
            } finally {
                c.close();
            }
        }

        uri = Uri.withAppendedPath(MedicineColumn.CONTENT_URI, medicineId3);
        c = mResolver.query(uri, null, null, null, null);
        if(c != null) {
            try {
                assertEquals(c.getCount(), 1);
                c.moveToFirst();
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.GROSS_WEIGHT)), 130);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.THRESHOLD)), 131);
                assertEquals(c.getInt(c.getColumnIndex(MedicineColumn.AMOUNT)), 60);
            } finally {
                c.close();
            }
        }

        //delete a exist medicine
        c = mResolver.query(RecipeContent.RecipeMedicineColumn.CONTENT_URI, null, RecipeContent.RecipeMedicineColumn.RECIPE_KEY + "=" + recipeId, null, null);
        try {
            assertEquals(2, c.getCount());
        } finally {
            c.close();
        }

        deleteRow(MedicineNameColumn.CONTENT_URI, nameId1);

        c = mResolver.query(RecipeContent.RecipeMedicineColumn.CONTENT_URI, null, RecipeContent.RecipeMedicineColumn.RECIPE_KEY + "=" + recipeId, null, null);
        try {
            assertEquals(1, c.getCount());
        } finally {
            c.close();
        }

        deleteRow(MedicineColumn.CONTENT_URI, medicineId2);
        c = mResolver.query(RecipeContent.RecipeMedicineColumn.CONTENT_URI, null, RecipeContent.RecipeMedicineColumn.RECIPE_KEY + "=" + recipeId, null, null);
        try {
            assertEquals(0, c.getCount());
        } finally {
            c.close();
        }

        String recipeMedicineId3 = buildRecipeMedicine(nameId3, recipeId, 3, 60);
    }

    private Object isRowExist(Uri uri, String id) {
        uri = Uri.withAppendedPath(uri, id);
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

    private void deleteRow(Uri uri, String id) {
        uri = Uri.withAppendedPath(uri, id);
        mResolver.delete(uri, null, null);
    }

    private String insertMedicineName(ContentValues values) {
        Log.d(TAG, "insert medicine name mValues = " + values.toString());
        Uri uri = mResolver.insert(MedicineNameColumn.CONTENT_URI, values);
        Log.d(TAG, "inserted uri = " + uri);
        return uri.getLastPathSegment();
    }

    private String insertMedicine(int amount, int weight, int threshold) {
        mValues.clear();
        mValues.put(MedicineColumn.AMOUNT, amount);
        mValues.put(MedicineColumn.GROSS_WEIGHT, weight);
        mValues.put(MedicineColumn.THRESHOLD, threshold);
        Uri uri = mResolver.insert(MedicineColumn.CONTENT_URI, mValues);
        return uri.getLastPathSegment();
    }

    private String buildRecipe(int count, String fee, String resFee) {
        mValues.clear();
        mValues.put(RecipeContent.RecipeColumn.COUNT, count);
        mValues.put(RecipeContent.RecipeColumn.NAME, "test_recipe_1");
        mValues.put(RecipeContent.RecipeColumn.NAME_ABBR, "t");
        mValues.put(RecipeContent.RecipeColumn.OTHER_FEE, fee);
        mValues.put(RecipeContent.RecipeColumn.REGISTER_FEE, resFee);
        mValues.put(RecipeContent.RecipeColumn.RECIPE_TYPE , RecipeContent.RecipeColumn.RECIPE_TYPE_CHARGE);
        Uri uri = mResolver.insert(RecipeContent.RecipeColumn.CONTENT_URI, mValues);
        return uri.getLastPathSegment();
    }

    private String buildRecipeMedicine(String medicineNameId, String recipeId, int index, int weight) {
        mValues.clear();
        mValues.put(RecipeContent.RecipeMedicineColumn.INDEX, index);
        mValues.put(RecipeContent.RecipeMedicineColumn.RECIPE_KEY, recipeId);
        mValues.put(RecipeContent.RecipeMedicineColumn.MEDICINE_NAME_KEY, medicineNameId);
        mValues.put(RecipeContent.RecipeMedicineColumn.WEIGHT, weight);
        Uri uri = mResolver.insert(RecipeContent.RecipeMedicineColumn.CONTENT_URI, mValues);
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
        Log.d(TAG, "dump end");
    }
}
