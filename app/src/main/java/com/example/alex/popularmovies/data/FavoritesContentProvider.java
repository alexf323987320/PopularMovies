package com.example.alex.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alex.popularmovies.data.FavoritesContract.FavoritesTable;

public class FavoritesContentProvider extends ContentProvider {

    private FavoritesOpenHelper mFavoritesOpenHelper;
    private UriMatcher mUriMatcher;
    private final int CODE_FAVORITES = 0;
    private final int CODE_FAVORITES_ID = 1;

    @Override
    public boolean onCreate() {
        mFavoritesOpenHelper = new FavoritesOpenHelper(getContext());
        initializeUriMatcher();
        return true;
    }

    private void initializeUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesTable.TABLE_PATH, CODE_FAVORITES);
        mUriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesTable.TABLE_PATH + "/#", CODE_FAVORITES_ID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mFavoritesOpenHelper.getReadableDatabase();
        switch (mUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                Cursor cursor = db.query(FavoritesTable.TABLE_NAME, projection, selection, selectionArgs, "", "", sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mFavoritesOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                long rowId = db.insert(FavoritesTable.TABLE_NAME, "", values);
                if (rowId == -1) {
                    return null;
                } else {
                    Uri newUri = FavoritesTable.FAVORITES_URI.buildUpon().appendPath(values.getAsString(FavoritesTable.COLUMN_ID)).build();
                    getContext().getContentResolver().notifyChange(uri, null);
                    return  newUri;
                }
            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mFavoritesOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case CODE_FAVORITES_ID:
                String id = uri.getLastPathSegment();
                int rowsDeleted = db.delete(FavoritesTable.TABLE_NAME, FavoritesTable.COLUMN_ID + "=" + id, null);
                getContext().getContentResolver().notifyChange(FavoritesTable.FAVORITES_URI, null);
                return rowsDeleted;
            default:
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


}
