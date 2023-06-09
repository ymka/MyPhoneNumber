package net.ginapps.myphonenumber.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import net.ginapps.myphonenumber.MainActivity;
import net.ginapps.myphonenumber.MyPhoneApplication;
import net.ginapps.myphonenumber.PermissionUtils;
import net.ginapps.myphonenumber.PhoneData;
import net.ginapps.myphonenumber.R;
import net.ginapps.myphonenumber.WidgetController;
import net.ginapps.myphonenumber.analytics.Analytics;

import timber.log.Timber;

/**
 * Created by Alexander Kondenko.
 */
public abstract class WidgetProvider extends AppWidgetProvider {

    private static final String sExtraPhoneNumber = "net.ginapps.myphonenumber.widget.WidgetProvider.ExtraPhoneNumber";
    private static final String sActionCopyToClipboard = "net.ginapps.myphonenumber.widget.WidgetProvider.ActionCopyToClipboard";

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
                    Analytics analytics = ((MyPhoneApplication) context.getApplicationContext()).getAnalytics();
                    analytics.sendWidgetStatistic(Analytics.sCopyToClipboard);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (!PermissionUtils.Companion.isPermissionsGranted(context)) {
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
                PendingIntent pendingIntent = getCopyToClipboardPendingIntent(context, widgetId, number, getProviderClass());
                remoteViews.setOnClickPendingIntent(R.id.copyPhoneToClipBoard, pendingIntent);
                PendingIntent sharePhoneIntent = getSharePhonePendingIntent(context, number);
                remoteViews.setOnClickPendingIntent(R.id.sharePhone, sharePhoneIntent);
            } else {
                remoteViews = new RemoteViews(context.getPackageName(), getLayoutDisabledWidgetId());
            }

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public static PendingIntent getCopyToClipboardPendingIntent(Context context, int widgetId, String line1Number, Class<? extends WidgetProvider> providerClass) {
        Intent intent = new Intent(context, providerClass);
        intent.setAction(sActionCopyToClipboard);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.putExtra(sExtraPhoneNumber, line1Number);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }

    public static PendingIntent getSharePhonePendingIntent(Context context, String phoneNumber) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_text_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, phoneNumber);
        sendIntent.setType("text/plain");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.getActivity(context, 0, Intent.createChooser(sendIntent, context.getString(R.string.share_text_label)), PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return PendingIntent.getActivity(context, 0, Intent.createChooser(sendIntent, context.getString(R.string.share_text_label)), PendingIntent.FLAG_UPDATE_CURRENT);
        }
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

    protected abstract Class<? extends WidgetProvider> getProviderClass();
}
