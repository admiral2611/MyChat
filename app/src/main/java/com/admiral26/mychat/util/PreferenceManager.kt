package com.admiral26.mychat.util

import android.content.Context
import android.content.SharedPreferences
import com.admiral26.mychat.util.Constants.KEY_PREFERENCE_NAME

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        KEY_PREFERENCE_NAME, Context.MODE_PRIVATE
    )

    fun putBoolean(key: String?, value: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value!!)
        editor.apply()
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key: String?, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
