package com.example.alex.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.alex.popularmovies.data.FavoritesContract.FavoritesTable;

import static com.example.alex.popularmovies.data.MovieDb.*;

public class MovieJson implements Parcelable {

    private String original_title;
    private String poster_path;
    private String overview;
    private float vote_average;
    private String release_date;
    private int id;

    //true if it is read once from db, or created from db
    private boolean mIsFavoriteInitialized;
    private boolean mIsFavorite;

    private MovieJson() {
    }

    public MovieJson(Cursor cursor) {
        original_title = cursor.getString(cursor.getColumnIndex(FavoritesTable.COLUMN_ORIGINAL_TITLE));
        poster_path = cursor.getString(cursor.getColumnIndex(FavoritesTable.COLUMN_POSTER_PATH));
        overview = cursor.getString(cursor.getColumnIndex(FavoritesTable.COLUMN_OVERVIEW));
        vote_average = cursor.getFloat(cursor.getColumnIndex(FavoritesTable.COLUMN_VOTE_AVERATE));
        release_date = cursor.getString(cursor.getColumnIndex(FavoritesTable.COLUMN_RELEASE_DATE));
        id = cursor.getInt(cursor.getColumnIndex(FavoritesTable.COLUMN_ID));
        mIsFavoriteInitialized = true;
        mIsFavorite = true;
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

    synchronized public boolean getFavorite(Context context) {
        if (!mIsFavoriteInitialized) {
            Cursor cursor = context.getContentResolver().query(FavoritesTable.FAVORITES_URI, null, FavoritesTable.COLUMN_ID + "=" + getId(), null, null);
            //if at least one row - movie is favorite
            if (cursor != null) {
                mIsFavorite = cursor.getCount() > 0;
                cursor.close();
            }
            mIsFavoriteInitialized = true;
        }
        return mIsFavorite;
    }

    synchronized public boolean setFavorite(Context context, Boolean isFavorite) {
        boolean isFavoriteNow = getFavorite(context);
        if (isFavorite == isFavoriteNow) {
            return true;
        } else if (isFavorite) {
            Uri newUri = context.getContentResolver().insert(FavoritesTable.FAVORITES_URI, getContentValues());
            mIsFavorite = newUri != null;
            return newUri != null;
        } else { //no more favorite
            int rowsDeleted = context.getContentResolver().delete(getUri(), null, null);
            mIsFavorite = !(rowsDeleted > 0);
            return rowsDeleted > 0;
        }
    }

    private Uri getUri() {
        return FavoritesTable.FAVORITES_URI.buildUpon().appendPath(String.valueOf(getId())).build();
    }

    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FavoritesTable.COLUMN_ID, id);
        cv.put(FavoritesTable.COLUMN_ORIGINAL_TITLE, original_title);
        cv.put(FavoritesTable.COLUMN_POSTER_PATH, poster_path);
        cv.put(FavoritesTable.COLUMN_OVERVIEW, overview);
        cv.put(FavoritesTable.COLUMN_VOTE_AVERATE, vote_average);
        cv.put(FavoritesTable.COLUMN_RELEASE_DATE, release_date);
        return cv;
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
