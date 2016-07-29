package com.example.nimish.udacitytracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by nimishsanghi on 27/07/16.
 */
public class UdacityAuthenticatorService extends Service {

    private UdacityAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new UdacityAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
