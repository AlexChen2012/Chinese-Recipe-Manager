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
        //gross weight of the medicine
        public static final String GROSS_WEIGHT = "gross_weight";
        //threshold of the medicine
        public static final String THRESHOLD = "threshold";

        public static final String ORDER_BY_WEIGHT_MINUS_THRESHOLD = GROSS_WEIGHT + "-" + THRESHOLD + " asc";
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
        public static final String COUNT = "recipe_count";

        // Doesn't use this column, all gross of stored recipe medicines are minus its
        // corresponding weight from MedicineColumn
        // medicines in recipe is put into storage.
        public static final String IS_STORAGE = "is_storage";
        // other fee
        public static final String OTHER_FEE = "other_fee";
        // recipe register fee
        public static final String REGISTER_FEE = "register_fee";

        // Doesn't use this column, each recipe price is dramatically calculated when recipe shows
        // to user.
        // gross cost
        public static final String GROSS_COST = "gross_price";

        public static final int RECIPE_NOT_STORAGE = 0;
        public static final int RECIPE_STORAGE = 1;

        // recipe type case_history/charge.
        public static final String RECIPE_TYPE = "recipe_type";

        public static final int RECIPE_TYPE_CASE_HISTORY = 1;
        public static final int RECIPE_TYPE_CHARGE = 2;

        public static final String DEFAULT_ORDER = TIMESTAMP + " desc";
    }

    public interface RecipeMedicineColumn extends BaseColumns {
        public static final String TABLE_NAME = "Recipe_Medicine";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/recipe_medicine");

        // foreign key to corresponding RecipeMedicine with Recipe.
        public static final String RECIPE_KEY = "recipe_medicine_recipe_key";
        // foreign key to corresponding Case RecipeMedicine with medicine name id
        public static final String MEDICINE_NAME_KEY = "recipe_medicine_name_key";
        // medicine count unit g
        public static final String WEIGHT = "weight";
        // recording the index of this medicine appears in corresponding recipe
        public static final String INDEX = "recipe_index";

        public static final String DEFAULT_ORDER = INDEX + " asc";
    }

    public interface NationColumn extends BaseColumns {
        public static final String TABLE_NAME = "NATION";
        public static final Uri CONTENT_URI = Uri.parse(RecipeContent.CONTENT_URI + "/nation");

        public static final String DEFAULT_ORDER = NationColumn._ID + " asc";
        public static final String NATION_NAME = "nation_name";
    }

}
