package net.ginapps.myphonenumber;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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

    public void resetPhonesData(List<PhoneData> phoneData) {
        mPhoneData.clear();
        mPhoneData.addAll(phoneData);
    }

    @Override
    public PhoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhoneHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false), this);
    }

    @Override
    public void onBindViewHolder(PhoneHolder holder, int position) {
        PhoneData phoneData = mPhoneData.get(position);
        String phoneNumber = phoneData.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            phoneNumber = mContext.getString(R.string.unknown_number);
        }

        if (phoneData.isShowEditNumber()) {
            holder.editBtn.setVisibility(View.VISIBLE);
        } else {
            holder.editBtn.setVisibility(View.GONE);
        }

        holder.phoneNumber.setText(phoneNumber);
        holder.operatorName.setText(phoneData.getOperatorName());
        int operatorBackgroundColor;
        if (phoneData.getColor() != -1) {
            operatorBackgroundColor = phoneData.getColor();
        } else {
            operatorBackgroundColor =ContextCompat.getColor(mContext, sColorIds[position]);
        }

        GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.operator_background);
        shapeDrawable.setColor(operatorBackgroundColor);
        holder.operatorName.setBackground(shapeDrawable);
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

    @Override
    public void onEditPhoneNumber(int position) {
        if (mActionListener != null) {
            mActionListener.onEditPhoneNumber(position);
        }
    }

    public PhoneData getItemOnPosition(int position) {
        return mPhoneData.get(position);
    }

    public interface ActionListener {
        void onCopyPhoneNumber(String phoneNumber);
        void onSharePhoneNumber(String phoneNumber);
        void onEditPhoneNumber(int position);
    }

}
