package com.example.alex.popularmovies.data;

import com.example.alex.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

//Popular movies example:
// https://api.themoviedb.org/3/movie/popular?api_key=<api_key>&language=en-US&page=1
//Movie's Image example:
// https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg

//Trailers example:
//https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=<api_key>&language=en-US

//Youtube video example:
// http://www.youtube.com/watch?v=GDFUdMvacI0
//Youtube video's thumbnail  example:
// http://img.youtube.com/vi/GDFUdMvacI0/0.jpg

public class MovieDb {

    public static final int SORT_BY_POPULAR = 0;
    public static final int SORT_BY_TOP_RATED = 1;

    public static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    public static final String PARAM_KEY_API_KEY = "api_key";
    public static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;

    //Movies
    public static final String POSTER_SIZE = "w342";
    public static final String THUMBNAIL_SIZE = "w154";
    public static final String POPULAR_SEGMENT = "popular";
    public static final String TOP_RATED_SEGMENT = "top_rated";
    public static final String DATE_PATTERN = "yyyy-MM-DD";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";

    //Trailers
    public static final String VIDEOS_SEGMENT = "videos";
    public static final String BASE_YOUTUBE_URL = "http://www.youtube.com/watch";
    public static final String YOUTUBE_WATCH_KEY = "v";
    public static final String BASE_YOUTUBE_IMAGE_URL = "http://img.youtube.com/vi/";
    public static final String FIRST_YOUTUBE_IMAGE_SEGMENT = "0.jpg";

    //Reviews
    public static final String REVIEWS_SEGMENT = "reviews";

    static String readInputStream(InputStream inputStream) throws IOException {
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
}