package net.ginapps.myphonenumber;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements PhoneNumbersAdapter.ActionListener,
                                                               PermissionDialog.ActionListener,
                                                               EditPhoneNumberDialog.OnEditPhoneListener {

    private static final String sKeyShowWarningDialog = "net.ginapps.myphonenumber.MainActivity.KeyShowWarningDialog";
    private static final String sKeyRatingStatus = "net.ginapps.myphonenumber.MainActivity.KeyRatingStatus";
    private static final String sKeyKeepScreenOn = "net.ginapps.myphonenumber.MainActivity.sKeyKeepScreenOn";
    private static final int sRequestAppSettings = 1233;
    private PhoneNumbersAdapter mPhoneNumbersAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private RatingStatus mRatingStatus;
    PhoneNumberDelegate mPhoneNumberDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ratingString = preferences.getString(sKeyRatingStatus, "");
        if (ratingString.isEmpty()) {
            mRatingStatus = new RatingStatus();
        } else {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(ratingString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject != null) {
                mRatingStatus = new RatingStatus(jsonObject);
            } else {
                mRatingStatus = new RatingStatus();
            }
        }

        mRatingStatus.increaseStartCount();
        if (mRatingStatus.isShowRatingDialog()) {
            showRatingDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initData();
        } else {
            showPermissionDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean keep = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(sKeyKeepScreenOn, false);
        if (keep) {
            setKeepScreenOn(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveRatingStatus();
        boolean keep = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(sKeyKeepScreenOn, false);
        if (keep) {
            setKeepScreenOn(false);
        }
    }

    private void saveRatingStatus() {
        if (mRatingStatus != null) {
            JSONObject jsonObject = mRatingStatus.toJson();
            if (jsonObject != null) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString(sKeyRatingStatus, jsonObject.toString()).apply();
            }
        }
    }

    private void initData() {
        boolean showWarning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Timber.d("Use default delegate");
            mPhoneNumberDelegate = new DefaultPhoneNumberDelegate(this);
        } else {
            Timber.d("Use legacy delegate");
            mPhoneNumberDelegate = new LegacyPhoneNumberDelegate(this);
            showWarning = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(sKeyShowWarningDialog, true);
        }

        if (showWarning) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(sKeyShowWarningDialog, false).apply();
            showWarningDialog();
        }

        TextView textLabel = (TextView) findViewById(R.id.textLabel);
        if (!mPhoneNumberDelegate.hasActiveSim()) {
            textLabel.setVisibility(View.VISIBLE);
            textLabel.setText(R.string.label_no_active_card);
        } else {
            List<PhoneData> simsData = mPhoneNumberDelegate.getSimsData();
            mPhoneNumbersAdapter.addPhonesData(simsData);
            mPhoneNumbersAdapter.notifyDataSetChanged();
            textLabel.setVisibility(View.GONE);
            int simNumberWithoutPhoneNumber = simNumberWithoutPhoneNumber(simsData);
            if (simNumberWithoutPhoneNumber != -1) {
                editPhoneNumber(simNumberWithoutPhoneNumber, true);
            }
        }
    }

    private int simNumberWithoutPhoneNumber(List<PhoneData> simsData) {
        int simNumber = -1;
        for (int i = 0; i < simsData.size(); i++) {
            PhoneData data = simsData.get(i);
            if (TextUtils.isEmpty(data.getPhoneNumber())) {
                simNumber = i;
                break;
            }
        }


        return simNumber;
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
        mRatingStatus.increaseActionCount();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.clip_label), phoneNumber);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(this, R.string.phone_copy_toast, Toast.LENGTH_SHORT).show();
        AnalyticsUtils.sendApplicationStatistic(mFirebaseAnalytics, AnalyticsUtils.sCopyToClipboard);
    }

    @Override
    public void onSharePhoneNumber(String phoneNumber) {
        mRatingStatus.increaseActionCount();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_text_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, phoneNumber);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_text_label)));
        AnalyticsUtils.sendApplicationStatistic(mFirebaseAnalytics, AnalyticsUtils.sShareEvent);
    }

    @Override
    public void onEditPhoneNumber(int position) {
        editPhoneNumber(position, false);
    }

    private void editPhoneNumber(int position, boolean showDescription) {
        Timber.d("Edit phone number");
        PhoneData phoneData = mPhoneNumbersAdapter.getItemOnPosition(position);
        Bundle args = new Bundle();
        args.putInt(EditPhoneNumberDialog.KEY_ITEM_POSITION, position);
        args.putString(EditPhoneNumberDialog.KEY_ISO, phoneData.getCountryIso());
        args.putString(EditPhoneNumberDialog.KEY_PHONE_NUMBER, phoneData.getPhoneNumber());
        args.putBoolean(EditPhoneNumberDialog.KEY_SHOW_DESCRIPTION, showDescription);
        EditPhoneNumberDialog editPhoneNumberDialog = new EditPhoneNumberDialog();
        editPhoneNumberDialog.setArguments(args);
        getFragmentManager().beginTransaction().add(editPhoneNumberDialog, "").commitAllowingStateLoss();
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

    @Override
    public void onPhoneNumberSaved(int position, String phoneNumber) {
        PhoneData phoneData = mPhoneNumbersAdapter.getItemOnPosition(position);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(String.valueOf(phoneData.getSlotIndex()), phoneNumber).apply();
        mPhoneNumbersAdapter.resetPhonesData(mPhoneNumberDelegate.getSimsData());
        mPhoneNumbersAdapter.notifyDataSetChanged();
    }

    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.rate_title_dialog));
        builder.setMessage(getString(R.string.rate_message_dialog));
        builder.setPositiveButton(getString(R.string.positive_btn_rate_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchGooglePlay();
            }
        });

        builder.setNegativeButton(getString(R.string.negative_btn_rate_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRatingStatus.remindLater();
                saveRatingStatus();
            }
        });

        builder.setNeutralButton(getString(R.string.neutral_btn_rate_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRatingStatus.never();
                saveRatingStatus();
            }
        });

        builder.create().show();
    }

    private void launchGooglePlay() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        boolean keep = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(sKeyKeepScreenOn, false);
        menu.findItem(R.id.keepScreenOn).setChecked(keep);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.keepScreenOn:
                boolean checked = !item.isChecked();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                preferences.edit().putBoolean(sKeyKeepScreenOn, checked).apply();
                item.setChecked(checked);
                setKeepScreenOn(checked);
                break;
            case R.id.about:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setKeepScreenOn(boolean keepOn) {
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        if (keepOn) {
            attr.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            attr.flags ^= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        }

        getWindow().setAttributes(attr);
    }
}
