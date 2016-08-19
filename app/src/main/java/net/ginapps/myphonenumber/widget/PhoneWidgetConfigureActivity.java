package net.ginapps.myphonenumber.widget;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.ginapps.myphonenumber.BuildConfig;
import net.ginapps.myphonenumber.PhoneData;
import net.ginapps.myphonenumber.R;
import net.ginapps.myphonenumber.WidgetController;

import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public abstract class PhoneWidgetConfigureActivity extends AppCompatActivity {

    private static final String sSetupWidgetEvent = "SetupWidget";

    private WidgetController mWidgetController;
    private TextView mPhoneNumber;
    private int mSelectedPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mWidgetController = new WidgetController(this);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                initLayout();
                sendStatistic();
            } else if (savedInstanceState == null) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            }
        }
    }

    private void sendStatistic() {
        if (BuildConfig.DEBUG) return;

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, sSetupWidgetEvent);
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        CustomEvent event = new CustomEvent(sSetupWidgetEvent);
        Answers.getInstance().logCustom(event);
    }

    private void initLayout() {
        if (mWidgetController.hasActiveSim()) {
            if (mWidgetController.getActiveSimsCount() > 1) {
                initLayoutWithActiveSimCard();
            } else {
                applyWidgetSettings();
            }
        } else {
            setContentView(R.layout.activity_widget_configure_no_active_sim);
        }
    }

    private void initLayoutWithActiveSimCard() {
        setContentView(R.layout.activity_widget_configure);
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        final List<PhoneData> phoneDataList = mWidgetController.getPhoneDataList();
        String[] spinnerData = new String[phoneDataList.size()];
        for (int i = 0; i < phoneDataList.size(); i++) {
            PhoneData phoneData = phoneDataList.get(i);
            spinnerData[i] = phoneData.getOperatorName();
        }

        Spinner spinner = (Spinner) findViewById(R.id.selectSimCard);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_phone, spinnerData);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                mPhoneNumber.setText(phoneDataList.get(mSelectedPosition).getPhoneNumber());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.applyWidgetSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyWidgetSettings();
            }
        });

    }

    private void applyWidgetSettings() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            mWidgetController.addWidgetForSimSlot(widgetId, mSelectedPosition);
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
            RemoteViews remoteViews = new RemoteViews(getPackageName(), getWidgetLayoutId());
            String number = mWidgetController.getPhoneDataList().get(mSelectedPosition).getPhoneNumber();
            remoteViews.setTextViewText(R.id.textView, number);
            PendingIntent pendingIntent = WidgetProvider.getCopyToClipboardPendingIntent(this, widgetId, number, getProviderClass());
            remoteViews.setOnClickPendingIntent(R.id.copyPhoneToClipBoard, pendingIntent);
            PendingIntent sharePhoneIntent = WidgetProvider.getSharePhonePendingIntent(PhoneWidgetConfigureActivity.this, number);
            remoteViews.setOnClickPendingIntent(R.id.sharePhone, sharePhoneIntent);
            widgetManager.updateAppWidget(widgetId, remoteViews);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initLayout();
        } else {
            finish();
        }
    }

    @LayoutRes
    protected abstract int getWidgetLayoutId();

    protected abstract Class<? extends  WidgetProvider> getProviderClass();

}
