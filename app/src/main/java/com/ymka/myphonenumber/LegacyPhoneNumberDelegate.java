package com.ymka.myphonenumber;

import android.content.Context;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public class LegacyPhoneNumberDelegate implements PhoneNumberDelegate {

    private final TelephonyManager mTelephonyManager;

    public LegacyPhoneNumberDelegate(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public boolean hasActiveSim() {
        return mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    @Override
    public List<PhoneData> getSimsData() {
        List<PhoneData> phonesData = new ArrayList<>();
        if (hasActiveSim()) {
            phonesData.add(getPhoneData());
        }

        return phonesData;
    }

    private PhoneData getPhoneData() {
        String line1Number = mTelephonyManager.getLine1Number();
        String simOperatorName = mTelephonyManager.getSimOperatorName();
        String countryIso = mTelephonyManager.getSimCountryIso();

        return new PhoneData(line1Number, simOperatorName, countryIso);
    }

    @Nullable
    @Override
    public PhoneData getSimBySlotIndex(int index) {
        PhoneData phoneData = null;
        if (hasActiveSim()) {
            phoneData = getPhoneData();
        }

        return phoneData;
    }
}
