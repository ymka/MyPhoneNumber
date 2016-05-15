package com.ymka.myphonenumber;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneData {

    private final String mPhoneNumber;
    private final String mOperatorName;

    public PhoneData(String phoneNumber, String operatorName) {
        mPhoneNumber = phoneNumber;
        mOperatorName = operatorName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getOperatorName() {
        return mOperatorName;
    }
}
