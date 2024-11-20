package com.andre_luong.myruns4

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.andre_luong.myruns4.model.Settings

class SettingsPreference(context: Context) {
    private val PRIVACY_KEY = context.getString(R.string.privacy_key)
    private val UNIT_KEY = context.getString(R.string.unit_key)
    private val COMMENT_KEY = context.getString(R.string.comment_key)

    val preference: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.settings_preference_name),
        MODE_PRIVATE
    )

    fun getSettings(): Settings {
        return Settings(
            preference.getBoolean(PRIVACY_KEY, false),
            preference.getInt(UNIT_KEY, 0),
            preference.getString(COMMENT_KEY, "").toString()
        )
    }

    fun savePrivacy(value: Boolean) {
        with (preference.edit()) {
            putBoolean(PRIVACY_KEY, value)
            apply()
        }
    }

    fun saveUnit(value: Int) {
        with (preference.edit()) {
            putInt(UNIT_KEY, value)
            apply()
        }
    }

    fun saveComment(value: String) {
        with (preference.edit()) {
            putString(COMMENT_KEY, value)
            apply()
        }
    }
}
