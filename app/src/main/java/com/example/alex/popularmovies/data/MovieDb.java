package com.example.alex.popularmovies.data;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

import com.example.alex.popularmovies.BuildConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MovieDb {

    public static final int SORT_BY_POPULAR = 0;
    public static final int SORT_BY_TOP_RATED = 1;

    @Nullable
    public static ArrayList<Movie> getMovies(int sortOrder){
        final String BASE_URL = "https://api.themoviedb.org/3/movie";
        final String POPULAR_SEGMENT = "popular";
        final String TOP_RATED_SEGMENT = "top_rated";
        final String PARAM_KEY_API_KEY = "api_key";
        final String API_KEY = BuildConfig.MOVIEDB_API_KEY;

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
            //Convert jsonString to array of Movie
           Gson gson = new Gson();
           MoviesDbJson moviesDbJson = gson.fromJson(jsonString, MoviesDbJson.class);
           return arrayListFromMoviesDbJson(moviesDbJson);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        final int BUFFER_LENGTH = 512;
        char[] buffer = new char[BUFFER_LENGTH];
        int readLength;
        StringBuilder sb = new StringBuilder();

        InputStreamReader reader = new InputStreamReader(inputStream);

        readLength = reader.read(buffer, 0, BUFFER_LENGTH);
        while (readLength != -1) {
            if (readLength < BUFFER_LENGTH) {
                sb.append(Arrays.copyOf(buffer, readLength));
            } else {
                sb.append(buffer);
            }
            readLength = reader.read(buffer, 0, BUFFER_LENGTH);
        }
        reader.close();
        return sb.toString();
    }

    private static String getFullPosterName(String path, Boolean thumbnail) {
        String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
        String POSTER_SIZE = "w342";
        String THUMBNAIL_SIZE = "w154";

        Uri uri = Uri.parse(BASE_IMAGE_URL).buildUpon().
                appendPath(thumbnail ? THUMBNAIL_SIZE : POSTER_SIZE).
                appendEncodedPath(path).
                build();
        return uri.toString();
    }

    @Nullable
    private static Date getDateFromString(String string) {
        final String PATTERN = "yyyy-MM-DD";
        Date result = null;
        try {
            result = new SimpleDateFormat(PATTERN).parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Class is used for working with GSON library
    private static class MoviesDbJson {
        private class MovieDbJson {
            private String original_title;
            private String poster_path;
            private String overview;
            private float vote_average;
            private String release_date;
        }
        private MovieDbJson[] results;
    }

    private static ArrayList<Movie> arrayListFromMoviesDbJson(MoviesDbJson moviesDbJson) {
        ArrayList<Movie> result = new ArrayList<>();
        for (MoviesDbJson.MovieDbJson movieDbJson : moviesDbJson.results) {
            Movie movie = new Movie(movieDbJson.original_title,
                    getFullPosterName(movieDbJson.poster_path, false),
                    getFullPosterName(movieDbJson.poster_path, true),
                    movieDbJson.overview,
                    movieDbJson.vote_average,
                    getDateFromString(movieDbJson.release_date));
            result.add(movie);
        }
        return result;
    }
}