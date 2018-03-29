package com.example.alex.popularmovies.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.example.alex.popularmovies.data.MovieDb.*;

public class MoviesJson {

    private MovieJson[] results;
    private int total_pages;
    private int total_results;

    //constructor
    @Nullable
    public static MoviesJson create(int sortOrder, ContentResolver cr) {
        if (sortOrder == SORT_BY_FAVORITES) {
            //in this case we should read db
            Cursor cursor = cr.query(FavoritesContract.FavoritesTable.FAVORITES_URI, null, null, null, null);
            MoviesJson moviesJson = new MoviesJson();
            moviesJson.total_pages = 1;
            if (cursor != null && cursor.getCount() != 0) {
                moviesJson.total_results = cursor.getCount();
                moviesJson.results = new MovieJson[cursor.getCount()];
                int i = 0;
                while (cursor.moveToNext()) {
                    moviesJson.results[i] = new MovieJson(cursor);
                    i++;
                }
            }
            if (cursor != null) cursor.close();
            return moviesJson;
        } else {
            //Forming proper URL
            Uri uri = Uri.parse(BASE_URL).buildUpon().
                    appendPath(sortOrder == SORT_BY_TOP_RATED ? TOP_RATED_SEGMENT : POPULAR_SEGMENT).
                    appendQueryParameter(PARAM_KEY_API_KEY, API_KEY).
                    build();
            try {
                URL url = new URL(uri.toString());
                //Getting jsonString from URL
                InputStream inputStream = url.openStream();
                String jsonString = readInputStream(inputStream);
                inputStream.close();
                Gson gson = new Gson();
                return gson.fromJson(jsonString, MoviesJson.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private MoviesJson() {
    }

    public int getTotalPages() {
        return total_pages;
    }

    public int getTotalResults() {
        return total_results;
    }

    public MovieJson[] getResults() {
        return results;
    }
}