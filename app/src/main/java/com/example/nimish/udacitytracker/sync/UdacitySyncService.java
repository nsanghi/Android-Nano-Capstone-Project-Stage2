package com.example.nimish.udacitytracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by nimishsanghi on 27/07/16.
 */
public class UdacitySyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static UdacitySyncAdapter sUdacitySyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - UdacitySyncService");
        synchronized (sSyncAdapterLock) {
            if (sUdacitySyncAdapter == null) {
                sUdacitySyncAdapter = new UdacitySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sUdacitySyncAdapter.getSyncAdapterBinder();
    }
}
