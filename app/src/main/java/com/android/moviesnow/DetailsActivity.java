package com.android.moviesnow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.moviesnow.database.FavoritesEntry;
import com.android.moviesnow.database.favoritesDatabase;
import com.android.moviesnow.databinding.ActivityDetailsBinding;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Movie;

public class DetailsActivity extends AppCompatActivity {

    private final static String TAG = DetailsActivity.class.getSimpleName();

    //setup for ReviewsRecyclerView
    ReviewsRecyclerViewAdapter mainAdapter;
    ArrayList<String> authors = new ArrayList<>();
    ArrayList<String> reviews = new ArrayList<>();

    //using dataBinding.
    ActivityDetailsBinding mBinding;
    private favoritesDatabase mFavoritesDatabase;
    JSONObject mTrailerJson;

    //will be used so task id can be receieved though an intent
    public final static String EXTRA_TASK_ID = "extraTaskId";
    //will be used so task id will be saved after rotation
    public final static String EXTRA_INSTANCE_ID = "extraInstanceId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Intent intent = getIntent();
        //This will get the entire Movie object for the movie selected on Screen.
        Movie launchedMovie = intent.getParcelableExtra("info");
        // this will build the trailer url.
        URL trailer = NetworkUtilities.buildTrailerUri(launchedMovie.getId());
        new TrailerDbQuery().execute(trailer);


        displayDetailsView(launchedMovie);
        //these next two lines will setup the reviews..
        URL reviewsURL = NetworkUtilities.buildReviewsUri(launchedMovie.getId());
        new reviewsQueryTask().execute(reviewsURL);


        mFavoritesDatabase = favoritesDatabase.getInstance(getApplicationContext());

        final FavoritesEntry favorite_movie = new FavoritesEntry(true, Integer.parseInt(launchedMovie.getId()));
        setupViewModel(favorite_movie);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final FavoritesEntry favoritesEntry = new FavoritesEntry(true, favorite_movie.getFavorited_movie_id());
                int id_being_checked = favoritesEntry.getFavorited_movie_id();
                Log.w(TAG, "ID IS......." + id_being_checked);
                FavoritesEntry dataCheck = mFavoritesDatabase.favoritesDao().queryFavoriteId(id_being_checked);
                if (dataCheck != null) {
                    mBinding.favoritesButton.favoritesButton.setBackgroundResource(R.drawable.favorite_button);
                }
            }
        });

    }

    private void displayDetailsView(Movie current_displayed_movie) {
        mBinding.movieTitle.setText(current_displayed_movie.getTitle());
        mBinding.rating.setText(current_displayed_movie.getVote_average());
        mBinding.plotSummary.setText(current_displayed_movie.getOverview());
        mBinding.releaseDate.setText((current_displayed_movie.getRelease_date()));

        final int movies_id = Integer.parseInt(current_displayed_movie.getId());
        mBinding.favoritesButton.favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movie_id_string = Integer.toString(movies_id);
                Log.w("Movies_id", movie_id_string);
                onFavoritesButtonClicked(movies_id);
                //this checks for when the favorite button is pressed, and it changes the drawable
                //imediately.
                if (mBinding.favoritesButton.favoritesButton.getBackground().getConstantState().equals(
                        getResources().getDrawable(R.drawable.favorite_button).getConstantState())) {
                    mBinding.favoritesButton.favoritesButton.setBackgroundResource(R.drawable.non_favorite);

                } else {
                    mBinding.favoritesButton.favoritesButton.setBackgroundResource(R.drawable.favorite_button);
                }
            }
        });

        //here we will set the image with glide..

        Glide.with(this)
                .asBitmap()
                .load(NetworkUtilities.buildImageUrl(current_displayed_movie.getPoster_path()))
                .into((ImageView) findViewById(R.id.poster_image));


    }

    private void setupViewModel(FavoritesEntry launched_movie) {

        AddFavoriteViewModelFactory factory = new AddFavoriteViewModelFactory(mFavoritesDatabase, launched_movie.getFavorited_movie_id());
        final AddFavoritesViewModel favoritesViewModel = ViewModelProviders.of(this, factory).get(AddFavoritesViewModel.class);
        favoritesViewModel.getFavorite().observe(this, new Observer<FavoritesEntry>() {
            @Override
            public void onChanged(FavoritesEntry favoritesEntry) {
                favoritesViewModel.getFavorite().removeObserver(this);
                //will need to implement this for favorites button being observed on change.


            }
        });
    }

    public void onFavoritesButtonClicked(final int movies_id) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final FavoritesEntry favoritesEntry = new FavoritesEntry(true, movies_id);
                int id_being_checked = favoritesEntry.getFavorited_movie_id();
                String theid = String.valueOf(favoritesEntry.getFavorited_movie_id());
                Log.w(TAG, "THIS IS THE VALUE OF GETFAVORITEMOVIEID " + theid);

                FavoritesEntry dataCheck = mFavoritesDatabase.favoritesDao().queryFavoriteId(id_being_checked);


                if (dataCheck == null) {
                    mFavoritesDatabase.favoritesDao().insertFavorite(favoritesEntry);

                    // will delete toast after testing
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailsActivity.this, "favorite added", Toast.LENGTH_SHORT).show();

                        }
                    });

                } else {
                    Log.w("FAVORITE", mFavoritesDatabase.favoritesDao().findFavoriteById(favoritesEntry.getFavorited_movie_id()).toString());
                    mFavoritesDatabase.favoritesDao().deleteFavorite(favoritesEntry);
                    //will need to delete this toast runnable as well...
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailsActivity.this, "favorite Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    public class TrailerDbQuery extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String trailerJsonString = null;
            try {
                trailerJsonString = NetworkUtilities.getResponseFromUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return trailerJsonString;
        }

        @Override
        protected void onPostExecute(String trailerJsonString) {
            if (trailerJsonString != null && !trailerJsonString.equals("")) {
                try {
                    mTrailerJson = new JSONObject(trailerJsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    List<String> id_list = JsonParsingUtil.parseTrailerJson(mTrailerJson);
                    final String first_trailer_id = id_list.get(0);
                    Log.d(TAG, "onPostExecute: id = " + first_trailer_id);
                    //sets the trailer button to visible if there is one available..
                    mBinding.TrailerButton.setVisibility(View.VISIBLE);
                    mBinding.TrailerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent launchTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtilities.YOUTUBE_BASE_URL + first_trailer_id));
                            startActivity(launchTrailer);
                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(DetailsActivity.this, "No trailer for this movie :(", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }
    }

    public class reviewsQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchurl = urls[0];
            String reviewsJsonString = null;
            try {
                reviewsJsonString = NetworkUtilities.getResponseFromUrl(searchurl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviewsJsonString;
        }
        @Override
        protected void onPostExecute(String reviewsJsonString) {
            if (reviewsJsonString != null && !reviewsJsonString.equals("")) {
                try {
                    mTrailerJson = new JSONObject(reviewsJsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    authors = JsonParsingUtil.parseAuthorJson(mTrailerJson);
                    reviews = JsonParsingUtil.parseReviewsJson(mTrailerJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
            initializeReviewsRecyclerView();

            }


        }

    private void initializeReviewsRecyclerView() {
        Log.d(TAG, "initializeRecyclerView: initializing recyclerView");
        ReviewsRecyclerViewAdapter adapter = new ReviewsRecyclerViewAdapter(authors, reviews, this);
        this.mainAdapter = adapter;
        mBinding.reviewsRecyclerView.setAdapter(adapter);
        //here we are using the grid layout for recyclerView.
        mBinding.reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
