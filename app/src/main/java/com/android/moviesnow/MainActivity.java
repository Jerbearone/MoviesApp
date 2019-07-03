package com.android.moviesnow;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.moviesnow.database.FavoritesEntry;
import com.android.moviesnow.database.favoritesDatabase;
import com.android.moviesnow.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Movie;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private favoritesDatabase mFavoritesDb;
    private List<FavoritesEntry> favoritesEntriesList;
    private List<URL> favoriteUrlsList = new ArrayList<>();

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<Movie> mMovieInfo = new ArrayList<>();
    RecyclerViewAdapter mainAdapter;
    JSONObject mJsonToParse;
    URL url = NetworkUtilities.buildUrl(NetworkUtilities.MOSTPOPULAR);

    private int onChangeSwitch = 0;
    JSONArray movieList;
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setLifecycleOwner(this);
        Log.d(TAG, "onCreate: Main onCreate started.");
        //creating Spinner
        initializeRecyclerView();
        ArrayAdapter<CharSequence> SpinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.sorting_selector, android.R.layout.simple_spinner_item);
        mainBinding.spinnerMain.setOnItemSelectedListener(this);
        mainBinding.spinnerMain.setAdapter(SpinnerAdapter);

        //here we are fetching the data from the url to get our JSON object.
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFavoritesDb = favoritesDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    //Override for spinner to use functionality.

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String spinnerChoice = parent.getItemAtPosition(position).toString();
        Log.d(TAG, "onItemSelected: " + spinnerChoice);
        if (position == 0) {
            mImageUrls.clear();
            mNames.clear();
            mMovieInfo.clear();
            url = NetworkUtilities.buildUrl(NetworkUtilities.MOSTPOPULAR);
            //retrive Json Data and start recyclerView afterwards.
            new MoviesDbQueryTask().execute(url);
        } else if (position == 1) {
            mImageUrls.clear();
            mNames.clear();
            mMovieInfo.clear();
            url = NetworkUtilities.buildUrl(NetworkUtilities.VOTEAVERAGE);
            //retrive Json Data and start recyclerView afterwards.
            new MoviesDbQueryTask().execute(url);
        } else if (position == 2) {
            mImageUrls.clear();
            mNames.clear();
            mMovieInfo.clear();
            //retrive Json Data and start recyclerView afterwards.
            favoriteUrlsList.clear();
            new FavoritesQueryTask().execute(favoriteUrlsList);
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class MoviesDbQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String moviesDbJson = null;
            try {
                moviesDbJson = NetworkUtilities.getResponseFromUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesDbJson;
        }

        // Override onPostExecute to prepare mJsonToParse for parsing the JSON information.
        @Override
        protected void onPostExecute(String moviesDbJson) {
            if (moviesDbJson != null && !moviesDbJson.equals("")) {
                String mJsonStringToParse = moviesDbJson;
                try {
                    mJsonToParse = new JSONObject(mJsonStringToParse);
                    Log.d("JSON FROM ONPOSTEXECUTE", mJsonToParse.getString("page"));

                    //Parsing Json here.

                    try {
                        Log.d("someTag", "Right before Json");
                        movieList = mJsonToParse.getJSONArray("results");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //itterating through each movie to add info to mNames and mImageUrls

                    for (int x = 0; x < movieList.length(); x++) {
                        try {

                            Movie parsedMovie = (JsonParsingUtil.parseMovieJson(movieList.getJSONObject(x)));
                            mImageUrls.add(NetworkUtilities.buildImageUrl(parsedMovie.getPoster_path()).toString());
                            mNames.add(parsedMovie.getTitle());
                            mMovieInfo.add(parsedMovie);
                            Log.d("HERE IS A TITLE", parsedMovie.getTitle());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //here we are initializing images and then recyclerView.
                    resetRecyclerViewAdapter();
                    //and it ends here..


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onPostExecute called json is " + moviesDbJson);
            }
        }
    }


    public class FavoritesQueryTask extends AsyncTask<List<URL>, Void, List<String>> {

        @Override
        protected List<String> doInBackground(List<URL>... params) {
            List<URL> UrlsWithIds = params[0];
            List<String> moviesDbJson = new ArrayList<>();

            List<FavoritesEntry> favorite_ids = favoritesEntriesList;
            for (int favoritesEntryIndex = 0; favoritesEntryIndex < favorite_ids.size(); favoritesEntryIndex++) {
                int someEntryId = favorite_ids.get(favoritesEntryIndex).getFavorited_movie_id();
                Log.w(TAG, "doInBackground: " + someEntryId);
                favoriteUrlsList.add(NetworkUtilities.buildFindByIdUrl(someEntryId));
                try {

                    // will need to loop this to get responses of all the movies built Urls..
                    moviesDbJson.add(NetworkUtilities.getResponseFromUrl(favoriteUrlsList.get(favoritesEntryIndex)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return moviesDbJson;
        }

        // Override onPostExecute to prepare mJsonToParse for parsing the JSON information.
        @Override
        protected void onPostExecute(List<String> moviesDbJson) {
            try {
                if (moviesDbJson != null && !moviesDbJson.get(0).equals("")) {
                    List<String> mJsonListToParse = moviesDbJson;
                    try {
                        for (int counter = 0; counter < mJsonListToParse.size(); counter++) {
                            mJsonToParse = new JSONObject(mJsonListToParse.get(counter));

                            //Parsing Json here.

                            Log.d("someTag", "Right before Json");
                            //movieList = mJsonToParse.getJSONArray("results");
                            JSONObject singleMovie = mJsonToParse;

                            //itterating through each movie to add info to mNames and mImageUrls

                            //will need to loop here..
                            Movie parsedMovie = (JsonParsingUtil.parseIdMovieJson(singleMovie));
                            mImageUrls.add(NetworkUtilities.buildImageUrl(parsedMovie.getPoster_path()).toString());
                            mNames.add(parsedMovie.getTitle());
                            mMovieInfo.add(parsedMovie);
                            Log.d(TAG, "Here is a title! " + parsedMovie.getTitle());
                        }

                        // will need to end loop here...

                        //here we are initializing images and then recyclerView.
                        resetRecyclerViewAdapter();
                        //and it ends here..

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "onPostExecute called json is " + moviesDbJson);
                }
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(MainActivity.this, "No Favorites Selected Yet!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onPostExecute: no favorite saved yet..");
                e.printStackTrace();
            }
            onChangeSwitch = 1;
        }
    }

    private void initializeRecyclerView() {
        Log.d(TAG, "initializeRecyclerView: initializing recyclerView");
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mImageUrls, mNames, mMovieInfo, this);
        this.mainAdapter = adapter;
        mainBinding.movieRecyclerView.setAdapter(adapter);
        //here we are using the grid layout for recyclerView.
        mainBinding.movieRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    public void resetRecyclerViewAdapter() {
        mainAdapter.notifyDataSetChanged();
    }


    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<FavoritesEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<FavoritesEntry> favoritesEntries) {
                        Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                        //will need to update...
                        favoritesEntriesList = favoritesEntries;
                        if (onChangeSwitch == 1 && mainBinding.spinnerMain.getSelectedItemPosition() == 2) {
                            mImageUrls.clear();
                            mNames.clear();
                            mMovieInfo.clear();
                            //retrive Json Data and start recyclerView afterwards.
                            favoriteUrlsList.clear();
                            new FavoritesQueryTask().execute(favoriteUrlsList);


                        }

                    }
                }
        );
    }


    // still need to implement this down the road.......
    public static class QueryTrailerLoaderTask extends AsyncTaskLoader<String> {

        private String movieDbJson;

        //this will be the url for the movie
        private URL movieSearchUrl;

        public QueryTrailerLoaderTask(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public String loadInBackground() {
            String moviesDbJson = null;
            try {
                moviesDbJson = NetworkUtilities.getResponseFromUrl(movieSearchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesDbJson;
        }

        @Override
        //the data is from the return value of loadInBackground.
        public void deliverResult(@Nullable String data) {
            super.deliverResult(data);
        }

    }

}
