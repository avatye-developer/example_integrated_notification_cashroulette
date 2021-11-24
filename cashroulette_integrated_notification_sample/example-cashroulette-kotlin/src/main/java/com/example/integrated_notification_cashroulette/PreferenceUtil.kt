package com.example.integrated_notification_cashroulette

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("notibar_prefs", Context.MODE_PRIVATE)

    fun getChecked(key: String, defValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun setChecked(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}