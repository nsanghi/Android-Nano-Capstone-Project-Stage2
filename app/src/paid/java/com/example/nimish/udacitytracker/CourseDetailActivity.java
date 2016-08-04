package com.example.nimish.udacitytracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.nimish.udacitytracker.data.Course;
import com.example.nimish.udacitytracker.data.CourseContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * An activity representing a single Course detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class CourseDetailActivity extends AppCompatActivity {

    public static final String LOG_TAG = CourseDetailActivity.class.getSimpleName();
    FloatingActionButton mFab;
    Course mCourse;
    private static final int FAVORITE = 1;
    private static final int NOT_FAVORITE = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FOr Google Analytics - initialize
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mCourse = getIntent().getParcelableExtra(CourseDetailFragment.ARG_COURSE);
        Log.d(LOG_TAG, "course:" + mCourse);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        if (mCourse != null) {
            setFabColor(mCourse.getFavorite());
        } else {
            setFabColor(NOT_FAVORITE);
        }


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String where =
                        CourseContract.CourseEntry.TABLE_NAME + "." +
                                CourseContract.CourseEntry._ID + " = ?";
                String[] selectionArgs = new String[]{Long.toString(mCourse.getId())};
                Uri courseUri = CourseContract.CourseEntry.CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(CourseContract.CourseEntry.COLUMN_FAVORITE, 1 - mCourse.getFavorite());
                int rowsUpdated = getContentResolver().update(courseUri, values, where,
                        selectionArgs);
                if (rowsUpdated > 0) {
                    mCourse.setFavorite(1 - mCourse.getFavorite());
                    setFabColor(mCourse.getFavorite());
                }

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mCourse.getCourseCode());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mCourse.getTitle());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "course");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle);
            }
        });


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(CourseDetailFragment.ARG_COURSE,
                    getIntent().getParcelableExtra(CourseDetailFragment.ARG_COURSE));
            CourseDetailFragment fragment = new CourseDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.course_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFabColor(int favorite) {
        if (favorite == FAVORITE) {
            mFab.setColorFilter(ContextCompat.getColor(this, R.color.pink));
        } else {
            mFab.setColorFilter(ContextCompat.getColor(this, R.color.white));
        }

    }

}
