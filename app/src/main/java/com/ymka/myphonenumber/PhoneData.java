package com.ymka.myphonenumber;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

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
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parse = numberUtil.parse(mPhoneNumber, mCountryIso.toUpperCase());
            phoneNumber = numberUtil.formatOutOfCountryCallingNumber(parse, mCountryIso.toUpperCase());
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        if (phoneNumber == null) {
            phoneNumber = mPhoneNumber;
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
