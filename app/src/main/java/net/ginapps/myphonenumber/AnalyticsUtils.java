package net.ginapps.myphonenumber;

import android.os.Bundle;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Alexander Kondenko
 */
public class AnalyticsUtils {

    public static final String sShareEvent = "Share";
    public static final String sCopyToClipboard = "Copy to clipboard";

    private static final String sApplicationEvent = "Application";
    private static final String sWidgetEvent = "Widget";
    private static final String sEventName = "EVENT_NAME";

    public static void sendApplicationStatistic(FirebaseAnalytics analytics, String name) {
        if (BuildConfig.DEBUG) return;

        sendStatistic(analytics, sApplicationEvent, name);
    }

    public static void sendWidgetStatistic(FirebaseAnalytics analytics, String name) {
        if (BuildConfig.DEBUG) return;

        sendStatistic(analytics, sWidgetEvent, name);
    }

    public static void sendStatistic(FirebaseAnalytics analytics, String eventId, String name) {
        if (BuildConfig.DEBUG) return;

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, eventId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        CustomEvent event = new CustomEvent(eventId);
        event.putCustomAttribute(sEventName, name);
        Answers.getInstance().logCustom(event);
    }

}
