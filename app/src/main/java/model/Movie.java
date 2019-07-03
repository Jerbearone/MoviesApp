package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Movie implements Parcelable {
    private String vote_count;
    private String id;
    private String vote_average;
    private String title;
    private String poster_path;
    private boolean adult;
    private List<String> genre_ids;
    private String overview;
    private String release_date;




    //here is the constructor for our movie object. This will be used to make Json parsing simple.
    public Movie(String vote_count, String id, String vote_average, String title, String poster_path,
                 boolean adult, List<String> genre_ids, String overview, String release_date) {
        this.vote_count=vote_count;
        this.id = id;
        this.vote_average = vote_average;
        this.title = title;
        this.poster_path = poster_path;
        this.adult = adult;
        this.genre_ids = genre_ids;
        this.overview = overview;
        this.release_date = release_date;
    }


    // All the setter and getter methods for the Movie object are below..

    public String getVote_count(){ return this.vote_count; }

    public void setVote_count(String vote_count){ this.vote_count = vote_count; }

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    public String getVote_average() { return this.vote_average; }

    public void setVote_average(String vote_average) { this.vote_average = vote_average; }

    public String getTitle() { return this.title; }

    public void setTitle(String title) { this.title = title; }

    public String getPoster_path() { return poster_path; }

    public void setPoster_path(String poster_path) { this.poster_path = poster_path; }

    public boolean isAdult() { return adult; }

    public void setAdult(boolean adult) { this.adult = adult; }

    public List<String> getGenre_ids() { return genre_ids; }

    public void setGenre_ids(List<String> genre_ids) { this.genre_ids = genre_ids; }

    public String getOverview() { return overview; }

    public void setOverview(String overview) { this.overview = overview; }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    /*{"vote_count":5378,"id":299534,"video":false,"vote_average":8.5,"title":"Avengers: Endgame","popularity":319.667,
    "poster_path":"\/or06FN3Dka5tukK1e9sl16pB3iy.jpg","original_language":"en",
    "original_title":"Avengers: Endgame","genre_ids":[12,878,28],
    "backdrop_path":"\/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg","adult":false,
    "overview":"After the devastating events of Avengers: Infinity War,
     the universe is in ruins due to the efforts of the Mad Titan, Thanos. With the help of remaining allies,
      the Avengers must assemble once more in order to undo Thanos' actions and restore order
     to the universe once and for all, no matter what consequences may be in store.","release_date":"2019-04-24"}/*
      */

    //this Parceable makes passing the Movie object possible using intents, instead of having to pass
    //each invidiual String as String Extras.

    protected Movie(Parcel in) {
        vote_count = in.readString();
        id = in.readString();
        vote_average = in.readString();
        title = in.readString();
        poster_path = in.readString();
        adult = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            genre_ids = new ArrayList<String>();
            in.readList(genre_ids, String.class.getClassLoader());
        } else {
            genre_ids = null;
        }
        overview = in.readString();
        release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vote_count);
        dest.writeString(id);
        dest.writeString(vote_average);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeByte((byte) (adult ? 0x01 : 0x00));
        if (genre_ids == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(genre_ids);
        }
        dest.writeString(overview);
        dest.writeString(release_date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
