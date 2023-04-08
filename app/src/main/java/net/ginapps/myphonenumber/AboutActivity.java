package net.ginapps.myphonenumber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alexander Kondenko.
 */

public class AboutActivity extends AppCompatActivity {

    private static String sEmail = "ginappscore@gmail.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView appNameView = findViewById(R.id.appName);
        appNameView.setText(getString(R.string.app_name_label, getString(R.string.app_name)));

        TextView appVersionView = findViewById(R.id.appVersion);
        appVersionView.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));

        findViewById(R.id.sendEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sEmail});
        String subject = getString(R.string.email_subject, BuildConfig.VERSION_NAME);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.send_email_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case android.R.  id.home:
                result = true;
                finish();
                break;
            default:
                result = false;
                break;
        }

        return result || super.onOptionsItemSelected(item);
    }
}
