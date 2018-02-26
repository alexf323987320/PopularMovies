package com.example.alex.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private final static String SHARED_PREFERENCES_NAME = Prefs.class.getName();
    private final static String KEY_SORT_ORDER = "sort_order";
    private final static int DEFAULT_SORTING = MovieDb.SORT_BY_POPULAR;

    //Not for initialization
    private Prefs() {}

    public static int getSortOrder(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_SORT_ORDER, DEFAULT_SORTING);
    }

    public static void setSortOrder(Context context, int sortOrder) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_SORT_ORDER, sortOrder);
        editor.apply();
    }
}