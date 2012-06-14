package com.alex.recipemanager.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alex.recipemanager.R;
import com.alex.recipemanager.ui.medicine.MedicineListActivity;
import com.alex.recipemanager.ui.patient.PatientListActivity;
import com.alex.recipemanager.ui.recipe.RecipesListActivity;

public class RecipeManager extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onRecipeClick(View v){
        Intent intent = new Intent(this, PatientListActivity.class);
        startActivity(intent);
    }

    public void onPricingClick(View v){
        Intent intent = new Intent(this, RecipesListActivity.class);
        intent.putExtra(BaseActivity.EXTRA_INT_VALUE_RECIPE_MODE, RecipesListActivity.MODE_CHARE);
        startActivity(intent);
    }

    public void onMedicineManagerClick(View v){
        Intent intent = new Intent(this, MedicineListActivity.class);
        startActivity(intent);
    }
}