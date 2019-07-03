package com.android.moviesnow;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.android.moviesnow.database.favoritesDatabase;

public class AddFavoriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final favoritesDatabase mFavoritesDb;
    private final int mFavoritesId;

    public AddFavoriteViewModelFactory(favoritesDatabase favoritesDb, int favoriteId) {
        mFavoritesDb = favoritesDb;
        mFavoritesId = favoriteId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddFavoritesViewModel(mFavoritesDb, mFavoritesId);
    }
}
