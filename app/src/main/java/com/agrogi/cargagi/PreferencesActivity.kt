package com.agrogi.cargagi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat


class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            val aboutPreference: Preference? = findPreference("about")
            aboutPreference?.setOnPreferenceClickListener {
                showAboutDialog()
                true
            }

        }

        private fun showAboutDialog() {
            AlertDialog.Builder(requireContext())
                .setTitle("Acerca de")
                .setMessage("Esta es una aplicación realizada por la empresa AgroGI. Desarrollada por Valentino Badano.")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }
}
