package net.ginapps.myphonenumber;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
    private final String mIccId;
    private int mColor = -1;
    private boolean mShowEditNumber = false;

    private PhoneData(String phoneNumber, String operatorName, String countryIso, String iccId) {
        mPhoneNumber = phoneNumber;
        mOperatorName = operatorName;
        mCountryIso = countryIso;
        mIccId = iccId;
    }

    public String getPhoneNumber() {
        String phoneNumber = null;
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        if (mPhoneNumber != null && (mPhoneNumber.length() > 7 || mPhoneNumber.contains("+"))) {
            try {
                String iso = "";
                if (!mShowEditNumber || mPhoneNumber.contains("+")) {
                    iso = mCountryIso.toUpperCase();
                }

                Phonenumber.PhoneNumber parse = numberUtil.parse(mPhoneNumber, iso);
                phoneNumber = numberUtil.format(parse, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        } else {
            phoneNumber = customFormat(mPhoneNumber);
        }

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            phoneNumber = customFormat(mPhoneNumber);
        }

        return phoneNumber;
    }

    private String customFormat(String originalString) {
        if (originalString == null) {
            return originalString;
        }

        String string = originalString.replace("+", "");
        if (string.length() < 3) {
            return originalString;
        }

        StringBuilder builder = new StringBuilder();
        if (string.length() <= 6) {
            builder.append(string.substring(0, 3))
                   .append(' ')
                   .append(string.substring(3));
        } else if (string.length() == 7){
            builder.append(string.substring(0, 3))
                   .append(string.substring(3, 5))
                   .append(' ')
                   .append(string.substring(5));
        } else {
            int start = 0;
            for (int i = string.length() - 3, j = string.length(); i >= 0; i -= 3, j -= 3) {
                start = i;
                builder.append(string.substring(i, j));
                if (i != 0) {
                    builder.append(' ');
                }
            }

            if (start != 0) {
                builder.append(string.substring(0, start));
            }

            builder.reverse();
        }

        if (originalString.contains("+")) {
            builder.insert(0, "+");
        }

        return builder.toString();
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

    public String getIccId() {
        return mIccId;
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
        private String mIccId = "";

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

        public void setIccId(String iccId) {
            mIccId = iccId;
        }

        public PhoneData build() {
            boolean phoneNumberEmpty = mPhoneNumber == null || mPhoneNumber.isEmpty();
            if (phoneNumberEmpty) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                if (preferences.contains(mIccId)) {
                    mPhoneNumber = preferences.getString(mIccId, "");
                } else {
                    String key = String.valueOf(mSlotIndex);
                    if (preferences.contains(key)) {
                        mPhoneNumber = preferences.getString(key, "");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(key);
                        editor.putString(mIccId, mPhoneNumber);
                        editor.apply();
                    }
                }
            }

            PhoneData phoneData = new PhoneData(mPhoneNumber, mOperatorName, mCountryIso, mIccId);
            phoneData.setColor(mColor);
            phoneData.setShowEditNumber(phoneNumberEmpty);

            return phoneData;
        }
    }

}
