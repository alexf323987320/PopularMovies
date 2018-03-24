package com.example.alex.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.alex.popularmovies.data.Movie;
import com.example.alex.popularmovies.data.MovieDb;
import com.example.alex.popularmovies.data.Prefs;

import java.util.ArrayList;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity++";

    private RecyclerView mMoviesRv;
    private ProgressBar mProgressPar;

    private ArrayList<Movie> mMovies;

    private final int MOVIES_LOADER_ID = 0;
    private MoviesLoaderCallbacks mMoviesLoaderCallbacks;

    private BroadcastReceiver mConnectionReceiver;
    private Boolean mConnectionReceiverWasCalled = false;

    private class MoviesLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
            return new MoviesLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
            mProgressPar.setVisibility(View.INVISIBLE);
            mMovies = data;
            setupRecyclerView();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        }
    }

    private static class MoviesLoader extends AsyncTaskLoader<ArrayList<Movie>> {

        private final String TAG = "MoviesLoader++";

        //It's bad to make your own cache here, but it's a framework
        private ArrayList<Movie> mCache;
        //Do not start new loading before previous finished
        private boolean mForceLoadingStarted = false;

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
        protected void onForceLoad() {
            Log.d(TAG, "onForceLoad() called - mForceLoadingStarted = " + mForceLoadingStarted);
            if (!mForceLoadingStarted) {
                mForceLoadingStarted = true;
                super.onForceLoad();
            }
        }

        @Override
        public ArrayList<Movie> loadInBackground() {
            try {
                Log.d(TAG, "loadInBackground() called - before sleep");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "loadInBackground() called - after sleep");
            return MovieDb.getMovies(Prefs.getSortOrder(getContext()));
        }

        @Override
        public void deliverResult(ArrayList<Movie> data) {
            Log.d(TAG, "deliverResult() called with: data = [" + data + "]");
            mForceLoadingStarted = false;
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
            forceLoadMoviesLoader();
        }

        //broadcast receiver to inform about internet connection
        mConnectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //skip informing on receiver creation
                //if otherNetwork <> null it means it's a attempt to connect, so no need to inform
                if (mConnectionReceiverWasCalled && intent.getParcelableExtra("otherNetwork") == null) {
                    isConnected(true, true);
                } else {
                    mConnectionReceiverWasCalled = true;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(CONNECTIVITY_ACTION);
        registerReceiver(mConnectionReceiver, intentFilter);
    }

    private void setupRecyclerView() {
        final int PORTRAIT_COLUMNS_COUNT = 2;
        final int LANDSCAPE_COLUMNS_COUNT = 3;

        MoviesAdapter adapter = (MoviesAdapter) mMoviesRv.getAdapter();
        //If adapter is null this is first initialization, else we should update only data
        if (adapter == null) {
            MoviesAdapter moviesAdapter = new MoviesAdapter(this, mMovies, movie -> {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(getString(R.string.parcel_key_movie), movie);
                    startActivity(intent);
                });
            int columnsCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? PORTRAIT_COLUMNS_COUNT: LANDSCAPE_COLUMNS_COUNT;
            mMoviesRv.setLayoutManager(new GridLayoutManager(this, columnsCount));
            mMoviesRv.setAdapter(moviesAdapter);
        } else {
            adapter.setNewData(mMovies);
        }
    }

    private boolean isConnected(boolean showPositive, boolean showNegative) {
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        result = ni != null && ni.isConnected();
        if (!result && showNegative) {
                Snackbar sb = Snackbar.make(findViewById(R.id.constraint_layout), R.string.no_internet_connection, Snackbar.LENGTH_LONG);
                sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                sb.show();
        }
        if (result && showPositive) {
            Snackbar sb = Snackbar.make(findViewById(R.id.constraint_layout), R.string.internet_connection_restored, Snackbar.LENGTH_LONG);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            sb.show();
        }
        return result;
    }

    //restores spinner position according prefs
    private void restoreSpinnerPosition(Spinner spinner){
        int spinnerPosition;
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (Prefs.getSortOrder(this) == MovieDb.SORT_BY_POPULAR) {
            spinnerPosition = adapter.getPosition(getString(R.string.sort_by_popular));
        } else {
            spinnerPosition = adapter.getPosition(getString(R.string.sort_by_top_rated));
        }
        spinner.setSelection(spinnerPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        //Fill up the spinner
        //1.get Spinner
        MenuItem menuItem = menu.findItem(R.id.item_sort_order);
        final Spinner spinner = (Spinner) menuItem.getActionView();
        //2. Prepare adapter
        ArrayList<String> dropDownList = new ArrayList<>();
        dropDownList.add(getString(R.string.sort_by_popular));
        dropDownList.add(getString(R.string.sort_by_top_rated));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                dropDownList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //3. Restore spinner selection
        restoreSpinnerPosition(spinner);
        //4. On selecting new value save it to preferences
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int sortOrder;
                if (parent.getItemAtPosition(position) == getString(R.string.sort_by_popular)) {
                    sortOrder = MovieDb.SORT_BY_POPULAR;
                } else {
                    sortOrder = MovieDb.SORT_BY_TOP_RATED;
                }
                if (Prefs.getSortOrder(MainActivity.this) != sortOrder) {
                    if (isConnected(false, true)) {
                        Prefs.setSortOrder(MainActivity.this, sortOrder);
                        forceLoadMoviesLoader();
                    } else {
                        restoreSpinnerPosition(spinner);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh:
                forceLoadMoviesLoader();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void forceLoadMoviesLoader(){
        if (!isConnected(false, true)) return;
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