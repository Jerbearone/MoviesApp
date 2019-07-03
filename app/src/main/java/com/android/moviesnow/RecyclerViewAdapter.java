package com.android.moviesnow;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import model.Movie;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //creating arrayList
    private ArrayList<String> mImages= new ArrayList<>();
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Movie> mMovieInfo = new ArrayList<>();
    private Context mContext;
    String Tag = RecyclerViewAdapter.class.getSimpleName();

    //creating constructor

    public RecyclerViewAdapter(ArrayList<String> mImages, ArrayList<String> mImageNames, ArrayList<Movie> mMovieInfo,
            Context mContext) {
        this.mImages = mImages;
        this.mImageNames = mImageNames;
        this.mMovieInfo = mMovieInfo;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout, viewGroup,
                false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, final int i) {
        Log.d(Tag, "onBindViewHolder: was called");
        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(i))
                .into(viewHolder.image);
        viewHolder.list_item_parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Tag, "onClick: clicked on: " + mImageNames.get(i));
                //Toast.makeText(mContext,mImageNames.get(i),Toast.LENGTH_SHORT).show();

                //will launch the detailsActivity from here.
                Intent selectedMovie = new Intent(v.getContext(), DetailsActivity.class);
                Movie singleMovie = mMovieInfo.get(i);
                selectedMovie.putExtra("info", singleMovie);
                mContext.startActivity(selectedMovie);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        RelativeLayout list_item_parent_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.list_item_image);
            list_item_parent_layout = itemView.findViewById(R.id.list_item_parent_layout);
        }
    }
}
