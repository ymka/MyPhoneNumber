package net.ginapps.myphonenumber;


import android.app.Application;

import com.crashlytics.android.Crashlytics;

import net.ginapps.myphonenumber.analytics.Analytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MyPhoneApplication extends Application{

    private Analytics analytics;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }

    public Analytics getAnalytics() {
        if (analytics == null) {
            analytics = new Analytics(this);
        }

        return analytics;
    }

}
