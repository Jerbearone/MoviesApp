package com.android.moviesnow.database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "FavoritedTable")
public class FavoritesEntry {
    private boolean favorited_bool;
    @PrimaryKey
    private int favorited_movie_id;

    public FavoritesEntry (boolean favorited_bool, int favorited_movie_id) {
        this.favorited_bool = favorited_bool;
        this.favorited_movie_id = favorited_movie_id;
    }

    //getters and setters..

    public boolean getFavorited_bool() {
        return favorited_bool;
    }

    public void setFavorited_bool(boolean favorited_bool) {
        this.favorited_bool = favorited_bool;
    }

    public int getFavorited_movie_id() {
        return favorited_movie_id;
    }

    public void setFavorited_movie_id(int favorited_movie_id) {
        this.favorited_movie_id = favorited_movie_id;
    }
}
