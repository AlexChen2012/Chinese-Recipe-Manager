package com.alex.recipemanager.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alex.recipemanager.R;
import com.alex.recipemanager.provider.RecipeContent.RecipeColumn;
import com.alex.recipemanager.ui.medicine.MedicineListActivity;
import com.alex.recipemanager.ui.patient.PatientListActivity;
import com.alex.recipemanager.ui.recipe.RecipeInfoEditActivity;
import com.alex.recipemanager.ui.recipe.RecipesListActivity;

public class RecipeManager extends Activity {
    /** Called when the activity is first created. */

    private static final int MENU_SETTING   = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onRecipeClick(View v) {
        Intent intent = new Intent(this, PatientListActivity.class);
        startActivity(intent);
    }

    public void onPricingClick(View v) {
        Intent intent = new Intent(this, RecipeInfoEditActivity.class);
        intent.putExtra(RecipesListActivity.EXTRA_INT_VALUE_RECIPE_MODE, RecipeColumn.RECIPE_TYPE_CHARGE);
        startActivity(intent);
    }

    public void onRecipeHistoryClicked(View v) {
      Intent intent = new Intent(this, RecipesListActivity.class);
      intent.putExtra(BaseActivity.EXTRA_INT_VALUE_RECIPE_MODE, RecipeColumn.RECIPE_TYPE_CHARGE);
      startActivity(intent);
    }

    public void onMedicineManagerClick(View v) {
        Intent intent = new Intent(this, MedicineListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SETTING, 0, R.string.menu_setting).setIcon(
                android.R.drawable.ic_menu_set_as);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_SETTING:
            Intent intent = new Intent(this, RecipeManagerSettingActivity.class);
            startActivity(intent);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}