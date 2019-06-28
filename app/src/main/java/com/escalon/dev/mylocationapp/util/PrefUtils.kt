package com.escalon.dev.mylocationapp.util

import android.content.Context
import android.content.SharedPreferences

class PrefUtils(context : Context) {

    companion object {
        const val PREFS_FILENAME = "com.escalon.dev.prefs"
        const val CURRENT_SCREEN_PREFERENCE = "current_screen_preference"

        const val LOGIN_SCREEN = 1
        const val MAP_SCREEN = 2
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    // after splash, set login screen as default in case is a fresh install
    var currentScreen: Int
        get() = preferences.getInt(
            CURRENT_SCREEN_PREFERENCE,
            LOGIN_SCREEN
        )
        set(value) = preferences.edit().putInt(CURRENT_SCREEN_PREFERENCE, value).apply()


}