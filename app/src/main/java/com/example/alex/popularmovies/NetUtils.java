package com.example.alex.popularmovies;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetUtils {

    public static boolean isConnected(AppCompatActivity activity, int constraintLayoutId, boolean showPositive, boolean showNegative) {
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        result = ni != null && ni.isConnected();
        if (!result && showNegative) {
            Snackbar sb = Snackbar.make(activity.findViewById(constraintLayoutId), R.string.no_internet_connection, Snackbar.LENGTH_LONG);
            sb.getView().setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            sb.show();
        }
        if (result && showPositive) {
            Snackbar sb = Snackbar.make(activity.findViewById(R.id.constraint_layout), R.string.internet_connection_restored, Snackbar.LENGTH_LONG);
            sb.getView().setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            sb.show();
        }
        return result;
    }

}
