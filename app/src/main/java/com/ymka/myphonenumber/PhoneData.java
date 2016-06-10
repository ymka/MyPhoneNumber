package com.ymka.myphonenumber;

import android.os.Build;
import android.telephony.PhoneNumberUtils;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneData {

    private final String mPhoneNumber;
    private final String mOperatorName;
    private final String mCountryIso;
    private int mColor = -1;

    public PhoneData(String phoneNumber, String operatorName, String countryIso) {
        mPhoneNumber = phoneNumber;
        mOperatorName = operatorName;
        mCountryIso = countryIso;
    }

    public String getPhoneNumber() {
        String phoneNumber = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneNumber = PhoneNumberUtils.formatNumber(mPhoneNumber, mCountryIso);
        }

        if (phoneNumber == null) {
            phoneNumber = PhoneNumberUtils.formatNumber(mPhoneNumber);
        }

        return phoneNumber;
    }

    public String getOperatorName() {
        return mOperatorName;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
