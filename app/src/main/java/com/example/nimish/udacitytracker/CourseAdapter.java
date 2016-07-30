package com.example.nimish.udacitytracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by nimishsanghi on 30/07/16.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseAdapterViewHolder> {

    final private Context mContext;
    private Cursor mCursor;

    public CourseAdapter(Context context) {
        mContext = context;
    }

    @Override
    public CourseAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_course,
                parent, false);
        view.setFocusable(true);
        return new CourseAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mTitleView.setText(mCursor.getString(MainActivityFragment.COL_COURSE_TITLE));
        holder.mShortSummaryView.setText(mCursor.getString(MainActivityFragment
                .COL_COURSE_SHORT_SUMMARY));
        String level = mCursor.getString(MainActivityFragment.COL_COURSE_LEVEL);
        if (level.length()>0)
            level = level.substring(0,1).toUpperCase() + level.substring(1);
        holder.mLevelView.setText(level);
        if (mCursor.getString(MainActivityFragment.COL_COURSE_NEW_RELESE).equalsIgnoreCase
                ("true")) {
            holder.mNewReleaseView.setVisibility(View.VISIBLE);
        } else {
            holder.mNewReleaseView.setVisibility(View.INVISIBLE);
        }

        String imageUri = mCursor.getString(MainActivityFragment.COL_COURSE_IMAGE);
        if (!imageUri.trim().isEmpty())
            Picasso.with(mContext).load(imageUri).into(holder.mIconView);


    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public class CourseAdapterViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIconView;
        public TextView mTitleView;
        public TextView mLevelView;
        public TextView mNewReleaseView;
        public TextView mShortSummaryView;

        public CourseAdapterViewHolder(View view) {

            super(view);
            mIconView = (ImageView) view.findViewById(R.id.course_image);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mLevelView = (TextView) view.findViewById(R.id.level);
            mNewReleaseView = (TextView) view.findViewById(R.id.new_release);
            mShortSummaryView = (TextView) view.findViewById(R.id.short_summary);
        }

    }
}
