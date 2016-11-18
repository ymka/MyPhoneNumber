package net.ginapps.myphonenumber;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    private int mSlotIndex = 0;
    private boolean mShowEditNumber = false;

    private PhoneData(String phoneNumber, String operatorName, String countryIso) {
        mPhoneNumber = phoneNumber;
        mOperatorName = operatorName;
        mCountryIso = countryIso;
    }

    public String getPhoneNumber() {
        String phoneNumber = null;
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parse = numberUtil.parse(mPhoneNumber, mCountryIso.toUpperCase());
            phoneNumber = numberUtil.formatNumberForMobileDialing(parse, "US", true);
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

    public boolean isShowEditNumber() {
        return mShowEditNumber;
    }

    private void setShowEditNumber(boolean showEditNumber) {
        mShowEditNumber = showEditNumber;
    }

    public int getSlotIndex() {
        return mSlotIndex;
    }

    private void setSlotIndex(int slotIndex) {
        mSlotIndex = slotIndex;
    }

    public String getCountryIso() {
        return mCountryIso;
    }

    public static class Builder {
        private final Context mContext;
        private String mPhoneNumber;
        private String mOperatorName;
        private String mCountryIso;
        private int mColor = -1;
        private int mSlotIndex = 0;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            mPhoneNumber = phoneNumber;

            return this;
        }

        public Builder setOperatorName(String operatorName) {
            mOperatorName = operatorName;

            return this;
        }

        public Builder setCountryIso(String countryIso) {
            mCountryIso = countryIso;

            return this;
        }

        public Builder setColor(int color) {
            mColor = color;

            return this;
        }

        public Builder setSlotIndex(int slotIndex) {
            mSlotIndex = slotIndex;

            return this;
        }

        public PhoneData build() {
            boolean phoneNumberEmpty = mPhoneNumber.isEmpty();
            if (phoneNumberEmpty) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String key = String.valueOf(mSlotIndex);
                if (preferences.contains(key)) {
                    mPhoneNumber = preferences.getString(key, "");
                }
            }

            PhoneData phoneData = new PhoneData(mPhoneNumber, mOperatorName, mCountryIso);
            phoneData.setColor(mColor);
            phoneData.setShowEditNumber(phoneNumberEmpty);
            phoneData.setSlotIndex(mSlotIndex);

            return phoneData;
        }
    }

}
