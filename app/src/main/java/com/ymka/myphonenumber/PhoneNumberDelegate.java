package com.ymka.myphonenumber;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public interface PhoneNumberDelegate {

    boolean hasActiveSim();
    @Nullable
    List<PhoneData> getSimsData();

}
