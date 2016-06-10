package com.ymka.myphonenumber;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneData {

    private final String mPhoneNumber;
    private final String mOperatorName;
    private int mColor = -1;

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

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
