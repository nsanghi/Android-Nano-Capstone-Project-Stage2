package com.example.nimish.udacitytracker;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nimish.udacitytracker.data.CourseContract;
import com.example.nimish.udacitytracker.sync.UdacitySyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager
        .LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {


    public static final int COL_COURSE_ID = 0;
    public static final int COL_COURSE_CODE = 1;
    public static final int COL_COURSE_TITLE = 2;
    public static final int COL_COURSE_HOMEPAGE = 3;
    public static final int COL_COURSE_SUBTITLE = 4;
    public static final int COL_COURSE_LEVEL = 5;
    public static final int COL_COURSE_IMAGE = 6;
    public static final int COL_COURSE_BANNER_IMAGE = 7;
    public static final int COL_COURSE_TEASER_VIDEO = 8;
    public static final int COL_COURSE_SUMMARY = 9;
    public static final int COL_COURSE_SHORT_SUMMARY = 10;
    public static final int COL_COURSE_REQUIRED_KNOWLEDGE = 11;
    public static final int COL_COURSE_EXPECTED_LEARING = 12;
    public static final int COL_COURSE_EXPECTED_DURATION = 13;
    public static final int COL_COURSE_EXPECTED_DURATION_UNIT = 14;
    public static final int COL_COURSE_NEW_RELEASE = 15;
    public static final int COL_COURSE_FAVORITE = 16;

    public static final String[] COURSE_COLUMNS = {
            CourseContract.CourseEntry._ID,
            CourseContract.CourseEntry.COLUMN_COURSE_CODE,
            CourseContract.CourseEntry.COLUMN_TITLE,
            CourseContract.CourseEntry.COLUMN_HOMEPAGE,
            CourseContract.CourseEntry.COLUMN_SUBTITLE,
            CourseContract.CourseEntry.COLUMN_LEVEL,
            CourseContract.CourseEntry.COLUMN_IMAGE,
            CourseContract.CourseEntry.COLUMN_BANNER_IMAGE,
            CourseContract.CourseEntry.COLUMN_TEASER_VIDEO,
            CourseContract.CourseEntry.COLUMN_SUMMARY,
            CourseContract.CourseEntry.COLUMN_SHORT_SUMMARY,
            CourseContract.CourseEntry.COLUMN_REQUIRED_KNOWLEDGE,
            CourseContract.CourseEntry.COLUMN_EXPECTED_LEARING,
            CourseContract.CourseEntry.COLUMN_EXPECTED_DURATION,
            CourseContract.CourseEntry.COLUMN_EXPECTED_DURATION_UNIT,
            CourseContract.CourseEntry.COLUMN_NEW_RELEASE,
            CourseContract.CourseEntry.COLUMN_FAVORITE
    };


    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";
    private static final int COURSE_LOADER = 0;
    private static final int COURSE_LOADER_FAVORITE = 1;
    private static final int COURSE_LOADER_SEARCH = 2;

    private CourseAdapter mCourseAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private boolean mFavorites = false;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_course);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        mCourseAdapter = new CourseAdapter(getActivity(), ((MainActivity) getActivity())
                .getPaneMode());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mRecyclerView.setAdapter(mCourseAdapter);

        getLoaderManager().initLoader(COURSE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        final MenuItem favoriteItem = menu.findItem(R.id.action_favorite);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat
                .OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getLoaderManager().restartLoader(COURSE_LOADER, null, MainActivityFragment.this);
                favoriteItem.setTitle(getString(R.string.action_favorite));
                mFavorites = false;
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            UdacitySyncAdapter.syncImmediately(getActivity());
            return true;
        } else if (id == R.id.action_favorite) {
            if (!mFavorites) {
                getLoaderManager().restartLoader(COURSE_LOADER_FAVORITE, null, this);
                item.setTitle(getString(R.string.action_all_courses));
            } else {
                getLoaderManager().restartLoader(COURSE_LOADER, null, this);
                item.setTitle(getString(R.string.action_favorite));
            }
            mFavorites = !mFavorites;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri courseUri;
        switch (id) {
            case COURSE_LOADER: {
                courseUri = CourseContract.CourseEntry.CONTENT_URI;
                return new CursorLoader(getActivity(), courseUri, COURSE_COLUMNS, null, null, null);
            }
            case COURSE_LOADER_FAVORITE: {
                courseUri = CourseContract.CourseEntry.favoriteCoursesUri();
                return new CursorLoader(getActivity(), courseUri, COURSE_COLUMNS, null, null, null);
            }
            case COURSE_LOADER_SEARCH: {

                String selection =
                        CourseContract.CourseEntry.TABLE_NAME + "." +
                                CourseContract.CourseEntry.COLUMN_TITLE + " LIKE ?";
                String[] selectionArgs = new String[]{"%" + args.getString("query") + "%"};
                courseUri = CourseContract.CourseEntry.CONTENT_URI;
                return new CursorLoader(getActivity(), courseUri, COURSE_COLUMNS, selection,
                        selectionArgs, null);

            }

            default:
                courseUri = CourseContract.CourseEntry.CONTENT_URI;
                return new CursorLoader(getActivity(), courseUri, COURSE_COLUMNS, null, null, null);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        Log.d(LOG_TAG, "queryText:" + query);
        Bundle args = new Bundle();
        args.putCharSequence("query", query);
        getLoaderManager().restartLoader(COURSE_LOADER_SEARCH, args, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(LOG_TAG, "queryText:" + newText);
        Bundle args = new Bundle();
        args.putCharSequence("query", newText);
        getLoaderManager().restartLoader(COURSE_LOADER_SEARCH, args, this);
        return true;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCourseAdapter.swapCursor(data);
        updateEmptyView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCourseAdapter.swapCursor(null);

    }

    private void updateEmptyView() {
        TextView tv = (TextView) getView().findViewById(R.id.recyclerview_course_empty);
        if (mCourseAdapter.getItemCount() == 0) {
            if (null != tv) {
                // if cursor is empty, why?
                int message = R.string.empty_course_list;
                @UdacitySyncAdapter.CourseServerStatus int status = Utility.getServerStatus
                        (getActivity());
                switch (status) {
                    case UdacitySyncAdapter.COURSE_SERVER_DOWN:
                        message = R.string.empty_course_list_server_down;
                        break;
                    case UdacitySyncAdapter.COURSE_SERVER_INVALID:
                        message = R.string.empty_course_list_server_error;
                        break;
                    default:
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_course_list_no_network;
                        }
                }
                tv.setText(message);
            }
        } else {
            if (null != tv) {
                tv.setText("");
            }

        }
    }

}
