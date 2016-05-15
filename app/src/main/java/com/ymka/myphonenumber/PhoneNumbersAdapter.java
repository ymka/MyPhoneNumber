package com.ymka.myphonenumber;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneNumbersAdapter extends RecyclerView.Adapter<PhoneNumbersAdapter.Holder> {

    private final List<PhoneData> mPhoneData;

    public PhoneNumbersAdapter() {
        mPhoneData = new ArrayList<>();
    }

    public void addPhonesData(List<PhoneData> phoneData) {
        mPhoneData.addAll(phoneData);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        PhoneData phoneData = mPhoneData.get(position);
        holder.mPhoneNumber.setText(phoneData.getPhoneNumber());
        holder.mOperatorName.setText(phoneData.getOperatorName());
    }

    @Override
    public int getItemCount() {
        return mPhoneData.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final TextView mPhoneNumber;
        private final TextView mOperatorName;

        public Holder(View itemView) {
            super(itemView);
            mPhoneNumber = (TextView) itemView.findViewById(R.id.phoneNumber);
            mOperatorName = (TextView) itemView.findViewById(R.id.operatorName);
        }
    }

    public interface ActionListener {
        void onCopyPhoneNumber(String phoneNumber);
        void onSharePhoneNumber(String phoneNumber);
    }

}
