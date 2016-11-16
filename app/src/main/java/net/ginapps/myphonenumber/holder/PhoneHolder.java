package net.ginapps.myphonenumber.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import net.ginapps.myphonenumber.R;

/**
 * Created by Alexander Kondenko.
 */
public class PhoneHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            Toolbar.OnMenuItemClickListener {

    private final ClickListener mClickListener;
    public final TextView phoneNumber;
    public final TextView operatorName;
    public final Toolbar toolbar;

    public PhoneHolder(@NonNull View itemView, @NonNull ClickListener clickListener) {
        super(itemView);
        mClickListener = clickListener;
        phoneNumber = (TextView) itemView.findViewById(R.id.phoneNumber);
        operatorName = (TextView) itemView.findViewById(R.id.operatorName);
        toolbar = (Toolbar) itemView.findViewById(R.id.phone_toolbar);
        toolbar.inflateMenu(R.menu.item_list);
        toolbar.setOnMenuItemClickListener(this);
        ImageButton copyToClipboard = (ImageButton) itemView.findViewById(R.id.copyPhoneToClipBoard);
        copyToClipboard.setOnClickListener(this);
        ImageButton share = (ImageButton) itemView.findViewById(R.id.sharePhone);
        share.setOnClickListener(this);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mClickListener.onEditPhoneNumber(getAdapterPosition());

        return true;
    }


    public interface ClickListener {
        void onCopyPhoneNumber(int position);
        void onSharePhoneNumber(int position);
        void onEditPhoneNumber(int position);
    }
}
