package com.example.alex.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavoritesOpenHelper extends SQLiteOpenHelper {

    public FavoritesOpenHelper(Context context) {
        super(context, FavoritesContract.DB_FILE_NAME, null, FavoritesContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FavoritesContract.FavoritesTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FavoritesContract.FavoritesTable.DROP_TABLE);
        db.execSQL(FavoritesContract.FavoritesTable.CREATE_TABLE);
    }
}
