package net.ginapps.myphonenumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import timber.log.Timber;

/**
 * Created by Alexander Kondenko
 */
public class EditPhoneNumberDialog extends DialogFragment {

    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_ITEM_POSITION = "itemPosition";
    public static final String KEY_ISO = "iso";
    public static final String KEY_SHOW_DESCRIPTION = "showDescription";

    private OnEditPhoneListener mListener;
    private int mItemPosition;
    private String mISO;

    private EditText mPhoneNumber;
    private TextInputLayout mPhoneInputLayout;
    private Button mPositiveButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnEditPhoneListener) {
            mListener = (OnEditPhoneListener) activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mItemPosition = arguments.getInt(KEY_ITEM_POSITION, 0);
        mISO = arguments.getString(KEY_ISO, "US");
        String phoneNumber = arguments.getString(KEY_PHONE_NUMBER, "");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.layout_input_phone, null);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneEditText);
        if (!phoneNumber.isEmpty()) {
            mPhoneNumber.setText(phoneNumber);
            mPhoneNumber.setSelection(0, phoneNumber.length());
        }

        mPhoneInputLayout = (TextInputLayout) view.findViewById(R.id.phoneInputLayout);
        builder.setTitle(R.string.dialog_title);
        if (arguments.getBoolean(KEY_SHOW_DESCRIPTION, false)) {
            builder.setMessage(R.string.dialog_description);
        }

        builder.setView(view);
        builder.setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPhoneNumber.getWindowToken(), 0);
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mPositiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                mPositiveButton.setEnabled(mPhoneNumber.getText().length() != 0);
                mPositiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishEditing();
                    }
                });

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                mPhoneNumber.requestFocus();
                mPhoneNumber.addTextChangedListener(mTextWatcher);
            }
        });

        return alertDialog;
    }

    private void finishEditing() {
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        String phone = mPhoneNumber.getText().toString();
        try {
            String formattedPhone = "+" + phone;
            Phonenumber.PhoneNumber phoneNumber = numberUtil.parse(formattedPhone, mISO.toUpperCase());
            boolean validNumber = numberUtil.isValidNumber(phoneNumber);
            Timber.d("Number validation %s", validNumber);
            if (validNumber) {
                saveNumber(phone);
            } else {
                setUnformattedNumber(phone);
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
            setUnformattedNumber(phone);
        }
    }

    private void saveNumber(String phone) {
        mListener.onPhoneNumberSaved(mItemPosition, phone);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPhoneNumber.getWindowToken(), 0);
        dismiss();
    }

    private void setUnformattedNumber(String number) {
        boolean containsPlus = number.contains("+");
        number = number.replace("+", "");
        number = number.replaceAll("[^\\d]", "");
        if (TextUtils.isDigitsOnly(number) && number.length() >= 5) {
            if (containsPlus) {
                number = "+" + number;
            }

            saveNumber(number);
        } else {
            mPhoneInputLayout.setError(getString(R.string.error_wrong_number));
        }
    }


    private String getErrorMessage(PhoneNumberUtil numberUtil) {
//        Phonenumber.PhoneNumber exampleNumberForType = numberUtil.getExampleNumberForType(PhoneNumberUtil.PhoneNumberType.MOBILE);
//        String forMobileDialing = numberUtil.formatNumberForMobileDialing(exampleNumberForType, "UA", true);

//        return "Example: " + forMobileDialing.toString();

        return getString(R.string.error_wrong_number);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPhoneNumber.removeTextChangedListener(mTextWatcher);
        mListener = null;
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPositiveButton.setEnabled(s.length() != 0);
            mPhoneInputLayout.setError("");
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    public interface OnEditPhoneListener {
        void onPhoneNumberSaved(int position, String phoneNumber);
    }

}
