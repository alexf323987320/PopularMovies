package com.example.alex.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.alex.popularmovies.data.MovieDb;
import com.example.alex.popularmovies.data.MoviesJson;
import com.example.alex.popularmovies.data.Prefs;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity++";

    private RecyclerView mMoviesRv;
    private ProgressBar mProgressPar;

    private final int MOVIES_LOADER_ID = 0;
    private MoviesLoaderCallbacks mMoviesLoaderCallbacks;

    private BroadcastReceiver mConnectionReceiver;
    private Boolean mConnectionReceiverWasCalled = false;

    private class MoviesLoaderCallbacks implements LoaderManager.LoaderCallbacks<MoviesJson> {

        @Override
        public Loader<MoviesJson> onCreateLoader(int id, Bundle args) {
            return new MoviesLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<MoviesJson> loader, MoviesJson data) {
            Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
            mProgressPar.setVisibility(View.INVISIBLE);
            setupRecyclerView(data);
        }

        @Override
        public void onLoaderReset(Loader<MoviesJson> loader) {
        }
    }

    private static class MoviesLoader extends AsyncTaskLoader<MoviesJson> {

        private final String TAG = "MoviesLoader++";

        //It's bad to make your own cache here, but it's a framework
        private MoviesJson mCache;

        MoviesLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            Log.d(TAG, "onStartLoading() called - mCache = " + mCache);
            if (mCache != null) {
                deliverResult(mCache);
            }
        }

        @Override
        public MoviesJson loadInBackground() {
            try {
                Log.d(TAG, "loadInBackground() called - before sleep");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "loadInBackground() called - after sleep");
            return MoviesJson.create(Prefs.getSortOrder(getContext()), getContext().getContentResolver());
        }

        @Override
        public void deliverResult(MoviesJson data) {
            Log.d(TAG, "deliverResult() called with: data = [" + data + "]");
            mCache = data;
            super.deliverResult(data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        mMoviesRv = findViewById(R.id.movies_rv);
        mProgressPar = findViewById(R.id.progress_bar);
        mMoviesLoaderCallbacks = new MoviesLoaderCallbacks();

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, mMoviesLoaderCallbacks);
        if (savedInstanceState == null) {
            if (NetUtils.isConnected(this, R.id.constraint_layout, false, true)) {
                forceLoadMoviesLoader();
            }
        }

        //broadcast receiver to inform about internet connection
        mConnectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //skip informing on receiver creation
                //if otherNetwork <> null it means it's an attempt to connect, so no need to inform
                if (mConnectionReceiverWasCalled && intent.getParcelableExtra("otherNetwork") == null) {
                    NetUtils.isConnected(MainActivity.this, R.id.constraint_layout, true, true);
                } else {
                    mConnectionReceiverWasCalled = true;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(CONNECTIVITY_ACTION);
        registerReceiver(mConnectionReceiver, intentFilter);

        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        int restoreId = 0;
        switch (Prefs.getSortOrder(this)) {
            case MovieDb.SORT_BY_POPULAR:
                restoreId = R.id.action_popular;
                break;
            case MovieDb.SORT_BY_TOP_RATED:
                restoreId = R.id.action_top_rated;
                break;
            case MovieDb.SORT_BY_FAVORITES:
                restoreId = R.id.action_favorite;
                break;
        }
        bottomNavigationView.setSelectedItemId(restoreId);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.action_favorite:
                    //show if no internet connection and continue
                    NetUtils.isConnected(this, R.id.constraint_layout, false, true);
                    Prefs.setSortOrder(MainActivity.this, MovieDb.SORT_BY_FAVORITES);
                    forceLoadMoviesLoader();
                    return true;
                case R.id.action_popular:
                    if (NetUtils.isConnected(this, R.id.constraint_layout, false, true)) {
                        Prefs.setSortOrder(MainActivity.this, MovieDb.SORT_BY_POPULAR);
                        forceLoadMoviesLoader();
                        return true;
                    }
                    return false;
                case R.id.action_top_rated:
                    if (NetUtils.isConnected(this, R.id.constraint_layout, false, true)) {
                        Prefs.setSortOrder(MainActivity.this, MovieDb.SORT_BY_TOP_RATED);
                        forceLoadMoviesLoader();
                        return true;
                    }
                    return false;
            }
            return false;
        });
    }

    private void setupRecyclerView(MoviesJson movies) {
        final int PORTRAIT_COLUMNS_COUNT = 2;
        final int LANDSCAPE_COLUMNS_COUNT = 3;

        MoviesAdapter adapter = (MoviesAdapter) mMoviesRv.getAdapter();
        //If adapter is null this is first initialization, else we should update only data
        if (adapter == null) {
            MoviesAdapter moviesAdapter = new MoviesAdapter(this, movies, movie -> {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(getString(R.string.parcel_key_movie), movie);
                    startActivity(intent);
                });
            int columnsCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? PORTRAIT_COLUMNS_COUNT: LANDSCAPE_COLUMNS_COUNT;
            mMoviesRv.setLayoutManager(new GridLayoutManager(this, columnsCount));
            mMoviesRv.setAdapter(moviesAdapter);
        } else {
            adapter.setNewData(movies);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh:
                if (NetUtils.isConnected(this, R.id.constraint_layout, false, true)) {
                    forceLoadMoviesLoader();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void forceLoadMoviesLoader(){
        mProgressPar.setVisibility(View.VISIBLE);
        Loader loader = getSupportLoaderManager().getLoader(MOVIES_LOADER_ID);
        loader.forceLoad();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mConnectionReceiver);
        super.onDestroy();
    }
}