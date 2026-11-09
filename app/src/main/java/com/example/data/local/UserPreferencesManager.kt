package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mobistream_prefs", Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var streamBufferMode: String
        get() = prefs.getString(KEY_BUFFER_MODE, "Low Latency") ?: "Low Latency"
        set(value) = prefs.edit().putString(KEY_BUFFER_MODE, value).apply()

    var detectedCountryCode: String
        get() = prefs.getString(KEY_COUNTRY_CODE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_COUNTRY_CODE, value).apply()

    var detectedCountryName: String
        get() = prefs.getString(KEY_COUNTRY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_COUNTRY_NAME, value).apply()

    var hasRequestedPermissions: Boolean
        get() = prefs.getBoolean(KEY_PERMISSIONS_REQUESTED, false)
        set(value) = prefs.edit().putBoolean(KEY_PERMISSIONS_REQUESTED, value).apply()

    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var preferredLanguages: Set<String>
        get() = prefs.getStringSet(KEY_PREFERRED_LANGUAGES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_PREFERRED_LANGUAGES, value).apply()

    var favoriteCategories: Set<String>
        get() = prefs.getStringSet(KEY_FAVORITE_CATEGORIES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_FAVORITE_CATEGORIES, value).apply()

    var lastWatchedChannelId: String
        get() = prefs.getString(KEY_LAST_WATCHED_CHANNEL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_WATCHED_CHANNEL, value).apply()

    companion object {
        private const val KEY_DARK_MODE = "key_dark_mode"
        private const val KEY_BUFFER_MODE = "key_buffer_mode"
        private const val KEY_COUNTRY_CODE = "key_country_code"
        private const val KEY_COUNTRY_NAME = "key_country_name"
        private const val KEY_PERMISSIONS_REQUESTED = "key_permissions_requested"
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        private const val KEY_PREFERRED_LANGUAGES = "key_preferred_languages"
        private const val KEY_FAVORITE_CATEGORIES = "key_favorite_categories"
        private const val KEY_LAST_WATCHED_CHANNEL = "key_last_watched_channel"
    }
}
