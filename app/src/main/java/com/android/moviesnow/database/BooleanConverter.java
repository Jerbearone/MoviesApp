package com.android.moviesnow.database;

import androidx.room.TypeConverter;

public class BooleanConverter {
    @TypeConverter
    public static boolean toInt(int theBool) {
        return theBool != 0;
    }

    @TypeConverter
    public static int toInt(boolean theInt) {
        if (theInt == true) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
