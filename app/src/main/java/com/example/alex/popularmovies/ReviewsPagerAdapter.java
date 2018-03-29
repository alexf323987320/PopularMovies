package com.example.alex.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.alex.popularmovies.data.ReviewsJson;

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
        args.putInt(ReviewFragment.ARG_POSITION_KEY, position);
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