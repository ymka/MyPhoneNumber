package com.ymka.myphonenumber;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public class MyPhoneWidgetConfigureActivity extends AppCompatActivity {

    private WidgetController mWidgetController;
    private TextView mPhoneNumber;
    private List<PhoneData> mPhoneDataList;
    private int mSelectedPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mWidgetController = new WidgetController(this);
            if (mWidgetController.hasActiveSim()) {
                initLayoutWithActiveSimCard(extras);
            } else {
                setContentView(R.layout.activity_widget_configure_no_active_sim);
            }
        }
    }

    private void initLayoutWithActiveSimCard(Bundle extras) {
        setContentView(R.layout.activity_widget_configure);
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        mPhoneDataList = mWidgetController.getPhoneDataList();
        String[] spinnerData = new String[mPhoneDataList.size()];
        for (int i = 0; i < mPhoneDataList.size(); i++) {
            PhoneData phoneData = mPhoneDataList.get(i);
            spinnerData[i] = phoneData.getOperatorName();
        }

        Spinner spinner = (Spinner) findViewById(R.id.selectSimCard);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spinnerData);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                mPhoneNumber.setText(mPhoneDataList.get(mSelectedPosition).getPhoneNumber());
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
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_main);
            String number = mPhoneDataList.get(mSelectedPosition).getPhoneNumber();
            remoteViews.setTextViewText(R.id.textView, number);
            PendingIntent pendingIntent = MyPhoneWidgetProvider.getPendingIntent(this, widgetId, number);
            remoteViews.setOnClickPendingIntent(R.id.copyPhoneToClipBoard, pendingIntent);
            widgetManager.updateAppWidget(widgetId, remoteViews);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }
}
