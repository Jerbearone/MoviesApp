package com.android.moviesnow.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {FavoritesEntry.class}, version = 1, exportSchema = false)
@TypeConverters(BooleanConverter.class)
public abstract class favoritesDatabase extends RoomDatabase {
    private final static String LOG_TAG = favoritesDatabase.class.getSimpleName();
    private final static Object LOCK = new Object();
    private final static String DATABASE_NAME = "theFavoritesDatabase";
    private static favoritesDatabase sInstance;

    public static favoritesDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new favorites Database");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        favoritesDatabase.class, favoritesDatabase.DATABASE_NAME)
                        .build();
            }

        }
        Log.d(LOG_TAG, "getting the database instance...");
        return sInstance;
    }

    public abstract FavoritesDao favoritesDao();
}
