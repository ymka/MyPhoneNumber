package net.ginapps.myphonenumber;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import net.ginapps.myphonenumber.analytics.Analytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
public class DefaultPhoneNumberDelegate implements PhoneNumberDelegate {

    private final Context mContext;
    private final SubscriptionManager mManager;
    private final Analytics analytics;

    public DefaultPhoneNumberDelegate(Context context) {
        mContext = context;
        mManager = SubscriptionManager.from(context);
        analytics = ((MyPhoneApplication) context.getApplicationContext()).getAnalytics();
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean hasActiveSim() {
        return mManager.getActiveSubscriptionInfoCount() != 0;
    }

    @SuppressLint("MissingPermission")
    @Override
    public List<PhoneData> getSimsData() {
        List<SubscriptionInfo> infoList = mManager.getActiveSubscriptionInfoList();
        List<PhoneData> phoneDataList = new ArrayList<>(infoList.size());
        for (int i = 0; i < infoList.size(); i++) {
            SubscriptionInfo info = infoList.get(i);
            PhoneData phoneData = createPhoneDate(info);
            phoneDataList.add(phoneData);
        }

        return phoneDataList;
    }

    @NonNull
    private PhoneData createPhoneDate(SubscriptionInfo info) {
        analytics.sendPhoneStatusEvent(info.getNumber() == null || info.getNumber().isEmpty());
        PhoneData.Builder builder = new PhoneData.Builder(mContext);
        builder.setPhoneNumber(info.getNumber());
        builder.setOperatorName(info.getDisplayName().toString());
        builder.setCountryIso(info.getCountryIso());
        builder.setColor(info.getIconTint());
        builder.setSlotIndex(info.getSimSlotIndex());
        builder.setIccId(info.getIccId());

        return builder.build();
    }

    @SuppressLint("MissingPermission")
    @Override
    public PhoneData getSimBySlotIndex(int index) {
        PhoneData phoneData = null;
        SubscriptionInfo info = mManager.getActiveSubscriptionInfoForSimSlotIndex(index);
        if (info != null) {
            phoneData = createPhoneDate(info);
        }

        return phoneData;
    }
}
