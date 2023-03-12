package net.ginapps.myphonenumber;


import android.app.Application;

import net.ginapps.myphonenumber.analytics.Analytics;

import timber.log.Timber;

public class MyPhoneApplication extends Application {

    private Analytics analytics;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }

    public Analytics getAnalytics() {
        if (analytics == null) {
            analytics = new Analytics(this);
        }

        return analytics;
    }

}
