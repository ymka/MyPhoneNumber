package com.ymka.myphonenumber;

import android.content.Context;
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
        List<PhoneData> phonesData = null;
        if (hasActiveSim()) {
            String line1Number = mTelephonyManager.getLine1Number();
            String simOperatorName = mTelephonyManager.getSimOperatorName();
            String countryIso = mTelephonyManager.getSimCountryIso();
            phonesData = new ArrayList<>();
            phonesData.add(new PhoneData(line1Number, simOperatorName, countryIso));

        }

        return phonesData;
    }
}
