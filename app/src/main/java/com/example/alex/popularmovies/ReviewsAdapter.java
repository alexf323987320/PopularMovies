package com.example.alex.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alex.popularmovies.data.ReviewsJson;
import com.example.alex.popularmovies.data.TrailerJson;
import com.example.alex.popularmovies.data.TrailersJson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private ReviewsJson mReviews;
    private Context mContext;

    public ReviewsAdapter(Context context, ReviewsJson reviews) {
        super();
        mReviews = reviews;
        mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        holder.contentTv.setText(mReviews.getResults().get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews == null ? 0 : mReviews.getResults().size();
    }

    public void setNewData(ReviewsJson reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView contentTv;

        ReviewViewHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.content_tv);
        }

    }
}