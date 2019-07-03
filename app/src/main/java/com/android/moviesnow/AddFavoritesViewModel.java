package com.android.moviesnow;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.moviesnow.database.FavoritesEntry;
import com.android.moviesnow.database.favoritesDatabase;

class AddFavoritesViewModel extends ViewModel {
    private LiveData<FavoritesEntry> favorite;

    private static final String TAG = AddFavoritesViewModel.class.getSimpleName();

    //here is the method  to find the favorite in the database.
    public AddFavoritesViewModel(favoritesDatabase mFavoritesDb, int mFavoritesId) {
        Log.d(TAG, "AddFavoritesViewModel: retreiving Tasks from favoritesDB");
        favorite = mFavoritesDb.favoritesDao().findFavoriteById(mFavoritesId);
    }


    public LiveData<FavoritesEntry> getFavorite() {

        return favorite;
    }
}
