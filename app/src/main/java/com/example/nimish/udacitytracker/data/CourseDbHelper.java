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


        //Create table to hold favorite movies
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME + " (" +
                CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CourseEntry.COLUMN_COURSE_CODE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_HOMEPAGE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_SUBTITLE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_LEVEL + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_BANNER_IMAGE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_TEASER_VIDEO + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_SUMMARY + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_SHORT_SUMMARY + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_REQUIRED_KNOWLEDGE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_EXPECTED_LEARING + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_EXPECTED_DURATION + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_EXPECTED_DURATION_UNIT + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_NEW_RELEASE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_FAVORITE + " BOOLEAN NOT NULL CHECK (" +
                        CourseEntry.COLUMN_FAVORITE + " IN (0,1))" +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        onCreate(db);

    }
}
