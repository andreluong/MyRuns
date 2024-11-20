package com.andre_luong.myruns4.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.andre_luong.myruns4.R

object SettingsUtils {
    fun getDistanceUnits(context: Context): Int {
        val settingsPreference = context.getSharedPreferences(
            context.getString(R.string.settings_preference_name),
            MODE_PRIVATE
        )
        return settingsPreference.getInt(context.getString(R.string.unit_key), 0)
    }
}