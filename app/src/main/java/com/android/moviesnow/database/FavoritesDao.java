package com.android.moviesnow.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoritesDao {
    @Query("SELECT * FROM favoritedtable WHERE favorited_movie_id = :unique_movie_id")

    //will need to wrap with live data later
    LiveData<FavoritesEntry> findFavoriteById(int unique_movie_id);

    @Query("SELECT * FROM favoritedtable WHERE favorited_movie_id = :unique_id")
    FavoritesEntry queryFavoriteId(int unique_id);

    @Query("SELECT * FROM favoritedtable ORDER BY favorited_movie_id")
    LiveData<List<FavoritesEntry>> loadAllTasks();

    @Insert
    void insertFavorite(FavoritesEntry favoritesEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavorite(FavoritesEntry favoritesEntry);

    @Delete
    void deleteFavorite(FavoritesEntry favoritesEntry);
}
