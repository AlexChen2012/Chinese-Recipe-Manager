package com.alex.recipemanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private TimeUtil(){
        //Forbid create object.
    }

    public static String translateTimeMillisToDate(long timeMillis) {
        Date date = new Date(timeMillis);
        SimpleDateFormat dateFormat=new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}