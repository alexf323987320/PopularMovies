package com.example.alex.popularmovies.data;

import android.net.Uri;

import static com.example.alex.popularmovies.data.MovieDb.BASE_YOUTUBE_IMAGE_URL;
import static com.example.alex.popularmovies.data.MovieDb.FIRST_YOUTUBE_IMAGE_SEGMENT;

public class ReviewJson {

    private String id;
    private String author;
    private String content;
    private String url;

    private ReviewJson() {
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
