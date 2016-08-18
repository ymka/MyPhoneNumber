package net.ginapps.myphonenumber.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import net.ginapps.myphonenumber.R;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ClickListener mClickListener;
    public final TextView mPhoneNumber;
    public final TextView mOperatorName;

    public PhoneHolder(@NonNull View itemView, @NonNull ClickListener clickListener) {
        super(itemView);
        mPhoneNumber = (TextView) itemView.findViewById(R.id.phoneNumber);
        mOperatorName = (TextView) itemView.findViewById(R.id.operatorName);
        ImageButton copyToClipboard = (ImageButton) itemView.findViewById(R.id.copyPhoneToClipBoard);
        copyToClipboard.setOnClickListener(this);
        ImageButton share = (ImageButton) itemView.findViewById(R.id.sharePhone);
        share.setOnClickListener(this);
        mClickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copyPhoneToClipBoard:
                mClickListener.onCopyPhoneNumber(getAdapterPosition());
                break;
            case R.id.sharePhone:
                mClickListener.onSharePhoneNumber(getAdapterPosition());
                break;
            default:
                // do nothing
                break;
        }
    }


    public interface ClickListener {
        void onCopyPhoneNumber(int position);
        void onSharePhoneNumber(int position);
    }
}
