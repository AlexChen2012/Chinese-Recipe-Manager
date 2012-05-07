package com.alex.recipemanager.ui.base;

import com.alex.recipemanager.R;
import com.alex.recipemanager.ui.medicine.MedicineListActivity;
import com.alex.recipemanager.ui.patient.PatientListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
        Toast.makeText(this, "This function dose not implement in this version", Toast.LENGTH_LONG).show();
    }

    public void onMedicineManagerClick(View v){
        Intent intent = new Intent(this, MedicineListActivity.class);
        startActivity(intent);
    }
}