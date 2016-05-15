package com.ymka.myphonenumber;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PhoneNumbersAdapter.ActionListener {

    private PhoneNumbersAdapter mPhoneNumbersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mPhoneNumbersAdapter = new PhoneNumbersAdapter();
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
        }
    }

    private void initData() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String line1Number = telephonyManager.getLine1Number();
        String simOperatorName = telephonyManager.getSimOperatorName();
        List<PhoneData> phonesData = new ArrayList<>();
        phonesData.add(new PhoneData(line1Number, simOperatorName));
        mPhoneNumbersAdapter.addPhonesData(phonesData);
        mPhoneNumbersAdapter.notifyDataSetChanged();
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
        Log.d("qwe", "Share number " + phoneNumber);
    }
}
