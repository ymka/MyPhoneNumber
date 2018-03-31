package net.ginapps.myphonenumber

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import net.ginapps.myphonenumber.analytics.Analytics

/**
 * Created by Alexander Kondenko.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = (applicationContext as MyPhoneApplication).analytics
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val analyticsLabel = findViewById<TextView>(R.id.analyticsLabel)
        val analyticsSwitch = findViewById<Switch>(R.id.analyticsSwitch)
        analyticsLabel.isEnabled = analytics.enable
        analyticsSwitch.isChecked = analytics.enable
        analyticsSwitch.setOnCheckedChangeListener { _, isChecked ->
            analytics.setAvalability(isChecked)
            analyticsLabel.isEnabled = isChecked
        }

        findViewById<View>(R.id.aboutView).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}
