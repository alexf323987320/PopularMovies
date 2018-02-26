package com.example.alex.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.alex.popularmovies.data.Movie;
import com.example.alex.popularmovies.data.MovieDb;
import com.example.alex.popularmovies.data.Prefs;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mMoviesRv;
    private ProgressBar mProgressPar;

    private ArrayList<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoviesRv = findViewById(R.id.movies_rv);
        mProgressPar = findViewById(R.id.progress_bar_pb);
        //If savedInstanceState not null we should get saved movies
        if (savedInstanceState == null) {
            getNewMoviesAndSetupRecyclerView();
        } else {
            mMovies = savedInstanceState.getParcelableArrayList(getString(R.string.parcel_key_movies));
            if (mMovies == null) {
                getNewMoviesAndSetupRecyclerView();
            } else {
                setupRecyclerView();
            }
        }
    }

    private void setupRecyclerView() {
        final int PORTRAIT_COLUMNS_COUNT = 2;
        final int LANDSCAPE_COLUMNS_COUNT = 3;

        //Data retrieved hide progress bar
        mProgressPar.setVisibility(View.INVISIBLE);
        MoviesAdapter adapter = (MoviesAdapter) mMoviesRv.getAdapter();
        //If adapter is null this is first initialization, else we should update only data
        if (adapter == null) {
            MoviesAdapter moviesAdapter = new MoviesAdapter(this, mMovies, new MoviesAdapter.OnClickListener() {
                @Override
                public void OnClick(Movie movie) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(getString(R.string.parcel_key_movie), movie);
                    startActivity(intent);
                }
            });
            int columnsCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? PORTRAIT_COLUMNS_COUNT: LANDSCAPE_COLUMNS_COUNT;
            mMoviesRv.setLayoutManager(new GridLayoutManager(this, columnsCount));
            mMoviesRv.setAdapter(moviesAdapter);
        } else {
            adapter.setNewData(mMovies);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getNewMoviesAndSetupRecyclerView() {
        if (!isConnected()) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            //return;
        }
        new AsyncTask<Void, Void, ArrayList<Movie>>() {
            @Override
            protected ArrayList<Movie> doInBackground(Void... voids) {
                return MovieDb.getMovies(Prefs.getSortOrder(MainActivity.this));
            }

            @Override
            protected void onPostExecute(ArrayList<Movie> movies) {
                mMovies = movies;
                setupRecyclerView();
            }

            @Override
            protected void onPreExecute() {
                mProgressPar.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.parcel_key_movies), mMovies);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        //Fill up the spinner
        //1.get Spinner
        MenuItem menuItem = menu.findItem(R.id.item_sort_order);
        Spinner spinner = (Spinner) menuItem.getActionView();
        //2. Prepare adapter
        ArrayList<String> dropDownList = new ArrayList<>();
        dropDownList.add(getString(R.string.sort_by_popular));
        dropDownList.add(getString(R.string.sort_by_top_rated));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                dropDownList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //3. Restore spinner selection
        int spinnerPosition;
        if (Prefs.getSortOrder(this) == MovieDb.SORT_BY_POPULAR) {
            spinnerPosition = adapter.getPosition(getString(R.string.sort_by_popular));
        } else {
            spinnerPosition = adapter.getPosition(getString(R.string.sort_by_top_rated));
        }
        spinner.setSelection(spinnerPosition);
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
                    Prefs.setSortOrder(MainActivity.this, sortOrder);
                    getNewMoviesAndSetupRecyclerView();
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
                getNewMoviesAndSetupRecyclerView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}