package com.example.alex.popularmovies.data;

import android.net.Uri;

import static com.example.alex.popularmovies.data.MovieDb.*;

public class TrailerJson {

    private String id;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    private TrailerJson() {
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getThumbnail() {
        Uri uri = Uri.parse(BASE_YOUTUBE_IMAGE_URL).buildUpon().
                appendPath(getKey()).
                appendEncodedPath(FIRST_YOUTUBE_IMAGE_SEGMENT).
                build();
        return uri.toString();
    }

    public Uri getYoutubeUri() {
        return Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_WATCH_KEY, getKey()).build();
    }
}
