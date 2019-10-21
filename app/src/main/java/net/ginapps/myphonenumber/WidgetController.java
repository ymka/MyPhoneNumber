package net.ginapps.myphonenumber;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public class WidgetController {

    private final PhoneNumberDelegate mNumberDelegate;
    private final SharedPreferences mPreferences;

    public WidgetController(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mNumberDelegate = new DefaultPhoneNumberDelegate(context);
        } else {
            mNumberDelegate = new LegacyPhoneNumberDelegate(context);
        }

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void addWidgetForSimSlot(int id, int slotIndex) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(String.valueOf(id), slotIndex).apply();
    }

    public void removeWidget(int... ids) {
        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = 0; i < ids.length; i++) {
            editor.remove(String.valueOf(ids[i]));
        }

        editor.apply();
    }

    public int getActiveSimsCount() {
        return mNumberDelegate.getSimsData().size();
    }

    public int getSlotIndexByWidgetId(int id) {
        return mPreferences.getInt(String.valueOf(id), 0);
    }

    public boolean hasActiveSim() {
        return mNumberDelegate.hasActiveSim();
    }

    public List<PhoneData> getPhoneDataList() {
        return mNumberDelegate.getSimsData();
    }

    public PhoneData getPhoneDataByWidgetId(int id) {
        int slotIndex = getSlotIndexByWidgetId(id);

        return mNumberDelegate.getSimBySlotIndex(slotIndex);
    }

}
