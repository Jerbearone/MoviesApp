package com.android.moviesnow;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.moviesnow.database.FavoritesEntry;
import com.android.moviesnow.database.favoritesDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final static String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<FavoritesEntry>> favorites;

    public MainViewModel(Application application) {
        super(application);
        favoritesDatabase database = favoritesDatabase.getInstance(this.getApplication());
        Log.d(TAG, "MainViewModel: retrieving task from database");
        favorites = database.favoritesDao().loadAllTasks();


    }
    public LiveData<List<FavoritesEntry>> getFavorites() {
        return favorites;
    }
}
