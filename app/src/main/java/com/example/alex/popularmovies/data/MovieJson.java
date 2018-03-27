package com.example.alex.popularmovies.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.alex.popularmovies.data.MovieDb.*;

public class MovieJson implements Parcelable {

    private String original_title;
    private String poster_path;
    private String overview;
    private float vote_average;
    private String release_date;
    private int id;

    private MovieJson() {
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getPoster() {
        Uri uri = Uri.parse(BASE_IMAGE_URL).buildUpon().
                appendPath(POSTER_SIZE).
                appendEncodedPath(poster_path).
                build();
        return uri.toString();
    }

    public String getThumbnail() {
        Uri uri = Uri.parse(BASE_IMAGE_URL).buildUpon().
                appendPath(THUMBNAIL_SIZE).
                appendEncodedPath(poster_path).
                build();
        return uri.toString();
    }

    public String getOverview() {
        return overview;
    }

    public float getVoteAverage() {
        return vote_average;
    }

    @Nullable
    public Date getReleaseDate() {
        Date result = null;
        try {
            result = new SimpleDateFormat(DATE_PATTERN).parse(release_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getId() {
        return id;
    }

    //parcelable interface description

    private MovieJson(Parcel parcel) {
        original_title = parcel.readString();
        poster_path = parcel.readString();
        overview = parcel.readString();
        vote_average = parcel.readFloat();
        release_date = parcel.readString();
        id = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeFloat(vote_average);
        dest.writeString(release_date);
        dest.writeInt(id);
    }

    public static final Creator<MovieJson> CREATOR = new Creator<MovieJson>() {
        public MovieJson createFromParcel(Parcel in) {
            return new MovieJson(in);
        }

        public MovieJson[] newArray(int size) {
            return new MovieJson[size];
        }
    };
}
