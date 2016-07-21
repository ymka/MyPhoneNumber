package com.ymka.myphonenumber;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements PhoneNumbersAdapter.ActionListener,
                                                               PermissionDialog.ActionListener {

    private static final String sKeyShowWarningDialog = "com.ymka.myphonenumber.MainActivity.KeyShowWarningDialog";
    private static final int sRequestAppSettings = 1233;
    private PhoneNumbersAdapter mPhoneNumbersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mPhoneNumbersAdapter = new PhoneNumbersAdapter(this);
        mPhoneNumbersAdapter.setActionListener(this);
        recyclerView.setAdapter(mPhoneNumbersAdapter);
        boolean isPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        Timber.d("On create. Permission is granted %s", isPermissionGranted);
        if (isPermissionGranted) {
            initData();
        } else if (savedInstanceState == null) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initData();
        } else {
            showPermissionDialog();
        }
    }

    private void initData() {
        PhoneNumberDelegate phoneNumberDelegate;
        boolean showWarning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Timber.d("Use default delegate");
            phoneNumberDelegate = new DefaultPhoneNumberDelegate(this);
        } else {
            Timber.d("Use legacy delegate");
            phoneNumberDelegate = new LegacyPhoneNumberDelegate(this);
            showWarning = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(sKeyShowWarningDialog, true);
        }

        if (showWarning) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(sKeyShowWarningDialog, false).apply();
            showWarningDialog();
        }

        Timber.d("Sims count %s", phoneNumberDelegate.getSimsData().size());
        mPhoneNumbersAdapter.addPhonesData(phoneNumberDelegate.getSimsData());
        mPhoneNumbersAdapter.notifyDataSetChanged();
        TextView textLabel = (TextView) findViewById(R.id.textLabel);
        if (mPhoneNumbersAdapter.getItemCount() == 0) {
            textLabel.setVisibility(View.VISIBLE);
            textLabel.setText(R.string.label_no_active_card);
        } else {
            textLabel.setVisibility(View.GONE);
        }
    }

    private void showWarningDialog() {
        Timber.d("Show warning dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_dual_sim_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void showPermissionDialog() {
        Timber.d("Show permission dialog");
        PermissionDialog dialog = new PermissionDialog();
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void onCopyPhoneNumber(String phoneNumber) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.clip_label), phoneNumber);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(this, R.string.phone_copy_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharePhoneNumber(String phoneNumber) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_text_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, phoneNumber);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_text_label)));
    }

    @Override
    public void onSettingsClicked(Intent intent) {
        startActivityForResult(intent, sRequestAppSettings);
    }

    @Override
    public void onPisitiveButtonClicked() {
        showRequestPermissionLabel();
    }

    private void showRequestPermissionLabel() {
        String requestText = getString(R.string.request_permissions);
        String mainText = getString(R.string.label_request_permissions, requestText);
        SpannableStringBuilder builder = new SpannableStringBuilder(mainText);
        int length = mainText.length() - requestText.length();
        builder.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.RequestText), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.RequestPermissionText), length, mainText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView requestLabel = (TextView) findViewById(R.id.textLabel);
        requestLabel.setText(builder);
        requestLabel.setVisibility(View.VISIBLE);
        requestLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case sRequestAppSettings:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    initData();
                } else {
                    showRequestPermissionLabel();
                }

                break;

            default:
                // do nothing
                break;
        }
    }
}
