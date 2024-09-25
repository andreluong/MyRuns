package com.andre.myruns

import android.content.Context
import android.content.Context.MODE_PRIVATE

class SettingsPreference(context: Context) {
    private val PREFERENCE_NAME = "SettingsPreference"
    private val PRIVACY_KEY = "privacy"
    private val UNIT_KEY = "unit"
    private val COMMENT_KEY = "comment"
    private val preference = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    fun getSettings(): Settings {
        return Settings(
            preference.getBoolean(PRIVACY_KEY, false),
            preference.getInt(UNIT_KEY, -1),
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
