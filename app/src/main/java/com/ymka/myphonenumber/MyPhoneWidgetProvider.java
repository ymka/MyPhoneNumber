package com.ymka.myphonenumber;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by Alexander Kondenko.
 */
public class MyPhoneWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_TOAST = "com.ymka.myphonenumber.MyPhoneWidgetProvider.ACTION_TOAST";
    public static final String EXTRA_PHONE_NUMBER = "com.ymka.myphonenumber.MyPhoneWidgetProvider.EXTRA_PHONE_NUMBER";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_TOAST)) {
            String phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(context.getString(R.string.clip_label), phoneNumber);
            clipboard.setPrimaryClip(clipData);
            Toast.makeText(context, R.string.phone_copy_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            String line1Number = telephonyManager.getLine1Number();
            remoteViews.setTextViewText(R.id.textView, line1Number);
            Intent intent = new Intent(context, MyPhoneWidgetProvider.class);
            intent.setAction(ACTION_TOAST);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.putExtra(EXTRA_PHONE_NUMBER, line1Number);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.copyPhoneToClipBoard, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

}
