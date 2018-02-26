package com.example.alex.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Movie implements Parcelable{

    private String mOriginalTitle;
    private String mPoster;
    private String mThumbnail;
    private String mOverview;
    private float mVoteAverage;
    private Date mReleaseDate;

    public Movie(String originalTitle, String poster, String thumbnail, String overview, float voteAverage, Date releaseDate) {
        this.mOriginalTitle = originalTitle;
        this.mPoster = poster;
        this.mThumbnail = thumbnail;
        this.mOverview = overview;
        this.mVoteAverage = voteAverage;
        this.mReleaseDate = releaseDate;
    }

    private Movie(Parcel parcel) {
        mOriginalTitle = parcel.readString();
        mPoster = parcel.readString();
        mThumbnail = parcel.readString();
        mOverview = parcel.readString();
        mVoteAverage = parcel.readFloat();
        mReleaseDate = new Date(parcel.readLong());
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPoster() {
        return mPoster;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public String getOverview() {
        return mOverview;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mPoster);
        dest.writeString(mThumbnail);
        dest.writeString(mOverview);
        dest.writeFloat(mVoteAverage);
        dest.writeLong(mReleaseDate.getTime());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}