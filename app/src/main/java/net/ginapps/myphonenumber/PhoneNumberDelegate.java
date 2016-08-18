package net.ginapps.myphonenumber;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public interface PhoneNumberDelegate {

    boolean hasActiveSim();
    List<PhoneData> getSimsData();
    @Nullable
    PhoneData getSimBySlotIndex(int index);

}
