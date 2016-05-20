package com.ymka.myphonenumber;

import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public class LegacyPhoneNumberDelegate implements PhoneNumberDelegate {

    private final TelephonyManager mTelephonyManager;

    public LegacyPhoneNumberDelegate(TelephonyManager telephonyManager) {
        mTelephonyManager = telephonyManager;
    }

    @Override
    public boolean hasActiveSim() {
        return mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    @Override
    public List<PhoneData> getSimsData() {
        List<PhoneData> phonesData = null;
        if (hasActiveSim()) {
            String line1Number = mTelephonyManager.getLine1Number();
            String simOperatorName = mTelephonyManager.getSimOperatorName();
            phonesData = new ArrayList<>();
            phonesData.add(new PhoneData(line1Number, simOperatorName));
        }

        return phonesData;
    }
}
