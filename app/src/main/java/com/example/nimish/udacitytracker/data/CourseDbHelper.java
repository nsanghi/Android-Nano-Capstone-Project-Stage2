package com.example.nimish.udacitytracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.nimish.udacitytracker.data.CourseContract.CourseEntry;

/**
 * Created by nimishsanghi on 29/07/16.
 */
public class CourseDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "course.db";

    public CourseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
        //Create table to hold favorite movies
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME  + " (" +
                CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CourseEntry.COLUMN_MOVIE_CODE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_RUNTIME + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_OVERVIEW + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);



        public static final String COLUMN_COURSE_CODE = "key";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_HOMEPAGE = "homepage";
        public static final String COLUMN_SUBTITLE = "subtitle";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_BANNER_IMAGE = "banner_image";
        public static final String COLUMN_TEASER_VIDEO = "teaser_video";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_SHORT_SUMMARY = "short_summary";
        public static final String COLUMN_REQUIRED_KNOWLEDGE = "required_knowledge";
        public static final String COLUMN_EXPECTED_LEARING = "expected_learning";
        public static final String COLUMN_EXPECTED_DURATION = "expected_duration";
        public static final String COLUMN_EXPECTED_DURATION_UNIT = "expected_duration_unit";
        public static final String COLUMN_NEW_RELEASE = "new_release";
        */

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        onCreate(db);

    }
}
