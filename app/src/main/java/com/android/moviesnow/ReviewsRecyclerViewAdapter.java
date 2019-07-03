package com.android.moviesnow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> authors;
    private ArrayList<String> reviews;
    private Context mContext;
    String Tag = ReviewsRecyclerViewAdapter.class.getSimpleName();

    public ReviewsRecyclerViewAdapter(ArrayList<String> author, ArrayList<String> review, Context mContext) {
        this.authors = author;
        this.reviews = review;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_layout, parent,
                false);

        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String singleAuthor = authors.get(position);
        String singleReview = reviews.get(position);
        authors.set(position, singleAuthor);
        reviews.set(position, singleReview);
        TextView authorView = holder.itemView.findViewById(R.id.list_item_author);
        TextView reviewsView = holder.itemView.findViewById(R.id.list_item_review_content);
        authorView.setText(authors.get(position));
        reviewsView.setText(reviews.get(position));


        //need to set the views once created.


    }

    @Override
    public int getItemCount() {
        return authors.size();
    }
}
