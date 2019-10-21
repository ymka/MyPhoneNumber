package net.ginapps.myphonenumber;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alexander Kondenko.
 */

public class RatingStatus {

    private static final String sKeyFirstStart = "firstStart";
    private static final String sKeyStartCount = "startCount";
    private static final String sKeyActionCount = "actionCount";
    private static final String sKeyDelay = "delay";
    private static final String sKeyNever = "never";

    private static final long sWeek = 604800000; // 1 week
    private static final int sMinStartCount = 5;
    private static final int sMinActionCount = 10;

    private final long mFirstStart;
    private int mStartCount = 0;
    private int mActionCount = 0;
    private long mDelay = 0;
    private boolean mNeverShow = false;


    public RatingStatus() {
        mFirstStart = System.currentTimeMillis();
    }

    public RatingStatus(JSONObject jsonObject) {
        mFirstStart = jsonObject.optLong(sKeyFirstStart);
        mStartCount = jsonObject.optInt(sKeyStartCount);
        mActionCount = jsonObject.optInt(sKeyActionCount);
        mDelay = jsonObject.optLong(sKeyDelay);
        mNeverShow = jsonObject.optBoolean(sKeyNever, false);
    }

    public void remindLater() {
        mDelay = System.currentTimeMillis();
    }

    public void increaseStartCount() {
        mStartCount++;
    }

    public void increaseActionCount() {
        mActionCount++;
    }

    public void never() {
        mNeverShow = true;
    }

    public boolean isShowRatingDialog() {
        long currentTime = System.currentTimeMillis();
        return !mNeverShow &&
               currentTime - mFirstStart > sWeek &&
               currentTime - mDelay > sWeek &&
               (mStartCount > sMinStartCount ||
                mActionCount > sMinActionCount);
    }

    @Nullable
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(sKeyFirstStart, mFirstStart);
            jsonObject.put(sKeyStartCount, mStartCount);
            jsonObject.put(sKeyActionCount, mActionCount);
            jsonObject.put(sKeyDelay, mDelay);
            jsonObject.put(sKeyNever, mNeverShow);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }

}
