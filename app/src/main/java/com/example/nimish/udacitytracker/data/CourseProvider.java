package com.example.nimish.udacitytracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by nimishsanghi on 29/07/16.
 */
public class CourseProvider extends ContentProvider {

    public static final String LOG_TAG = CourseProvider.class.getSimpleName();

    static final int COURSE = 100;
    static final int COURSE_WITH_ID = 200;
    static final int COURSE_WITH_COURSE_CODE = 300;
    static final int COURSE_FAVORITE = 400;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final String sCourseSelection =
            CourseContract.CourseEntry.TABLE_NAME + "." +
                    CourseContract.CourseEntry._ID + " = ?";
    private static final String sCourseSelectionCourseCode =
            CourseContract.CourseEntry.TABLE_NAME + "." +
                    CourseContract.CourseEntry.COLUMN_COURSE_CODE + " = ?";
    private static final String sCourseSelectionFavorite =
            CourseContract.CourseEntry.TABLE_NAME + "." +
                    CourseContract.CourseEntry.COLUMN_FAVORITE + " = ?";


    private CourseDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CourseContract.CONTENT_AUTHORITY;
        // "course"
        matcher.addURI(authority, CourseContract.PATH_COURSE, COURSE);
        // "course/#"
        matcher.addURI(authority, CourseContract.PATH_COURSE + "/#/", COURSE_WITH_ID);
        // "course/course_code/*"
        matcher.addURI(authority, CourseContract.PATH_COURSE + "/" + CourseContract.CourseEntry
                        .COLUMN_COURSE_CODE + "/*",
                COURSE_WITH_COURSE_CODE);
        // "course/favorite"
        matcher.addURI(authority, CourseContract.PATH_COURSE + "/" + CourseContract.CourseEntry
                        .COLUMN_FAVORITE,
                COURSE_FAVORITE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CourseDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COURSE:
                return CourseContract.CourseEntry.CONTENT_TYPE;
            case COURSE_WITH_ID:
                return CourseContract.CourseEntry.CONTENT_ITEM_TYPE;
            case COURSE_WITH_COURSE_CODE:
                return CourseContract.CourseEntry.CONTENT_ITEM_TYPE;
            case COURSE_FAVORITE:
                return CourseContract.CourseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        Log.d(LOG_TAG, "query uri called: " + uri);
        Log.d(LOG_TAG, "selection is: " + selection);
        Log.d(LOG_TAG, "slectionArgs: " + Arrays.toString(selectionArgs));

        switch (sUriMatcher.match(uri)) {
            //"course"
            case COURSE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CourseContract.CourseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "course/#"
            case COURSE_WITH_ID: {
                selectionArgs = new String[]{Long.toString(ContentUris.parseId(uri))};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CourseContract.CourseEntry.TABLE_NAME,
                        projection,
                        sCourseSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "course/course_code/*"
            case COURSE_WITH_COURSE_CODE: {
                selectionArgs = new String[]{CourseContract.CourseEntry.getCourseCodeFromUri(uri)};

                Log.d(LOG_TAG, "Selection Args now : " + Arrays.toString(selectionArgs));
                Log.d(LOG_TAG, "sCourseSelectionCourseCode: " + sCourseSelectionCourseCode);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        CourseContract.CourseEntry.TABLE_NAME,
                        projection,
                        sCourseSelectionCourseCode,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "course/course_code/*"
            case COURSE_FAVORITE: {
                // 1 means true i.e. course is favorite
                selectionArgs = new String[]{Long.toString(1)};

                Log.d(LOG_TAG, "Selection Args : " + Arrays.toString(selectionArgs));
                Log.d(LOG_TAG, "sCourseSelectionFavorite: " + sCourseSelectionFavorite);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        CourseContract.CourseEntry.TABLE_NAME,
                        projection,
                        sCourseSelectionFavorite,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        Log.d(LOG_TAG, "insert uri called: " + uri);
        switch (match) {
            case COURSE: {
                long _id = db.insert(CourseContract.CourseEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CourseContract.CourseEntry.buildCourseUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //TODO: Need to implement the logic to generate notification for new courses
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";

        switch (match) {
            case COURSE:
                rowsDeleted =
                        db.delete(CourseContract.CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//        if (rowsDeleted != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (null == selection) selection = "1";

        switch (match) {
            case COURSE:
                rowsUpdated =
                        db.update(CourseContract.CourseEntry.TABLE_NAME, values, selection,
                                selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//        if (rowsUpdated != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
        return rowsUpdated;
    }
}
