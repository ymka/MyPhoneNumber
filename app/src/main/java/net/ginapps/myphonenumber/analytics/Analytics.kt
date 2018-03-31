package net.ginapps.myphonenumber.analytics

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import net.ginapps.myphonenumber.BuildConfig

/**
 * Created by Alexander Kondenko.
 */
class Analytics(context: Context) {

    companion object {
        private const val ANALYTIC_ENABLE = "ANALYTIC_ENABLE"

        const val sShareEvent = "Share"
        const val sCopyToClipboard = "Copy to clipboard"
        const val sSetNumber = "Set number"

        private const val sApplicationEvent = "Application"
        private const val sWidgetEvent = "Widget"
        private const val sEventName = "EVENT_NAME"
    }

    var enable: Boolean
    private set

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val answers: Answers?
    private val firebaseAnalytics: FirebaseAnalytics

    init {
        enable = !BuildConfig.DEBUG && sharedPreferences.getBoolean(ANALYTIC_ENABLE, true)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.setAnalyticsCollectionEnabled(enable)
        if (enable) {
            Fabric.with(context, Crashlytics())
            answers = Answers.getInstance()
        } else {
            answers = null
        }
    }

    fun setAvalability(value: Boolean) {
        sharedPreferences.edit().putBoolean(ANALYTIC_ENABLE, value).apply()
        enable = value
    }

    fun sendApplicationStatistic(name: String) {
        call {
            sendStatistic(sApplicationEvent, name)
        }
    }

    fun sendWidgetStatistic(name: String) {
        call {
            sendStatistic(sWidgetEvent, name)
        }
    }

    private fun sendStatistic(eventId: String, name: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, eventId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val event = CustomEvent(eventId).apply {
            putCustomAttribute(sEventName, name)
        }
        answers?.logCustom(event)
    }

    private inline fun call(body: () -> Unit) {
        if (enable) {
            body()
        }
    }

}
