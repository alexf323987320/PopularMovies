package com.example.alex.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.alex.popularmovies.data.MovieJson;
import com.example.alex.popularmovies.data.ReviewsJson;
import com.example.alex.popularmovies.data.TrailersJson;
import com.example.alex.popularmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    private final String TAG = "DetailActivity++";

    private MovieJson mMovie;
    private final int TRAILERS_LOADER_ID = 0;
    private final int REVIEWS_LOADER_ID = 1;

    private ActivityDetailBinding mBinding;

    private class TrailersLoaderCallbacks implements LoaderManager.LoaderCallbacks<TrailersJson> {

        @Override
        public Loader<TrailersJson> onCreateLoader(int id, Bundle args) {
            return new DetailActivity.TrailersLoader(DetailActivity.this, mMovie.getId());
        }

        @Override
        public void onLoadFinished(Loader<TrailersJson> loader, TrailersJson data) {
            Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
            mBinding.trailersProgressBar.setVisibility(View.INVISIBLE);
            setupTrailersRecyclerView(data);
        }

        @Override
        public void onLoaderReset(Loader<TrailersJson> loader) {
        }
    }

    private static class TrailersLoader extends AsyncTaskLoader<TrailersJson> {

        private final String TAG = "TrailersLoader++";

        //It's bad to make your own cache here, but it's a framework
        private TrailersJson mCache;
        private int mMovieId;

        TrailersLoader(Context context, int movieId) {
            super(context);
            mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            Log.d(TAG, "onStartLoading() called - mCache = " + mCache);
            if (mCache != null) {
                deliverResult(mCache);
            }
        }

        @Override
        public TrailersJson loadInBackground() {
            try {
                Log.d(TAG, "loadInBackground() called - before sleep");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "loadInBackground() called - after sleep");
            return TrailersJson.create(mMovieId);
        }

        @Override
        public void deliverResult(TrailersJson data) {
            Log.d(TAG, "deliverResult() called with: data = [" + data + "]");
            mCache = data;
            super.deliverResult(data);
        }
    }

    private class ReviewsLoaderCallbacks implements LoaderManager.LoaderCallbacks<ReviewsJson> {

        @Override
        public Loader<ReviewsJson> onCreateLoader(int id, Bundle args) {
            return new DetailActivity.ReviewsLoader(DetailActivity.this, mMovie.getId());
        }

        @Override
        public void onLoadFinished(Loader<ReviewsJson> loader, ReviewsJson data) {
            Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
            mBinding.reviewsProgressBar.setVisibility(View.INVISIBLE);
            setupReviewsViewPager(data);
        }

        @Override
        public void onLoaderReset(Loader<ReviewsJson> loader) {
        }
    }

    private static class ReviewsLoader extends AsyncTaskLoader<ReviewsJson> {

        private final String TAG = "ReviewsLoader++";

        //It's bad to make your own cache here, but it's a framework
        private ReviewsJson mCache;
        private int mMovieId;

        ReviewsLoader(Context context, int movieId) {
            super(context);
            mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            if (mCache != null) {
                deliverResult(mCache);
            }
        }

        @Override
        public ReviewsJson loadInBackground() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ReviewsJson.create(mMovieId);
        }

        @Override
        public void deliverResult(ReviewsJson data) {
            mCache = data;
            super.deliverResult(data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(getString(R.string.parcel_key_movie));
        if (mMovie == null) closeWithError();

        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, new TrailersLoaderCallbacks());
        if (savedInstanceState == null) {
            forceLoadTrailersLoader();
        }

        mBinding.reviewsTv.setText(getString(R.string.reviews_label, 0, 0));
        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, new ReviewsLoaderCallbacks());
        if (savedInstanceState == null) {
            forceLoadReviewsLoader();
        }

        populateUI();
    }

    private void closeWithError() {
        finish();
        Toast.makeText(this, getString(R.string.error_detail_activity), Toast.LENGTH_LONG).show();
    }

    private void populateUI() {
        Picasso.with(this).load(mMovie.getThumbnail()).into(mBinding.thumbnailIv, new Callback() {
            @Override
            public void onSuccess() {
                mBinding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                mBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        mBinding.originalTitleTv.setText(mMovie.getOriginalTitle());
        mBinding.overviewTv.setText(mMovie.getOverview());
        mBinding.releaseDateTv.setText(new SimpleDateFormat("yyyy").format(mMovie.getReleaseDate()));
        mBinding.voteAverageTv.setText(String.valueOf(mMovie.getVoteAverage()));
        //blocking call, it's ok here
        mBinding.heartTb.setChecked(mMovie.getFavorite(this));
        mBinding.heartTb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //blocking call, it's ok here
            if (!mMovie.setFavorite(this, isChecked)) {
                buttonView.setChecked(!isChecked);
            }
        });
        mBinding.shareB.setOnClickListener(v -> {
            TrailersAdapter adapter = (TrailersAdapter) mBinding.trailersRv.getAdapter();
            if (adapter != null ) {
                TrailersJson trailersJson = adapter.getTrailers();
                if (trailersJson.getResults().size() > 0) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, trailersJson.getResults().get(0).getYoutubeUri().toString());
                    sendIntent.setType("text/plain");
                    //? sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.send_to)));
                }
            }
        });
    }

    private void forceLoadTrailersLoader(){
        if (!NetU.isConnected(this, R.id.constraint_layout, false, true)) return;
        mBinding.trailersProgressBar.setVisibility(View.VISIBLE);
        Loader loader = getSupportLoaderManager().getLoader(TRAILERS_LOADER_ID);
        loader.forceLoad();
    }

    private void forceLoadReviewsLoader(){
        if (!NetU.isConnected(this, R.id.constraint_layout, false, true)) return;
        mBinding.reviewsProgressBar.setVisibility(View.VISIBLE);
        Loader loader = getSupportLoaderManager().getLoader(REVIEWS_LOADER_ID);
        loader.forceLoad();
    }

    private void setupTrailersRecyclerView(TrailersJson trailers) {
        TrailersAdapter adapter = (TrailersAdapter) mBinding.trailersRv.getAdapter();
        //If adapter is null this is the first initialization, else we should update only data
        if (adapter == null) {
            TrailersAdapter trailersAdapter = new TrailersAdapter(this, trailers, trailer -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, trailer.getYoutubeUri());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(R.id.constraint_layout), R.string.no_app, Snackbar.LENGTH_LONG).show();
                }
                startActivity(intent);
            });
            mBinding.trailersRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mBinding.trailersRv.setAdapter(trailersAdapter);
        } else {
            adapter.setNewData(trailers);
        }
    }

    private void setupReviewsViewPager(ReviewsJson reviews) {
        ReviewsPagerAdapter adapter = (ReviewsPagerAdapter) mBinding.reviewsVp.getAdapter();
        //If adapter is null this is the first initialization, else we should update only data
        if (adapter == null) {
            adapter = new ReviewsPagerAdapter(getSupportFragmentManager(), reviews);
            mBinding.reviewsVp.setAdapter(adapter);
            mBinding.reviewsTv.setText(getString(R.string.reviews_label, Math.min(adapter.getCount(), 1), adapter.getCount()));
            mBinding.reviewsVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    mBinding.reviewsTv.setText(getString(R.string.reviews_label, position + 1, mBinding.reviewsVp.getAdapter().getCount()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } else {
            adapter.setNewData(reviews);
        }
    }
}