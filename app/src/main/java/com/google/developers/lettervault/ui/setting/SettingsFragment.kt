package com.google.developers.lettervault.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.developers.lettervault.R
import com.google.developers.lettervault.util.NightMode
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var preferences: SharedPreferences
    private lateinit var sharedPrefListener: SharedPreferences.OnSharedPreferenceChangeListener
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        sharedPrefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
            when (key) {
                getString(R.string.pref_key_night) -> {
                    sharedPref.getString(key, getString(R.string.pref_night_auto))?.let {
                        val mode = NightMode.valueOf(it.toUpperCase(Locale.US))
                        updateTheme(mode.value)
                    }
                }
            }
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preferences.unregisterOnSharedPreferenceChangeListener(sharedPrefListener)
    }
}
