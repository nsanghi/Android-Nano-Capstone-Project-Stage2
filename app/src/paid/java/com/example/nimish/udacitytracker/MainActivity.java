package com.example.nimish.udacitytracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.nimish.udacitytracker.sync.UdacitySyncAdapter;

public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.course_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        } else {
            mTwoPane = false;
            //getSupportActionBar().setElevation(0f);
        }


        UdacitySyncAdapter.initializeSyncAdapter(this);
    }


}

    public boolean getPaneMode() {
        return this.mTwoPane;
    }


}
