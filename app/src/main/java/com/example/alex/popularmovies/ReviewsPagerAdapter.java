package com.example.alex.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class ReviewsPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

    private ReviewsJson mReviews;

    public ReviewsPagerAdapter(FragmentManager fm, ReviewsJson reviews) {
        super(fm);
        mReviews = reviews;
    }

    @Override
    public Fragment getItem(int position) {
        ReviewFragment reviewFragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ReviewFragment.ARG_TEXT_KEY, mReviews.getResults().get(position).getContent());
        reviewFragment.setArguments(args);
        return reviewFragment;
    }

    @Override
    public int getCount() {
        if (mReviews.getResults() == null) {
            return 0;
        } else {
            return mReviews.getResults().size();
        }
    }

    public void setNewData(ReviewsJson reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }
}