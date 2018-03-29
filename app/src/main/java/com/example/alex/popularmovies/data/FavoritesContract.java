package com.example.alex.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final int DB_VERSION = 1;
    public static final String DB_FILE_NAME = "database.db";
    public static final String CONTENT_AUTHORITY = "com.example.alex.popularmovies";

    public static class FavoritesTable implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERATE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ID = "id";
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(\n" +
                _ID +                       " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                COLUMN_ORIGINAL_TITLE +     " STRING,\n" +
                COLUMN_POSTER_PATH +        " STRING,\n" +
                COLUMN_OVERVIEW +           " STRING,\n" +
                COLUMN_VOTE_AVERATE +       " DECIMAL,\n" +
                COLUMN_RELEASE_DATE +       " STRING,\n" +
                COLUMN_ID +                 " INTEGER UNIQUE NOT NULL\n" +
                ");";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String TABLE_PATH = "favorites";
        public static final Uri FAVORITES_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/" + TABLE_PATH);

    }
}
