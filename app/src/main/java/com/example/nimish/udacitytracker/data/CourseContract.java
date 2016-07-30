package com.example.nimish.udacitytracker.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nimishsanghi on 28/07/16.
 */
public class CourseContract {
    public static final String CONTENT_AUTHORITY = "com.example.nimish.udacitytracker.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COURSE = "course";

    public static final class CourseEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_COURSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_COURSE;

        public static final String TABLE_NAME = "course";

        /*
        key, title, homepage, subtitle, level, image, banner_image, teaser_video, summary, short_summary, required_knowledge, expected_learning,
        expected_duration, expected_duration_unit, new_release
         */

        public static final String COLUMN_COURSE_CODE = "course_code";
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
        public static final String COLUMN_FAVORITE = "favorite";

        public static Uri buildCourseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri favoriteCoursesUri() {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_FAVORITE).build();
        }

        public static String getCourseCodeFromUri(Uri uri) {
            return uri.getPathSegments().get(2);  // 0 will be "course" table name
                                                                // 1 will be "course_code"
                                                                // 2 will be the actual course code
        }

        public static Uri buildCourseUriFromCourseCode(String courseCode) {
            return CourseEntry.CONTENT_URI.buildUpon().appendPath(COLUMN_COURSE_CODE)
                    .appendPath(courseCode).build();
        }


    }
}
