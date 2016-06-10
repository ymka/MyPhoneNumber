package com.ymka.myphonenumber;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
public class DefaultPhoneNumberDelegate implements PhoneNumberDelegate {

    private final SubscriptionManager mManager;

    public DefaultPhoneNumberDelegate(Context context) {
        mManager = SubscriptionManager.from(context);
    }

    @Override
    public boolean hasActiveSim() {
        return false;
    }

    @Nullable
    @Override
    public List<PhoneData> getSimsData() {
        List<SubscriptionInfo> infoList = mManager.getActiveSubscriptionInfoList();
        List<PhoneData> phoneDataList = new ArrayList<>(infoList.size());
        for (int i = 0; i < infoList.size(); i++) {
            SubscriptionInfo info = infoList.get(i);
            PhoneData phoneData = new PhoneData(info.getNumber(), info.getDisplayName().toString());
            phoneData.setColor(info.getIconTint());
            phoneDataList.add(phoneData);
        }

        return phoneDataList;
    }
}
