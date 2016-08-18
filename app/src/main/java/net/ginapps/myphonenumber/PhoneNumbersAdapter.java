package net.ginapps.myphonenumber;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.ginapps.myphonenumber.holder.PhoneHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneNumbersAdapter extends RecyclerView.Adapter<PhoneHolder> implements PhoneHolder.ClickListener {
    @ColorRes
    private static final  int[] sColorIds = new int[]{R.color.operatorBackground1, R.color.operatorBackground2, R.color.operatorBackground3};

    private final Context mContext;
    private final List<PhoneData> mPhoneData;
    private ActionListener mActionListener;

    public PhoneNumbersAdapter(Context context) {
        mContext = context;
        mPhoneData = new ArrayList<>();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void addPhonesData(List<PhoneData> phoneData) {
        mPhoneData.addAll(phoneData);
    }

    @Override
    public PhoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhoneHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false), this);
    }

    @Override
    public void onBindViewHolder(PhoneHolder holder, int position) {
        PhoneData phoneData = mPhoneData.get(position);
        holder.mPhoneNumber.setText(phoneData.getPhoneNumber());
        holder.mOperatorName.setText(phoneData.getOperatorName());
        int operatorBackgroundColor;
        if (phoneData.getColor() != -1) {
            operatorBackgroundColor = phoneData.getColor();
        } else {
            operatorBackgroundColor =ContextCompat.getColor(mContext, sColorIds[position]);
        }

        GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.operator_background);
        shapeDrawable.setColor(operatorBackgroundColor);
        holder.mOperatorName.setBackground(shapeDrawable);
    }

    @Override
    public int getItemCount() {
        return mPhoneData.size();
    }

    @Override
    public void onCopyPhoneNumber(int position) {
        if (mActionListener != null) {
            mActionListener.onCopyPhoneNumber(mPhoneData.get(position).getPhoneNumber());
        }
    }

    @Override
    public void onSharePhoneNumber(int position) {
        if (mActionListener != null) {
            mActionListener.onSharePhoneNumber(mPhoneData.get(position).getPhoneNumber());
        }
    }

    public interface ActionListener {
        void onCopyPhoneNumber(String phoneNumber);
        void onSharePhoneNumber(String phoneNumber);
    }

}
