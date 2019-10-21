package net.ginapps.myphonenumber.analytics

import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import net.ginapps.myphonenumber.R

/**
 * Created by Alexander Kondenko.
 */
class AnalyticsInformer {

    companion object {
        private const val SHOW_DIALOG = "SHOW_DIALOG"
    }

    fun showDialogIfNeeded(activity: AppCompatActivity) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val showDialog = preferences.getBoolean(SHOW_DIALOG, true)
        if (showDialog) {
            preferences.edit().putBoolean(SHOW_DIALOG, false).apply()
            AlertDialog.Builder(activity).setMessage(R.string.analytics_info_message)
                    .setPositiveButton(R.string.button_ok) { dialog, _ -> dialog.dismiss()}
                    .show()
        }
    }

}
