package com.example.alex.popularmovies.data;

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
    public static MoviesJson create(int sortOrder) {
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