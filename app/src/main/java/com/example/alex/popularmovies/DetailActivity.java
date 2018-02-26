package com.example.alex.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.alex.popularmovies.data.Movie;
import com.example.alex.popularmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_key_movie));
        if (movie == null) closeWithError();
        populateUI(movie);
    }

    private void closeWithError() {
        finish();
        Toast.makeText(this, getString(R.string.error_detail_activity), Toast.LENGTH_LONG).show();
    }

    private void populateUI(Movie movie) {
        Picasso.with(this).load(movie.getThumbnail()).into(mBinding.thumbnailIv, new Callback() {
            @Override
            public void onSuccess() {
                mBinding.progressBarPb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                mBinding.progressBarPb.setVisibility(View.INVISIBLE);
            }
        });
        mBinding.originalTitleTv.setText(movie.getOriginalTitle());
        mBinding.overviewTv.setText(movie.getOverview());
        mBinding.releaseDateTv.setText(new SimpleDateFormat("yyyy").format(movie.getReleaseDate()));
        mBinding.voteAverageTv.setText(String.valueOf(movie.getVoteAverage()));
    }
}