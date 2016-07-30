package com.example.nimish.udacitytracker.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.nimish.udacitytracker.MainActivity;
import com.example.nimish.udacitytracker.R;
import com.example.nimish.udacitytracker.data.CourseContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nimishsanghi on 27/07/16.
 */
public class UdacitySyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int COURSE_NOTIFICATION_ID = 3001;
    public static final int SYNC_INTERVAL_IN_SEC = 60 * 60 * 24; //one day
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL_IN_SEC / 3;
    public static final int COURSE_SERVER_OK = 0;
    public static final int COURSE_SERVER_DOWN = 1;
    public static final int COURSE_SERVER_INVALID = 2;
    public static final String LOG_TAG = UdacitySyncAdapter.class.getSimpleName();
    //private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final String[] NOTIFY_COURSE_PROJECTION = new String[]{
            CourseContract.CourseEntry.COLUMN_COURSE_CODE,
            CourseContract.CourseEntry.COLUMN_TITLE,
    };
    // these indices must match the projection
    private static final int INDEX_COURSE_CODE = 0;
    private static final int INDEX_TITLE = 1;

    public UdacitySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "Calling sync Immediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string
                .sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        UdacitySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL_IN_SEC, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string
                .content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Sets the server status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     *
     * @param c            Context to get the PreferenceManager from.
     * @param serverStatus The IntDef value to set
     */
    static private void setCourseServerStatus(Context c, @CourseServerStatus int serverStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_server_status_key), serverStatus);
        spe.commit();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient
            contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String courseJsonStr = null;

        try {
            final String COURSE_BASE_URL =
                    "https://www.udacity.com/public-api/v0/courses";
            URL url = new URL(COURSE_BASE_URL);

            // Create the request to Udacity, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                setCourseServerStatus(getContext(), COURSE_SERVER_DOWN);
                return;
            }
            courseJsonStr = buffer.toString();
            Log.d(LOG_TAG, "DataFetched:\n" + courseJsonStr);
            getCourseDataFromJson(courseJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            setCourseServerStatus(getContext(), COURSE_SERVER_DOWN);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            setCourseServerStatus(getContext(), COURSE_SERVER_INVALID);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;

    }


    private void getCourseDataFromJson(String courseJsonStr) throws JSONException {

        //TODO: Implement code to get data from udacity and use content provider to insert data

        Set<String> currentCourses = getCurrentCourses();

        final String COURSE_CODE = "key";
        final String TITLE = "title";
        final String HOMEPAGE = "homepage";
        final String SUBTITLE = "subtitle";
        final String LEVEL = "level";
        final String IMAGE = "image";
        final String BANNER_IMAGE = "banner_image";
        final String TEASER_VIDEO = "teaser_video";
        final String YOUTUBE_URL = "youtube_url";
        final String SUMMARY = "summary";
        final String SHORT_SUMMARY = "short_summary";
        final String REQUIRED_KNOWLEDGE = "required_knowledge";
        final String EXPECTED_LEARING = "expected_learning";
        final String EXPECTED_DURATION = "expected_duration";
        final String EXPECTED_DURATION_UNIT = "expected_duration_unit";
        final String NEW_RELEASE = "new_release";

        final String COURSE_LIST = "courses";
        final String STATUS = "status";

        try {

            JSONObject courseJson = new JSONObject(courseJsonStr);
            // do we have an error?
            if (courseJson.has(STATUS)) {
                int errorCode = courseJson.getInt(STATUS);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        setCourseServerStatus(getContext(), COURSE_SERVER_INVALID);
                        return;
                    default:
                        setCourseServerStatus(getContext(), COURSE_SERVER_DOWN);
                        return;
                }
            }


            JSONArray courseArray = courseJson.getJSONArray(COURSE_LIST);

            String courseCode;
            String title;
            String homepage;
            String subtitle;
            String level;
            String image;
            String bannerImage;
            String teaserVideo;
            String summary;
            String shortSummary;
            String requiredKnowledge;
            String expectedLearning;
            String expectedDuration;
            String expectedDurationUnit;
            String newRelease;

            for (int i = 0; i < courseArray.length(); i++) {

                JSONObject course = courseArray.getJSONObject(i);

                courseCode = course.getString(COURSE_CODE);
                title = course.getString(TITLE);
                homepage = course.getString(HOMEPAGE);
                subtitle = course.getString(SUBTITLE);
                level = course.getString(LEVEL);
                image = course.getString(IMAGE);
                bannerImage = course.getString(BANNER_IMAGE);
                teaserVideo = course.getJSONObject(TEASER_VIDEO).getString(YOUTUBE_URL);
                summary = course.getString(SUMMARY);
                shortSummary = course.getString(SHORT_SUMMARY);
                requiredKnowledge = course.getString(REQUIRED_KNOWLEDGE);
                expectedLearning = course.getString(EXPECTED_LEARING);
                expectedDuration = course.getString(EXPECTED_DURATION);
                expectedDurationUnit = course.getString(EXPECTED_DURATION_UNIT);
                newRelease = course.getString(NEW_RELEASE);


                if (!currentCourses.contains(courseCode)) {
                    ContentValues values = new ContentValues();
                    values.put(CourseContract.CourseEntry.COLUMN_COURSE_CODE, courseCode);
                    values.put(CourseContract.CourseEntry.COLUMN_TITLE, title);
                    values.put(CourseContract.CourseEntry.COLUMN_HOMEPAGE, homepage);
                    values.put(CourseContract.CourseEntry.COLUMN_SUBTITLE, subtitle);
                    values.put(CourseContract.CourseEntry.COLUMN_LEVEL, level);
                    values.put(CourseContract.CourseEntry.COLUMN_IMAGE, image);
                    values.put(CourseContract.CourseEntry.COLUMN_BANNER_IMAGE, bannerImage);
                    values.put(CourseContract.CourseEntry.COLUMN_TEASER_VIDEO, teaserVideo);
                    values.put(CourseContract.CourseEntry.COLUMN_SUMMARY, summary);
                    values.put(CourseContract.CourseEntry.COLUMN_SHORT_SUMMARY, shortSummary);
                    values.put(CourseContract.CourseEntry.COLUMN_REQUIRED_KNOWLEDGE,
                            requiredKnowledge);
                    values.put(CourseContract.CourseEntry.COLUMN_EXPECTED_LEARING,
                            expectedLearning);
                    values.put(CourseContract.CourseEntry.COLUMN_EXPECTED_DURATION,
                            expectedDuration);
                    values.put(CourseContract.CourseEntry.COLUMN_EXPECTED_DURATION_UNIT,
                            expectedDurationUnit);
                    values.put(CourseContract.CourseEntry.COLUMN_NEW_RELEASE, newRelease);
                    values.put(CourseContract.CourseEntry.COLUMN_FAVORITE, 0); //0 == not favorite
                    Uri returnUri = getContext().getContentResolver().insert(CourseContract
                            .CourseEntry.CONTENT_URI, values);
                    Log.d(LOG_TAG, "insert into Course returned with Uri: " + returnUri);

                    notifyCourse(title);


                }

            }

            // Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
            setCourseServerStatus(getContext(), COURSE_SERVER_OK);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            setCourseServerStatus(getContext(), COURSE_SERVER_INVALID);
        }


    }

    private void notifyCourse(String title) {

        Context context = getContext();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(android.R.drawable.alert_dark_frame)
                        .setContentText(title)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(COURSE_NOTIFICATION_ID, mBuilder.build());

    }

    private Set<String> getCurrentCourses() {

        Set<String> currentCourses = new HashSet<>();

        Cursor cursor = getContext().getContentResolver().query(CourseContract.CourseEntry
                .CONTENT_URI, NOTIFY_COURSE_PROJECTION, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            currentCourses.add(cursor.getString(INDEX_COURSE_CODE));
        }

        Log.d(LOG_TAG, "getCurrentCourses returned:" + currentCourses);
        return currentCourses;

    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COURSE_SERVER_OK, COURSE_SERVER_DOWN, COURSE_SERVER_INVALID})
    public @interface CourseServerStatus {
    }


}
