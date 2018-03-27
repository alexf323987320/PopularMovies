package com.example.alex.popularmovies.data;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static com.example.alex.popularmovies.data.MovieDb.API_KEY;
import static com.example.alex.popularmovies.data.MovieDb.BASE_URL;
import static com.example.alex.popularmovies.data.MovieDb.PARAM_KEY_API_KEY;
import static com.example.alex.popularmovies.data.MovieDb.VIDEOS_SEGMENT;
import static com.example.alex.popularmovies.data.MovieDb.readInputStream;

public class TrailersJson {

    private int id;
    private ArrayList<TrailerJson> results;

    //constructor
    @Nullable
    public static TrailersJson create(int movieId) {
        //Forming proper URL
        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendPath(String.valueOf(movieId)).
                appendPath(VIDEOS_SEGMENT).
                appendQueryParameter(PARAM_KEY_API_KEY, API_KEY).
                build();
        try {
            URL url = new URL(uri.toString());
            //Getting jsonString from URL
            InputStream inputStream = url.openStream();
            String jsonString = readInputStream(inputStream);
            inputStream.close();
            Gson gson = new Gson();
            TrailersJson result = gson.fromJson(jsonString, TrailersJson.class);
            result.leaveOnlyYoutubeTrailers();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TrailersJson() {
    }

    private void leaveOnlyYoutubeTrailers(){
        ArrayList<TrailerJson> arrayList = new ArrayList<>();
        for (int i = results.size(); i != 0; i--) {
            if(!results.get(i-1).getSite().equals("YouTube")) {
                results.remove(i-1);
            }
        }
    }

    public int getId() {
        return id;
    }

    public ArrayList<TrailerJson> getResults() {
        return results;
    }
}