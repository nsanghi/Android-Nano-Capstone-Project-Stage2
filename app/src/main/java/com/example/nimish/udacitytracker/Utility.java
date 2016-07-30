package com.example.nimish.udacitytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.example.nimish.udacitytracker.sync.UdacitySyncAdapter;

/**
 * Created by nimishsanghi on 30/07/16.
 */
public class Utility {

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @SuppressWarnings("ResourceType")
    static public @UdacitySyncAdapter.CourseServerStatus
    int getServerStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_server_status_key), UdacitySyncAdapter.COURSE_SERVER_OK);
    }
}
