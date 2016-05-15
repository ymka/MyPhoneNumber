package com.ymka.myphonenumber;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ymka.myphonenumber.holder.PhoneHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneNumbersAdapter extends RecyclerView.Adapter<PhoneHolder> implements PhoneHolder.ClickListener {

    private final List<PhoneData> mPhoneData;
    private ActionListener mActionListener;

    public PhoneNumbersAdapter() {
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
