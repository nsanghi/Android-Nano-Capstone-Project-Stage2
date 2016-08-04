package com.example.nimish.udacitytracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nimish.udacitytracker.data.Course;
import com.example.nimish.udacitytracker.data.CourseContract;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


/**
 * A fragment representing a single Course detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link CourseDetailActivity}
 * on handsets.
 */
public class CourseDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_COURSE = "course";

    /**
     * The dummy content this fragment is presenting.
     */
    private Course mCourse;
    private ImageView mBackdropImageView;
    private CollapsingToolbarLayout mAppBarLayout;
    private FloatingActionButton mFab;
    private static final int FAVORITE = 1;
    private static final int NOT_FAVORITE = 0;
    private FirebaseAnalytics mFirebaseAnalytics;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_COURSE)) {
            mCourse = getArguments().getParcelable(ARG_COURSE);


        }
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        // FOr Google Analytics - initialize
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.course_detail, container, false);

        ImageView courseImageView = (ImageView) rootView.findViewById(R.id.course_image);
        //Show the backdrop image
        String imageUri = mCourse != null ? mCourse.getImage().trim() : "";
        if (courseImageView != null && !imageUri.isEmpty()) {
            Picasso.with(getActivity()).load(imageUri).into(courseImageView);
        }


        TextView reqdKnowledgeTextView = (TextView) rootView.findViewById(R.id.required_knowledge);
        reqdKnowledgeTextView.setText(Utility.formatText(mCourse.getRequiredKnowledge()));
        reqdKnowledgeTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title);
        titleTextView.setText(mCourse.getTitle());

        TextView subTitleTextView = (TextView) rootView.findViewById(R.id.sub_title);
        subTitleTextView.setText(mCourse.getSubtitle());

        TextView durationTextView = (TextView) rootView.findViewById(R.id.expected_duration);
        durationTextView.setText(mCourse.getExpectedDuration() + " " + mCourse
                .getExpectedDurationUnit());


        String level = mCourse.getLevel();
        if (level.length() > 0) {

            level = level.substring(0, 1).toUpperCase() + level.substring(1);
            //level = level.toUpperCase();
        }
        TextView levelTextVIew = (TextView) rootView.findViewById(R.id.level);
        levelTextVIew.setText(level);

        TextView shortSummaryTextView = (TextView) rootView.findViewById(R.id.short_summary);
        shortSummaryTextView.setText(mCourse.getShortSummary());

        TextView summaryTextView = (TextView) rootView.findViewById(R.id.summary);
        summaryTextView.setText(Utility.formatText(mCourse.getSummary()));
        summaryTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView expectedLearningTextView = (TextView) rootView.findViewById(R.id
                .expected_learning);
        expectedLearningTextView.setText(Utility.formatText(mCourse.getExpectedLearning()));
        expectedLearningTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView rqdKnowledgeTextView = (TextView) rootView.findViewById(R.id.required_knowledge);
        rqdKnowledgeTextView.setText(Utility.formatText(mCourse.getRequiredKnowledge()));
        rqdKnowledgeTextView.setMovementMethod(LinkMovementMethod.getInstance());

        ImageButton teaserVideoImageBtn = (ImageButton) rootView.findViewById(R.id.teaser_video);
        if (!mCourse.getTeaserVideo().trim().isEmpty()) {
            teaserVideoImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mCourse.getTeaserVideo().trim()));
                    startActivity(intent);
                }
            });
            teaserVideoImageBtn.setColorFilter(Color.argb(255, 0, 0, 0));
        } else {
            teaserVideoImageBtn.setColorFilter(Color.argb(255, 220, 220, 220));
        }


        ImageButton homepageImageBtn = (ImageButton) rootView.findViewById(R.id.homepage);
        if (!mCourse.getHomepage().trim().isEmpty()) {
            homepageImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mCourse.getHomepage().trim()));
                    startActivity(intent);
                }
            });
            homepageImageBtn.setColorFilter(Color.argb(255, 0, 0, 0));
        } else {
            homepageImageBtn.setColorFilter(Color.argb(255, 220, 220, 220));
        }


        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab_fragment);


        if (mFab != null) {

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
                    values.put(CourseContract.CourseEntry.COLUMN_FAVORITE, 1 - mCourse
                            .getFavorite());
                    int rowsUpdated = getActivity().getContentResolver().update(courseUri,
                            values, where, selectionArgs);
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
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = this.getActivity();
        mBackdropImageView = (ImageView) activity.findViewById(R.id.backdrop);
        mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        //Show the backdrop image
        String imageUri = mCourse != null ? mCourse.getImage().trim() : "";
        if (mBackdropImageView != null && !imageUri.isEmpty()) {
            Picasso.with(getActivity()).load(imageUri).into(mBackdropImageView);
        }

        //SHow the Course Title in App Bar
        if (mAppBarLayout != null) {
            mAppBarLayout.setTitle(mCourse.getTitle());
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_course_details, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reminder) {

            Calendar beginTime = Calendar.getInstance();
            beginTime.add(Calendar.DATE, 1);
            Calendar endTime = Calendar.getInstance();
            endTime.add(Calendar.DATE, 1);
            endTime.add(Calendar.MINUTE, 30);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, getString(R.string.event_title))
                    .putExtra(CalendarContract.Events.DESCRIPTION, mCourse.getTitle())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, getString(R.string
                            .event_location))
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events
                            .AVAILABILITY_BUSY);
            startActivity(intent);


        }

        return super.onOptionsItemSelected(item);
    }

    private void setFabColor(int favorite) {
        if (favorite == FAVORITE) {
            mFab.setColorFilter(ContextCompat.getColor(getActivity(), R.color.pink));
        } else {
            mFab.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white));
        }

    }


}
