package net.ginapps.myphonenumber;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
public class LegacyPhoneNumberDelegate implements PhoneNumberDelegate {

    private final Context mContext;
    private final TelephonyManager mTelephonyManager;

    public LegacyPhoneNumberDelegate(Context context) {
        mContext = context;
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

    @SuppressLint("MissingPermission")
    private PhoneData getPhoneData() {
        String line1Number = mTelephonyManager.getLine1Number();
        String simOperatorName = mTelephonyManager.getSimOperatorName();
        String countryIso = mTelephonyManager.getSimCountryIso();
        String iccId = mTelephonyManager.getSubscriberId();

        PhoneData.Builder builder = new PhoneData.Builder(mContext);
        builder.setPhoneNumber(line1Number);
        builder.setOperatorName(simOperatorName);
        builder.setCountryIso(countryIso);
        builder.setIccId(iccId);

        return builder.build();
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
