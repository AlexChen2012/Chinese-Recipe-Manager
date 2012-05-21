package com.alex.recipemanager.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class RecipeContent {

    private RecipeContent(){}

    public static final String AUTHORITY = "ChineseMedicineClient";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public interface PatientColumns extends BaseColumns {

        public static final String TABLE_NAME = "Patient";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/patient");
        public static final String DEFAULT_ORDER = PatientColumns.TIMESTAMP + " desc";

        // patient name.
        public static final String NAME = "patient_name";
        // patient name abbr.
        public static final String NAME_ABBR = "patient_name_abbr";
        // patient gender
        public static final String GENDER     = "patient_gender";
        public static final int GENDER_MALE   = 0;
        public static final int GENDER_FEMALE = 1;
        // patient address
        public static final String ADDRESS = "address";
        // patient age
        public static final String AGE = "age";
        // patient nation
        public static final String NATION = "nation";
        // patient history
        public static final String HISTORY = "history";
        // patient telephone
        public static final String TELEPHONE = "telephone";
        // first see doctor time
        public static final String FIRST_TIME ="first_time";
        // last modify time
        public static final String TIMESTAMP = "last_modify_time";
    }

    public interface CaseHistoryColumn extends BaseColumns{
        public static final String TABLE_NAME = "Case_History";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/case_history");
        public static final String DEFAULT_ORDER = CaseHistoryColumn.TIMESTAMP + " asc";

        // foreign key to corresponding patient with this case_history.
        public static final String PATIENT_KEY = "history_patient_key";
        // illness description.
        public static final String DESCRIPTION = "description";
        // category of the illness; 
        public static final String SYMPTOM = "symptom";
        // abbr of symptom
        public static final String SYMPTOM_ABBR = "symptom_abbr";
        // record the time.
        public static final String TIMESTAMP = "case_history_time";
        // first time record.
        public static final String FIRST_TIME ="first_time";
    }

    public interface MedicineColumn extends BaseColumns {
        public static final String TABLE_NAME = "Medicine";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/medicine");

        //amount of the medicine
        public static final String AMOUNT = "amount";
    }

    public interface MedicineNameColumn extends BaseColumns {
        public static final String TABLE_NAME = "Medicine_Name";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/medicine_name");
        //TODO: May be we need this URI.
        public static final Uri FETCH_MEDICINE_AND_NAME_URI = Uri.parse(RecipeContent.CONTENT_URI
                + "/medicine_alias/medicine");
        public static final String DEFAULT_ORDER = MedicineNameColumn.MEDICINE_NAME + " asc";

        //foreign key to corresponding Medicine id with MedicineAliasColumn.
        public static final String MEDICINE_KEY = "medicineAlias_key";
        //name of medicine
        public static final String MEDICINE_NAME = "medicine_name";
        //PinYin abbreviation.
        public static final String MEDICINE_NAME_ABBR = "pinyin_abbreviation";
    }

    public interface RecipeColumn extends BaseColumns {
        public static final String TABLE_NAME = "Recipe";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/recipe");

        // foreign key to corresponding patient with recipe.
        public static final String PATIENT_KEY = "recipe_patient_key";
        // foreign key to corresponding case_history with this recipe.
        public static final String CASE_HISTORY_KEY = "recipe_case_history_key";
        // recipe name
        public static final String NAME = "recipe_name";
        // recipe name abbr
        public static final String NAME_ABBR = "recipe_name_abbr";
        // recipe time
        public static final String TIMESTAMP = "recipe_time";
        // recipe count
        public static final String NUMBER = "recipe_count";
    }

    public interface RecipeMedicineColumn extends BaseColumns {
        public static final String TABLE_NAME = "Recipe_Medicine";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/recipe_medicine");

        // foreign key to corresponding RecipeMedicine with Recipe.
        public static final String RECIPE_KEY = "recipe_medicine_recipe_key";
        // foreign key to corresponding Case RecipeMedicine with medicine
        public static final String MEDICINE_KEY = "recipe_medicine_medicine_key";
        // medicine count unit g
        public static final String WEIGHT = "weight";
    }

    public interface NationColumn extends BaseColumns {
        public static final String TABLE_NAME = "NATION";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/nation");

        public static final String DEFAULT_ORDER = NationColumn._ID + " asc";
        public static final String NATION_NAME = "nation_name";
    }

}
