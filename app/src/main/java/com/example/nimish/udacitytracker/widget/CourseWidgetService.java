package com.example.nimish.udacitytracker.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.nimish.udacitytracker.MainActivityFragment;
import com.example.nimish.udacitytracker.R;
import com.example.nimish.udacitytracker.data.CourseContract;
import com.squareup.picasso.Picasso;


public class CourseWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CourseRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class CourseRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor mCursor;
    private Context mContext;

    public CourseRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    public void onCreate() {
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        if (mCursor != null) {
            mCursor.close();
        }
    }

    public int getCount() {
        return mCursor.getCount();
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        mCursor.moveToPosition(position);
        rv.setTextViewText(R.id.widget_item_text, mCursor.getString(
                MainActivityFragment.COL_COURSE_TITLE));
        String imageUrl = mCursor.getString(MainActivityFragment.COL_COURSE_IMAGE).trim();
        if (!imageUrl.isEmpty()) {
            try {
                Bitmap b = Picasso.with(mContext).load(imageUrl).get();
                rv.setImageViewBitmap(R.id.widget_item_image, b);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }


        Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(CourseContract.CourseEntry.CONTENT_URI,
                MainActivityFragment.COURSE_COLUMNS, null,
                null, null);
    }


}