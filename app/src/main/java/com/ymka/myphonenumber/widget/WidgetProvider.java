package com.ymka.myphonenumber.widget;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ymka.myphonenumber.PhoneData;
import com.ymka.myphonenumber.R;
import com.ymka.myphonenumber.WidgetController;

import timber.log.Timber;

/**
 * Created by Alexander Kondenko.
 */
public abstract class WidgetProvider extends AppWidgetProvider {

    private static final String sExtraPhoneNumber = "com.ymka.myphonenumber.widget.WidgetProvider.ExtraPhoneNumber";
    private static final String sActionCopyToClipboard = "com.ymka.myphonenumber.widget.WidgetProvider.ActionCopyToClipboard";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case sActionCopyToClipboard:
                    String phoneNumber = intent.getStringExtra(sExtraPhoneNumber);
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(context.getString(R.string.clip_label), phoneNumber);
                    clipboard.setPrimaryClip(clipData);
                    Toast.makeText(context, R.string.phone_copy_toast, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final int count = appWidgetIds.length;
        WidgetController widgetController = new WidgetController(context);
        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            PhoneData phoneData = widgetController.getPhoneDataByWidgetId(widgetId);
            RemoteViews remoteViews;
            boolean isPhoneDataPresent = phoneData != null;
            Timber.d("Phone data is present %s for widget", isPhoneDataPresent);
            if (isPhoneDataPresent) {
                remoteViews = new RemoteViews(context.getPackageName(), getLayoutWidgetId());
                String number = phoneData.getPhoneNumber();
                remoteViews.setTextViewText(R.id.textView, number);
                PendingIntent pendingIntent = getCopyToClipboardPendingIntent(context, widgetId, number);
                remoteViews.setOnClickPendingIntent(R.id.copyPhoneToClipBoard, pendingIntent);
                PendingIntent sharePhoneIntent = getSharePhonePendingIntent(context, number);
                remoteViews.setOnClickPendingIntent(R.id.sharePhone, sharePhoneIntent);
            } else {
                remoteViews = new RemoteViews(context.getPackageName(), getLayoutDisabledWidgetId());
            }

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public static PendingIntent getCopyToClipboardPendingIntent(Context context, int widgetId, String line1Number) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(sActionCopyToClipboard);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.putExtra(sExtraPhoneNumber, line1Number);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getSharePhonePendingIntent(Context context, String phoneNumber) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_text_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, phoneNumber);
        sendIntent.setType("text/plain");

        return PendingIntent.getActivity(context, 0, Intent.createChooser(sendIntent, context.getString(R.string.share_text_label)), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        WidgetController widgetController = new WidgetController(context);
        widgetController.removeWidget(appWidgetIds[0]);
    }

    @LayoutRes
    protected abstract int getLayoutWidgetId();

    @LayoutRes
    protected abstract int getLayoutDisabledWidgetId();
}
