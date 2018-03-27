package com.example.alex.popularmovies.data;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static com.example.alex.popularmovies.data.MovieDb.*;

public class ReviewsJson {

    private int id;
    private int page;
    private int total_pages;
    private int total_results;
    private ArrayList<ReviewJson> results;

    //constructor
    @Nullable
    public static ReviewsJson create(int movieId) {
        //Forming proper URL
        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendPath(String.valueOf(movieId)).
                appendPath(REVIEWS_SEGMENT).
                appendQueryParameter(PARAM_KEY_API_KEY, API_KEY).
                build();
        try {
            URL url = new URL(uri.toString());
            //Getting jsonString from URL
            InputStream inputStream = url.openStream();
            String jsonString = readInputStream(inputStream);
            inputStream.close();
            Gson gson = new Gson();
            ReviewsJson result = gson.fromJson(jsonString, ReviewsJson.class);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ReviewsJson() {
    }

    public int getId() {
        return id;
    }

    public ArrayList<ReviewJson> getResults() {
        return results;
    }
}