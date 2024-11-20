package com.andre_luong.myruns5

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.andre_luong.myruns5.model.Profile

class ProfilePreference(context: Context) {
    private val PREFERENCE_NAME = "ProfilePreference"
    private val NAME_KEY = "name"
    private val EMAIL_KEY = "email"
    private val PHONE_KEY = "phone"
    private val GENDER_KEY = "gender"
    private val CLASS_KEY = "class"
    private val MAJOR_KEY = "major"
    private val preference = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    fun getProfile(): Profile {
        return Profile(
            preference.getString(NAME_KEY, "").toString(),
            preference.getString(EMAIL_KEY, "").toString(),
            preference.getString(PHONE_KEY, "").toString(),
            preference.getString(GENDER_KEY, "").toString(),
            preference.getString(CLASS_KEY, "").toString(),
            preference.getString(MAJOR_KEY, "").toString()
        )
    }

    fun setProfile(
        nameValue: String,
        emailValue: String,
        phoneValue: String,
        genderValue: String,
        classValue: String,
        majorValue: String
    ) {
        val editor = preference.edit()
        editor.putString(NAME_KEY, nameValue)
        editor.putString(EMAIL_KEY, emailValue)
        editor.putString(PHONE_KEY, phoneValue)
        editor.putString(GENDER_KEY, genderValue)
        editor.putString(CLASS_KEY, classValue)
        editor.putString(MAJOR_KEY, majorValue)
        editor.apply()
    }
}