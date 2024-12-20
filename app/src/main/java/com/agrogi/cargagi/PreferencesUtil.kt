package com.agrogi.cargagi

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PreferencesUtil {
    private const val SERVER_IP_KEY = "server_ip"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getServerIp(context: Context): String? {
        return getSharedPreferences(context).getString(SERVER_IP_KEY, null)
    }
}