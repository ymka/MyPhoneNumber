package com.ymka.myphonenumber;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PhoneNumbersAdapter.ActionListener,
                                                               PermissionDialog.ActionListener {

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            phoneNumberDelegate = new DefaultPhoneNumberDelegate(this);
        } else {
            phoneNumberDelegate = new LegacyPhoneNumberDelegate(this);
        }
        mPhoneNumbersAdapter.addPhonesData(phoneNumberDelegate.getSimsData());
        mPhoneNumbersAdapter.notifyDataSetChanged();
    }

    private void showPermissionDialog() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case sRequestAppSettings:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    initData();
                }

                break;

            default:
                // do nothing
                break;
        }
    }
}
