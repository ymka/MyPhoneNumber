package net.ginapps.myphonenumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by Alexander Kondenko.
 */
public class PermissionDialog extends DialogFragment {

    private static final String SCHEME = "package";
    private ActionListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ActionListener) {
            mListener = (ActionListener) activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_permission_dialog);
        builder.setMessage(R.string.message_permission_dialog);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (mListener != null) {
                mListener.onPisitiveButtonClicked();
            }
        });

        builder.setNegativeButton(R.string.settings, (dialog, which) -> {
            if (mListener != null) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts(SCHEME, BuildConfig.APPLICATION_ID, null));
                mListener.onSettingsClicked(intent);
            }
        });


        return builder.create();
    }

    public interface ActionListener {
        void onSettingsClicked(Intent intent);
        void onPisitiveButtonClicked();
    }

}
